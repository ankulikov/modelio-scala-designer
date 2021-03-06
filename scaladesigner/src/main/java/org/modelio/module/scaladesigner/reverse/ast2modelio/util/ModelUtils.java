package org.modelio.module.scaladesigner.reverse.ast2modelio.util;

import org.modelio.api.model.IUmlModel;
import org.modelio.metamodel.factory.ExtensionNotFoundException;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.infrastructure.TaggedValue;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;

public class ModelUtils {
    public static TaggedValue setTaggedValue(IUmlModel model, ModelElement element, String moduleName, String tagName, boolean add) {
        TaggedValue tag = element.getTag(moduleName, tagName);
        if (!add) {
            if (tag != null) {
                tag.delete();
            }
        } else {
            if (tag == null) {
                // Create the tagged value
                try {
                    tag = model.createTaggedValue(moduleName, tagName, element);
                } catch (ExtensionNotFoundException e) {
                    ScalaDesignerModule.logService.error("Tagged value '" + tagName + "' from module '" + moduleName + "' was not found");
                }
            }
        }
        return tag;
    }

    public static TaggedValue setTaggedValue(IUmlModel model, ModelElement element, String moduleName, String tagName, String value, boolean add) {
        if (add) {
            try {
                element.putTagValue(moduleName, tagName, value);
            } catch (ExtensionNotFoundException e) {
                ScalaDesignerModule.logService.error("Tagged value '" + tagName + "' from module '" + moduleName + "' was not found");
            }
        } else {
            element.removeTags(moduleName, tagName);
        }
        return element.getTag(moduleName, tagName);
    }

    public static void setStereotype(IUmlModel model, ModelElement element, String moduleName, String stereotype, boolean add) {
        try {
            if (add && !element.isStereotyped(moduleName, stereotype)) {
                element.addStereotype(moduleName, stereotype);
            } else if (!add && element.isStereotyped(moduleName, stereotype)){
                element.removeStereotypes(moduleName, stereotype);
            }
        } catch (ExtensionNotFoundException e) {
            ScalaDesignerModule.logService.error("Stereotype '" + stereotype + "' from module '" + moduleName + "' was not found");
        }

    }

    public static void deleteElement(ModelElement element) {
        if (!element.isDeleted())
            element.delete();
    }
}
