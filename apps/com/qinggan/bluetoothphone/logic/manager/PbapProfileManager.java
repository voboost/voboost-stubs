package com.qinggan.bluetoothphone.logic.manager;

public class PbapProfileManager {
    private static PbapProfileManager instance;

    private PbapProfileManager() {}

    public static PbapProfileManager getInstance() {
        if (instance == null) {
            instance = new PbapProfileManager();
        }
        return instance;
    }

    public static void startSync() {
        // Implementation stub
    }
}
