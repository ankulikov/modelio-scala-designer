package org.modelio.module.scaladesigner.reverse.ast2modelio.impl;

import edu.kulikov.ast_parser.elements.AstElement;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContext;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IAstVisitHandler;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContextable;

public class ContextFiller implements IAstVisitHandler, IContextable{
    private IContext context;

    @Override
    public void onStartVisit(AstElement element) {

    }

    @Override
    public void onEndVisit(AstElement element) {

    }

    @Override
    public void setContext(IContext context) {
        this.context = context;
    }
}
