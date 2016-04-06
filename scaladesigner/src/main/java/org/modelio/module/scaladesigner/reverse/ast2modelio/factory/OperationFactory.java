package org.modelio.module.scaladesigner.reverse.ast2modelio.factory;

import edu.kulikov.ast_parser.elements.ClassDef;
import edu.kulikov.ast_parser.elements.DefDef;
import edu.kulikov.ast_parser.elements.ValDef;
import org.modelio.api.model.IUmlModel;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.statik.Classifier;
import org.modelio.metamodel.uml.statik.Operation;
import org.modelio.metamodel.uml.statik.Parameter;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContext;

public class OperationFactory extends AbstractElementFactory<DefDef, Operation> {
    @Override
    public Operation createElement(DefDef defDef, IUmlModel model, IContext context, boolean fill) {
        Operation operation = rm.getByAst(defDef, Operation.class);
        if (operation == null) {
            ModelElement owner = rm.getByAst(parent(defDef, ClassDef.class)).get(0);
            ScalaDesignerModule.logService.info("Create operation: " + defDef + " owner: " + owner);
            operation = model.createOperation(
                    defDef.isConstructor() ? parent(defDef, ClassDef.class).getIdentifier() : defDef.getIdentifier(), (Classifier) owner);
            for (ValDef arg : defDef.getArguments().get(0)) {
                Parameter parameter = model.createParameter();
                parameter.setName(arg.getIdentifier());
                //TODO: get type of parameter (all classes must be visited before)
                parameter.setComposed(operation);
            }
            //TODO: set return type (all classes must be visited before)
            setVisibility(operation, defDef.getModifiers(), model);
            putModifierTags(operation, defDef.getModifiers(), model);
            rm.attachIdentToModelio(operation, defDef.getFullIdentifier());
        }
        return operation;
    }
}
