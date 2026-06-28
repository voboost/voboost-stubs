package com.qinggan.common.widget;

import android.content.Context;
import android.view.View;

/**
 * Mock CustomTab class for Frida agent testing
 */
public class CustomTab extends View {
    private String text;
    private int icon;
    private boolean selected;

    public CustomTab(Context context) {
        super(context);
        System.out.println("[CustomTab] CustomTab created");
        this.text = "";
        this.icon = 0;
        this.selected = false;
    }

    public CustomTab(Context context, String text, int icon) {
        super(context);
        System.out.println("[CustomTab] CustomTab created with text: " + text + ", icon: " + icon);
        this.text = text;
        this.icon = icon;
        this.selected = false;
    }

    public String getText() {
        System.out.println("[CustomTab] getText called, returning: " + text);
        return text;
    }

    public void setText(String text) {
        System.out.println("[CustomTab] setText called with: " + text);
        this.text = text;
    }

    public int getIcon() {
        System.out.println("[CustomTab] getIcon called, returning: " + icon);
        return icon;
    }

    public void setIcon(int icon) {
        System.out.println("[CustomTab] setIcon called with: " + icon);
        this.icon = icon;
    }

    public boolean isSelected() {
        System.out.println("[CustomTab] isSelected called, returning: " + selected);
        return selected;
    }

    public void setSelected(boolean selected) {
        System.out.println("[CustomTab] setSelected called with: " + selected);
        this.selected = selected;
    }

    @Override
    public boolean performClick() {
        System.out.println("[CustomTab] performClick called");
        this.selected = !this.selected;
        return true;
    }
}
