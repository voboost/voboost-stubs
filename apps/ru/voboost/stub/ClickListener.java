package ru.voboost.stub;

import android.view.View;

/**
 * Unified click listener implementation for Voboost agents
 * Used by voboost-to-menu-mod and app-launcher-mod agents
 */
public class ClickListener implements View.OnClickListener {
    @Override
    public void onClick(View view) {
        // Stub implementation - actual logic is in Frida agent
        System.out.println("[ClickListener] onClick called with view: " + view);
    }
}
