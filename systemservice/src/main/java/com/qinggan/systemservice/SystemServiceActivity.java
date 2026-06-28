package com.qinggan.systemservice;

import android.app.Activity;
import android.os.Bundle;

/**
 * Transparent SystemService activity that starts the
 * {@link SystemServiceService} and finishes immediately. The activity only
 * exists so the app icon (and the system) can bring the foreground service up
 * on demand; the long-lived work is done by the service.
 */
public class SystemServiceActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemServiceService.start(this);
        finish();
    }
}
