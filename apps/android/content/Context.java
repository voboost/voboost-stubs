package android.content;

import android.content.res.Resources;
import android.content.pm.PackageManager;

/**
 * Mock Context class for Frida agent testing
 */
public class Context {
    // Context properties
    public String packageName = "com.qinggan.app.launcher";
    public String applicationInfo = null;
    public Resources resources = null;
    public PackageManager packageManager = null;

    /**
     * Default constructor
     */
    public Context() {
        System.out.println("[Context] Context created");
        this.resources = new Resources();
        this.packageManager = new PackageManager();
    }

    /**
     * Gets the application context
     */
    public Context getApplicationContext() {
        System.out.println("[Context] getApplicationContext called");
        return this;
    }

    /**
     * Gets the package name
     */
    public String getPackageName() {
        System.out.println("[Context] getPackageName called, returning: " + packageName);
        return packageName;
    }

    /**
     * Gets the package manager
     */
    public PackageManager getPackageManager() {
        System.out.println("[Context] getPackageManager called");
        return packageManager;
    }

    /**
     * Gets the resources
     */
    public Resources getResources() {
        System.out.println("[Context] getResources called");
        return resources;
    }

    /**
     * Gets the application info
     */
    public Object getApplicationInfo() {
        System.out.println("[Context] getApplicationInfo called");
        return applicationInfo;
    }

    /**
     * Starts an activity
     */
    public void startActivity(Intent intent) {
        System.out.println("[Context] startActivity called with: " + intent);
    }

    /**
     * Starts a service
     */
    public Object startService(Intent intent) {
        System.out.println("[Context] startService called with: " + intent);
        return null;
    }

    /**
     * Binds to a service
     */
    public boolean bindService(Intent intent, Object serviceConnection, int flags) {
        System.out.println("[Context] bindService called with: " + intent + ", flags: " + flags);
        return true;
    }

    /**
     * Gets system service
     */
    public Object getSystemService(String name) {
        System.out.println("[Context] getSystemService called with: " + name);
        return null;
    }

    // Service constants
    public static final String ACTIVITY_SERVICE = "activity";
    public static final String WINDOW_SERVICE = "window";
    public static final String LAYOUT_INFLATER_SERVICE = "layout_inflater";
    public static final String POWER_SERVICE = "power";
    public static final String ALARM_SERVICE = "alarm";
    public static final String NOTIFICATION_SERVICE = "notification";
    public static final String KEYGUARD_SERVICE = "keyguard";
    public static final String LOCATION_SERVICE = "location";
    public static final String SEARCH_SERVICE = "search";
    public static final String VIBRATOR_SERVICE = "vibrator";
    public static final String CONNECTIVITY_SERVICE = "connectivity";
    public static final String WIFI_SERVICE = "wifi";
    public static final String AUDIO_SERVICE = "audio";
    public static final String TELEPHONY_SERVICE = "phone";
    public static final String INPUT_METHOD_SERVICE = "input_method";
    public static final String UI_MODE_SERVICE = "uimode";
    public static final String DOWNLOAD_SERVICE = "download";
}
