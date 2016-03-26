package org.modelio.module.scaladesigner.reverse.ast2modelio.impl;

import edu.kulikov.ast_parser.elements.AstElement;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContext;

public class Context implements IContext {
    private AstElement scope;

    @Override
    public AstElement getScope() {
        return scope;
    }

    @Override
    public void setScope(AstElement scope) {
        this.scope = scope;
    }
}
