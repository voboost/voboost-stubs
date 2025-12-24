package android.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

/**
 * Stub implementation of TextView for testing
 */
public class TextView extends View {

    private CharSequence text;
    private float textSize = 14.0f;
    private int textColor = 0xFF000000;
    private int gravity = 0;
    private int maxWidth = Integer.MAX_VALUE;
    private Typeface typeface;

    public TextView(Context context) {
        super(context);
        this.typeface = Typeface.DEFAULT;
    }

    public TextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.typeface = Typeface.DEFAULT;
    }

    public TextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.typeface = Typeface.DEFAULT;
    }

    /**
     * Set text
     */
    public void setText(CharSequence text) {
        this.text = text;
        System.out.println("[TextView] setText called with: " + text);
    }

    /**
     * Get text
     */
    public CharSequence getText() {
        return text;
    }

    /**
     * Set text size
     */
    public void setTextSize(int unit, float size) {
        this.textSize = size;
        System.out.println("[TextView] setTextSize called with: " + size);
    }

    /**
     * Set text size (pixels)
     */
    public void setTextSize(float size) {
        this.textSize = size;
        System.out.println("[TextView] setTextSize called with: " + size);
    }

    /**
     * Get text size
     */
    public float getTextSize() {
        return textSize;
    }

    /**
     * Set text color
     */
    public void setTextColor(int color) {
        this.textColor = color;
        System.out.println("[TextView] setTextColor called with: " + color);
    }

    /**
     * Set text color (ColorStateList)
     */
    public void setTextColor(Object colors) {
        System.out.println("[TextView] setTextColor called with ColorStateList: " + colors);
    }

    /**
     * Get text colors
     */
    public Object getTextColors() {
        return null; // Stub ColorStateList
    }

    /**
     * Set gravity
     */
    public void setGravity(int gravity) {
        this.gravity = gravity;
        System.out.println("[TextView] setGravity called with: " + gravity);
    }

    /**
     * Get gravity
     */
    public int getGravity() {
        return gravity;
    }

    /**
     * Set max width
     */
    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
        System.out.println("[TextView] setMaxWidth called with: " + maxWidth);
    }

    /**
     * Get max width
     */
    public int getMaxWidth() {
        return maxWidth;
    }

    /**
     * Set typeface
     */
    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
        System.out.println("[TextView] setTypeface called with: " + typeface);
    }

    /**
     * Get typeface
     */
    public Typeface getTypeface() {
        return typeface;
    }

    // Gravity constants
    public static final int GRAVITY_LEFT = 0x03;
    public static final int GRAVITY_RIGHT = 0x05;
    public static final int GRAVITY_CENTER = 0x11;
    public static final int GRAVITY_CENTER_VERTICAL = 0x10;
    public static final int GRAVITY_CENTER_HORIZONTAL = 0x01;
}
