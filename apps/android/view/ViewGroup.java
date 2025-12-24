package android.view;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Mock ViewGroup class for Frida agent testing
 */
public class ViewGroup extends View {
    public ViewGroup(Context context) {
        super(context);
        System.out.println("[ViewGroup] ViewGroup created");
    }
    
    public ViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        System.out.println("[ViewGroup] ViewGroup created with attrs");
    }
    
    public ViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        System.out.println("[ViewGroup] ViewGroup created with attrs and defStyleAttr");
    }

    public static class LayoutParams {
        public int width;
        public int height;

        public LayoutParams(int width, int height) {
            this.width = width;
            this.height = height;
            System.out.println("[ViewGroup.LayoutParams] LayoutParams created with width: " + width + ", height: " + height);
        }
        
        public LayoutParams(LayoutParams source) {
            this.width = source.width;
            this.height = source.height;
            System.out.println("[ViewGroup.LayoutParams] LayoutParams created from source");
        }
    }

    public void addView(View child) {
        System.out.println("[ViewGroup] addView called");
    }

    public void addView(View child, int index) {
        System.out.println("[ViewGroup] addView called with index: " + index);
    }

    public void addView(View child, LayoutParams params) {
        System.out.println("[ViewGroup] addView called with LayoutParams");
    }

    public void removeView(View view) {
        System.out.println("[ViewGroup] removeView called");
    }

    public int getChildCount() {
        System.out.println("[ViewGroup] getChildCount called");
        return 0;
    }

    public View getChildAt(int index) {
        System.out.println("[ViewGroup] getChildAt called with index: " + index);
        return null;
    }
}
