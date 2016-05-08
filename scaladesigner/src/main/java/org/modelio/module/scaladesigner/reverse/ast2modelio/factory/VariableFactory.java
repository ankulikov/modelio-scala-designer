package org.modelio.module.scaladesigner.reverse.ast2modelio.factory;

import edu.kulikov.ast_parser.elements.ValDef;
import org.modelio.api.model.IUmlModel;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.statik.Attribute;
import org.modelio.metamodel.uml.statik.Classifier;
import org.modelio.module.scaladesigner.reverse.ast2modelio.analyzers.TypeResolver;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContext;

import static org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContext.Scope.CONTENT_BLOCK;

public class VariableFactory extends AbstractElementFactory<ValDef, Attribute> {
    @Override
    public Attribute createElement(ValDef valDef, IUmlModel model, IContext context, Stage stage) {
        Attribute attribute = rm.getByAst(valDef, Attribute.class);
        if (context.getCurrentScopeType() == CONTENT_BLOCK) return attribute;
        if (stage == Stage.REVERSE_SELF_MINIMUM || stage == Stage.REVERSE_SELF_FULL) {
            if (attribute == null) {
                //TODO: check that it is field, use context
                ModelElement owner = rm.getParentFromRepo(valDef);
                attribute = model.createAttribute();

                attribute.setOwner((Classifier) owner);
                attribute.setName(valDef.getIdentifier());

                //TODO: set value of field (initializer)?
                //TODO: process generic types
                TypeResolver typeResolver = new TypeResolver(rm);
                attribute.setType(typeResolver.resolveType(valDef.getType(), context, model.getUmlTypes()).get(0));
                setVisibility(attribute, valDef.getModifiers(), model);
                putModifierTags(attribute, valDef.getModifiers(), model);
                rm.attachIdentToModelio(attribute, valDef.getFullIdentifier());
            }
        } else if (stage == Stage.REVERSE_RELATIONS) {
            //TODO: analyze content block
        }
        return attribute;
    }


}
