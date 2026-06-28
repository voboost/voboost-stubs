package com.qinggan.app.vehiclesetting;

import android.os.Bundle;

public class CarSettingActivity {
    public CarSettingActivityBinding carSettingBinding;

    public void onCreate(Bundle savedInstanceState) {
        // Initialize binding
        carSettingBinding = new CarSettingActivityBinding();
    }

    public static class CarSettingActivityBinding {
        public ValueWrapper menuContainer;
        public ValueWrapper mainMenuItemSystemSetting;

        public CarSettingActivityBinding() {
            menuContainer = new ValueWrapper();
            mainMenuItemSystemSetting = new ValueWrapper();
        }
    }

    public static class ValueWrapper {
        public Object value;

        public ValueWrapper() {
            this.value = new Object();
        }
    }
}
