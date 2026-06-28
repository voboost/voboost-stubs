package com.qinggan.theme;

import android.content.Context;
import com.qinggan.media.helper.FieldWrapper;

public class ThemeManager {
    private static ThemeManager instance;
    public static final FieldWrapper DEFAULT_THEME_TITLE2 = new FieldWrapper("white");

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
        return (String) DEFAULT_THEME_TITLE2.value;
    }
}
