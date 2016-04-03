package org.modelio.module.scaladesigner.reverse.ast2modelio.impl;

import edu.kulikov.ast_parser.elements.AstElement;
import edu.kulikov.ast_parser.elements.Import;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IAstVisitHandler;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContext;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContextable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContextFillerHandler implements IAstVisitHandler, IContextable {
    private IContext context;
    //we need to know scope of import because in Scala imports can be anywhere (*WOOOOO!*)
    private Map<AstElement, List<Import>> scopeHolder = new HashMap<>();

    @Override
    public void onStartVisit(AstElement element) {
        if (element instanceof Import) {
            ScalaDesignerModule.logService.info("Context, add import: "+element);
            saveWithParent((Import) element);
            context.getImportScope().add((Import) element);
        }
    }

    @Override
    public void onEndVisit(AstElement element) {
        //if element with imports is visited, remove all imports in its scope
        if (scopeHolder.containsKey(element)) {
            List<Import> toRemove = scopeHolder.remove(element);
            ScalaDesignerModule.logService.info("Context, delete import: "+toRemove+", parent:"+element);
            context.getImportScope().removeAll(toRemove);
        }
    }

    @Override
    public void setContext(IContext context) {
        this.context = context;
    }

    private void saveWithParent(Import iimport) {
        AstElement parent = iimport.getParent();
        if (!scopeHolder.containsKey(parent)) {
            scopeHolder.put(parent, new ArrayList<>());
        }
        scopeHolder.get(parent).add(iimport);
    }
}
