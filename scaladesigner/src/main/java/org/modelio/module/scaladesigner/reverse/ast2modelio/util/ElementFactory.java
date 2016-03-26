package org.modelio.module.scaladesigner.reverse.ast2modelio.util;

import edu.kulikov.ast_parser.elements.ClassDef;
import edu.kulikov.ast_parser.elements.DefDef;
import edu.kulikov.ast_parser.elements.PackageDef;
import edu.kulikov.ast_parser.elements.ValDef;
import org.modelio.api.model.IUmlModel;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.statik.Class;
import org.modelio.metamodel.uml.statik.*;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;
import org.modelio.module.scaladesigner.reverse.ast2modelio.Ast2ModelioRepo;

public class ElementFactory {

    private final IUmlModel factory;
    private Ast2ModelioRepo repo;

    public ElementFactory(IUmlModel factory, Ast2ModelioRepo repo) {
        this.factory = factory;
        this.repo = repo;
    }

    //TODO: use repo to get owner!

    public Package createPackage(PackageDef packageDef, ModelElement owner) {
        ScalaDesignerModule.logService.info("Create package: " + packageDef + " owner: " + owner);
        return factory.createPackage(packageDef.getName(), (NameSpace) owner);
    }

    public Class createClass(ClassDef classDef, ModelElement owner) {
        ScalaDesignerModule.logService.info("Create classDef: " + classDef + " owner: " + owner);
        return factory.createClass(classDef.getName(), (NameSpace) owner);
    }

    public Operation createOperation(DefDef defDef, ModelElement owner) {
        ScalaDesignerModule.logService.info("Create defDef: " + defDef + " owner: " + owner);
        Operation operation = factory.createOperation(defDef.getName(), (Classifier) owner);
        for (ValDef arg : defDef.getArguments().get(0)) {
            Parameter parameter = factory.createParameter();
            parameter.setName(arg.getName());
            //TODO: get type of parameter (all classes must be visited before)
            parameter.setComposed(operation);
        }
        //TODO: set return type (all classes must be visited before)

        return operation;
    }

    public Attribute createField(ValDef valDef, ModelElement owner) {
        Attribute attribute = factory.createAttribute();
        attribute.setOwner((Classifier) owner);
        attribute.setName(valDef.getName());
        //TODO: set value of field (initializer)?
        //attribute.setValue();
        //TODO: set type of field (all classes must be visited before)
        //attribute.setType();
        return attribute;
    }
}
