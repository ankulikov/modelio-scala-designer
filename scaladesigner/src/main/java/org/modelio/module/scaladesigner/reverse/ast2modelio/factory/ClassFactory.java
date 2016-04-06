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
        Class aClass = rm.getByAst(classDef, Class.class);
        if (aClass == null) {
            ModelElement owner = rm.getByAst(classDef.getParent()).get(0);
            ScalaDesignerModule.logService.info("Create classDef: " + classDef + " owner: " + owner);
            aClass = model.createClass(classDef.getIdentifier(), (NameSpace) owner);
            rm.attachIdentToModelio(aClass, classDef.getFullIdentifier());
        }
        if (fill) {
            setVisibility(aClass, classDef.getModifiers(), model);
            putModifierTags(aClass, classDef.getModifiers(), model);
        }
        return aClass;
    }
}
