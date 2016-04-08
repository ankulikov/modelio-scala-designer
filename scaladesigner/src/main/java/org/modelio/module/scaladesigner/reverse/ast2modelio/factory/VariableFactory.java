package org.modelio.module.scaladesigner.reverse.ast2modelio.factory;

import edu.kulikov.ast_parser.elements.ClassDef;
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
            //FIXME: object may be parent too!
            ModelElement owner = rm.getByAst((parent(valDef, ClassDef.class))).get(0);
            attribute = model.createAttribute();

            attribute.setOwner((Classifier) owner);
            attribute.setName(valDef.getIdentifier());

            //TODO: set value of field (initializer)?
            //attribute.setValue();
            //TODO: set type of field (all classes must be visited before)


            attribute.setType(resolveType(valDef.getType(), context, model.getUmlTypes()));
            //attribute.setType();
            setVisibility(attribute, valDef.getModifiers(), model);
            putModifierTags(attribute, valDef.getModifiers(), model);
            rm.attachIdentToModelio(attribute, valDef.getFullIdentifier());
        }
        return attribute;
    }

    public GeneralClass resolveType(String typeIdent, IContext context, IUMLTypes types) {
        DataType umlPrimitive = resolveUMLPrimitive(typeIdent, types);
        if (umlPrimitive == null) {
            GeneralClass byAnyIdent = rm.getByAnyIdent(typeIdent, context.getImportScope(), GeneralClass.class);
            ScalaDesignerModule.logService.info("ResolveType, byIdent="+byAnyIdent);
            return byAnyIdent;
        }
        return types.getUNDEFINED();
    }


    DataType resolveUMLPrimitive(String typeIdent, IUMLTypes types) {
        if (typeIdent == null)
            return types.getUNDEFINED();
        switch (typeIdent) {
            case "Int":
            case "scala.Int":
                return types.getINTEGER();
            case "Char":
            case "scala.Char":
                return types.getCHAR();
            case "Byte":
            case "scala.Byte":
                return types.getBYTE();
            case "Double":
            case "scala.Double":
                return types.getDOUBLE();
            case "Boolean":
            case "scala.Boolean":
                return types.getBOOLEAN();
            case "Long":
            case "scala.Long":
                return types.getLONG();
            case "Short":
            case "scala.Short":
                return types.getSHORT();
            case "String":
            case "Predef.String":
                return types.getSTRING();
            default:
                return null;
        }
    }
}
