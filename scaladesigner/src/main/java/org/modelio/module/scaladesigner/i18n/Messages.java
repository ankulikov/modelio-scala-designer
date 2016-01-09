package org.modelio.module.scaladesigner.i18n;

import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle ("org.modelio.module.scaladesigner.i18n.messages");


    /**
     * Empty private constructor, services are accessed through static methods.
     */
    private Messages() {
        // Nothing to do 
    }

    public static String getString(String key, Object ... params) {
        try {
            return MessageFormat.format (RESOURCE_BUNDLE.getString (key), params);
        } catch (MissingResourceException e) {
            ScalaDesignerModule.logService.error("Missing key: " + key);
            return '!' + key + '!';
        } catch (IllegalArgumentException e) {
            ScalaDesignerModule.logService.error(e);
            return '!' + key + '!';
        }
    }

    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString (key);
        } catch (MissingResourceException e) {
            ScalaDesignerModule.logService.error("Missing key: " + key);
            return '!' + key + '!';
        }
    }
}
