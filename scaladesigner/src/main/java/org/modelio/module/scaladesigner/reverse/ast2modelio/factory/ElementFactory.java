package org.modelio.module.scaladesigner.reverse.ast2modelio.factory;

import edu.kulikov.ast_parser.elements.*;
import org.modelio.api.model.IUmlModel;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContext;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IElementFactory;
import org.modelio.module.scaladesigner.reverse.ast2modelio.repos.ReposManager;

import java.util.HashMap;
import java.util.Map;

public class ElementFactory extends AbstractElementFactory {

    private static ReposManager reposManager;
    private static Map<Class<? extends AstElement>, IElementFactory> handlers;

    static {
        registerReposManager();
        registerFactories();
    }

    private static void registerReposManager() {
        reposManager = ReposManager.getInstance();
    }

    private static void registerFactories() {
        if (handlers == null) {
            handlers = new HashMap<>();
            handlers.put(PackageDef.class, initFactory(new PackageFactory()));
            handlers.put(ClassDef.class, initFactory(new ClassFactory()));
            handlers.put(DefDef.class, initFactory(new OperationFactory()));
            handlers.put(ValDef.class, initFactory(new VariableFactory()));
        }
    }

    private static IElementFactory initFactory(IElementFactory factory) {
        factory.setReposManager(reposManager);
        return factory;
    }

    @Override
    public ModelElement createElement(AstElement from, IUmlModel model, IContext context, boolean fill) {
        IElementFactory factory = handlers.get(from.getClass());
        if (factory == null) return null;
        //noinspection unchecked
        return factory.createElement(from, model, context, fill);
    }


}
