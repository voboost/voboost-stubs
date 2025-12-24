package com.qinggan.launcher.base.adapter;

import java.util.ArrayList;

public class AllAppAdapter {
    public FieldWrapper<ArrayList<Object>> mAppBeans = new FieldWrapper<>(null);

    public void onBindViewHolder(AppViewHolder viewHolder, int position) {
        // Mock implementation
    }

    public static class AppViewHolder {
        public FieldWrapper<Object> iconView = new FieldWrapper<>(null);
        public FieldWrapper<Object> nameView = new FieldWrapper<>(null);
        public FieldWrapper<Object> itemView = new FieldWrapper<>(null);
    }

    public static class FieldWrapper<T> {
        public T value;

        public FieldWrapper(T value) {
            this.value = value;
        }
    }
}
