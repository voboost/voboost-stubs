package com.qinggan.app.phone;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

/**
 * Java stub application for com.qinggan.app.phone.
 *
 * <p>This class provides a realistic Java environment for Frida agent testing by simulating
 * Bluetooth phone functionality including device management, call operations,
 * and connection handling.
 *
 * <p>The application runs continuously, simulating various Bluetooth phone operations at
 * different intervals to provide a realistic testing environment for dynamic instrumentation.
 */
public class BluetoothPhoneStub {
    // Process and application constants
    private static final String PROCESS_NAME = "com.qinggan.app.phone";
    private static final String FRIDA_TEST_COMMAND = "frida -n " + PROCESS_NAME + " -l agent.js";

    // Timing constants (in seconds)
    private static final long MAIN_LOOP_SLEEP_MS = 1000L;
    private static final long BLUETOOTH_INITIAL_DELAY_SEC = 2L;
    private static final long BLUETOOTH_INTERVAL_SEC = 10L;
    private static final long CALL_INITIAL_DELAY_SEC = 4L;
    private static final long CALL_INTERVAL_SEC = 8L;

    // Bluetooth device states
    private static final String[] DEVICE_STATES = {
        "Connected",
        "Disconnected",
        "Connecting",
        "Pairing",
        "Error"
    };

    // Call states
    private static final String[] CALL_STATES = {
        "Idle",
        "Incoming",
        "Outgoing",
        "Active",
        "Held",
        "Ended"
    };

    // Thread pool names
    private static final String BLUETOOTH_THREAD_NAME = "BluetoothManager";
    private static final String CALL_THREAD_NAME = "CallManager";

    // Runtime state
    private static final AtomicBoolean isRunning = new AtomicBoolean(true);
    private static final Random randomGenerator = new Random();

    // Executor services for scheduled tasks
    private static ScheduledExecutorService bluetoothExecutor;
    private static ScheduledExecutorService callExecutor;

    // Bluetooth phone components
    private static Context mockContext;
    private static List<BluetoothDevice> connectedDevices;
    private static Map<String, CallState> activeCalls;

