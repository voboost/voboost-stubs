package com.pateo.material.dialog;

import android.content.Context;

public class QGToast {
    private static QGToast instance;
    private Context context;
    private CharSequence message;
    private int duration;

    private QGToast(Context context, CharSequence message, int duration) {
        this.context = context;
        this.message = message;
        this.duration = duration;
    }

    public static QGToast makeText(Context context, CharSequence message, int duration) {
        return new QGToast(context, message, duration);
    }

    public void show() {
        // Mock implementation
        System.out.println("Toast: " + message);
    }
}
