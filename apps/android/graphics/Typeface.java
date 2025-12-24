package android.graphics;

/**
 * Stub implementation of Typeface for testing
 */
public class Typeface {

    public static final Typeface DEFAULT;
    public static final Typeface DEFAULT_BOLD;
    public static final Typeface SANS_SERIF;
    public static final Typeface SERIF;
    public static final Typeface MONOSPACE;

    static {
        DEFAULT = new Typeface();
        DEFAULT_BOLD = new Typeface();
        SANS_SERIF = new Typeface();
        SERIF = new Typeface();
        MONOSPACE = new Typeface();
    }

    private Typeface() {
        // Private constructor
    }

    /**
     * Create a typeface object
     */
    public static Typeface create(String familyName, int style) {
        return new Typeface();
    }

    /**
     * Create a typeface object from assets
     */
    public static Typeface createFromAsset(Object assets, String path) {
        return new Typeface();
    }

    /**
     * Get style
     */
    public int getStyle() {
        return 0; // Normal style
    }

    /**
     * Check if bold
     */
    public boolean isBold() {
        return this == DEFAULT_BOLD;
    }

    /**
     * Check if italic
     */
    public boolean isItalic() {
        return false;
    }
}
