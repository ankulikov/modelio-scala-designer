package org.modelio.module.scaladesigner.reverse.ast2modelio.impl;

import edu.kulikov.ast_parser.elements.AstElement;
import edu.kulikov.ast_parser.elements.ClassDef;
import edu.kulikov.ast_parser.elements.PackageDef;
import org.modelio.api.model.IModelingSession;
import org.modelio.api.model.IUmlModel;
import org.modelio.metamodel.mda.Project;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.statik.Class;
import org.modelio.metamodel.uml.statik.NameSpace;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;
import org.modelio.module.scaladesigner.reverse.ast2modelio.Ast2ModelioRepo;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IAstVisitHandler;
import org.modelio.vcore.smkernel.mapi.MObject;

/**
 * Converts {@link AstElement}s into {@link ModelElement}s
 */
public class ElementCreatorFromAst implements IAstVisitHandler {
    private final IModelingSession session;
    private final IUmlModel factory;
    private final Ast2ModelioRepo repo;

    public ElementCreatorFromAst(IModelingSession session) {
        this.session = session;
        this.factory = session.getModel();
        this.repo = Ast2ModelioRepo.getInstance();
    }

    public Package getModelRoot() {
        for (MObject mObject : factory.getModelRoots()) {
            if (mObject instanceof Project) {
                return ((Project) mObject).getModel();
            }
        }
        throw new IllegalArgumentException("UML model doesn't have root package");
    }

    @Override
    public void onVisit(AstElement astElement) {
        ModelElement element = null;
        if (astElement instanceof PackageDef) {
            //Package is the only type which can be top-level => then use getModelRoot()
            element = createPackage(astElement, astElement.getParent() == null ? getModelRoot() : repo.get(astElement.getParent()));
        } else if (astElement instanceof ClassDef) {
            element = createClass(astElement, repo.get(astElement.getParent()));
        }
        else {
            ScalaDesignerModule.logService.warning("Unknown AstElement: " + astElement);
        }
        if (element != null)
            repo.save(astElement, element);
    }

    public Package createPackage(AstElement packageDef, ModelElement owner) {
        ScalaDesignerModule.logService.info("Create package: "+packageDef+" owner: "+owner);
        return factory.createPackage(((PackageDef) packageDef).getName(), (NameSpace) owner);
    }

    public Class createClass(AstElement classDef, ModelElement owner) {
        ScalaDesignerModule.logService.info("Create classDef: "+classDef+" owner: "+owner);
        return factory.createClass(((ClassDef) classDef).getName(), (NameSpace) owner);
    }
}
