package android.graphics;

/**
 * Stub implementation of Android Typeface class for testing purposes.
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
        // Private constructor to prevent direct instantiation
    }

    /**
     * Creates a typeface object with the specified family name and style.
     *
     * @param familyName The font family name
     * @param style The font style
     * @return A new Typeface instance
     */
    public static Typeface create(String familyName, int style) {
        return new Typeface();
    }

    /**
     * Creates a typeface object from assets.
     *
     * @param assets The asset manager
     * @param path The path to the font file in assets
     * @return A new Typeface instance
     */
    public static Typeface createFromAsset(Object assets, String path) {
        return new Typeface();
    }

    /**
     * Gets the style of this Typeface.
     *
     * @return The style constant
     */
    public int getStyle() {
        return 0; // Normal style
    }

    /**
     * Checks if this Typeface is bold.
     *
     * @return True if bold, false otherwise
     */
    public boolean isBold() {
        return this == DEFAULT_BOLD;
    }

    /**
     * Checks if this Typeface is italic.
     *
     * @return True if italic, false otherwise
     */
    public boolean isItalic() {
        return false;
    }
}
