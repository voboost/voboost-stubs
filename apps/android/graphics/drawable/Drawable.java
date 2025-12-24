package android.graphics.drawable;

/**
 * Mock Drawable class for Frida agent testing
 */
public class Drawable {
    public Drawable() {
        System.out.println("[Drawable] Drawable created");
    }

    public int getIntrinsicWidth() {
        System.out.println("[Drawable] getIntrinsicWidth called");
        return 24;
    }

    public int getIntrinsicHeight() {
        System.out.println("[Drawable] getIntrinsicHeight called");
        return 24;
    }

    public void setBounds(int left, int top, int right, int bottom) {
        System.out.println("[Drawable] setBounds called");
    }

    public void draw(android.graphics.Canvas canvas) {
        System.out.println("[Drawable] draw called");
    }

    public void setAlpha(int alpha) {
        System.out.println("[Drawable] setAlpha called with: " + alpha);
    }

    public void setColorFilter(android.graphics.ColorFilter colorFilter) {
        System.out.println("[Drawable] setColorFilter called");
    }

    public int getOpacity() {
        System.out.println("[Drawable] getOpacity called");
        return 0;
    }
}
