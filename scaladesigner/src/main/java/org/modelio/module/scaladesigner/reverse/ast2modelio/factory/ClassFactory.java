package org.modelio.module.scaladesigner.reverse.ast2modelio.factory;

import edu.kulikov.ast_parser.elements.ClassDef;
import edu.kulikov.ast_parser.elements.TypeBoundsTree;
import edu.kulikov.ast_parser.elements.TypeDef;
import org.modelio.api.model.IUmlModel;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.statik.GeneralClass;
import org.modelio.metamodel.uml.statik.NameSpace;
import org.modelio.metamodel.uml.statik.TemplateParameter;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;
import org.modelio.module.scaladesigner.reverse.ast2modelio.analyzers.ParentAnalyzer;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContext;
import org.modelio.module.scaladesigner.reverse.ast2modelio.util.ModelUtils;
import org.modelio.module.scaladesigner.util.Constants.Stereotype;

import static org.modelio.module.scaladesigner.api.IScalaDesignerPeerModule.MODULE_NAME;
import static org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContext.Scope.CONTENT_BLOCK;
import static org.modelio.module.scaladesigner.reverse.ast2modelio.util.ModelUtils.setTaggedValue;
import static org.modelio.module.scaladesigner.util.Constants.Tag.*;

public class ClassFactory extends AbstractElementFactory<ClassDef, GeneralClass> {

    @Override
    public GeneralClass createElement(ClassDef classDef, IUmlModel model, IContext context, Stage stage) {
        GeneralClass aClass = rm.getByAst(classDef, GeneralClass.class);
        if (context.getCurrentScopeType() == CONTENT_BLOCK) return aClass;
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
            addTypeParameters(aClass, classDef, model);
        } else if (stage == Stage.REVERSE_RELATIONS) {
            ScalaDesignerModule.logService.info("REVERSE_RELATIONS for Class, baseTypes=" + classDef.getBaseTypes());
            new ParentAnalyzer(model, rm)
                    .createParentConnections(classDef, context);


            //model.createTemplateParameterSubstitution().
            // model.createTemplateParameter().setTy
            //TODO: analyze hierarchy
        }
        return aClass;
    }

    private void addTypeParameters(GeneralClass generalClass, ClassDef classDef, IUmlModel model) {
        if (!classDef.hasTypeParams()) return;
        for (TypeDef typeDef : classDef.getTypeParams()) {
            TemplateParameter typeParam = model.createTemplateParameter();
            typeParam.setName(typeDef.getIdentifier());
            if (typeDef.isCovariant()) {
                setTaggedValue(model, typeParam, MODULE_NAME, COVARIANT, true);
            }
            if (typeDef.isContrvariant()) {
                setTaggedValue(model, typeParam, MODULE_NAME, CONTRAVARIANT, true);
            }
            TypeBoundsTree boundsTree = typeDef.getConstraints();
            if (boundsTree != null) {
                if (boundsTree.hasLowerConstraint()) {
                    setTaggedValue(model, typeParam, MODULE_NAME, LOWER_BOUND, boundsTree.getLowerConstraint(), true);
                }
                if (boundsTree.hasUpperConstraint()) {
                    setTaggedValue(model, typeParam, MODULE_NAME, UPPER_BOUND, boundsTree.getUpperConstraint(), true);
                }
            }
            typeParam.setParameterized(generalClass);
            //generalClass.getTypingParameter().add(typeParam);
            rm.attachIdentToModelio(typeParam, typeDef.getFullIdentifier());
        }
    }
}
