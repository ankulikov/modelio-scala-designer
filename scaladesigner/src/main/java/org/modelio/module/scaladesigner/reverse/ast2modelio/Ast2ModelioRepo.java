package org.modelio.module.scaladesigner.reverse.ast2modelio;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import edu.kulikov.ast_parser.elements.AstElement;
import org.modelio.metamodel.uml.infrastructure.ModelElement;

public class Ast2ModelioRepo {
    private static Ast2ModelioRepo instance;
    private BiMap<AstElement, ModelElement> cache;

    private Ast2ModelioRepo() {
        cache = HashBiMap.create();
    }

    public static Ast2ModelioRepo getInstance() {
        if (instance == null)
            instance = new Ast2ModelioRepo();
        return instance;
    }

    public void save(AstElement key, ModelElement value) {
        cache.put(key, value);
    }

    public ModelElement get(AstElement key) {
        if (key == null) return null;
        return cache.get(key);
    }

    public AstElement get(ModelElement key) {
        if (key == null) return null;
        return cache.inverse().get(key);
    }

    public void clear() {
        cache.clear();
    }
}
