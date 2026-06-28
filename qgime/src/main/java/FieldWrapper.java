package com.qinggan.app.qgime;

/**
 * FieldWrapper - A wrapper class for fields to support .value access pattern.
 * Used by Frida agents to access Java fields through the .value property.
 */
public class FieldWrapper<T> {
    // This is the field that Frida agents will access as the .value property
    public T value;

    public FieldWrapper(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
