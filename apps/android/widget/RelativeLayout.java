package android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Stub implementation of RelativeLayout for testing
 */
public class RelativeLayout extends ViewGroup {

    public RelativeLayout(Context context) {
        super(context);
    }

    public RelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * LayoutParams class for RelativeLayout
     */
    public static class LayoutParams extends ViewGroup.LayoutParams {

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(LayoutParams source) {
            super(source);
        }

        /**
         * Add rule
         */
        public void addRule(int verb) {
            System.out.println("[RelativeLayout.LayoutParams] addRule called with: " + verb);
        }

        /**
         * Add rule with anchor
         */
        public void addRule(int verb, int anchor) {
            System.out.println("[RelativeLayout.LayoutParams] addRule called with: " + verb + ", anchor: " + anchor);
        }

        // RelativeLayout rules
        public static final int TRUE = -1;
        public static final int LEFT_OF = 0;
        public static final int RIGHT_OF = 1;
        public static final int ABOVE = 2;
        public static final int BELOW = 3;
        public static final int ALIGN_LEFT = 5;
        public static final int ALIGN_TOP = 6;
        public static final int ALIGN_RIGHT = 7;
        public static final int ALIGN_BOTTOM = 8;
        public static final int ALIGN_BASELINE = 9;
        public static final int ALIGN_PARENT_LEFT = 9;
        public static final int ALIGN_PARENT_TOP = 10;
        public static final int ALIGN_PARENT_RIGHT = 11;
        public static final int ALIGN_PARENT_BOTTOM = 12;
        public static final int CENTER_IN_PARENT = 13;
        public static final int CENTER_HORIZONTAL = 14;
        public static final int CENTER_VERTICAL = 15;
    }
}
