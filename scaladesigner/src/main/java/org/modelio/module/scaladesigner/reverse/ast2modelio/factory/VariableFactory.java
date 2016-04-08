package org.modelio.module.scaladesigner.reverse.ast2modelio.factory;

import edu.kulikov.ast_parser.elements.ClassDef;
import edu.kulikov.ast_parser.elements.Entity;
import edu.kulikov.ast_parser.elements.ValDef;
import org.modelio.api.model.IUMLTypes;
import org.modelio.api.model.IUmlModel;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.statik.Attribute;
import org.modelio.metamodel.uml.statik.Classifier;
import org.modelio.metamodel.uml.statik.DataType;
import org.modelio.metamodel.uml.statik.GeneralClass;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContext;

public class VariableFactory extends AbstractElementFactory<ValDef, Attribute> {
    @Override
    public Attribute createElement(ValDef valDef, IUmlModel model, IContext context, boolean fill) {

        Attribute attribute = rm.getByAst(valDef, Attribute.class);
        if (attribute == null) {
            //TODO: check that it is field, use context
            ModelElement owner = rm.getByAst((parent(valDef, Entity.class))).get(0);
            attribute = model.createAttribute();

            attribute.setOwner((Classifier) owner);
            attribute.setName(valDef.getIdentifier());

            //TODO: set value of field (initializer)?
            //TODO: process generic types
            attribute.setType(resolveType(valDef.getType(), context, model.getUmlTypes()));
            setVisibility(attribute, valDef.getModifiers(), model);
            putModifierTags(attribute, valDef.getModifiers(), model);
            rm.attachIdentToModelio(attribute, valDef.getFullIdentifier());
        }
        return attribute;
    }



}
