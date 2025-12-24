package com.android.server.wm;

import android.content.res.Configuration;
import android.graphics.Rect;
import java.util.Locale;

/**
 * Stub implementation of ActivityRecord for testing
 */
public class ActivityRecord {

    public String packageName;
    public Rect mSizeCompatBounds;
    public float mSizeCompatScale;
    public Configuration mConfiguration;

    public ActivityRecord() {
        this.packageName = "com.example.app";
        this.mSizeCompatBounds = new Rect(0, 0, 1920, 1080);
        this.mSizeCompatScale = 1.0f;
        this.mConfiguration = new Configuration();
    }

    public ActivityRecord(String packageName) {
        this();
        this.packageName = packageName;
    }

    /**
     * Get configuration
     */
    public Configuration getConfiguration() {
        return mConfiguration;
    }

    /**
     * Called when configuration changes
     */
    public void onConfigurationChanged(Configuration newConfig) {
        // Stub implementation - just log the call
        System.out.println("ActivityRecord.onConfigurationChanged called");
    }

    /**
     * Called when display changes
     */
    public void onDisplayChanged(Object displayContent) {
        // Stub implementation - just log the call
        System.out.println("ActivityRecord.onDisplayChanged called with Object");
    }

    /**
     * Called when display changes - overload for DisplayContent
     */
    public void onDisplayChanged(DisplayContent displayContent) {
        // Stub implementation - just log the call
        System.out.println("ActivityRecord.onDisplayChanged called with DisplayContent");
    }

    /**
     * Inner Configuration class
     */
    public static class Configuration {
        public int densityDpi;
        public int orientation;
        public Locale locale;

        public Configuration() {
            this.densityDpi = 240; // Default DPI
            this.orientation = 2; // Landscape
            this.locale = Locale.US;
        }

        public void setLocale(Locale locale) {
            this.locale = locale;
        }
    }
}
