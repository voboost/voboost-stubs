package android.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Stub implementation of ImageView for testing
 */
public class ImageView extends View {

    private Drawable drawable;
    private int scaleType = 0;

    public ImageView(Context context) {
        super(context);
    }

    public ImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Set image drawable
     */
    public void setImageDrawable(Drawable drawable) {
        this.drawable = drawable;
        System.out.println("[ImageView] setImageDrawable called");
    }

    /**
     * Get image drawable
     */
    public Drawable getDrawable() {
        return drawable;
    }

    /**
     * Set image resource
     */
    public void setImageResource(int resId) {
        System.out.println("[ImageView] setImageResource called with: " + resId);
    }

    /**
     * Set scale type
     */
    public void setScaleType(int scaleType) {
        this.scaleType = scaleType;
        System.out.println("[ImageView] setScaleType called with: " + scaleType);
    }

    /**
     * Get scale type
     */
    public int getScaleType() {
        return scaleType;
    }

    // ScaleType constants
    public static final int SCALE_TYPE_CENTER = 0;
    public static final int SCALE_TYPE_CENTER_CROP = 1;
    public static final int SCALE_TYPE_CENTER_INSIDE = 2;
    public static final int SCALE_TYPE_FIT_CENTER = 3;
    public static final int SCALE_TYPE_FIT_END = 4;
    public static final int SCALE_TYPE_FIT_START = 5;
    public static final int SCALE_TYPE_FIT_XY = 6;
    public static final int SCALE_TYPE_MATRIX = 7;
}
