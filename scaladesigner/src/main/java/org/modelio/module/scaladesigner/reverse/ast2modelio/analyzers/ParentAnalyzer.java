package org.modelio.module.scaladesigner.reverse.ast2modelio.analyzers;

import edu.kulikov.ast_parser.elements.Entity;
import edu.kulikov.ast_parser.elements.ModuleDef;
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
import java.util.Map;

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
        return !noForAll.contains(parentIdent) && !(isCase(element) && noForCases.contains(parentIdent));
    }

    /*           |________parent_________|
           | 		 | trait| class | object |
           |trait  | gen  | gen   | x      |
      this |class  | real | gen   | x      |
           |object | real | gen   | gen    | */
    public void createParentConnections(Entity thisAst, Map<Entity.BaseTypeWrapper, List<GeneralClass>> parents, List<Entity.BaseTypeWrapper> astParents) {
        GeneralClass thisModel = reposManager.getByAst(thisAst, GeneralClass.class);
        int i = 0;
        for (Map.Entry<Entity.BaseTypeWrapper, List<GeneralClass>> entry : parents.entrySet()) {
            if (!needToShow(thisModel, entry.getKey().getBaseType()))
                continue;
            List<GeneralClass> parentModels = entry.getValue();
            if (parentModels == null || parentModels.isEmpty()) continue; //TODO: think about creating unknown classes
            GeneralClass parentModel = chooseCorrectParent(thisModel, parentModels);
            if (i == 0) {
                if (isTrait(parentModel) && !isTrait(thisModel))
                    createExtendsRealization(parentModel, thisModel);
                else createExtendsGeneralization(parentModel, thisModel);
            } else {
                if (isTrait(parentModel) && !isTrait(thisModel))
                    createMixinRealization(parentModel, thisModel, i);
                else createMixinGeneralization(parentModel, thisModel, i);
            }
            i++;
        }
    }

    private GeneralClass chooseCorrectParent(GeneralClass thisAst, List<GeneralClass> parentModels) {
        if (parentModels.size() == 1) return parentModels.get(0);
        for (GeneralClass parentModel : parentModels) {
            if (thisAst == parentModel) continue;
            return parentModel;
        }
        throw new UnsupportedOperationException("Can't choose correct parent for " + thisAst + ", candidates are: " + parentModels);
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
