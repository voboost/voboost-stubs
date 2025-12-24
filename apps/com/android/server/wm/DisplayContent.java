package com.android.server.wm;

/**
 * Mock DisplayContent for Frida agent testing
 */
public class DisplayContent {
    private int displayId;

    /**
     * Default constructor
     */
    public DisplayContent() {
        this.displayId = 0;
        System.out.println("[DisplayContent] Created with default display ID: " + displayId);
    }

    /**
     * Constructor with display ID
     */
    public DisplayContent(int displayId) {
        this.displayId = displayId;
        System.out.println("[DisplayContent] Created with display ID: " + displayId);
    }

    /**
     * Get the display ID
     */
    public int getDisplayId() {
        System.out.println("[DisplayContent] getDisplayId called, returning: " + displayId);
        return displayId;
    }

    /**
     * Set the display ID
     */
    public void setDisplayId(int displayId) {
        System.out.println("[DisplayContent] setDisplayId called with: " + displayId);
        this.displayId = displayId;
    }
}
