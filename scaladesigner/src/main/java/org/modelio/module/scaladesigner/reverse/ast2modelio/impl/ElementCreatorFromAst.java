package org.modelio.module.scaladesigner.reverse.ast2modelio.impl;

import edu.kulikov.ast_parser.elements.*;
import edu.kulikov.ast_parser.elements.util.AstTraverser;
import edu.kulikov.ast_parser.elements.util.NoElement;
import org.modelio.api.model.IModelingSession;
import org.modelio.api.model.IUmlModel;
import org.modelio.metamodel.mda.Project;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.statik.*;
import org.modelio.metamodel.uml.statik.Class;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;
import org.modelio.module.scaladesigner.reverse.ast2modelio.Ast2ModelioRepo;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IAstVisitHandler;
import org.modelio.module.scaladesigner.reverse.ast2modelio.util.ElementFactory;
import org.modelio.vcore.smkernel.mapi.MObject;

/**
 * Converts {@link AstElement}s into {@link ModelElement}s
 */
public class ElementCreatorFromAst implements IAstVisitHandler {
    private final ElementFactory factory;
    private final IUmlModel model;
    private final Ast2ModelioRepo repo;

    public ElementCreatorFromAst(IModelingSession session) {
        this.model = session.getModel();
        this.factory = new ElementFactory(model);
        this.repo = Ast2ModelioRepo.getInstance();

    }

    public Package getModelRoot() {
        for (MObject mObject : model.getModelRoots()) {
            if (mObject instanceof Project) {
                return ((Project) mObject).getModel();
            }
        }
        throw new IllegalArgumentException("UML model doesn't have root package");
    }

    @Override
    public void onStartVisit(AstElement astElement) {
        ModelElement element = null;
        if (astElement instanceof PackageDef) {
            //Package is the only type which can be top-level => then use getModelRoot()
            element = factory.createPackage((PackageDef) astElement, astElement.getParent() == NoElement.instance() ? getModelRoot() : repo.get(astElement.getParent()));
        } else if (astElement instanceof ClassDef) {
            element = factory.createClass((ClassDef) astElement, repo.get(astElement.getParent()));
        } else if (astElement instanceof DefDef) {
            element = factory.createOperation((DefDef) astElement, repo.get(parent(astElement, ClassDef.class)));
        }
        else {
            ScalaDesignerModule.logService.warning("Unknown AstElement: " + astElement);
        }
        if (element != null)
            repo.save(astElement, element);
    }

    @Override
    public void onEndVisit(AstElement element) {

    }

    public static AstElement parent(AstElement element, java.lang.Class<? extends AstElement> type) {
        return AstTraverser.getParentByType(element, type);
    }
}
