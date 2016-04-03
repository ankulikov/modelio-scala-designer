package org.modelio.module.scaladesigner.reverse.ast2modelio.impl;

import edu.kulikov.ast_parser.elements.AstElement;
import edu.kulikov.ast_parser.elements.Import;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContext;

import java.util.ArrayList;
import java.util.List;

public class Context implements IContext {
    public Context() {
        importScope = new ArrayList<>();
    }

    private List<Import> importScope;

    @Override
    public List<Import> getImportScope() {
        return importScope;
    }
}
