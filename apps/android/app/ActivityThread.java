package android.app;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Mock ActivityThread class for Frida agent testing
 */
public class ActivityThread {
    private static ActivityThread currentActivityThread;
    private static Context currentApplication;

    /**
     * Gets the current activity thread
     */
    public static ActivityThread currentActivityThread() {
        System.out.println("[ActivityThread] currentActivityThread called");
        if (currentActivityThread == null) {
            currentActivityThread = new ActivityThread();
        }
        return currentActivityThread;
    }

    /**
     * Gets the current application
     */
    public static Context currentApplication() {
        System.out.println("[ActivityThread] currentApplication called");
        if (currentApplication == null) {
            currentApplication = new Application();
        }
        return currentApplication;
    }

    /**
     * Sets the current application
     */
    public static void setCurrentApplication(Context app) {
        System.out.println("[ActivityThread] setCurrentApplication called with: " + app);
        currentApplication = app;
    }

    /**
     * Gets the application context
     */
    public Context getApplication() {
        System.out.println("[ActivityThread] getApplication called");
        return currentApplication();
    }

    /**
     * Gets the system context
     */
    public Context getSystemContext() {
        System.out.println("[ActivityThread] getSystemContext called");
        return new Context();
    }

    /**
     * Gets the application thread
     */
    public Object getApplicationThread() {
        System.out.println("[ActivityThread] getApplicationThread called");
        return new ApplicationThread();
    }

    /**
     * Mock ApplicationThread class
     */
    public static class ApplicationThread {
        public ApplicationThread() {
            System.out.println("[ActivityThread.ApplicationThread] ApplicationThread created");
        }

        public void schedulePauseActivity(Object token, boolean finished, boolean userLeaving, int configChanges) {
            System.out.println("[ActivityThread.ApplicationThread] schedulePauseActivity called");
        }

        public void scheduleStopActivity(Object token, boolean showWindow, int configChanges) {
            System.out.println("[ActivityThread.ApplicationThread] scheduleStopActivity called");
        }

        public void scheduleResumeActivity(Object token, boolean isForward, boolean isProvisional) {
            System.out.println("[ActivityThread.ApplicationThread] scheduleResumeActivity called");
        }
    }

    /**
     * Mock Application class
     */
    public static class Application extends Context {
        private PackageManager.ApplicationInfo applicationInfo;
        private PackageManager packageManager;

        public Application() {
            super();
            this.applicationInfo = new PackageManager.ApplicationInfo();
            this.applicationInfo.packageName = "com.qinggan.app.launcher";
            this.applicationInfo.name = "LauncherApplication";
            this.packageManager = new PackageManager();
            System.out.println("[ActivityThread.Application] Application created");
        }

        @Override
        public PackageManager.ApplicationInfo getApplicationInfo() {
            System.out.println("[ActivityThread.Application] getApplicationInfo called");
            return applicationInfo;
        }

        public PackageManager getPackageManager() {
            System.out.println("[ActivityThread.Application] getPackageManager called");
            return packageManager;
        }

        public Context getApplicationContext() {
            System.out.println("[ActivityThread.Application] getApplicationContext called");
            return this;
        }

        public void onCreate() {
            System.out.println("[ActivityThread.Application] onCreate called");
        }

        public void onTerminate() {
            System.out.println("[ActivityThread.Application] onTerminate called");
        }

        public void onLowMemory() {
            System.out.println("[ActivityThread.Application] onLowMemory called");
        }

        public void onTrimMemory(int level) {
            System.out.println("[ActivityThread.Application] onTrimMemory called with level: " + level);
        }

        public void onConfigurationChanged(Object newConfig) {
            System.out.println("[ActivityThread.Application] onConfigurationChanged called");
        }
    }
}
