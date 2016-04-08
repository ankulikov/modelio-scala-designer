package org.modelio.module.scaladesigner.reverse.ast2modelio.factory;

import edu.kulikov.ast_parser.elements.ModuleDef;
import org.modelio.api.model.IUmlModel;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.statik.Class;
import org.modelio.metamodel.uml.statik.GeneralClass;
import org.modelio.metamodel.uml.statik.NameSpace;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContext;
import org.modelio.module.scaladesigner.reverse.ast2modelio.util.ModelUtils;
import org.modelio.module.scaladesigner.util.Constants.Stereotype;

import static org.modelio.module.scaladesigner.api.IScalaDesignerPeerModule.MODULE_NAME;

public class ObjectFactory extends AbstractElementFactory<ModuleDef, GeneralClass> {
    @Override
    public GeneralClass createElement(ModuleDef moduleDef, IUmlModel model, IContext context, boolean fill) {
        GeneralClass object = rm.getByAst(moduleDef, GeneralClass.class);
        //there is may be class with the same name => need to check stereotype 'Object'
        if (object == null || !object.isStereotyped(MODULE_NAME, Stereotype.OBJECT)) {
            ModelElement owner = rm.getByAst(moduleDef.getParent()).get(0);
            ScalaDesignerModule.logService.info("Create object: " + moduleDef + " owner: " + owner);
            if (moduleDef.isCase()) {
                object = model.createDataType(moduleDef.getIdentifier(), (NameSpace) owner);
            } else {
                object = model.createClass(moduleDef.getIdentifier(), (NameSpace) owner);
            }
            //add stereotype here to distinct object from class with the same name
            ModelUtils.setStereotype(model, object, MODULE_NAME, Stereotype.OBJECT, true);
            rm.attachIdentToModelio(object, moduleDef.getFullIdentifier());
        }
        if (fill) {
            setVisibility(object, moduleDef.getModifiers(), model);
            putModifierTags(object, moduleDef.getModifiers(), model);
        }
        return object;

    }
}
