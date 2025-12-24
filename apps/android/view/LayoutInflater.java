package android.view;

import android.content.Context;

/**
 * Mock LayoutInflater class for Frida agent testing
 */
public class LayoutInflater {
    private Context context;

    protected LayoutInflater(Context context) {
        this.context = context;
        System.out.println("[LayoutInflater] LayoutInflater created");
    }

    public static LayoutInflater from(Context context) {
        System.out.println("[LayoutInflater] from called");
        return new LayoutInflater(context);
    }

    public View inflate(int resource, ViewGroup root) {
        System.out.println("[LayoutInflater] inflate called with resource: " + resource + ", root: " + root);
        return new View(context);
    }

    public View inflate(int resource, ViewGroup root, boolean attachToRoot) {
        System.out.println("[LayoutInflater] inflate called with resource: " + resource + ", root: " + root + ", attachToRoot: " + attachToRoot);
        return new View(context);
    }
}
