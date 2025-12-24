package com.qinggan.app.vehiclesetting;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Random;

/**
 * Java stub application for com.qinggan.app.vehiclesetting.
 *
 * <p>This class provides a realistic Java environment for Frida agent testing by simulating
 * vehicle settings functionality including ADAS (Advanced Driver Assistance Systems) configuration,
 * menu creation, and vehicle settings lifecycle management.
 *
 * <p>The application runs continuously, simulating various vehicle settings operations at
 * different intervals to provide a realistic testing environment for dynamic instrumentation.
 */
public class VehicleSettingStub {
    // Process and application constants
    private static final String PROCESS_NAME = "com.qinggan.app.vehiclesetting";
    private static final String FRIDA_TEST_COMMAND = "frida -n " + PROCESS_NAME + " -l agent.js";

    // Timing constants (in seconds)
    private static final long MAIN_LOOP_SLEEP_MS = 1000L;
    private static final long ACTIVITY_INITIAL_DELAY_SEC = 2L;
    private static final long ACTIVITY_INTERVAL_SEC = 12L;
    private static final long ADAS_INITIAL_DELAY_SEC = 4L;
    private static final long ADAS_INTERVAL_SEC = 8L;
    private static final long MENU_INITIAL_DELAY_SEC = 6L;
    private static final long MENU_INTERVAL_SEC = 15L;

    // ADAS configuration constants
    private static final double ADAS_AVAILABILITY_THRESHOLD = 0.8;
    private static final double ADAS_AVAILABILITY_PROBABILITY = 0.2; // 20% chance of being available

    // Menu items constants
    private static final String[] VEHICLE_MENU_ITEMS = {
        "System Settings",
        "Display Settings",
        "Vehicle Settings",
        "ADAS Settings",
        "Connectivity"
    };

    // Thread pool names
    private static final String ACTIVITY_THREAD_NAME = "VehicleSettingsActivity";
    private static final String ADAS_THREAD_NAME = "AdasProvider";
    private static final String MENU_THREAD_NAME = "MenuCreation";

    // Runtime state
    private static final AtomicBoolean isRunning = new AtomicBoolean(true);
    private static final Random randomGenerator = new Random();

    // Executor services for scheduled tasks
    private static ScheduledExecutorService activityExecutor;
    private static ScheduledExecutorService adasExecutor;
    private static ScheduledExecutorService menuExecutor;

