package org.modelio.module.scaladesigner.util;

public interface Constants {
    interface Stereotype {
        String VISIBILITY = "ScalaVisibility";
        String OBJECT = "ScalaObject";
        String CLASS = "ScalaClass";
        String TRAIT = "ScalaTrait";
        String CASE = "ScalaCaseClass";
        String PACKAGE = "ScalaPackage";
        String EXTENDS = "ScalaExtends";
        String MIXIN = "ScalaMixin";
    }
    interface Tag {
        String LAZY = "ScalaLazy";
        String FINAL = "ScalaFinal";
        String SEALED = "ScalaSealed";
        String IMPLICIT = "ScalaImplicit";
        String OVERRIDE = "ScalaOverride";
        String MUTABLE = "ScalaMutable";
        String CONTRAVARIANT = "ScalaContravariant";
        String COVARIANT = "ScalaCovariant";
        String LOWER_BOUND = "ScalaLowerTypeBound";
        String UPPER_BOUND = "ScalaUpperTypeBound";
    }

}
