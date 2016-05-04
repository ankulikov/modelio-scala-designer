package org.modelio.module.scaladesigner.reverse.ast2modelio.factory;

import edu.kulikov.ast_parser.elements.ModuleDef;
import org.modelio.api.model.IUmlModel;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.statik.GeneralClass;
import org.modelio.metamodel.uml.statik.NameSpace;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;
import org.modelio.module.scaladesigner.reverse.ast2modelio.analyzers.ParentAnalyzer;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContext;
import org.modelio.module.scaladesigner.reverse.ast2modelio.util.ModelUtils;
import org.modelio.module.scaladesigner.util.Constants.Stereotype;

import static org.modelio.module.scaladesigner.api.IScalaDesignerPeerModule.MODULE_NAME;
import static org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContext.Scope.CONTENT_BLOCK;

public class ObjectFactory extends AbstractElementFactory<ModuleDef, GeneralClass> {
    @Override
    public GeneralClass createElement(ModuleDef moduleDef, IUmlModel model, IContext context, Stage stage) {
        GeneralClass object = rm.getByAst(moduleDef, GeneralClass.class);
        if (context.getCurrentScopeType() == CONTENT_BLOCK) return object;
        if (stage == Stage.REVERSE_SELF_MINIMUM) {
            //there is may be class with the same name => need to check stereotype 'Object'
            if (object == null || !object.isStereotyped(MODULE_NAME, Stereotype.OBJECT)) {
                ModelElement owner = rm.getParentFromRepo(moduleDef);
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
        } else if (stage == Stage.REVERSE_SELF_FULL) {
            setVisibility(object, moduleDef.getModifiers(), model);
            putModifierTags(object, moduleDef.getModifiers(), model);
        } else if (stage == Stage.REVERSE_RELATIONS) {
            ScalaDesignerModule.logService.info("REVERSE_RELATIONS for Object, baseTypes=" + moduleDef.getBaseTypes());
            new ParentAnalyzer(model, rm)
                    .createParentConnections(
                            moduleDef,
                            resolveTypes(moduleDef.getBaseTypes(), context, model.getUmlTypes()),
                            moduleDef.getBaseTypes());
            //TODO: analyze hierarchy
        }
        return object;
    }
}
