package org.modelio.module.scaladesigner.reverse.ast2modelio.impl;

import edu.kulikov.ast_parser.elements.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IAstVisitHandler;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContext;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContext.Scope;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContextable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class ContextFillerHandler implements IAstVisitHandler, IContextable {
    private IContext context;

    private Deque<Pair<AstElement, Scope>> currentScopeHolders = new ArrayDeque<>();
    //we need to know scope of import because in Scala imports can be anywhere (*WOOOOO!*)
    private Deque<Pair<AstElement, List<Import>>> importScopeHolders = new ArrayDeque<>();

    @Override
    public void onStartVisit(AstElement element) {
        //import scope
        if (element instanceof Import) {
            ScalaDesignerModule.logService.info("[onStartVisit] Context, add import=" + element);
            saveImport((Import) element);
            context.getImportScope().add((Import) element);
        }
        //TODO: think about object
        //current scope
        if (currentScope() == Scope.METHOD || currentScope() == Scope.VARIABLE) {
            //don't go into body of method or variable
            saveCurrentScope(element, Scope.CONTENT_BLOCK);
        } else if (element instanceof PackageDef) {
            saveCurrentScope(element, Scope.PACKAGE);
        } else if (element instanceof ClassDef && currentScope() != Scope.CONTENT_BLOCK) {
            //Class can be defined in methods => skip it
            saveCurrentScope(element, Scope.CLASS);
        } else if (element instanceof DefDef && currentScope() != Scope.CONTENT_BLOCK) {
            //Method can be defined in method => skip it
            saveCurrentScope(element, Scope.METHOD);
        } else if (element instanceof ValDef && currentScope() != Scope.CONTENT_BLOCK) {
            //Variable can be defined in method or in other variable => skip it
            saveCurrentScope(element, Scope.VARIABLE);
        }
        ScalaDesignerModule.logService.info("[onStartVisit] Set current scope to context="+currentScope());
        context.setCurrentScope(currentScope());

    }

    @Override
    public void onEndVisit(AstElement element) {
        //if element with imports is visited, remove all imports in its scope
        if (!importScopeHolders.isEmpty() && importScopeHolders.peek().getKey() == element) {
            List<Import> toRemove = importScopeHolders.pop().getRight();
            ScalaDesignerModule.logService.info("[onEndVisit] Context, delete import=" + toRemove + ", parent=" + element);
            context.getImportScope().removeAll(toRemove);
        }
        if (!currentScopeHolders.isEmpty() && currentScopeHolders.peek().getKey() == element) {
            currentScopeHolders.pop();
            ScalaDesignerModule.logService.info("[onEndVisit] Set current scope to context="+currentScope());
            context.setCurrentScope(currentScope());
        }
    }

    @Override
    public void setContext(IContext context) {
        this.context = context;
    }

    private void saveImport(Import iimport) {
        AstElement parent = iimport.getParent();
        if (!importScopeHolders.isEmpty() &&
                importScopeHolders.peek().getKey() == iimport.getParent())
            importScopeHolders.peek().getRight().add(iimport);
        else {
            ArrayList<Import> imports = new ArrayList<>();
            imports.add(iimport);
            importScopeHolders.push(new ImmutablePair<>(iimport.getParent(), imports));
        }
    }

    private void saveCurrentScope(AstElement element, Scope scope) {
        ScalaDesignerModule.logService.info("Save current scope="+scope+", element="+element);
        currentScopeHolders.push(new ImmutablePair<>(element, scope));
    }

    private Scope currentScope() {
        if (currentScopeHolders.isEmpty()) return Scope.UNKNOWN;
        return currentScopeHolders.peek().getRight();
    }
}
