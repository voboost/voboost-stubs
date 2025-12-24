package com.qinggan.mainlauncher.navigation;

import android.view.View;

public class NavigationBar {
    public FieldWrapper<Integer> mScreenId = new FieldWrapper<>(0);
    public FieldWrapper<View> mScreenUpItemView1 = new FieldWrapper<>(null);
    public FieldWrapper<View> mScreenUpItemView2 = new FieldWrapper<>(null);

    public void updateTheme() {
        // Mock implementation
    }

    public static class FieldWrapper<T> {
        public T value;

        public FieldWrapper(T value) {
            this.value = value;
        }
    }
}
