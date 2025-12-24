package com.qinggan.app.qgime;

public class QingganIME {
    public FieldWrapper<InputModeSwitcher> mInputModeSwitcher = new FieldWrapper<>(null);
    public FieldWrapper<Object> mSkbContainer = new FieldWrapper<>(null);

    public QingganIME() {
        mInputModeSwitcher.value = InputModeSwitcher.getInstance();
    }

    public void responseSoftKeyEvent(SoftKey softKey) {
        // Implementation
    }

    public void resetToIdleState(boolean flag) {
        // Implementation
    }
}
