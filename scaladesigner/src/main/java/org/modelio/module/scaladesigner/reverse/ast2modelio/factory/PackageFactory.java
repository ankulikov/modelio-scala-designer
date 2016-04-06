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

import static org.modelio.module.scaladesigner.reverse.ast2modelio.util.StringUtils.*;

public class PackageFactory extends AbstractElementFactory<PackageDef, Package> {

    @Override
    public Package createElement(PackageDef from, IUmlModel model, IContext context, boolean fill) {
        if (from.isForImportsOnly())
            return null; //don't create package for synthetic package
        Package aPackage = rm.getByAst(from, Package.class);
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
            return model.createPackage(simpleName, (NameSpace) owner);
        } else {
            String simpleBeforeDot = beforeFirstDot(simpleName);
            String fullIdent = namePrefix + '.' + simpleBeforeDot;
            Package aPackage = rm.getByFullIdent(fullIdent, Package.class);
            if (aPackage == null) {
                aPackage = model.createPackage(simpleBeforeDot, (NameSpace) owner);
                //save intermediate packages
                rm.attachIdentToModelio(aPackage, fullIdent);
            }
            return createPackageRecursive(model, aPackage, namePrefix, afterFirstDot(simpleName));
        }
    }
}