    /**
     * Main entry point for the Vehicle Settings stub application.
     *
     * <p>Initializes the process environment, starts simulation threads, and keeps the
     * application running until interrupted. The application simulates various vehicle
     * settings operations to provide a realistic testing environment.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        logStartup();

        try {
            initializeProcessEnvironment();
            initializeAndroidEnvironment();
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

            // Set up mock ActivityThread
            android.app.ActivityThread.setCurrentApplication(mockApplication);

            // The Application class already initializes resources and package manager in its constructor
            // No need to set them manually

            System.out.println("[" + PROCESS_NAME + "] Android environment initialized successfully");
        } catch (Exception e) {
            logWarning("Failed to initialize Android environment: " + e.getMessage());
            // Continue without Android environment - some agents may still work
        }
    }

    /**
     * Starts all simulation threads for vehicle settings operations.
     *
     * <p>Creates and starts three separate scheduled executor services:
     * <ul>
     *   <li>Vehicle settings activity simulation</li>
     *   <li>ADAS provider simulation</li>
     *   <li>Menu creation simulation</li>
     * </ul>
     *
     * Each simulation runs at different intervals to provide realistic timing behavior.
     */
    private static void startSimulations() {
        activityExecutor = createScheduledExecutor(ACTIVITY_THREAD_NAME);
        adasExecutor = createScheduledExecutor(ADAS_THREAD_NAME);
        menuExecutor = createScheduledExecutor(MENU_THREAD_NAME);

        scheduleActivitySimulation();
        scheduleAdasSimulation();
        scheduleMenuSimulation();
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
     * Schedules the vehicle settings activity simulation.
     *
     * <p>The activity simulation runs every 12 seconds after an initial 2-second delay,
     * simulating the lifecycle of a vehicle settings activity.
     */
    private static void scheduleActivitySimulation() {
        activityExecutor.scheduleAtFixedRate(
            VehicleSettingStub::simulateVehicleSettingsActivity,
            ACTIVITY_INITIAL_DELAY_SEC,
            ACTIVITY_INTERVAL_SEC,
            TimeUnit.SECONDS
        );
    }

    /**
     * Schedules the ADAS provider simulation.
     *
     * <p>The ADAS simulation runs every 8 seconds after an initial 4-second delay,
     * simulating ADAS availability checks and configuration.
     */
    private static void scheduleAdasSimulation() {
        adasExecutor.scheduleAtFixedRate(
            VehicleSettingStub::simulateAdasProvider,
            ADAS_INITIAL_DELAY_SEC,
            ADAS_INTERVAL_SEC,
            TimeUnit.SECONDS
        );
    }

    /**
     * Schedules the menu creation simulation.
     *
     * <p>The menu simulation runs every 15 seconds after an initial 6-second delay,
     * simulating the creation and selection of menu items in the vehicle settings UI.
     */
    private static void scheduleMenuSimulation() {
        menuExecutor.scheduleAtFixedRate(
            VehicleSettingStub::simulateMenuCreation,
            MENU_INITIAL_DELAY_SEC,
            MENU_INTERVAL_SEC,
            TimeUnit.SECONDS
        );
    }

    /**
     * Simulates the vehicle settings activity lifecycle.
     *
     * <p>This method simulates the typical Android activity lifecycle methods
     * that would be called in a real vehicle settings application.
     */
    private static void simulateVehicleSettingsActivity() {
        try {
            logInfo("Simulated vehicle settings activity lifecycle");
            logInfo("Activity: onCreate() -> onResume()");
        } catch (Exception e) {
            logError("Vehicle settings activity simulation failed", e);
        }
    }

    /**
     * Simulates ADAS provider operations and availability checks.
     *
     * <p>This method simulates the behavior of an ADAS (Advanced Driver Assistance Systems)
     * provider, including availability checks and potential activation based on random
     * probability to simulate real-world conditions.
     */
    private static void simulateAdasProvider() {
        try {
            boolean isAdasAvailable = randomGenerator.nextDouble() > ADAS_AVAILABILITY_THRESHOLD;
            logInfo("ADAS provider availability check: " + isAdasAvailable);

            if (isAdasAvailable) {
                logInfo("ADAS provider would be enabled");
                // Additional ADAS configuration logic would go here
            }
        } catch (Exception e) {
            logError("ADAS provider simulation failed", e);
        }
    }

    /**
     * Simulates menu creation and selection in the vehicle settings UI.
     *
     * <p>This method randomly selects a menu item from the predefined vehicle settings
     * menu items array to simulate user interaction with the settings interface.
     */
    private static void simulateMenuCreation() {
        try {
            String selectedMenuItem = selectRandomMenuItem();
            logInfo("Simulated menu item creation: " + selectedMenuItem);
        } catch (Exception e) {
            logError("Menu creation simulation failed", e);
        }
    }

    /**
     * Selects a random menu item from the vehicle settings menu.
     *
     * @return a randomly selected menu item string
     * @throws IllegalArgumentException if the menu items array is empty or null
     */
    private static String selectRandomMenuItem() {
        if (VEHICLE_MENU_ITEMS.length == 0) {
            throw new IllegalArgumentException("Menu items array cannot be null or empty");
        }

        int randomIndex = randomGenerator.nextInt(VEHICLE_MENU_ITEMS.length);
        return VEHICLE_MENU_ITEMS[randomIndex];
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
        logInfo("Shutting down Vehicle Settings stub application");

        shutdownExecutorService(activityExecutor, ACTIVITY_THREAD_NAME);
        shutdownExecutorService(adasExecutor, ADAS_THREAD_NAME);
        shutdownExecutorService(menuExecutor, MENU_THREAD_NAME);

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

