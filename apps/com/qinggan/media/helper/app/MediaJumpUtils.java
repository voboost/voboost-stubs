package com.qinggan.media.helper.app;

import android.content.Context;
import android.content.Intent;
import com.qinggan.media.helper.MediaEnum;

public class MediaJumpUtils {
    public static void starApp(Context context, Intent intent, int flags) {
        context.startActivity(intent);
    }

    public static Intent getStartIntent(MediaEnum mediaEnum) {
        // Stub implementation
        return null;
    }
}
