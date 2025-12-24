package com.qinggan.theme;

import android.content.Context;
import com.qinggan.app.qgime.FieldWrapper;

public class ThemeManager {
    private static ThemeManager instance;
    public static final FieldWrapper<String> DEFAULT_THEME_TITLE2 = new FieldWrapper<>("white");

    private ThemeManager() {
        // Private constructor for singleton
    }

    public static ThemeManager getInstance(Context context) {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    public String getCurrentThemeTitle() {
        return DEFAULT_THEME_TITLE2.value;
    }
}
