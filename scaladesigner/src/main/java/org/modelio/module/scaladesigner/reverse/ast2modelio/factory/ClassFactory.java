package org.modelio.module.scaladesigner.reverse.ast2modelio.factory;

import edu.kulikov.ast_parser.elements.ClassDef;
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

public class ClassFactory extends AbstractElementFactory<ClassDef, GeneralClass> {

    @Override
    public GeneralClass createElement(ClassDef classDef, IUmlModel model, IContext context, Stage stage) {
        GeneralClass aClass = rm.getByAst(classDef, GeneralClass.class);
        if (stage == Stage.REVERSE_SELF_MINIMUM) {
            if (aClass == null) {
                ModelElement owner = rm.getParentFromRepo(classDef);
                ScalaDesignerModule.logService.info("Create classDef: " + classDef + " owner: " + owner);
                if (classDef.isCase()) {
                    aClass = model.createDataType(classDef.getIdentifier(), (NameSpace) owner);
                } else {
                    aClass = model.createClass(classDef.getIdentifier(), (NameSpace) owner);
                }
                rm.attachIdentToModelio(aClass, classDef.getFullIdentifier());
            }
        } else if (stage == Stage.REVERSE_SELF_FULL) {
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
        } else if (stage == Stage.REVERSE_RELATIONS) {
            ScalaDesignerModule.logService.info("REVERSE_RELATIONS, baseTypes=" + classDef.getBase());
            ParentAnalyzer.analyzeParents(
                    classDef,
                    resolveTypes(classDef.getBase(), context, model.getUmlTypes()),
                    model, rm);
            //TODO: analyze hierarchy
        }
        return aClass;
    }
}
