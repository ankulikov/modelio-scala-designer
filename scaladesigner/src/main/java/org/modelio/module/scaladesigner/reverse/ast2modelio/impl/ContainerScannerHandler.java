package org.modelio.module.scaladesigner.reverse.ast2modelio.impl;

import edu.kulikov.ast_parser.elements.AstElement;
import org.modelio.api.model.IUmlModel;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IAstVisitHandler;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContext;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContext.Scope;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContextable;
import org.modelio.module.scaladesigner.reverse.ast2modelio.factory.ElementFactory;
import org.modelio.module.scaladesigner.reverse.ast2modelio.repos.ReposManager;

import static org.modelio.module.scaladesigner.reverse.ast2modelio.repos.Ast2ModelioRepo.Status.REVERSE_ONLY_NAME;

public class ContainerScannerHandler implements IAstVisitHandler, IContextable {

    private final ElementFactory factory;
    private final ReposManager rm;
    private final IUmlModel model;
    private IContext context;

    public ContainerScannerHandler(IUmlModel model) {
        this.model = model;
        this.factory = new ElementFactory();
        this.rm = ReposManager.getInstance();
    }


    @Override
    public void onStartVisit(AstElement astElement) {
        if (context == null)
            throw new IllegalArgumentException("Context was not initialized!");
        if (context.getCurrentScopeType() == Scope.PACKAGE ||
                context.getCurrentScopeType() == Scope.CLASS) {
            ModelElement element = factory.createElement(astElement, model, context, false);
            if (element != null) {
                rm.attachAstToModelio(astElement, element, REVERSE_ONLY_NAME);
            } else {
                ScalaDesignerModule.logService.warning("Unknown AstElement: " + astElement);
            }
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
