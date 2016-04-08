package org.modelio.module.scaladesigner.reverse.ast2modelio.factory;

import edu.kulikov.ast_parser.elements.ClassDef;
import org.modelio.api.model.IUmlModel;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.statik.GeneralClass;
import org.modelio.metamodel.uml.statik.NameSpace;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContext;
import org.modelio.module.scaladesigner.reverse.ast2modelio.util.ModelUtils;
import org.modelio.module.scaladesigner.util.Constants.Stereotype;

import static org.modelio.module.scaladesigner.api.IScalaDesignerPeerModule.MODULE_NAME;

public class ClassFactory extends AbstractElementFactory<ClassDef, GeneralClass> {

    @Override
    public GeneralClass createElement(ClassDef classDef, IUmlModel model, IContext context, boolean fill) {
        GeneralClass aClass = rm.getByAst(classDef, GeneralClass.class);
        if (aClass == null) {
            ModelElement owner = rm.getByAst(classDef.getParent()).get(0);
            ScalaDesignerModule.logService.info("Create classDef: " + classDef + " owner: " + owner);
            if (classDef.isCase()) {
                aClass = model.createDataType(classDef.getIdentifier(), (NameSpace) owner);
            } else {
                aClass = model.createClass(classDef.getIdentifier(), (NameSpace) owner);
            }
            rm.attachIdentToModelio(aClass, classDef.getFullIdentifier());
        }
        if (fill) {
            setVisibility(aClass, classDef.getModifiers(), model);
            putModifierTags(aClass, classDef.getModifiers(), model);
            if (classDef.isTrait()) {
                ModelUtils.setStereotype(model, aClass, MODULE_NAME, Stereotype.TRAIT, true);
            } else {
                ModelUtils.setStereotype(model, aClass, MODULE_NAME, Stereotype.CLASS, true);
                if (classDef.isCase()) {
                    ModelUtils.setStereotype(model, aClass, MODULE_NAME, Stereotype.CASE, true);
                }
            }
        }
        return aClass;
    }
}
