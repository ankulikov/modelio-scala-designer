package org.modelio.module.scaladesigner.util;

public interface Constants {
    interface Stereotype {
        String VISIBILITY = "ScalaVisibility";
        String OBJECT = "ScalaObject";
        String CLASS = "ScalaClass";
        String TRAIT = "ScalaTrait";
        String CASE = "ScalaCaseClass";
        String PACKAGE = "ScalaPackage";
    }
    interface Tag {
        String LAZY = "ScalaLazy";
        String FINAL = "ScalaFinal";
        String SEALED = "ScalaSealed";
        String IMPLICIT = "ScalaImplicit";
        String OVERRIDE = "ScalaOverride";
        String MUTABLE = "ScalaMutable";
    }

}
