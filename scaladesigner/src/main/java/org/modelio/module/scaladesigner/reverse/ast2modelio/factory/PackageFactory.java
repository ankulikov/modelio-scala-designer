package org.modelio.module.scaladesigner.reverse.ast2modelio.factory;

import edu.kulikov.ast_parser.elements.PackageDef;
import edu.kulikov.ast_parser.elements.util.NoElement;
import org.modelio.api.model.IUmlModel;
import org.modelio.metamodel.mda.Project;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.statik.NameSpace;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContext;
import org.modelio.vcore.smkernel.mapi.MObject;

import static org.modelio.module.scaladesigner.reverse.ast2modelio.repos.Ast2ModelioRepo.Status.NOT_REVERSED;
import static org.modelio.module.scaladesigner.reverse.ast2modelio.util.StringUtils.afterFirstDot;
import static org.modelio.module.scaladesigner.reverse.ast2modelio.util.StringUtils.beforeFirstDot;
import static org.modelio.module.scaladesigner.reverse.ast2modelio.util.StringUtils.prefix;

public class PackageFactory extends AbstractElementFactory<PackageDef, Package> {

    @Override
    public Package createElement(PackageDef from, IUmlModel model, IContext context, boolean fill) {
        Package toReturn = null;
        if (transformRepo.getStatus(from) == NOT_REVERSED) {
            ModelElement owner = from.getParent() == NoElement.instance() ? getModelRoot(model) : transformRepo.get(from.getParent());
            ScalaDesignerModule.logService.info("Create package: " + from + " owner: " + owner);
            toReturn = createPackageRecursive(model, owner, prefix(from.getFullIdentifier(), from.getIdentifier()), from.getIdentifier());
            saveInIdentRepo(toReturn, from.getFullIdentifier());
        } else {
            //may be some additional info for packages?
            toReturn = (Package) transformRepo.get(from);
        }
        return toReturn;
    }

    private Package getModelRoot(IUmlModel model) {
        for (MObject mObject : model.getModelRoots()) {
            if (mObject instanceof Project) {
                return ((Project) mObject).getModel();
            }
        }
        throw new IllegalArgumentException("UML model doesn't have root package");
    }


    private Package createPackageRecursive(IUmlModel model, ModelElement owner, String namePrefix, String simpleName) {
        if (!simpleName.contains(".")) {
            Package modelPackage = model.createPackage(simpleName, (NameSpace) owner);
            return modelPackage;
        } else {
            String nameBeforeFirstDot = beforeFirstDot(simpleName);
            Package modelPackage = model.createPackage(nameBeforeFirstDot, (NameSpace) owner);
            //save intermediate packages
            saveInIdentRepo(modelPackage, namePrefix+'.'+nameBeforeFirstDot);
            return createPackageRecursive(model, modelPackage, namePrefix, afterFirstDot(simpleName));
        }
    }
}
