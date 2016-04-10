package org.modelio.module.scaladesigner.reverse.ast2modelio.handlers;

import edu.kulikov.ast_parser.elements.AstElement;
import org.modelio.api.model.IUmlModel;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IAstVisitHandler;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContext;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContextable;
import org.modelio.module.scaladesigner.reverse.ast2modelio.factory.ElementFactory;
import org.modelio.module.scaladesigner.reverse.ast2modelio.repos.ReposManager;

import static org.modelio.module.scaladesigner.reverse.ast2modelio.api.IElementFactory.Stage.REVERSE_RELATIONS;

public class RelationsCreatorHandler implements IAstVisitHandler, IContextable {
    private final ElementFactory factory;
    private final ReposManager rm;
    private final IUmlModel model;

    private IContext context;

    public RelationsCreatorHandler(IUmlModel model) {
        this.model = model;
        this.rm = ReposManager.getInstance();
        this.factory = new ElementFactory();
    }


    @Override
    public void onStartVisit(AstElement astElement) {
        if (context == null)
            throw new IllegalArgumentException("Context was not initialized!");
        factory.createElement(astElement, model, context, REVERSE_RELATIONS);
    }

    @Override
    public void onEndVisit(AstElement element) {

    }

    @Override
    public void setContext(IContext context) {
        this.context = context;
    }
}