    /**
     * Main entry point for the Bluetooth Phone stub application.
     *
     * <p>Initializes the process environment, starts simulation threads, and keeps the
     * application running until interrupted. The application simulates various Bluetooth
     * phone operations to provide a realistic testing environment.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        logStartup();

        try {
            initializeProcessEnvironment();
            initializeAndroidEnvironment();
            initializeBluetoothComponents();
            startSimulations();
            logReadyState();

            runMainLoop();
        } catch (Exception e) {
            logError("Unexpected error in main loop", e);
        } finally {
            shutdown();
        }
    }

    /**
     * Initializes the process environment by setting environment variables.
     *
     * <p>Sets the PROCESS_NAME environment variable which may be used by the system
     * or monitoring tools to identify this process.
     *
     * @throws SecurityException if access to the process environment is denied
     */
    private static void initializeProcessEnvironment() throws SecurityException {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            java.util.Map<String, String> environment = processBuilder.environment();
            environment.put("PROCESS_NAME", PROCESS_NAME);
        } catch (SecurityException e) {
            logError("Failed to set process environment", e);
            throw e;
        }
    }

    /**
     * Initializes mock Android environment for Frida agent compatibility.
     *
     * <p>Sets up mock Android classes and context that Frida agents may expect
     * to find when hooking into Android applications.
     */
    private static void initializeAndroidEnvironment() {
        try {
            // Initialize mock Android environment
            System.out.println("[" + PROCESS_NAME + "] Initializing Android environment");

            // Create mock application context
            android.app.ActivityThread.Application mockApplication = new android.app.ActivityThread.Application();
            mockContext = mockApplication;

            // Set up mock ActivityThread
            android.app.ActivityThread.setCurrentApplication(mockApplication);

            System.out.println("[" + PROCESS_NAME + "] Android environment initialized successfully");
        } catch (Exception e) {
            logWarning("Failed to initialize Android environment: " + e.getMessage());
            // Continue without Android environment - some agents may still work
        }
    }

    /**
     * Initializes Bluetooth phone components for the application.
     *
     * <p>Creates the device list and call map and populates them with initial data
     * to provide a realistic testing environment.
     */
    private static void initializeBluetoothComponents() {
        try {
            System.out.println("[" + PROCESS_NAME + "] Initializing Bluetooth phone components");

            // Initialize components
            connectedDevices = new ArrayList<>();
            activeCalls = new HashMap<>();

            // Populate with initial data
            populateInitialBluetoothData();

            System.out.println("[" + PROCESS_NAME + "] Bluetooth phone components initialized successfully");
        } catch (Exception e) {
            logError("Failed to initialize Bluetooth phone components", e);
        }
    }

    /**
     * Populates the Bluetooth components with initial data.
     *
     * <p>Creates sample Bluetooth devices and call states to simulate
     * a realistic Bluetooth phone environment.
     */
    private static void populateInitialBluetoothData() {
        try {
            // Create sample Bluetooth devices
            for (int i = 0; i < 3; i++) {
                BluetoothDevice device = new BluetoothDevice();
                device.name = "Device " + i;
                device.address = "00:11:22:33:44:5" + i;
                device.state = DEVICE_STATES[i % DEVICE_STATES.length];
                device.batteryLevel = 50 + (i * 20);
                connectedDevices.add(device);
            }

            // Create sample call states
            for (int i = 0; i < 2; i++) {
                CallState callState = new CallState();
                callState.phoneNumber = "+123456789" + i;
                callState.contactName = "Contact " + i;
                callState.state = CALL_STATES[i + 1]; // Skip "Idle"
                callState.duration = i * 60; // seconds
                activeCalls.put("call_" + i, callState);
            }

            System.out.println("[" + PROCESS_NAME + "] Initial Bluetooth data populated");
        } catch (Exception e) {
            logError("Failed to populate initial Bluetooth data", e);
        }
    }

    /**
     * Starts all simulation threads for Bluetooth phone operations.
     *
     * <p>Creates and starts two separate scheduled executor services:
     * <ul>
     *   <li>Bluetooth manager simulation</li>
     *   <li>Call manager simulation</li>
     * </ul>
     *
     * Each simulation runs at different intervals to provide realistic timing behavior.
     */
    private static void startSimulations() {
        bluetoothExecutor = createScheduledExecutor(BLUETOOTH_THREAD_NAME);
        callExecutor = createScheduledExecutor(CALL_THREAD_NAME);

        scheduleBluetoothSimulation();
        scheduleCallSimulation();
    }

    /**
     * Creates a scheduled executor service with a custom thread factory.
     *
     * @param threadName the name to assign to threads created by this executor
     * @return a new single-thread scheduled executor service
     */
    private static ScheduledExecutorService createScheduledExecutor(String threadName) {
        ThreadFactory threadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable, threadName);
                thread.setDaemon(true);
                return thread;
            }
        };
        return Executors.newSingleThreadScheduledExecutor(threadFactory);
    }

    /**
     * Schedules the Bluetooth manager simulation.
     *
     * <p>The Bluetooth simulation runs every 10 seconds after an initial 2-second delay,
     * simulating device connection and management operations.
     */
    private static void scheduleBluetoothSimulation() {
        bluetoothExecutor.scheduleAtFixedRate(
            BluetoothPhoneStub::simulateBluetoothManager,
            BLUETOOTH_INITIAL_DELAY_SEC,
            BLUETOOTH_INTERVAL_SEC,
            TimeUnit.SECONDS
        );
    }

    /**
     * Schedules the call manager simulation.
     *
     * <p>The call simulation runs every 8 seconds after an initial 4-second delay,
     * simulating call operations and state changes.
     */
    private static void scheduleCallSimulation() {
        callExecutor.scheduleAtFixedRate(
            BluetoothPhoneStub::simulateCallManager,
            CALL_INITIAL_DELAY_SEC,
            CALL_INTERVAL_SEC,
            TimeUnit.SECONDS
        );
    }

    /**
     * Simulates the Bluetooth manager operations.
     *
     * <p>This method simulates the typical Bluetooth manager operations
     * that would be called in a real Bluetooth phone application.
     */
    private static void simulateBluetoothManager() {
        try {
            logInfo("Bluetooth manager operation: " + connectedDevices.size() + " devices connected");

            // Simulate Bluetooth operations
            if (randomGenerator.nextBoolean() && !connectedDevices.isEmpty()) {
                BluetoothDevice device = connectedDevices.get(randomGenerator.nextInt(connectedDevices.size()));
                String newState = DEVICE_STATES[randomGenerator.nextInt(DEVICE_STATES.length)];
                device.state = newState;
                logInfo("Device state updated: " + device.name + " -> " + newState);
            } else {
                logInfo("Bluetooth scan would be performed");
            }
        } catch (Exception e) {
            logError("Bluetooth manager simulation failed", e);
        }
    }

    /**
     * Simulates the call manager operations.
     *
     * <p>This method simulates call operations including incoming calls,
     * outgoing calls, and call state changes.
     */
    private static void simulateCallManager() {
        try {
            logInfo("Call manager operation: " + activeCalls.size() + " active calls");

            // Simulate call operations
            if (randomGenerator.nextBoolean()) {
                if (activeCalls.isEmpty()) {
                    // Simulate incoming call
                    CallState newCall = new CallState();
                    newCall.phoneNumber = "+9876543210";
                    newCall.contactName = "New Contact";
                    newCall.state = "Incoming";
                    newCall.duration = 0;
                    activeCalls.put("call_new", newCall);
                    logInfo("Incoming call simulated: " + newCall.contactName);
                } else {
                    // Update existing call state
                    String callId = new ArrayList<>(activeCalls.keySet()).get(0);
                    CallState call = activeCalls.get(callId);
                    String newState = CALL_STATES[randomGenerator.nextInt(CALL_STATES.length)];
                    call.state = newState;
                    call.duration += randomGenerator.nextInt(30);
                    logInfo("Call state updated: " + call.contactName + " -> " + newState);
                }
            } else {
                logInfo("Call history would be updated");
            }
        } catch (Exception e) {
            logError("Call manager simulation failed", e);
        }
    }

    /**
     * Runs the main application loop, keeping the process alive.
     *
     * <p>This method sleeps in a loop until the application is interrupted or shutdown
     * is requested. The sleep interval allows the application to remain responsive while
     * consuming minimal resources.
     *
     * @throws InterruptedException if the thread is interrupted during sleep
     */
    private static void runMainLoop() throws InterruptedException {
        while (isRunning.get()) {
            Thread.sleep(MAIN_LOOP_SLEEP_MS);
        }
    }

    /**
     * Gracefully shuts down all executor services and cleans up resources.
     */
    private static void shutdown() {
        logInfo("Shutting down Bluetooth Phone stub application");

        shutdownExecutorService(bluetoothExecutor, BLUETOOTH_THREAD_NAME);
        shutdownExecutorService(callExecutor, CALL_THREAD_NAME);

        logInfo("Shutdown completed");
    }

    /**
     * Safely shuts down a scheduled executor service.
     *
     * @param executor the executor service to shutdown
     * @param executorName the name of the executor for logging purposes
     */
    private static void shutdownExecutorService(
        ScheduledExecutorService executor,
        String executorName
    ) {
        if (executor != null && !executor.isShutdown()) {
            try {
                executor.shutdown();
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                    logWarning("Forced shutdown of " + executorName + " executor");
                } else {
                    logInfo("Graceful shutdown of " + executorName + " executor");
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
                logWarning("Interrupted during shutdown of " + executorName + " executor");
            }
        }
    }

    // Logging methods
    private static void logStartup() {
        System.out.println("[" + PROCESS_NAME + "] Starting Java stub application");
    }

    private static void logReadyState() {
        System.out.println("[" + PROCESS_NAME + "] Ready for Frida injection");
        System.out.println("[" + PROCESS_NAME + "] Test with: " + FRIDA_TEST_COMMAND);
    }

    private static void logInfo(String message) {
        System.out.println("[" + PROCESS_NAME + "] " + message);
    }

    private static void logWarning(String message) {
        System.out.println("[" + PROCESS_NAME + "] WARNING: " + message);
    }

    private static void logError(String message, Exception exception) {
        System.out.println("[" + PROCESS_NAME + "] ERROR: " + message + " - " + exception.getMessage());
    }

    // Inner classes for Bluetooth phone simulation
    private static class BluetoothDevice {
        String name;
        String address;
        String state;
        int batteryLevel;
    }

    private static class CallState {
        String phoneNumber;
        String contactName;
        String state;
        int duration;
    }

    // Static initialization block for shutdown hook
    static {
        Runtime.getRuntime().addShutdownHook(
            new Thread(() -> {
                isRunning.set(false);
                logInfo("Shutdown hook called");
            }, "ShutdownHook")
        );
    }
}
