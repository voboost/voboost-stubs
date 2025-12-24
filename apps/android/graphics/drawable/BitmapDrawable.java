package android.graphics.drawable;

import android.content.res.Resources;
import android.graphics.Bitmap;

/**
 * Mock BitmapDrawable for Frida agent testing
 */
public class BitmapDrawable {

    /**
     * Default constructor
     */
    public BitmapDrawable() {
        System.out.println("[BitmapDrawable] Created without parameters");
    }

    /**
     * Constructor with resources and bitmap
     */
    public BitmapDrawable(Resources resources, Bitmap bitmap) {
        System.out.println("[BitmapDrawable] Created with bitmap");
    }

    /**
     * Static factory method for Frida compatibility
     */
    public static BitmapDrawable $new(Resources resources, Bitmap bitmap) {
        return new BitmapDrawable(resources, bitmap);
    }
}
