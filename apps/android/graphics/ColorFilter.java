package android.graphics;

/**
 * Mock ColorFilter class for Frida agent testing
 */
public class ColorFilter {
    public ColorFilter() {
        System.out.println("[ColorFilter] ColorFilter created");
    }

    public static ColorFilter createLightingColorFilter(int mul, int add) {
        System.out.println("[ColorFilter] createLightingColorFilter called");
        return new ColorFilter();
    }

    public static ColorFilter createColorMatrixColorFilter(ColorMatrix matrix) {
        System.out.println("[ColorFilter] createColorMatrixColorFilter called");
        return new ColorFilter();
    }

    public static ColorFilter createPorterDuffColorFilter(int color, PorterDuff.Mode mode) {
        System.out.println("[ColorFilter] createPorterDuffColorFilter called");
        return new ColorFilter();
    }
}
