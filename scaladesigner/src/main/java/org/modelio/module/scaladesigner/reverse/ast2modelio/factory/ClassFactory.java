package org.modelio.module.scaladesigner.reverse.ast2modelio.factory;

import edu.kulikov.ast_parser.elements.ClassDef;
import org.modelio.api.model.IUmlModel;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.statik.Class;
import org.modelio.metamodel.uml.statik.NameSpace;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContext;

public class ClassFactory extends AbstractElementFactory<ClassDef, Class> {

    @Override
    public Class createElement(ClassDef classDef, IUmlModel model, IContext context, boolean fill) {
        ModelElement owner = transformRepo.get(classDef.getParent());
        ScalaDesignerModule.logService.info("Create classDef: " + classDef + " owner: " + owner);
        Class aClass = model.createClass(classDef.getIdentifier(), (NameSpace) owner);
        setVisibility(aClass, classDef.getModifiers(), model);
        putModifierTags(aClass, classDef.getModifiers(), model);
        saveInIdentRepo(aClass, classDef.getFullIdentifier());
        return aClass;
    }
}
