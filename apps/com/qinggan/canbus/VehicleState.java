package com.qinggan.canbus;

public class VehicleState {
    public static final int HUM_VSP_FUNCTION_SW = 1;
    public static final int IVI_SOC_MODESET = 2;

    public int value;

    static {
        System.out.println("[VehicleState] VehicleState class loaded");
        System.out.println("[VehicleState] HUM_VSP_FUNCTION_SW = " + HUM_VSP_FUNCTION_SW);
        System.out.println("[VehicleState] IVI_SOC_MODESET = " + IVI_SOC_MODESET);
    }

    private VehicleState(int value) {
        System.out.println("[VehicleState] VehicleState constructor called with value: " + value);
        try {
            this.value = value;
            System.out.println("[VehicleState] VehicleState initialized successfully with value: " + value);
        } catch (Exception e) {
            System.out.println("[VehicleState] Error initializing VehicleState with value: " + value + " - " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int getValue() {
        System.out.println("[VehicleState] getValue called, returning: " + value);
        try {
            return value;
        } catch (Exception e) {
            System.out.println("[VehicleState] Error in getValue: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
}
