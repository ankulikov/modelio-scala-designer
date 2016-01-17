package org.modelio.module.scaladesigner.util;

import org.modelio.api.module.IModuleUserConfiguration;

public class ScalaDesignerUtils  {
    public static boolean noParamter(IModuleUserConfiguration configuration,
                                     String parameter) {
        String parameterValue = configuration.getParameterValue(parameter);
        return parameterValue == null || parameter.isEmpty();
    }
}
