package android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Stub implementation of LinearLayout for testing
 */
public class LinearLayout extends ViewGroup {

    public LinearLayout(Context context) {
        super(context);
    }

    public LinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * LayoutParams class for LinearLayout
     */
    public static class LayoutParams extends ViewGroup.LayoutParams {

        public float weight = 0.0f;
        public int gravity = -1;

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, float weight) {
            super(width, height);
            this.weight = weight;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(LayoutParams source) {
            super(source);
            this.weight = source.weight;
            this.gravity = source.gravity;
        }
    }

    // Orientation constants
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
}
