package com.qinggan.systemservice;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.List;

/**
 * Java stub application for com.qinggan.systemservice.
 * Provides realistic Java environment for Frida agent testing.
 *
 * <p>This application simulates various system service behaviors including:
 * <ul>
 *   <li>Multi-display management</li>
 *   <li>Vehicle settings</li>
 *   <li>CAN bus communication</li>
 * </ul>
 */
public class SystemServiceStub {
    // Constants
    private static final String PROCESS_NAME = "com.qinggan.systemservice";
    private static final String PROCESS_NAME_ENV_KEY = "PROCESS_NAME";
    private static final long MAIN_LOOP_SLEEP_MS = 1000L;
    private static final long DISPLAY_INITIAL_DELAY_SEC = 2L;
    private static final long DISPLAY_PERIOD_SEC = 10L;
    private static final long VEHICLE_INITIAL_DELAY_SEC = 4L;
    private static final long VEHICLE_PERIOD_SEC = 15L;
    private static final long CAN_INITIAL_DELAY_SEC = 6L;
    private static final long CAN_PERIOD_SEC = 8L;
    private static final double CHARGING_CHANGE_PROBABILITY = 0.7;
    private static final int MIN_BATTERY_LEVEL = 75;
    private static final int BATTERY_LEVEL_RANGE = 20;
    private static final int DEFAULT_IVI_SOC_MODESET = 1;
    private static final int DEFAULT_ENGINE_STATUS = 0;
    private static final int DEFAULT_BATTERY_LEVEL = 85;
    private static final int DEFAULT_CHARGING_STATUS = 0;
    private static final int CHARGING_STATUS_OFF = 0;
    private static final int CHARGING_STATUS_ON = 1;

    // Vehicle state keys
    private static final String KEY_IVI_SOC_MODESET = "IVI_SOC_MODESET";
    private static final String KEY_ENGINE_STATUS = "ENGINE_STATUS";
    private static final String KEY_BATTERY_LEVEL = "BATTERY_LEVEL";
    private static final String KEY_CHARGING_STATUS = "CHARGING_STATUS";

    // Display keys
    private static final String KEY_MAIN_DISPLAY = "main_display";
    private static final String KEY_PASSENGER_DISPLAY = "passenger_display";
    private static final String KEY_ACTIVE = "active";
    private static final String KEY_RESOLUTION = "resolution";
    private static final String RESOLUTION_MAIN = "1920x720";
    private static final String RESOLUTION_PASSENGER = "1280x720";

    // Test packages for multi-display simulation
    private static final String[] TEST_PACKAGES = {
        "com.android.settings",
        "ru.yandex.music",
        "com.spotify.music",
        "com.example.customapp"
    };

    private static final List<String> WHITELISTED_PACKAGES = Arrays.asList(
        "com.android.settings",
        "ru.yandex.music",
        "com.spotify.music"
    );

    // Thread-safe mock data for simulation
    private static final ConcurrentHashMap<String, Object> mockVehicleStates = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Object> mockDisplayStates = new ConcurrentHashMap<>();

    private static volatile boolean running = true;
    private static final Random random = new Random();

    /**
     * Main entry point for the SystemServiceStub application.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        logMessage("Starting Java stub application");

        try {
            setProcessName();
        } catch (SecurityException e) {
            logMessage("Security exception when setting process name: " + e.getMessage());
        } catch (Exception e) {
            logMessage("Unexpected error when setting process name: " + e.getMessage());
        }

        initializeMockData();
        startSimulations();

        logMessage("Ready for Frida injection");
        logMessage("Test with: frida -n " + PROCESS_NAME + " -l agent.js");

        runMainLoop();
    }

    /**
     * Sets the process name in the environment (works on some systems).
     *
     * @throws SecurityException if access to process environment is denied
     * @throws Exception if any other error occurs
     */
    private static void setProcessName() throws SecurityException, Exception {
        ProcessBuilder processBuilder = new ProcessBuilder();
        java.util.Map<String, String> environment = processBuilder.environment();
        environment.put(PROCESS_NAME_ENV_KEY, PROCESS_NAME);
    }

