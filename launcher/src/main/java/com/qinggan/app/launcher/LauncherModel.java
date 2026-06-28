package com.qinggan.app.launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.qinggan.media.helper.FieldWrapper;

public class LauncherModel extends BroadcastReceiver {
    public FieldWrapper mContext;

    public LauncherModel() {
        mContext = new FieldWrapper(null);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Implementation stub
    }

    public void handleUpdateMainNavigationBar(String packageName, String appName, boolean show) {
        // Implementation stub
    }

    public void handleUpdateSecondNavigationBar(String packageName, String appName, boolean show) {
        // Implementation stub
    }
}
