package org.modelio.module.scaladesigner.reverse.ast2modelio.factory;

import edu.kulikov.ast_parser.elements.*;
import org.modelio.api.model.IUmlModel;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.statik.Classifier;
import org.modelio.metamodel.uml.statik.Operation;
import org.modelio.metamodel.uml.statik.Parameter;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContext;

import static org.modelio.module.scaladesigner.reverse.ast2modelio.util.ModelUtils.setStereotype;

public class OperationFactory extends AbstractElementFactory<DefDef, Operation> {
    @Override
    public Operation createElement(DefDef defDef, IUmlModel model, IContext context, Stage stage) {
        Operation operation = rm.getByAst(defDef, Operation.class);
        if (stage == Stage.REVERSE_SELF_MINIMUM || stage == Stage.REVERSE_SELF_FULL) {
            if (operation == null) {
                Entity parent = (Entity) parent(defDef, Entity.class);
                ModelElement owner = rm.getParentFromRepo(defDef);
                ScalaDesignerModule.logService.info("Create operation: " + defDef + " owner: " + owner);
                operation = model.createOperation(
                        defDef.isConstructor() ? parent.getIdentifier() : defDef.getIdentifier(), (Classifier) owner);
                if (defDef.isConstructor()) {
                    setStereotype(model, operation, "ModelerModule", "create", true);
                } else {
                    //constructor doesn't have return parameters
                    //TODO: process generic types
                    model.createReturnParameter("return", resolveType(defDef.getReturnType(), context, model.getUmlTypes()), operation);
                }
                for (ValDef arg : defDef.getArguments().get(0)) {
                    Parameter parameter = model.createParameter();
                    parameter.setName(arg.getIdentifier());
                    //TODO: process generic types
                    parameter.setType(resolveType(arg.getType(), context, model.getUmlTypes()));
                    parameter.setComposed(operation);
                }

                setVisibility(operation, defDef.getModifiers(), model);
                putModifierTags(operation, defDef.getModifiers(), model);
                rm.attachIdentToModelio(operation, defDef.getFullIdentifier());
            }
        } else if (stage == Stage.REVERSE_RELATIONS) {
            //TODO: analyze body content
        }
        return operation;
    }
}
