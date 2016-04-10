package org.modelio.module.scaladesigner.reverse.ast2modelio;

import edu.kulikov.ast_parser.elements.AstElement;
import edu.kulikov.ast_parser.elements.NoElement;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IAstVisitHandler;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContext;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContextable;
import org.modelio.module.scaladesigner.reverse.ast2modelio.handlers.Context;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

//precondition: in transaction
public class AstVisitor {
    private List<IAstVisitHandler> handlers;
    private AstElement astModel;
    private Deque<Pair<AstElement, Integer>> stack; //element; children_position
    private IContext context;

    public AstVisitor(AstElement astModel) {
        this.astModel = astModel;
        handlers = new ArrayList<>();
        stack = new ArrayDeque<>();
        context = new Context();
    }

    public void addHandler(IAstVisitHandler handler) {
        handlers.add(handler);
        if (handler instanceof IContextable)
            ((IContextable) handler).setContext(context);
    }

    public void visit() {
        //add root element (package) to stack
        stack.add(new MutablePair<>(astModel, 0));
        //manually handle it; root package doesn't have parent
        onStart(astModel);
        while (!stack.isEmpty()) {
            //get reference to parent element
            Pair<AstElement, Integer> currentPair = stack.peek();
            Integer index = currentPair.getValue();
            AstElement child = currentPair.getKey().get(index);
            //if not all children are processed...
            if (child != NoElement.instance()) {
                //handle one child
                onStart(child);
                currentPair.setValue(index + 1);
                //and go deeper
                stack.push(new MutablePair<>(child, 0));
            } else {
                //all children are processed, remove reference to parent
                //and say goodbye...
                onEnd(stack.pop().getKey());
            }
        }
    }

    private void onStart(AstElement element) {
        handlers.stream().forEach(h -> h.onStartVisit(element));
    }

    private void onEnd(AstElement element) {
        handlers.forEach(handler -> handler.onEndVisit(element));
    }


}
