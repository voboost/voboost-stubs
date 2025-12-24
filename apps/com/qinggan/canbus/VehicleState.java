package com.qinggan.canbus;

public class VehicleState {
    public static final int HUM_VSP_FUNCTION_SW = 1;
    public static final int IVI_SOC_MODESET = 2;

    public int value;

    private VehicleState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
