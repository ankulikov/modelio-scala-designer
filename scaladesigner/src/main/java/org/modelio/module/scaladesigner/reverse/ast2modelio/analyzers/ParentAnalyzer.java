package org.modelio.module.scaladesigner.reverse.ast2modelio.analyzers;

import edu.kulikov.ast_parser.elements.ClassDef;
import org.modelio.api.model.IUmlModel;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.statik.ElementRealization;
import org.modelio.metamodel.uml.statik.GeneralClass;
import org.modelio.module.scaladesigner.reverse.ast2modelio.repos.ReposManager;
import org.modelio.module.scaladesigner.reverse.ast2modelio.util.ModelUtils;
import org.modelio.module.scaladesigner.util.Constants;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

import static org.modelio.module.scaladesigner.api.IScalaDesignerPeerModule.MODULE_NAME;

public class ParentAnalyzer {
    public static void analyzeParents(ClassDef thisAst, List<GeneralClass> parents, IUmlModel umlModel, ReposManager reposManager) {
        GeneralClass thisModel = reposManager.getByAst(thisAst, GeneralClass.class);

        if (!thisAst.isTrait()) {
            parents.stream().filter(p -> p != null).filter(ParentAnalyzer::isTrait).forEach(parent -> {
                ElementRealization elementRealization = umlModel.createElementRealization();
                parent.getImpactedDependency().add(elementRealization);
                thisModel.getDependsOnDependency().add(elementRealization);
                ModelUtils.setStereotype(umlModel, elementRealization, MODULE_NAME, Constants.Stereotype.EXTENDS, true);
            });
        }

//        if (parentAst instanceof ClassDef) {
//            if (((ClassDef) parentAst).isTrait()) {
//                if (!thisAst.isTrait()) { //non-trait realizes trait
//                    ElementRealization elementRealization = umlModel.createElementRealization();
//                    parentModel.getImpactedDependency().add(elementRealization);
//                    thisModel.getDependsOnDependency().add(elementRealization);
//                } else { //trait extends trait
//
//                }
//            }
//        }
//        if (thisAst.isTrait()) {
//            if (parentAst instanceof ClassDef && ((ClassDef) parentAst).isTrait()) {
//
//            }
//        }
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
