package android.graphics;

/**
 * Mock Bitmap class for Frida agent testing
 */
public class Bitmap {
    public Bitmap() {
        System.out.println("[Bitmap] Bitmap created");
    }

    public static Bitmap createBitmap(int width, int height, Bitmap.Config config) {
        System.out.println("[Bitmap] createBitmap called");
        return new Bitmap();
    }

    public int getWidth() {
        System.out.println("[Bitmap] getWidth called");
        return 100;
    }

    public int getHeight() {
        System.out.println("[Bitmap] getHeight called");
        return 100;
    }

    public void recycle() {
        System.out.println("[Bitmap] recycle called");
    }

    public boolean isRecycled() {
        System.out.println("[Bitmap] isRecycled called");
        return false;
    }

    public enum Config {
        ALPHA_8,
        RGB_565,
        ARGB_4444,
        ARGB_8888
    }
}
