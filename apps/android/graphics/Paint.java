package android.graphics;

/**
 * Mock Paint class for Frida agent testing
 */
public class Paint {
    public Paint() {
        System.out.println("[Paint] Paint created");
    }

    public Paint(int flags) {
        System.out.println("[Paint] Paint created with flags: " + flags);
    }

    public void setColor(int color) {
        System.out.println("[Paint] setColor called with: " + color);
    }

    public void setAlpha(int alpha) {
        System.out.println("[Paint] setAlpha called with: " + alpha);
    }

    public void setStrokeWidth(float width) {
        System.out.println("[Paint] setStrokeWidth called with: " + width);
    }

    public void setTextSize(float textSize) {
        System.out.println("[Paint] setTextSize called with: " + textSize);
    }

    public void setAntiAlias(boolean antiAlias) {
        System.out.println("[Paint] setAntiAlias called with: " + antiAlias);
    }

    public void setStyle(Style style) {
        System.out.println("[Paint] setStyle called");
    }

    public int getColor() {
        System.out.println("[Paint] getColor called");
        return 0xFF000000;
    }

    public float getTextWidth(String text) {
        System.out.println("[Paint] getTextWidth called with: " + text);
        return text.length() * 10.0f;
    }

    public float measureText(String text) {
        System.out.println("[Paint] measureText called with: " + text);
        return text.length() * 10.0f;
    }

    public enum Style {
        FILL,
        STROKE,
        FILL_AND_STROKE
    }

    public enum Align {
        LEFT,
        CENTER,
        RIGHT
    }
}
