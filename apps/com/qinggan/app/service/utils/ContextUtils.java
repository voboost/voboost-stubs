package com.qinggan.app.service.utils;

import android.content.Context;
import android.app.ActivityThread;
import com.qinggan.media.helper.FieldWrapper;

public class ContextUtils {
    public static FieldWrapper context;

    static {
        // Initialize with actual application context
        try {
            Context appContext = ActivityThread.currentApplication().getApplicationContext();
            context = new FieldWrapper(appContext);
        } catch (Exception e) {
            // Fallback to null if context not available
            context = new FieldWrapper(null);
        }
    }
}
