package org.modelio.module.scaladesigner.reverse.ast2modelio.impl;

import edu.kulikov.ast_parser.elements.*;
import org.modelio.api.model.IModelingSession;
import org.modelio.api.model.IUmlModel;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;
import org.modelio.module.scaladesigner.reverse.ast2modelio.repos.Ast2ModelioRepo;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContext;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IAstVisitHandler;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContextable;
import org.modelio.module.scaladesigner.reverse.ast2modelio.factory.ElementFactory;

import static org.modelio.module.scaladesigner.reverse.ast2modelio.repos.Ast2ModelioRepo.Status.REVERSE_FULL_SIGNATURE;

/**
 * Converts {@link AstElement}s into {@link ModelElement}s
 */
public class ElementCreatorFromAstHandler implements IAstVisitHandler, IContextable {
    private final ElementFactory factory;
    private final Ast2ModelioRepo repo;
    private final IUmlModel model;

    private IContext context;

    public ElementCreatorFromAstHandler(IModelingSession session) {
        this.model = session.getModel();
        this.repo = Ast2ModelioRepo.getInstance();
        this.factory = new ElementFactory();
    }



    @Override
    public void onStartVisit(AstElement astElement) {
        if (context == null)
            throw new IllegalArgumentException("Context was not initialized!");
        ModelElement element = factory.createElement(astElement, model, context, true);
        if (element != null) {
            repo.save(astElement, element, REVERSE_FULL_SIGNATURE);
        } else {
            ScalaDesignerModule.logService.warning("Unknown AstElement: " + astElement);
        }
    }

    @Override
    public void onEndVisit(AstElement element) {

    }

    @Override
    public void setContext(IContext context) {
        this.context = context;
    }
}
