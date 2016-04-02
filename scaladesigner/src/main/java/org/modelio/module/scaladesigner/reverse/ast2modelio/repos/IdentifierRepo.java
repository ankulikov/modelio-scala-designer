package org.modelio.module.scaladesigner.reverse.ast2modelio.repos;

import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.module.scaladesigner.reverse.ast2modelio.util.ModelUtils;
import org.modelio.vcore.smkernel.mapi.MObject;

import java.util.*;

public class IdentifierRepo implements Repository {
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

    public Set<ModelElement> getByFullIdentifier(String fullIdentifier) {
        return Collections.unmodifiableSet(fullIdentifiers.get(fullIdentifier));
    }

    public Set<ModelElement> getBySimpleIdentifier(String simpleIdentifier) {
        return Collections.unmodifiableSet(fullIdentifiers.get(simpleIdentifier));
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
        fullIdentifiers.forEach((key, list)->list.forEach(ModelUtils::deleteElement));
        fullIdentifiers.clear();
        simpleIdentifiers.clear();
    }
}