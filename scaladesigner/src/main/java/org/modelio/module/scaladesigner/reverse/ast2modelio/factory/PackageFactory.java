package org.modelio.module.scaladesigner.reverse.ast2modelio.factory;

import edu.kulikov.ast_parser.elements.NoElement;
import edu.kulikov.ast_parser.elements.PackageDef;
import org.modelio.api.model.IUmlModel;
import org.modelio.metamodel.mda.Project;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.statik.NameSpace;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContext;
import org.modelio.module.scaladesigner.reverse.ast2modelio.util.ModelUtils;
import org.modelio.module.scaladesigner.util.Constants.Stereotype;
import org.modelio.vcore.smkernel.mapi.MObject;

import java.util.List;

import static org.modelio.module.scaladesigner.api.IScalaDesignerPeerModule.MODULE_NAME;
import static org.modelio.module.scaladesigner.reverse.ast2modelio.util.StringUtils.*;

public class PackageFactory extends AbstractElementFactory<PackageDef, Package> {

    @Override
    public Package createElement(PackageDef from, IUmlModel model, IContext context, Stage stage) {
        if (from.isForImportsOnly())
            return null; //don't create element for synthetic package
        Package aPackage = rm.getByAst(from, Package.class);
        if (stage == Stage.REVERSE_SELF_MINIMUM || stage == Stage.REVERSE_SELF_FULL) {

            if (aPackage == null) {
                ModelElement owner = from.getParent() == NoElement.instance() ||
                        ((from.getParent() instanceof PackageDef &&
                                ((PackageDef) from.getParent()).isForImportsOnly())) ? getModelRoot(model) :
                        rm.getByAst(from.getParent()).get(0);
                ScalaDesignerModule.logService.info("Create package: " + from + " owner: " + owner);
                //if full ident == ident => no upper packages => empty prefix, else
                //prefix + '.' + ident == fullIdent
                aPackage = createPackageRecursive(model, owner, from.getFullIdentifier().equals(from.getIdentifier()) ? "" : prefix(from.getFullIdentifier(), '.' + from.getIdentifier()), from.getIdentifier());
                rm.attachIdentToModelio(aPackage, from.getFullIdentifier());
            }
        }
        return aPackage;
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
        ScalaDesignerModule.logService.info("CreatePackageRecursive, namePrefix=" + namePrefix + ", simpleName=" + simpleName);
        if (!simpleName.contains(".")) {
            Package aPackage = model.createPackage(simpleName, (NameSpace) owner);
            ModelUtils.setStereotype(model, aPackage, MODULE_NAME, Stereotype.PACKAGE, true);
            return aPackage;
        } else {
            String simpleBeforeDot = beforeFirstDot(simpleName);
            ScalaDesignerModule.logService.info("CreatePackageRecursive, simpleBeforeDot:" + simpleBeforeDot);
            String fullIdent = (namePrefix.isEmpty() ? "": namePrefix + '.') + simpleBeforeDot;
            ScalaDesignerModule.logService.info("CreatePackageRecursive, get package by fullIdent: " + fullIdent);
            List<Package> packages = rm.getByFullIdent(fullIdent, Package.class);
            Package aPackage;
            if (packages.isEmpty()) {
                ScalaDesignerModule.logService.info("Package not found");
                aPackage = model.createPackage(simpleBeforeDot, (NameSpace) owner);
                ModelUtils.setStereotype(model, aPackage, MODULE_NAME, Stereotype.PACKAGE, true);
                //save intermediate packages
                rm.attachIdentToModelio(aPackage, fullIdent);
            } else {
                aPackage = packages.get(0);
                ScalaDesignerModule.logService.info("CreatePackageRecursive, package found=" + aPackage);
            }
            return createPackageRecursive(model, aPackage, fullIdent, afterFirstDot(simpleName));
        }
    }
}
