package com.qinggan.app.launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Receives {@link Intent#ACTION_BOOT_COMPLETED} and starts the
 * {@link LauncherService} so the launcher process comes back up after a device
 * reboot. This keeps the voboost-inject daemon's spawn-gate target available
 * across reboots.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            LauncherService.start(context);
        }
    }
}
