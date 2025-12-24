package com.qinggan.app.qgime;

import android.content.Context;
import java.util.Vector;

public class SkbPool {
    private static SkbPool instance;
    public FieldWrapper<Vector<SoftKeyboard>> mSoftKeyboards = new FieldWrapper<>(new Vector<>());

    private SkbPool() {
        // Private constructor for singleton
    }

    public static SkbPool getInstance() {
        if (instance == null) {
            instance = new SkbPool();
        }
        return instance;
    }

    public SoftKeyboard getSoftKeyboard(int cacheId, int xmlId, int width, int height, Context context) {
        // Return a default soft keyboard
        return new SoftKeyboard(xmlId, null, width, height);
    }

    public Object getSkbTemplate(int skbTemplateResId, Context context) {
        // Return a mock template
        return new Object();
    }

    public void resetCachedSkb() {
        mSoftKeyboards.value.clear();
    }
}
