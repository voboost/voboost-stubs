package com.qinggan.canbus;

public class CanBusManager {
    private static CanBusManager instance;

    private CanBusManager() {}

    public static CanBusManager getInstance() {
        if (instance == null) {
            instance = new CanBusManager();
        }
        return instance;
    }

    public int getVehicleState(int stateId) {
        return 0;
    }

    public int setVehicleState(int stateId, int value) {
        return 0;
    }
}
