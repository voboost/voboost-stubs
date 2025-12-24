package android.content.pm;

import android.content.Intent;

/**
 * Mock PackageManager class for Frida agent testing
 */
public class PackageManager {
    // Package manager constants
    public static final int GET_ACTIVITIES = 0x00000001;
    public static final int GET_SERVICES = 0x00000004;
    public static final int GET_RECEIVERS = 0x00000008;
    public static final int GET_PROVIDERS = 0x00000010;
    public static final int GET_INSTRUMENTATION = 0x00000020;
    public static final int GET_META_DATA = 0x00000080;
    public static final int GET_SHARED_LIBRARY_FILES = 0x00000400;
    public static final int GET_URI_PERMISSION_PATTERNS = 0x00000800;
    public static final int GET_PERMISSIONS = 0x00001000;
    public static final int GET_CONFIGURATIONS = 0x00002000;
    public static final int GET_GIDS = 0x00004000;
    public static final int GET_SIGNATURES = 0x00000040;
    public static final int GET_SIGNING_CERTIFICATES = 0x08000000;
    public static final int MATCH_DEFAULT_ONLY = 0x00010000;
    public static final int GET_DISABLED_COMPONENTS = 0x00000200;
    public static final int GET_DISABLED_UNTIL_USED_COMPONENTS = 0x00008000;
    public static final int GET_UNINSTALLED_PACKAGES = 0x00002000;

    /**
     * Default constructor
     */
    public PackageManager() {
        System.out.println("[PackageManager] PackageManager created");
    }

    /**
     * Gets launch intent for package
     */
    public Intent getLaunchIntentForPackage(String packageName) {
        System.out.println("[PackageManager] getLaunchIntentForPackage called with: " + packageName);
        if ("com.example.app1".equals(packageName) ||
            "com.example.app2".equals(packageName) ||
            "com.example.app3".equals(packageName) ||
            "ru.yandex.music".equals(packageName)) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setPackage(packageName);
            return intent;
        }
        return null;
    }

    /**
     * Gets package info
     */
    public PackageInfo getPackageInfo(String packageName, int flags) {
        System.out.println("[PackageManager] getPackageInfo called with: " + packageName + ", flags: " + flags);
        PackageInfo info = new PackageInfo();
        info.packageName = packageName;
        info.versionName = "1.0";
        info.versionCode = 1;
        return info;
    }

    /**
     * Gets application info
     */
    public ApplicationInfo getApplicationInfo(String packageName, int flags) {
        System.out.println("[PackageManager] getApplicationInfo called with: " + packageName + ", flags: " + flags);
        ApplicationInfo info = new ApplicationInfo();
        info.packageName = packageName;
        info.name = "MockApp";
        return info;
    }

    /**
     * Gets installed packages
     */
    public java.util.List<PackageInfo> getInstalledPackages(int flags) {
        System.out.println("[PackageManager] getInstalledPackages called with flags: " + flags);
        java.util.List<PackageInfo> packages = new java.util.ArrayList<>();

        // Add some mock packages
        PackageInfo info1 = new PackageInfo();
        info1.packageName = "com.example.app1";
        info1.versionName = "1.0";
        info1.versionCode = 1;
        packages.add(info1);

        PackageInfo info2 = new PackageInfo();
        info2.packageName = "com.example.app2";
        info2.versionName = "2.0";
        info2.versionCode = 2;
        packages.add(info2);

        PackageInfo info3 = new PackageInfo();
        info3.packageName = "com.example.app3";
        info3.versionName = "3.0";
        info3.versionCode = 3;
        packages.add(info3);

        return packages;
    }

    /**
     * Gets installed applications
     */
    public java.util.List<ApplicationInfo> getInstalledApplications(int flags) {
        System.out.println("[PackageManager] getInstalledApplications called with flags: " + flags);
        java.util.List<ApplicationInfo> apps = new java.util.ArrayList<>();

        // Add some mock applications
        ApplicationInfo app1 = new ApplicationInfo();
        app1.packageName = "com.example.app1";
        app1.name = "Example App 1";
        apps.add(app1);

        ApplicationInfo app2 = new ApplicationInfo();
        app2.packageName = "com.example.app2";
        app2.name = "Example App 2";
        apps.add(app2);

        ApplicationInfo app3 = new ApplicationInfo();
        app3.packageName = "com.example.app3";
        app3.name = "Example App 3";
        apps.add(app3);

        return apps;
    }

    /**
     * Checks if package is installed
     */
    public boolean isPackageAvailable(String packageName) {
        System.out.println("[PackageManager] isPackageAvailable called with: " + packageName);
        return "com.example.app1".equals(packageName) ||
               "com.example.app2".equals(packageName) ||
               "com.example.app3".equals(packageName);
    }

    /**
     * Mock PackageInfo class
     */
    public static class PackageInfo {
        public String packageName;
        public String versionName;
        public int versionCode;
        public ApplicationInfo applicationInfo;
    }

    /**
     * Mock ApplicationInfo class
     */
    public static class ApplicationInfo {
        public String packageName;
        public String name;
        public String className;
        public int flags;
        public String sourceDir;
        public String publicSourceDir;
    }
}
