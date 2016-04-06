package org.modelio.module.scaladesigner.reverse.ast2modelio.repos;

import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IRepository;
import org.modelio.module.scaladesigner.reverse.ast2modelio.util.ModelUtils;

import java.util.*;

public class IdentifierRepo implements IRepository {
    private static IdentifierRepo instance;
    //simple identifier - name without dots
    private Map<String, Set<ModelElement>> simpleIdentifiers;
    //full identifier - name with dots. There may be several elements with the same
    //identifiers. For example, method 'A' and inner class 'A' in one class.
    private Map<String, Set<ModelElement>> fullIdentifiers;

    private IdentifierRepo() {
        simpleIdentifiers = new HashMap<>();
        fullIdentifiers = new HashMap<>();
    }

    public static IdentifierRepo getInstance() {
        if (instance == null)
            instance = new IdentifierRepo();
        return instance;
    }

    public void save(String fullIdentifier, ModelElement element) {
        checkAndSave(fullIdentifiers, fullIdentifier, element);
        checkAndSave(simpleIdentifiers,
                fullIdentifier.substring(fullIdentifier.contains(".") ? fullIdentifier.lastIndexOf('.') : 0),
                element);
    }

    public Set<ModelElement> getByIdentifier(String identifier) {
        if (identifier.contains("."))
            return getByFullIdentifier(identifier);
        return getBySimpleIdentifier(identifier);
    }

    private Set<ModelElement> getByFullIdentifier(String fullIdentifier) {
        return fullIdentifiers.containsKey(fullIdentifier)?Collections.unmodifiableSet(fullIdentifiers.get(fullIdentifier)):Collections.emptySet();
    }

    private Set<ModelElement> getBySimpleIdentifier(String simpleIdentifier) {
        return simpleIdentifiers.containsKey(simpleIdentifier)?Collections.unmodifiableSet(simpleIdentifiers.get(simpleIdentifier)):Collections.emptySet();
    }

    //TODO: get by simple name in scope (known imports)
//    public Set<ModelElement> getBySimpleIdentifier(String simpleIdentifier, Set<String> scope) {
//
//    }


    private void checkAndSave(Map<String, Set<ModelElement>> map,
                              String key,
                              ModelElement value) {
        if (map.containsKey(key)) {
            map.get(key).add(value);
        } else {
            Set<ModelElement> set = new LinkedHashSet<>();
            set.add(value);
            map.put(key, set);
        }
    }


    @Override
    public void clear() {
        fullIdentifiers.forEach((key, list) -> list.forEach(ModelUtils::deleteElement));
        fullIdentifiers.clear();
        simpleIdentifiers.clear();
    }
}
