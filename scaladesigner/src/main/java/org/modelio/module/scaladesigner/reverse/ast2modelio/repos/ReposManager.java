package org.modelio.module.scaladesigner.reverse.ast2modelio.repos;

import edu.kulikov.ast_parser.elements.AstElement;
import edu.kulikov.ast_parser.elements.Identifiable;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;
import org.modelio.module.scaladesigner.reverse.ast2modelio.repos.Ast2ModelioRepo.Status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ReposManager {
    private static ReposManager instance;
    private Ast2ModelioRepo transformRepo;
    private IdentifierRepo identifierRepo;

    public static ReposManager getInstance() {
        if (instance == null) {
            instance = new ReposManager();
            instance.setTransformRepo(Ast2ModelioRepo.getInstance());
            instance.setIdentifierRepo(IdentifierRepo.getInstance());
        }
        return instance;
    }

    public void setTransformRepo(Ast2ModelioRepo transformRepo) {
        this.transformRepo = transformRepo;
    }

    public void setIdentifierRepo(IdentifierRepo identifierRepo) {
        this.identifierRepo = identifierRepo;
    }

    public void attachIdentToModelio(ModelElement element, String fullIdentifier) {
        ScalaDesignerModule.logService.info("Save in ident repo: name=" + fullIdentifier + ", element=" + element);
        identifierRepo.save(fullIdentifier, element);
    }

    public void attachAstToModelio(AstElement ast, ModelElement modelio, Status status) {
        transformRepo.save(ast, modelio, status);
    }

    public List<ModelElement> getByAst(AstElement element) {
        ModelElement fromTransformRepo = transformRepo.get(element);
        if (fromTransformRepo != null) return Collections.singletonList(fromTransformRepo);
        if (element instanceof Identifiable) {
            Set<ModelElement> byIdentifier = identifierRepo.getByIdentifier(((Identifiable) element).getFullIdentifier());
            if (byIdentifier == null) return null;
            return new ArrayList<>(byIdentifier);
        }
        return null;
    }

    public <T extends ModelElement> T getByAst(AstElement element, Class<T> filter) {
        ModelElement fromTransformRepo = transformRepo.get(element);
        if (fromTransformRepo != null && filter.isInstance(fromTransformRepo)) return filter.cast(fromTransformRepo);
        if (element instanceof Identifiable)
            return filter.cast(identifierRepo.getByIdentifier(((Identifiable) element).getFullIdentifier()).stream().findFirst().filter(filter::isInstance).orElse(null));
        return null;
    }

    public List<ModelElement> getByFullIdent(String fullIdent) {
        Set<ModelElement> byIdentifier = identifierRepo.getByIdentifier(fullIdent);
        if (byIdentifier == null) return null;
        return new ArrayList<>(byIdentifier);
    }

    public <T extends ModelElement> T getByFullIdent(String fullIdent, Class<T> filter) {
        return filter.cast(identifierRepo.getByIdentifier(fullIdent).stream().findFirst().filter(filter::isInstance).orElse(null));
    }

}
