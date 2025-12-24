package android.content;

import android.net.Uri;

/**
 * Mock Intent class for Frida agent testing
 */
public class Intent {
    // Intent properties
    public String action = null;
    public String dataString = null;
    public Uri data = null;
    public String type = null;
    public String packageName = null;
    public String component = null;
    public int flags = 0;

    /**
     * Default constructor
     */
    public Intent() {
        System.out.println("[Intent] Intent created");
    }

    /**
     * Constructor with action
     */
    public Intent(String action) {
        this.action = action;
        System.out.println("[Intent] Intent created with action: " + action);
    }

    /**
     * Constructor with action and URI
     */
    public Intent(String action, Uri data) {
        this.action = action;
        this.data = data;
        System.out.println("[Intent] Intent created with action: " + action + ", data: " + data);
    }

    /**
     * Sets the action
     */
    public Intent setAction(String action) {
        this.action = action;
        System.out.println("[Intent] setAction called with: " + action);
        return this;
    }

    /**
     * Gets the action
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the data
     */
    public Intent setData(Uri data) {
        this.data = data;
        System.out.println("[Intent] setData called with: " + data);
        return this;
    }

    /**
     * Sets the data and type
     */
    public Intent setDataAndType(Uri data, String type) {
        this.data = data;
        this.type = type;
        System.out.println("[Intent] setDataAndType called with data: " + data + ", type: " + type);
        return this;
    }

    /**
     * Sets the package name
     */
    public Intent setPackage(String packageName) {
        this.packageName = packageName;
        System.out.println("[Intent] setPackage called with: " + packageName);
        return this;
    }

    /**
     * Sets the component
     */
    public Intent setComponent(String component) {
        this.component = component;
        System.out.println("[Intent] setComponent called with: " + component);
        return this;
    }

    /**
     * Sets the flags
     */
    public Intent setFlags(int flags) {
        this.flags = flags;
        System.out.println("[Intent] setFlags called with: " + flags);
        return this;
    }

    /**
     * Adds a flag
     */
    public Intent addFlags(int flags) {
        this.flags |= flags;
        System.out.println("[Intent] addFlags called with: " + flags);
        return this;
    }

    /**
     * Puts an extra
     */
    public Intent putExtra(String name, Object value) {
        System.out.println("[Intent] putExtra called with name: " + name + ", value: " + value);
        return this;
    }

    /**
     * Puts a string extra
     */
    public Intent putExtra(String name, String value) {
        System.out.println("[Intent] putExtra called with name: " + name + ", value: " + value);
        return this;
    }

    /**
     * Puts an int extra
     */
    public Intent putExtra(String name, int value) {
        System.out.println("[Intent] putExtra called with name: " + name + ", value: " + value);
        return this;
    }

    /**
     * Puts a boolean extra
     */
    public Intent putExtra(String name, boolean value) {
        System.out.println("[Intent] putExtra called with name: " + name + ", value: " + value);
        return this;
    }

    @Override
    public String toString() {
        return "Intent{action='" + action + "', data=" + data + ", type='" + type +
               "', package='" + packageName + "', component='" + component + "', flags=" + flags + "}";
    }

    // Standard actions
    public static final String ACTION_MAIN = "android.intent.action.MAIN";
    public static final String ACTION_VIEW = "android.intent.action.VIEW";
    public static final String ACTION_EDIT = "android.intent.action.EDIT";
    public static final String ACTION_CALL = "android.intent.action.CALL";
    public static final String ACTION_DIAL = "android.intent.action.DIAL";
    public static final String ACTION_SEND = "android.intent.action.SEND";
    public static final String ACTION_SENDTO = "android.intent.action.SENDTO";
    public static final String ACTION_ANSWER = "android.intent.action.ANSWER";
    public static final String ACTION_INSERT = "android.intent.action.INSERT";
    public static final String ACTION_DELETE = "android.intent.action.DELETE";
    public static final String ACTION_RUN = "android.intent.action.RUN";
    public static final String ACTION_SYNC = "android.intent.action.SYNC";
    public static final String ACTION_PICK = "android.intent.action.PICK";
    public static final String ACTION_CHOOSER = "android.intent.action.CHOOSER";

    // Flags
    public static final int FLAG_ACTIVITY_NEW_TASK = 0x10000000;
    public static final int FLAG_ACTIVITY_CLEAR_TOP = 0x04000000;
    public static final int FLAG_ACTIVITY_SINGLE_TOP = 0x20000000;
    public static final int FLAG_ACTIVITY_NO_HISTORY = 0x40000000;
    public static final int FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS = 0x00800000;
}
