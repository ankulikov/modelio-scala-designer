package org.modelio.module.scaladesigner.reverse.ast2modelio.analyzers;

import edu.kulikov.ast_parser.elements.ClassDef;
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

import java.util.List;

import static org.modelio.module.scaladesigner.api.IScalaDesignerPeerModule.MODULE_NAME;

public class ParentAnalyzer {

    /*           |________parent_________|
         | 		 | trait| class | object |
         |trait  | gen  | gen   | x      |
    this |class  | real | gen   | x      |
         |object | real | gen   | gen    | */
    public static void analyzeParents(Entity thisAst, List<GeneralClass> parents, IUmlModel umlModel, ReposManager reposManager) {
        GeneralClass thisModel = reposManager.getByAst(thisAst, GeneralClass.class);

        for (int i = 0; i < parents.size(); i++) {
            GeneralClass parentModel = parents.get(i);
            if (parentModel == null) continue; //TODO: think about creating unknown classes
            if (i == 0) {
                if (isTrait(parentModel) && !isTrait(thisModel))
                    createExtendsRealization(parentModel, thisModel, umlModel);
                else createExtendsGeneralization(parentModel, thisModel, umlModel);
            } else {
                if (isTrait(parentModel) && !isTrait(thisModel))
                    createMixinRealization(parentModel, thisModel, umlModel, i);
                else createMixinGeneralization(parentModel, thisModel, umlModel, i);
            }
        }

    }

    private static void createExtendsRealization(GeneralClass parent, GeneralClass child, IUmlModel umlModel) {
        ElementRealization elementRealization = umlModel.createElementRealization();
        parent.getImpactedDependency().add(elementRealization);
        child.getDependsOnDependency().add(elementRealization);
        ModelUtils.setStereotype(umlModel, elementRealization, MODULE_NAME, Constants.Stereotype.EXTENDS, true);
    }

    private static void createExtendsGeneralization(GeneralClass parent, GeneralClass child, IUmlModel umlModel) {
        Generalization generalization = umlModel.createGeneralization();
        generalization.setSuperType(parent);
        generalization.setSubType(child);
        ModelUtils.setStereotype(umlModel, generalization, MODULE_NAME, Constants.Stereotype.EXTENDS, true);
    }

    private static void createMixinRealization(GeneralClass parent, GeneralClass child, IUmlModel umlModel, int mixinOrderNumber) {
        ElementRealization elementRealization = umlModel.createElementRealization();
        parent.getImpactedDependency().add(elementRealization);
        child.getDependsOnDependency().add(elementRealization);
        ModelUtils.setStereotype(umlModel, elementRealization, MODULE_NAME, Constants.Stereotype.MIXIN, true);
        elementRealization.setName("[" + mixinOrderNumber + "]");
    }

    private static void createMixinGeneralization(GeneralClass parent, GeneralClass child, IUmlModel umlModel, int mixinOrderNumber) {
        Generalization generalization = umlModel.createGeneralization();
        generalization.setSuperType(parent);
        generalization.setSubType(child);
        ModelUtils.setStereotype(umlModel, generalization, MODULE_NAME, Constants.Stereotype.MIXIN, true);
        generalization.setName("[" + mixinOrderNumber + "]");
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


}