    /**
     * Runs the main application loop, keeping the process alive.
     */
    private static void runMainLoop() {
        try {
            while (running) {
                Thread.sleep(MAIN_LOOP_SLEEP_MS);
            }
        } catch (InterruptedException e) {
            logMessage("Interrupted, shutting down");
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Initializes mock data for vehicle and display states.
     */
    private static void initializeMockData() {
        // Initialize vehicle states
        mockVehicleStates.put(KEY_IVI_SOC_MODESET, DEFAULT_IVI_SOC_MODESET);
        mockVehicleStates.put(KEY_ENGINE_STATUS, DEFAULT_ENGINE_STATUS);
        mockVehicleStates.put(KEY_BATTERY_LEVEL, DEFAULT_BATTERY_LEVEL);
        mockVehicleStates.put(KEY_CHARGING_STATUS, DEFAULT_CHARGING_STATUS);

        // Initialize display states
        ConcurrentHashMap<String, Object> mainDisplayState = new ConcurrentHashMap<>();
        mainDisplayState.put(KEY_ACTIVE, true);
        mainDisplayState.put(KEY_RESOLUTION, RESOLUTION_MAIN);
        mockDisplayStates.put(KEY_MAIN_DISPLAY, mainDisplayState);

        ConcurrentHashMap<String, Object> passengerDisplayState = new ConcurrentHashMap<>();
        passengerDisplayState.put(KEY_ACTIVE, false);
        passengerDisplayState.put(KEY_RESOLUTION, RESOLUTION_PASSENGER);
        mockDisplayStates.put(KEY_PASSENGER_DISPLAY, passengerDisplayState);

        logMessage("Mock data initialized");
    }

    /**
     * Starts all system service simulations.
     */
    private static void startSimulations() {
        startMultiDisplaySimulation();
        startVehicleSettingsSimulation();
        startCanBusSimulation();
    }

    /**
     * Starts the multi-display simulation executor.
     */
    private static void startMultiDisplaySimulation() {
        var displayExecutor = Executors.newSingleThreadScheduledExecutor();
        displayExecutor.scheduleAtFixedRate(
            SystemServiceStub::simulateMultiDisplay,
            DISPLAY_INITIAL_DELAY_SEC,
            DISPLAY_PERIOD_SEC,
            TimeUnit.SECONDS
        );
    }

    /**
     * Starts the vehicle settings simulation executor.
     */
    private static void startVehicleSettingsSimulation() {
        var vehicleExecutor = Executors.newSingleThreadScheduledExecutor();
        vehicleExecutor.scheduleAtFixedRate(
            SystemServiceStub::simulateVehicleSettings,
            VEHICLE_INITIAL_DELAY_SEC,
            VEHICLE_PERIOD_SEC,
            TimeUnit.SECONDS
        );
    }

    /**
     * Starts the CAN bus simulation executor.
     */
    private static void startCanBusSimulation() {
        var canExecutor = Executors.newSingleThreadScheduledExecutor();
        canExecutor.scheduleAtFixedRate(
            SystemServiceStub::simulateCanBus,
            CAN_INITIAL_DELAY_SEC,
            CAN_PERIOD_SEC,
            TimeUnit.SECONDS
        );
    }

    /**
     * Simulates multi-display package checking.
     */
    private static void simulateMultiDisplay() {
        try {
            String packageName = TEST_PACKAGES[random.nextInt(TEST_PACKAGES.length)];
            boolean isWhitelisted = WHITELISTED_PACKAGES.contains(packageName);
            logMessage("Multi-display check: " + packageName + " -> " + isWhitelisted);
        } catch (ArrayIndexOutOfBoundsException e) {
            logMessage("Multi-display simulation error: Invalid package array access");
        } catch (Exception e) {
            logMessage("Multi-display simulation error: " + e.getMessage());
        }
    }

    /**
     * Simulates vehicle settings activity creation.
     */
    private static void simulateVehicleSettings() {
        try {
            logMessage("Simulated vehicle settings activity creation");
        } catch (Exception e) {
            logMessage("Vehicle setting simulation error: " + e.getMessage());
        }
    }

    /**
     * Simulates CAN bus activity including battery level and charging status changes.
     */
    private static void simulateCanBus() {
        try {
            updateBatteryLevel();
            updateChargingStatus();
            int currentBatteryLevel = (Integer) mockVehicleStates.get(KEY_BATTERY_LEVEL);
            logMessage("CAN bus activity - Battery: " + currentBatteryLevel + "%");
        } catch (ClassCastException e) {
            logMessage("CAN bus simulation error: Invalid type for battery level");
        } catch (NullPointerException e) {
            logMessage("CAN bus simulation error: Missing battery level data");
        } catch (Exception e) {
            logMessage("CAN bus simulation error: " + e.getMessage());
        }
    }

    /**
     * Updates the battery level with a random value.
     */
    private static void updateBatteryLevel() {
        int batteryLevel = MIN_BATTERY_LEVEL + random.nextInt(BATTERY_LEVEL_RANGE);
        mockVehicleStates.put(KEY_BATTERY_LEVEL, batteryLevel);
    }

    /**
     * Randomly updates the charging status based on probability.
     */
    private static void updateChargingStatus() {
        if (random.nextDouble() > CHARGING_CHANGE_PROBABILITY) {
            Integer currentStatus = (Integer) mockVehicleStates.get(KEY_CHARGING_STATUS);
            if (currentStatus != null) {
                int newChargingStatus = (currentStatus == CHARGING_STATUS_OFF) ?
                    CHARGING_STATUS_ON : CHARGING_STATUS_OFF;
                mockVehicleStates.put(KEY_CHARGING_STATUS, newChargingStatus);
                logMessage("Charging status changed: " + newChargingStatus);
            }
        }
    }

    /**
     * Logs a message with the process name prefix.
     *
     * @param message the message to log
     */
    private static void logMessage(String message) {
        System.out.println("[" + PROCESS_NAME + "] " + message);
    }

    // Static initialization block for shutdown hook
    static {
        Runtime.getRuntime().addShutdownHook(
            new Thread(() -> {
                running = false;
                logMessage("Shutdown hook called");
            }, "ShutdownHook")
        );
    }
}
