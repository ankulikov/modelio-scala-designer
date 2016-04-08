package org.modelio.module.scaladesigner.reverse.ast2modelio.repos;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IRepository;
import org.modelio.module.scaladesigner.reverse.ast2modelio.util.ModelUtils;
import org.modelio.module.scaladesigner.reverse.ast2modelio.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class IdentifierRepo implements IRepository {
    private static IdentifierRepo instance;
    //simple_ident -> set((full_ident, model_element))
    private Map<String, Set<Pair<String, ModelElement>>> identifiers;


    private IdentifierRepo() {
        identifiers = new HashMap<>();
    }

    public static IdentifierRepo getInstance() {
        if (instance == null)
            instance = new IdentifierRepo();
        return instance;
    }

    public void save(String fullIdentifier, ModelElement element) {
        String simpleIdent = StringUtils.afterLastDot(fullIdentifier);
        ScalaDesignerModule.logService.info("IdentRepo, save: fullIdent=" + fullIdentifier + ", simpleIdent=" + simpleIdent + ", element=" + element);
        if (!identifiers.containsKey(simpleIdent)) {
            identifiers.put(simpleIdent, new HashSet<>());
        }
        identifiers.get(simpleIdent).add(new ImmutablePair<>(fullIdentifier, element));
    }

    public Set<Pair<String, ModelElement>> getBySimpleIdentifier(String simpleIdentifier) {
        return identifiers.getOrDefault(simpleIdentifier, Collections.emptySet());
    }

    public Set<ModelElement> getByIdentifier(String identifier) {
        if (identifier.contains("."))
            return getByFullIdentifier(identifier);
        return getBySimpleIdentifier(identifier).stream().map(Pair::getRight).collect(Collectors.toSet());
    }

    public Set<ModelElement> getByFullIdentifier(String fullIdentifier) {
        //TODO: can it cause NPE?
        return identifiers.getOrDefault(StringUtils.afterLastDot(fullIdentifier), Collections.emptySet())
                .stream().filter(p -> p.getKey().equals(fullIdentifier)).map(Pair::getRight).collect(Collectors.toSet());
    }

    //TODO: get by simple name in scope (known imports)
//    public Set<ModelElement> getBySimpleIdentifier(String simpleIdentifier, Set<String> scope) {
//
//    }


    @Override
    public void clear() {
        identifiers.forEach((key,set) -> set.forEach(p->ModelUtils.deleteElement(p.getRight())));
        identifiers.clear();
    }
}
