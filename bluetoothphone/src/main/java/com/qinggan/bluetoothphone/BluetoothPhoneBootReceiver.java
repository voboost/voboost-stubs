package com.qinggan.bluetoothphone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Receives {@link Intent#ACTION_BOOT_COMPLETED} and starts the
 * {@link BluetoothPhoneService} so the BluetoothPhone process comes back up
 * after a device reboot. This keeps the voboost-inject daemon's spawn-gate
 * target available across reboots.
 */
public class BluetoothPhoneBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            BluetoothPhoneService.start(context);
        }
    }
}
