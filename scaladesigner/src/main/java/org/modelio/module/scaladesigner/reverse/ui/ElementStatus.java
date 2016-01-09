package org.modelio.module.scaladesigner.reverse.ui;


public class ElementStatus {
    private String value;

    private ReverseStatus reverseStatus;

    private ElementType type;


    public ElementStatus(String value, ElementType type) {
        this (value, type, ReverseStatus.NO_REVERSE);
    }

    public ElementStatus(String value, ElementType type, ReverseStatus reverseStatus) {
        super ();
        this.value = value;
        this.type = type;
        this.reverseStatus = reverseStatus;
    }

    public ReverseStatus getReverseStatus() {
        return this.reverseStatus;
    }

    public void setReverseStatus(ReverseStatus reverseStatus) {
        this.reverseStatus = reverseStatus;
    }

    public ElementType getType() {
        return this.type;
    }

    public String getValue() {
        return this.value;
    }

    public void setType(ElementType i) {
        this.type = i;
    }

    public void setValue(String string) {
        this.value = string;
    }

    public enum ReverseStatus {
        NO_REVERSE,
        REVERSE_SOME_CHILDREN,
        REVERSE;
    }

    public enum ElementType {
        DIRECTORY,
        JAVA_FILE,
        CLASS_FILE;
    }

}
