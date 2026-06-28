package com.qinggan.app.launcher;

import android.app.Activity;
import android.os.Bundle;

/**
 * Transparent launcher activity that starts the {@link LauncherService} and
 * finishes immediately. The activity only exists so the launcher icon (and the
 * system) can bring the foreground service up on demand; the long-lived work is
 * done by the service.
 */
public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LauncherService.start(this);
        finish();
    }
}
