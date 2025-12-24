package com.qingang.asgard.media.general.src;

import android.os.Handler;
import com.qinggan.media.helper.FieldWrapper;

public class SrcMediaActivity {
    public FieldWrapper handler;

    public SrcMediaActivity() {
        handler = new FieldWrapper(new Handler());
    }

    public void openPage(MediaResEnum mediaResEnum) {
        // Implementation stub
    }
}
