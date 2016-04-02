package org.modelio.module.scaladesigner.reverse.ast2modelio.repos;

import edu.kulikov.ast_parser.elements.AstElement;
import org.apache.commons.lang3.tuple.MutablePair;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.module.scaladesigner.reverse.ast2modelio.util.ModelUtils;

import java.util.HashMap;
import java.util.Map;

public class Ast2ModelioRepo implements Repository{
    private static Ast2ModelioRepo instance;
    private Map<AstElement, MutablePair<Status, ModelElement>> cache;

    // private BiMap<AstElement, ModelElement> cache;

    private Ast2ModelioRepo() {
        cache = new HashMap<>();
    }

    public static Ast2ModelioRepo getInstance() {
        if (instance == null)
            instance = new Ast2ModelioRepo();
        return instance;
    }

    public void save(AstElement from, ModelElement to) {
        save(from, to, Status.ONLY_NAME);
    }

    public void save(AstElement from, Status status) {
        save(from, cache.get(from).getRight(), status);
    }

    public void save(AstElement from, ModelElement to, Status status) {
        if (from == null | to == null)
            throw new IllegalArgumentException("Source or target elements can't be null");
        if (cache.containsKey(from)) {
            MutablePair<Status, ModelElement> pair = cache.get(from);
            if (!status.equals(pair.getLeft()))
                pair.setLeft(status);
            if (!to.equals(pair.getRight()))
                pair.setRight(to);
        } else {
            cache.put(from, new MutablePair<>(status, to));
        }
    }

    public ModelElement get(AstElement key) {
        if (key == null) return null;
        ModelElement right = cache.get(key).getRight();
//        if (right != null && right.isDeleted()) {
//            cache.remove(key); //if someone
//        }
        return cache.get(key).getRight();
    }

    public void clear() {
        //TODO: check if delete causes exception
        cache.forEach((key,value)-> ModelUtils.deleteElement(value.getRight()));
        cache.clear();
    }

    enum Status {
        ONLY_NAME,
        FULL_SIGNATURE,
        WITH_DEPENDENCIES
    }

    static class MapInfo {
        private String simpleName;
        private String fullName;
        private Status status;

        public MapInfo(String simpleName, String fullName, Status status) {
            this.simpleName = simpleName;
            this.fullName = fullName;
            this.status = status;
        }

        public String getSimpleName() {
            return simpleName;
        }

        private void setSimpleName(String simpleName) {
            this.simpleName = simpleName;
        }

        public String getFullName() {
            return fullName;
        }

        private void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }
    }
}