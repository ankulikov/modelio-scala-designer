package org.modelio.module.scaladesigner.reverse.ast2modelio.analyzers;

import edu.kulikov.ast_parser.elements.Entity;
import org.modelio.api.model.IUmlModel;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.statik.ElementRealization;
import org.modelio.metamodel.uml.statik.GeneralClass;
import org.modelio.metamodel.uml.statik.Generalization;
import org.modelio.module.scaladesigner.reverse.ast2modelio.repos.ReposManager;
import org.modelio.module.scaladesigner.reverse.ast2modelio.util.ModelUtils;
import org.modelio.module.scaladesigner.util.Constants;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Arrays;
import java.util.List;

import static org.modelio.module.scaladesigner.api.IScalaDesignerPeerModule.MODULE_NAME;

public class ParentAnalyzer {
    private IUmlModel umlModel;
    private ReposManager reposManager;

    public ParentAnalyzer(IUmlModel umlModel, ReposManager reposManager) {
        this.umlModel = umlModel;
        this.reposManager = reposManager;
    }

    public static void checkForInnerClass() {
        throw new NotImplementedException();
    }

    private static boolean isTrait(ModelElement element) {
        return element.isStereotyped(MODULE_NAME, Constants.Stereotype.TRAIT);
    }

    private static boolean isObject(ModelElement element) {
        return element.isStereotyped(MODULE_NAME, Constants.Stereotype.OBJECT);
    }

    private static boolean isCase(ModelElement element) {
        return element.isStereotyped(MODULE_NAME, Constants.Stereotype.CASE);
    }

    private static boolean needToShow(ModelElement element, String parentIdent) {
        List<String> noForAll = Arrays.asList("Any", "AnyRef", "scala.Any", "scala.AnyRef");
        List<String> noForCases = Arrays.asList("Serializable", "scala.Serializable", "Product", "scala.Product");
        if (noForAll.contains(parentIdent))
            return false;
        if (isCase(element) && noForCases.contains(parentIdent))
            return false;
        return true;
    }

    /*           |________parent_________|
           | 		 | trait| class | object |
           |trait  | gen  | gen   | x      |
      this |class  | real | gen   | x      |
           |object | real | gen   | gen    | */
    public void createParentConnections(Entity thisAst, List<GeneralClass> parents, List<Entity.BaseTypeWrapper> astParents) {
        GeneralClass thisModel = reposManager.getByAst(thisAst, GeneralClass.class);
        for (int i = 0; i < parents.size(); i++) {
            if (!needToShow(thisModel, astParents.get(i).getBaseType()))
                continue;
            GeneralClass parentModel = parents.get(i);
            if (parentModel == null) continue; //TODO: think about creating unknown classes
            if (i == 0) {
                if (isTrait(parentModel) && !isTrait(thisModel))
                    createExtendsRealization(parentModel, thisModel);
                else createExtendsGeneralization(parentModel, thisModel);
            } else {
                if (isTrait(parentModel) && !isTrait(thisModel))
                    createMixinRealization(parentModel, thisModel, i);
                else createMixinGeneralization(parentModel, thisModel, i);
            }
        }
    }

    private void createExtendsRealization(GeneralClass parent, GeneralClass child) {
        ElementRealization elementRealization = umlModel.createElementRealization();
        parent.getImpactedDependency().add(elementRealization);
        child.getDependsOnDependency().add(elementRealization);
        ModelUtils.setStereotype(umlModel, elementRealization, MODULE_NAME, Constants.Stereotype.EXTENDS, true);
    }

    private void createExtendsGeneralization(GeneralClass parent, GeneralClass child) {
        Generalization generalization = umlModel.createGeneralization();
        generalization.setSuperType(parent);
        generalization.setSubType(child);
        ModelUtils.setStereotype(umlModel, generalization, MODULE_NAME, Constants.Stereotype.EXTENDS, true);
    }

    private void createMixinRealization(GeneralClass parent, GeneralClass child, int mixinOrderNumber) {
        ElementRealization elementRealization = umlModel.createElementRealization();
        parent.getImpactedDependency().add(elementRealization);
        child.getDependsOnDependency().add(elementRealization);
        ModelUtils.setStereotype(umlModel, elementRealization, MODULE_NAME, Constants.Stereotype.MIXIN, true);
        elementRealization.setName("[" + mixinOrderNumber + "]");
    }

    private void createMixinGeneralization(GeneralClass parent, GeneralClass child, int mixinOrderNumber) {
        Generalization generalization = umlModel.createGeneralization();
        generalization.setSuperType(parent);
        generalization.setSubType(child);
        ModelUtils.setStereotype(umlModel, generalization, MODULE_NAME, Constants.Stereotype.MIXIN, true);
        generalization.setName("[" + mixinOrderNumber + "]");
    }


}
