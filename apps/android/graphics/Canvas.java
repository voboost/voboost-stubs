package android.graphics;

/**
 * Mock Canvas class for Frida agent testing
 */
public class Canvas {
    public Canvas() {
        System.out.println("[Canvas] Canvas created");
    }

    public Canvas(android.graphics.Bitmap bitmap) {
        System.out.println("[Canvas] Canvas created with bitmap");
    }

    public void drawRect(float left, float top, float right, float bottom, Paint paint) {
        System.out.println("[Canvas] drawRect called");
    }

    public void drawCircle(float cx, float cy, float radius, Paint paint) {
        System.out.println("[Canvas] drawCircle called");
    }

    public void drawText(String text, float x, float y, Paint paint) {
        System.out.println("[Canvas] drawText called with: " + text);
    }

    public void drawBitmap(Bitmap bitmap, float left, float top, Paint paint) {
        System.out.println("[Canvas] drawBitmap called");
    }

    public int getWidth() {
        System.out.println("[Canvas] getWidth called");
        return 100;
    }

    public int getHeight() {
        System.out.println("[Canvas] getHeight called");
        return 100;
    }

    public void translate(float dx, float dy) {
        System.out.println("[Canvas] translate called");
    }

    public void scale(float sx, float sy) {
        System.out.println("[Canvas] scale called");
    }

    public void rotate(float degrees) {
        System.out.println("[Canvas] rotate called");
    }

    public void save() {
        System.out.println("[Canvas] save called");
    }

    public void restore() {
        System.out.println("[Canvas] restore called");
    }
}
