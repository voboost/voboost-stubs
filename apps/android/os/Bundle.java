package android.os;

/**
 * Mock Bundle class for Frida agent testing
 */
public class Bundle {
    public Bundle() {
        System.out.println("[Bundle] Bundle created");
    }

    public Bundle(Bundle bundle) {
        System.out.println("[Bundle] Bundle created from another bundle");
    }

    public void putString(String key, String value) {
        System.out.println("[Bundle] putString called with key: " + key + ", value: " + value);
    }

    public String getString(String key) {
        System.out.println("[Bundle] getString called with key: " + key);
        return null;
    }

    public String getString(String key, String defaultValue) {
        System.out.println("[Bundle] getString called with key: " + key + ", defaultValue: " + defaultValue);
        return defaultValue;
    }

    public void putInt(String key, int value) {
        System.out.println("[Bundle] putInt called with key: " + key + ", value: " + value);
    }

    public int getInt(String key) {
        System.out.println("[Bundle] getInt called with key: " + key);
        return 0;
    }

    public int getInt(String key, int defaultValue) {
        System.out.println("[Bundle] getInt called with key: " + key + ", defaultValue: " + defaultValue);
        return defaultValue;
    }

    public void putBoolean(String key, boolean value) {
        System.out.println("[Bundle] putBoolean called with key: " + key + ", value: " + value);
    }

    public boolean getBoolean(String key) {
        System.out.println("[Bundle] getBoolean called with key: " + key);
        return false;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        System.out.println("[Bundle] getBoolean called with key: " + key + ", defaultValue: " + defaultValue);
        return defaultValue;
    }
}
