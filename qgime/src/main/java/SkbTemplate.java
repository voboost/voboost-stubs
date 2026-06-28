package com.qinggan.app.qgime;

import android.graphics.drawable.Drawable;

/**
 * Mock SkbTemplate class for Frida agent testing
 */
public class SkbTemplate {
    public SkbTemplate() {
        System.out.println("[SkbTemplate] SkbTemplate created");
    }

    public SoftKey getDefaultKey(int keyId) {
        System.out.println("[SkbTemplate] getDefaultKey called with keyId: " + keyId);
        return new SoftKey();
    }

    public Object getKeyType(int keyTypeId) {
        System.out.println("[SkbTemplate] getKeyType called with keyTypeId: " + keyTypeId);
        return new Object();
    }

    public Drawable getDefaultKeyIcon(int keyCode) {
        System.out.println("[SkbTemplate] getDefaultKeyIcon called with keyCode: " + keyCode);
        return null;
    }

    public Drawable getDefaultKeyIconPopup(int keyCode) {
        System.out.println("[SkbTemplate] getDefaultKeyIconPopup called with keyCode: " + keyCode);
        return null;
    }
}
