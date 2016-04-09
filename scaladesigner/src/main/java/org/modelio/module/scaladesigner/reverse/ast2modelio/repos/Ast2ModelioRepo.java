package org.modelio.module.scaladesigner.reverse.ast2modelio.repos;

import edu.kulikov.ast_parser.elements.AstElement;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IRepository;
import org.modelio.module.scaladesigner.reverse.ast2modelio.util.ModelUtils;

import java.util.HashMap;
import java.util.Map;

public class Ast2ModelioRepo implements IRepository {
    private static Ast2ModelioRepo instance;
    private Map<AstElement, ModelElement> cache;

    private Ast2ModelioRepo() {
        cache = new HashMap<>();
    }

    public static Ast2ModelioRepo getInstance() {
        if (instance == null)
            instance = new Ast2ModelioRepo();
        return instance;
    }


    public void save(AstElement from, ModelElement to) {
        if (from == null | to == null)
            throw new IllegalArgumentException("Source or target elements can't be null");
        cache.put(from, to);

    }

    public ModelElement get(AstElement key) {
        if (key == null || !cache.containsKey(key)) return null;
        return cache.get(key);
    }

    public void clear() {
        cache.forEach((key, value) -> ModelUtils.deleteElement(value));
        cache.clear();
    }
}
