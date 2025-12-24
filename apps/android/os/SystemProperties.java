package android.os;

/**
 * Stub implementation of SystemProperties for testing
 */
public class SystemProperties {

    public SystemProperties() {
        // Stub constructor
    }

    /**
     * Get system property
     */
    public static String get(String key) {
        // Stub implementation - return default value
        if ("persist.qg.canbus.bcm_screenAutoLiftFdb".equals(key)) {
            return "2"; // Screen raised by default
        }
        return "";
    }

    /**
     * Get system property with default value
     */
    public static String get(String key, String defaultValue) {
        String value = get(key);
        return value.isEmpty() ? defaultValue : value;
    }

    /**
     * Get system property as integer
     */
    public static int getInt(String key, int defaultValue) {
        String value = get(key);
        if (value.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Get system property as boolean
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        if (value.isEmpty()) {
            return defaultValue;
        }
        return "1".equals(value) || "true".equalsIgnoreCase(value);
    }

    /**
     * Set system property
     */
    public static void set(String key, String value) {
        // Stub implementation - just log the call
        System.out.println("SystemProperties.set called with key: " + key + ", value: " + value);
    }
}
