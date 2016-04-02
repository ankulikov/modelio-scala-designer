package org.modelio.module.scaladesigner.reverse.ast2modelio.factory;

import edu.kulikov.ast_parser.elements.ClassDef;
import edu.kulikov.ast_parser.elements.ValDef;
import org.modelio.api.model.IUmlModel;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.statik.Attribute;
import org.modelio.metamodel.uml.statik.Classifier;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContext;

public class VariableFactory extends AbstractElementFactory<ValDef, Attribute> {
    @Override
    public Attribute createElement(ValDef valDef, IUmlModel model, IContext context, boolean fill) {
        //TODO: check that it is field, use context
        ModelElement owner = transformRepo.get(parent(valDef, ClassDef.class));
        Attribute attribute = model.createAttribute();
        attribute.setOwner((Classifier) owner);
        attribute.setName(valDef.getIdentifier());

        //TODO: set value of field (initializer)?
        //attribute.setValue();
        //TODO: set type of field (all classes must be visited before)
        //attribute.setType();
        setVisibility(attribute, valDef.getModifiers(), model);
        putModifierTags(attribute, valDef.getModifiers(), model);

        return attribute;
    }
}
