package org.modelio.module.scaladesigner.reverse.ast2modelio.factory;

import edu.kulikov.ast_parser.elements.AstElement;
import edu.kulikov.ast_parser.elements.Constants;
import edu.kulikov.ast_parser.elements.Entity;
import edu.kulikov.ast_parser.elements.Entity.BaseTypeWrapper;
import edu.kulikov.ast_parser.elements.Modifiers;
import edu.kulikov.ast_parser.elements.util.AstTraverser;
import org.modelio.api.model.IUMLTypes;
import org.modelio.api.model.IUmlModel;
import org.modelio.metamodel.factory.ExtensionNotFoundException;
import org.modelio.metamodel.uml.infrastructure.Constraint;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.statik.*;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContext;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IElementFactory;
import org.modelio.module.scaladesigner.reverse.ast2modelio.repos.ReposManager;
import org.modelio.module.scaladesigner.reverse.ast2modelio.util.ModelUtils;
import org.modelio.module.scaladesigner.util.Constants.Stereotype;
import org.modelio.module.scaladesigner.util.Constants.Tag;

import java.util.*;
import java.util.stream.Collectors;

import static org.modelio.module.scaladesigner.api.IScalaDesignerPeerModule.MODULE_NAME;

abstract class AbstractElementFactory<From extends AstElement, To extends ModelElement>
        implements IElementFactory<From, To> {

    protected ReposManager rm;

    static <T extends AstElement> AstElement parent(AstElement element,  java.lang.Class<T> ...filter) {
        return  AstTraverser.getParentByType(element, filter);
    }

    @Override
    public void setReposManager(ReposManager reposManager) {

        this.rm = reposManager;
    }

    void setVisibility(ModelElement owner, Modifiers modifiers, IUmlModel model) {
        //TODO: read about modifies
        //http://alvinalexander.com/scala/how-to-control-scala-method-scope-object-private-package
        List<String> values = modifiers.getValues();
        VisibilityMode modifier = null;
        if (values.contains(Constants.PRIVATE))
            modifier = VisibilityMode.PRIVATE; //as in Java, yes
        else if (values.contains(Constants.PROTECTED))
            //if without qualifiers then classes from the same package can't access the member
            modifier = VisibilityMode.PROTECTED;
        if (modifiers.getQualifier() != null) {
            Constraint qualifier = model.createConstraint();
            try {
                qualifier.addStereotype(MODULE_NAME, Stereotype.VISIBILITY);
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
            if (owner instanceof Feature)
                ((Feature) owner).setVisibility(modifier);
            else if (owner instanceof NameSpace)
                ((NameSpace) owner).setVisibility(modifier);
        }
    }

    void putModifierTags(ModelElement element, Modifiers modifiers, IUmlModel model) {
        for (String tag : modifiers.getValues()) {
            try {
                switch (tag) {
                    case Constants.FINAL:
                        ModelUtils.setTaggedValue(model, element, MODULE_NAME,
                                Tag.FINAL, true);
                        if (element instanceof NameSpace)
                            ((NameSpace) element).setIsLeaf(true);
                        break;
                    case Constants.SEALED:
                        ModelUtils.setTaggedValue(model, element, MODULE_NAME,
                                Tag.SEALED, true);
                        break;
                    case Constants.IMPLICIT:
                        ModelUtils.setTaggedValue(model, element, MODULE_NAME,
                                Tag.IMPLICIT, true);
                        break;
                    case Constants.LAZY:
                        ModelUtils.setTaggedValue(model, element, MODULE_NAME,
                                Tag.LAZY, true);
                        break;
                    case Constants.OVERRIDE:
                        ModelUtils.setTaggedValue(model, element, MODULE_NAME,
                                Tag.OVERRIDE, true);
                        break;
                    case Constants.MUTABLE:
                        ModelUtils.setTaggedValue(model, element, MODULE_NAME,
                                Tag.MUTABLE, true);
                        break;
                    case Constants.ABSTRACT:
                    case Constants.DEFERRED:
                        if (element instanceof Feature)
                            ((Feature) element).setIsAbstract(true);
                        else if (element instanceof NameSpace)
                            ((NameSpace) element).setIsAbstract(true);
                        break;
                }
            } catch (ExtensionNotFoundException e) {
                ScalaDesignerModule.logService.error(e);
            }
        }
    }

    List<GeneralClass> resolveType(String type, IContext context, IUMLTypes types) {
        List<GeneralClass> undef = Collections.singletonList(types.getUNDEFINED());
        if (type == null) {
            return undef;
        }
        List<GeneralClass> toReturn = Collections.singletonList(resolveUMLPrimitive(type, types));
        if (toReturn.get(0) == null) {
            toReturn = rm.getByAnyIdent(type, context.getCurrentPackage(), context.getImportScope(), GeneralClass.class);
            ScalaDesignerModule.logService.info("ResolveType, byIdent=" + toReturn);
        }
        return (toReturn == null || toReturn.isEmpty()) ? undef : toReturn;
    }

    Map<BaseTypeWrapper, List<GeneralClass>> resolveTypes(List<BaseTypeWrapper> types, IContext context, IUMLTypes umlTypes) {
        HashMap<BaseTypeWrapper, List<GeneralClass>> map = new HashMap<>();
        for (BaseTypeWrapper type : types) {
            map.put(type, resolveType(type.getBaseType(), context, umlTypes));
        }
        return map;
    }

    private DataType resolveUMLPrimitive(String typeIdent, IUMLTypes types) {
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
