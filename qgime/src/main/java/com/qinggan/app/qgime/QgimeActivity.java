package com.qinggan.app.qgime;

import android.app.Activity;
import android.os.Bundle;

/**
 * Transparent QG IME activity that starts the {@link QgimeService} and finishes
 * immediately. The activity only exists so the app icon (and the system) can
 * bring the foreground service up on demand; the long-lived work is done by the
 * service.
 */
public class QgimeActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QgimeService.start(this);
        finish();
    }
}
