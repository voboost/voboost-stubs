package com.qinggan.bluetoothphone;

import android.app.Activity;
import android.os.Bundle;

/**
 * Transparent BluetoothPhone activity that starts the
 * {@link BluetoothPhoneService} and finishes immediately. The activity only
 * exists so the app icon (and the system) can bring the foreground service up
 * on demand; the long-lived work is done by the service.
 */
public class BluetoothPhoneActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BluetoothPhoneService.start(this);
        finish();
    }
}
