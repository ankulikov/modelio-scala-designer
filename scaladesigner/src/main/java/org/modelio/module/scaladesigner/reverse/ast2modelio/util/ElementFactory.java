package org.modelio.module.scaladesigner.reverse.ast2modelio.util;

import edu.kulikov.ast_parser.elements.*;
import edu.kulikov.ast_parser.elements.util.AstTraverser;
import edu.kulikov.ast_parser.elements.util.NoElement;
import org.modelio.api.model.IUmlModel;
import org.modelio.metamodel.factory.ExtensionNotFoundException;
import org.modelio.metamodel.mda.Project;
import org.modelio.metamodel.uml.infrastructure.Constraint;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.statik.*;
import org.modelio.metamodel.uml.statik.Class;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.module.scaladesigner.api.IScalaDesignerPeerModule;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;
import org.modelio.module.scaladesigner.reverse.ast2modelio.Ast2ModelioRepo;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContext;
import org.modelio.vcore.smkernel.mapi.MObject;

import java.util.List;

public class ElementFactory {

    private final IUmlModel model;
    private Ast2ModelioRepo repo;

    public ElementFactory(IUmlModel model, Ast2ModelioRepo repo) {
        this.model = model;
        this.repo = repo;
    }

    private static <T extends AstElement> T parent(AstElement element, java.lang.Class<T> type) {
        return (T) AstTraverser.getParentByType(element, type);
    }

    //TODO: use repo to get owner!

    private Package getModelRoot() {
        for (MObject mObject : model.getModelRoots()) {
            if (mObject instanceof Project) {
                return ((Project) mObject).getModel();
            }
        }
        throw new IllegalArgumentException("UML model doesn't have root package");
    }

    public Package createPackage(PackageDef packageDef, IContext context) {
        ModelElement owner = packageDef.getParent() == NoElement.instance() ? getModelRoot() : repo.get(packageDef.getParent());
        ScalaDesignerModule.logService.info("Create package: " + packageDef + " owner: " + owner);
        return model.createPackage(packageDef.getName(), (NameSpace) owner);
    }

    public Class createClass(ClassDef classDef, IContext context) {
        ModelElement owner = repo.get(classDef.getParent());
        ScalaDesignerModule.logService.info("Create classDef: " + classDef + " owner: " + owner);
        return model.createClass(classDef.getName(), (NameSpace) owner);
    }

    public Operation createOperation(DefDef defDef, IContext context) {
        ModelElement owner = repo.get(parent(defDef, ClassDef.class));
        ScalaDesignerModule.logService.info("Create operation: " + defDef + " owner: " + owner);
        Operation operation = model.createOperation(
                defDef.isConstructor() ? parent(defDef, ClassDef.class).getName() : defDef.getName(), (Classifier) owner);
        for (ValDef arg : defDef.getArguments().get(0)) {
            Parameter parameter = model.createParameter();
            parameter.setName(arg.getName());
            //TODO: get type of parameter (all classes must be visited before)
            parameter.setComposed(operation);
        }
        //TODO: set return type (all classes must be visited before)

        return operation;
    }

    public Attribute createVariable(ValDef valDef, IContext context) {
        //TODO: check that it is field, use context
        ModelElement owner = repo.get(parent(valDef, ClassDef.class));

        Attribute attribute = model.createAttribute();
        attribute.setOwner((Classifier) owner);
        attribute.setName(valDef.getName());

        //TODO: set value of field (initializer)?
        //attribute.setValue();
        //TODO: set type of field (all classes must be visited before)
        //attribute.setType();
        setVisibilityForMember(attribute, valDef.getModifiers());
        return attribute;
    }

    private void setVisibilityForMember(Feature owner, Modifiers modifiers) {
        //TODO: read about modifies
        //http://alvinalexander.com/scala/how-to-control-scala-method-scope-object-private-package
        List<String> values = modifiers.getValues();
        VisibilityMode modifier = null;
        if (values.contains("PRIVATE"))
            modifier = VisibilityMode.PRIVATE; //as in Java, yes
        else if (values.contains("PROTECTED"))
            //if without qualifiers then classes from the same package can't access the member
            modifier = VisibilityMode.PROTECTED;
        if (modifiers.getQualifier() != null) {
            Constraint qualifier = model.createConstraint();
            try {
                qualifier.addStereotype(IScalaDesignerPeerModule.MODULE_NAME, "ScalaVisibility");
                qualifier.setBody("Qualifier = " + modifiers.getQualifier().getValue());
                owner.getConstraintDefinition().add(qualifier);
                if (modifier == null) {
                    modifier = VisibilityMode.PRIVATE; //private with access from enclosing entities ~ something between private and public visibility => private visibility with qualifier
                }
            } catch (ExtensionNotFoundException e) {
                ScalaDesignerModule.logService.error("stereotype ScalaVisibility not found");
            }
        }
        if (modifier != null) {
            owner.setVisibility(modifier);
        }


    }
}
