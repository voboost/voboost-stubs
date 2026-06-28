package com.qinggan.canbus;

public class CanBusManager {
    private static CanBusManager instance;

    private CanBusManager() {
        System.out.println("[CanBusManager] CanBusManager constructor called");
    }

    public static CanBusManager getInstance() {
        System.out.println("[CanBusManager] getInstance called");
        try {
            if (instance == null) {
                System.out.println("[CanBusManager] Creating new CanBusManager instance");
                instance = new CanBusManager();
                System.out.println("[CanBusManager] CanBusManager instance created successfully");
            } else {
                System.out.println("[CanBusManager] Returning existing CanBusManager instance");
            }
            return instance;
        } catch (Exception e) {
            System.out.println("[CanBusManager] Error in getInstance: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public int getVehicleState(int stateId) {
        System.out.println("[CanBusManager] getVehicleState called with stateId: " + stateId);
        try {
            int result = 0;
            System.out.println("[CanBusManager] getVehicleState returning: " + result);
            return result;
        } catch (Exception e) {
            System.out.println("[CanBusManager] Error in getVehicleState for stateId: " + stateId + " - " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    public int setVehicleState(int stateId, int value) {
        System.out.println("[CanBusManager] setVehicleState called with stateId: " + stateId + ", value: " + value);
        try {
            int result = 0;
            System.out.println("[CanBusManager] setVehicleState returning: " + result);
            return result;
        } catch (Exception e) {
            System.out.println("[CanBusManager] Error in setVehicleState for stateId: " + stateId + ", value: " + value + " - " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
}
