package com.qinggan.app.vehiclesetting;

import android.app.Activity;
import android.os.Bundle;

/**
 * Transparent VehicleSetting activity that starts the
 * {@link VehicleSettingService} and finishes immediately. The activity only
 * exists so the app icon (and the system) can bring the foreground service up
 * on demand; the long-lived work is done by the service.
 */
public class VehicleSettingActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VehicleSettingService.start(this);
        finish();
    }
}
