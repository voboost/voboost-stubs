package android.view;

/**
 * Mock View class for Frida agent testing
 */
public class View {
    // Basic view properties
    public int id = 0;
    public int width = 100;
    public int height = 100;
    public float x = 0.0f;
    public float y = 0.0f;
    public float alpha = 1.0f;
    public boolean visible = true;
    public boolean enabled = true;

    // Layout parameters
    public Object layoutParams = null;

    // Parent and children
    public View parent = null;

    // Tag and click listener
    public Object tag = null;
    public OnClickListener clickListener = null;

    /**
     * Default constructor
     */
    public View() {
        System.out.println("[View] View created");
    }

    /**
     * Constructor with context
     */
    public View(Object context) {
        System.out.println("[View] View created with context");
    }

    /**
     * Constructor with context and AttributeSet
     */
    public View(Object context, Object attrs) {
        System.out.println("[View] View created with context and attrs");
    }

    /**
     * Constructor with context, AttributeSet and defStyleAttr
     */
    public View(Object context, Object attrs, int defStyleAttr) {
        System.out.println("[View] View created with context, attrs and defStyleAttr");
    }

    /**
     * Sets the view visibility
     */
    public void setVisibility(int visibility) {
        System.out.println("[View] setVisibility called with: " + visibility);
        this.visible = (visibility == VISIBLE);
    }

    /**
     * Gets the view visibility
     */
    public int getVisibility() {
        return visible ? VISIBLE : GONE;
    }

    /**
     * Sets the view alpha
     */
    public void setAlpha(float alpha) {
        System.out.println("[View] setAlpha called with: " + alpha);
        this.alpha = alpha;
    }

    /**
     * Gets the view alpha
     */
    public float getAlpha() {
        return alpha;
    }

    /**
     * Sets the view position
     */
    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    /**
     * Invalidates the view
     */
    public void invalidate() {
        System.out.println("[View] invalidate called");
    }

    /**
     * Requests a layout pass
     */
    public void requestLayout() {
        System.out.println("[View] requestLayout called");
    }

    /**
     * Sets the tag for this view
     */
    public void setTag(Object tag) {
        System.out.println("[View] setTag called with: " + tag);
        this.tag = tag;
    }

    /**
     * Gets the tag for this view
     */
    public Object getTag() {
        System.out.println("[View] getTag called, returning: " + tag);
        return tag;
    }

    /**
     * Sets the click listener for this view
     */
    public void setOnClickListener(OnClickListener listener) {
        System.out.println("[View] setOnClickListener called with: " + listener);
        this.clickListener = listener;
    }

    /**
     * Sets the background drawable for this view
     */
    public void setBackground(Object drawable) {
        System.out.println("[View] setBackground called with: " + drawable);
    }

    /**
     * OnClickListener interface - nested class
     */
    public interface OnClickListener {
        void onClick(View v);
    }

    /**
     * Simulates a click on this view
     */
    public void performClick() {
        System.out.println("[View] performClick called");
        if (clickListener != null) {
            clickListener.onClick(this);
        }
    }

    // Visibility constants
    public static final int VISIBLE = 0;
    public static final int INVISIBLE = 1;
    public static final int GONE = 2;
}
