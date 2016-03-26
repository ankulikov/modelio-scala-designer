package org.modelio.module.scaladesigner.reverse.ast2modelio.impl;

import edu.kulikov.ast_parser.elements.*;
import edu.kulikov.ast_parser.elements.util.AstTraverser;
import org.modelio.api.model.IModelingSession;
import org.modelio.api.model.IUmlModel;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;
import org.modelio.module.scaladesigner.reverse.ast2modelio.Ast2ModelioRepo;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContext;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IAstVisitHandler;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContextable;
import org.modelio.module.scaladesigner.reverse.ast2modelio.util.ElementFactory;

/**
 * Converts {@link AstElement}s into {@link ModelElement}s
 */
public class ElementCreatorFromAst implements IAstVisitHandler, IContextable {
    private final ElementFactory factory;
    private final Ast2ModelioRepo repo;

    private IContext context;

    public ElementCreatorFromAst(IModelingSession session) {
        IUmlModel model = session.getModel();
        this.repo = Ast2ModelioRepo.getInstance();
        this.factory = new ElementFactory(model, repo);
    }



    @Override
    public void onStartVisit(AstElement astElement) {
        if (context == null)
            throw new IllegalArgumentException("Context was not initialized!");
        ModelElement element = null;
        if (astElement instanceof PackageDef) {
            //Package is the only type which can be top-level => then use getModelRoot()
            element = factory.createPackage((PackageDef) astElement, context);
        } else if (astElement instanceof ClassDef) {
            element = factory.createClass((ClassDef) astElement, context);
        } else if (astElement instanceof DefDef) {
            element = factory.createOperation((DefDef) astElement, context);
        } else if (astElement instanceof ValDef) {
            element = factory.createVariable((ValDef) astElement, context);
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

    @Override
    public void setContext(IContext context) {
        this.context = context;
    }
}
