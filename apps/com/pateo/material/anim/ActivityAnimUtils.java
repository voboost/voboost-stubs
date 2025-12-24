package com.pateo.material.anim;

import android.content.Context;
import android.content.Intent;

public class ActivityAnimUtils {
    public static ActivityAnimUtils startActivityByAnim;

    public void startActivityByAnim(Context context, Intent intent) {
        context.startActivity(intent);
    }
}
