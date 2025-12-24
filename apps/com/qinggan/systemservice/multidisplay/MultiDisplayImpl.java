package com.qinggan.systemservice.multidisplay;

/**
 * Mock implementation of MultiDisplayImpl for testing
 */
public class MultiDisplayImpl {

    /**
     * Checks if an app is whitelisted for multi-display
     * @param packageName The package name to check
     * @return true if whitelisted, false otherwise
     */
    public boolean isWhiteListApp(String packageName) {
        // Default implementation - return false for all apps
        return false;
    }

    /**
     * Default constructor
     */
    public MultiDisplayImpl() {
        // Mock implementation
    }
}
