package android.content.res;

/**
 * Mock Resources class for Frida agent testing
 */
public class Resources {
    // Resource properties
    public String packageName = "com.qinggan.app.launcher";

    /**
     * Default constructor
     */
    public Resources() {
        System.out.println("[Resources] Resources created");
    }

    /**
     * Gets a string resource
     */
    public String getString(int id) {
        System.out.println("[Resources] getString called with id: " + id);
        return "MockString_" + id;
    }

    /**
     * Gets a string resource with formatting
     */
    public String getString(int id, Object... formatArgs) {
        System.out.println("[Resources] getString called with id: " + id + ", args: " + formatArgs.length);
        return "MockString_" + id;
    }

    /**
     * Gets a string array resource
     */
    public String[] getStringArray(int id) {
        System.out.println("[Resources] getStringArray called with id: " + id);
        return new String[]{"MockString1", "MockString2", "MockString3"};
    }

    /**
     * Gets an integer resource
     */
    public int getInteger(int id) {
        System.out.println("[Resources] getInteger called with id: " + id);
        return 0;
    }

    /**
     * Gets a boolean resource
     */
    public boolean getBoolean(int id) {
        System.out.println("[Resources] getBoolean called with id: " + id);
        return false;
    }

    /**
     * Gets a dimension resource
     */
    public float getDimension(int id) {
        System.out.println("[Resources] getDimension called with id: " + id);
        return 0.0f;
    }

    /**
     * Gets a dimension pixel offset resource
     */
    public int getDimensionPixelOffset(int id) {
        System.out.println("[Resources] getDimensionPixelOffset called with id: " + id);
        return 0;
    }

    /**
     * Gets a dimension pixel size resource
     */
    public int getDimensionPixelSize(int id) {
        System.out.println("[Resources] getDimensionPixelSize called with id: " + id);
        return 0;
    }

    /**
     * Gets a color resource
     */
    public int getColor(int id) {
        System.out.println("[Resources] getColor called with id: " + id);
        return 0xFF000000; // Black
    }

    /**
     * Gets a color resource with theme
     */
    public int getColor(int id, Object theme) {
        System.out.println("[Resources] getColor called with id: " + id + ", theme: " + theme);
        return 0xFF000000; // Black
    }

    /**
     * Gets a drawable resource
     */
    public Object getDrawable(int id) {
        System.out.println("[Resources] getDrawable called with id: " + id);
        return null;
    }

    /**
     * Gets a drawable resource with theme
     */
    public Object getDrawable(int id, Object theme) {
        System.out.println("[Resources] getDrawable called with id: " + id + ", theme: " + theme);
        return null;
    }

    /**
     * Gets the resource identifier for a resource name
     */
    public int getIdentifier(String name, String defType, String defPackage) {
        System.out.println("[Resources] getIdentifier called with name: " + name + ", type: " + defType + ", package: " + defPackage);
        return 0;
    }

    /**
     * Gets the resource entry name for a resource id
     */
    public String getResourceEntryName(int resid) {
        System.out.println("[Resources] getResourceEntryName called with id: " + resid);
        return "mock_resource_" + resid;
    }

    /**
     * Gets the resource name for a resource id
     */
    public String getResourceName(int resid) {
        System.out.println("[Resources] getResourceName called with id: " + resid);
        return "com.qinggan.app.launcher:mock/mock_resource_" + resid;
    }

    /**
     * Gets the resource package name for a resource id
     */
    public String getResourcePackageName(int resid) {
        System.out.println("[Resources] getResourcePackageName called with id: " + resid);
        return "com.qinggan.app.launcher";
    }

    /**
     * Gets the resource type name for a resource id
     */
    public String getResourceTypeName(int resid) {
        System.out.println("[Resources] getResourceTypeName called with id: " + resid);
        return "mock";
    }

    /**
     * Opens a raw resource
     */
    public java.io.InputStream openRawResource(int id) {
        System.out.println("[Resources] openRawResource called with id: " + id);
        return new java.io.ByteArrayInputStream(new byte[0]);
    }

    /**
     * Gets the display metrics
     */
    public DisplayMetrics getDisplayMetrics() {
        System.out.println("[Resources] getDisplayMetrics called");
        return new DisplayMetrics();
    }

    /**
     * Mock DisplayMetrics class
     */
    public static class DisplayMetrics {
        public int widthPixels = 1080;
        public int heightPixels = 1920;
        public float density = 2.0f;
        public int densityDpi = 320;
        public float scaledDensity = 2.0f;
        public float xdpi = 320.0f;
        public float ydpi = 320.0f;

        public DisplayMetrics() {
            System.out.println("[Resources.DisplayMetrics] DisplayMetrics created");
        }
    }
}
