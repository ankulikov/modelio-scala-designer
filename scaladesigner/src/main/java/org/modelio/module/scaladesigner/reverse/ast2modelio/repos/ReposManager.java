package org.modelio.module.scaladesigner.reverse.ast2modelio.repos;

import edu.kulikov.ast_parser.elements.AstElement;
import edu.kulikov.ast_parser.elements.Identifiable;
import edu.kulikov.ast_parser.elements.Import;
import org.apache.commons.lang3.tuple.Pair;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;
import org.modelio.module.scaladesigner.reverse.ast2modelio.repos.Ast2ModelioRepo.Status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public <T extends ModelElement> T getByAnyIdent(String ident, String currentPackage, List<Import> importContext, Class<T> filter) {
        String[] split = ident.split("\\.");
        ScalaDesignerModule.logService.info("getByAnyIdent, ident=" + ident);
        if (split.length == 1) {
            //no dots => simple ident
            return resolveSimpleName(ident, currentPackage, importContext).stream().filter(filter::isInstance).map(filter::cast).findFirst().orElse(null);
        }
        if (split.length > 1) {
            return identifierRepo.getByFullIdentifier(ident).stream().filter(filter::isInstance).map(filter::cast).findFirst().orElse(null);
            //2+ dots => full ident
            //TODO: package with class (when import package A, ident is A.class_name)
        }
        return null;
    }

    private Set<ModelElement> resolveSimpleName(String ident, String currentPackage, List<Import> importContext) {
        Set<Pair<String, ModelElement>> fullIdentsSet = identifierRepo.getBySimpleIdentifier(ident);
        if (fullIdentsSet.isEmpty())
            ScalaDesignerModule.logService.warning("Cannot find element by simple ident ");
        if (fullIdentsSet.size() == 1)
            //noinspection OptionalGetWithoutIsPresent
            return fullIdentsSet.stream().map(Pair::getRight).collect(Collectors.toSet());
        //if class in the current package => return from this package, don't scan imports
        if (currentPackage != null) {
            Set<ModelElement> set = fullIdentsSet.stream().filter(p -> p.getKey().equals(currentPackage + "." + ident)).map(Pair::getRight).collect(Collectors.toSet());
            if (!set.isEmpty()) return set;
        }

        //=== find in importContext ===
        //if import is wildcard, we add simple identifier as a suffix and try to find full identifier in
        //set of full identifiers
        Set<String> flatImports = importContext.stream()
                .flatMap(imprt -> imprt.getSuffixes().stream()
                        .map(selector -> imprt.getPrefixName() + "." + (selector.isWildcard() ? ident : selector.getSuffix())))
                .collect(Collectors.toSet());
        return fullIdentsSet.stream()
                .filter(p -> flatImports.contains(p.getKey()))
                .map(Pair::getRight).collect(Collectors.toSet());
    }


}
