package com.qinggan.app.qgime;

import android.view.KeyEvent;

public class EnglishInputProcessor {
    public FieldWrapper<Integer> mLastKeyCode = new FieldWrapper<>(0);

    public boolean processKey(Object ic, KeyEvent event, boolean isShift, boolean commit) {
        // Mock implementation
        return true;
    }
}
