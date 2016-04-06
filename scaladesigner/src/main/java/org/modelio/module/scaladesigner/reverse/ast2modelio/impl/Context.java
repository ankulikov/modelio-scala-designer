package org.modelio.module.scaladesigner.reverse.ast2modelio.impl;

import edu.kulikov.ast_parser.elements.Import;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContext;

import java.util.ArrayList;
import java.util.List;

public class Context implements IContext {
    private List<Import> importScope;
    private Scope currentScope = Scope.UNKNOWN;

    public Context() {
        importScope = new ArrayList<>();
    }

    @Override
    public List<Import> getImportScope() {
        return importScope;
    }

    @Override
    public void setCurrentScope(Scope scope) {
        this.currentScope = scope;
    }

    @Override
    public Scope getCurrentScope() {
        return currentScope;
    }
}
