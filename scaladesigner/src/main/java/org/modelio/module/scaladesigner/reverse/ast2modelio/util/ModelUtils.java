package org.modelio.module.scaladesigner.reverse.ast2modelio.util;

import org.modelio.api.model.IModelingSession;
import org.modelio.api.model.IUmlModel;
import org.modelio.metamodel.factory.ExtensionNotFoundException;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.infrastructure.TaggedValue;

public class ModelUtils {
    public static TaggedValue setTaggedValue(IUmlModel model, ModelElement element, String moduleName, String tagName, boolean add) throws ExtensionNotFoundException {
        TaggedValue tag = element.getTag (moduleName, tagName);
        if (!add) {
            if (tag != null) {
                tag.delete ();
            }
        } else {
            if (tag == null) {
                // Create the tagged value
                tag = model.createTaggedValue (moduleName, tagName, element);
            }
        }
        return tag;
    }
}
