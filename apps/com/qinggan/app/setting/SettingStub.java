package com.qinggan.app.setting;

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
 * Java stub application for com.qinggan.app.setting.
 *
 * <p>This class provides a realistic Java environment for Frida agent testing by simulating
 * settings functionality including preference management, settings categories,
 * and configuration operations.
 *
 * <p>The application runs continuously, simulating various settings operations at
 * different intervals to provide a realistic testing environment for dynamic instrumentation.
 */
public class SettingStub {
    // Process and application constants
    private static final String PROCESS_NAME = "com.qinggan.app.setting";
    private static final String FRIDA_TEST_COMMAND = "frida -n " + PROCESS_NAME + " -l agent.js";

    // Timing constants (in seconds)
    private static final long MAIN_LOOP_SLEEP_MS = 1000L;
    private static final long SETTINGS_INITIAL_DELAY_SEC = 2L;
    private static final long SETTINGS_INTERVAL_SEC = 8L;
    private static final long PREFERENCE_INITIAL_DELAY_SEC = 4L;
    private static final long PREFERENCE_INTERVAL_SEC = 6L;

    // Settings categories
    private static final String[] SETTINGS_CATEGORIES = {
        "System Settings",
        "Display Settings",
        "Sound Settings",
        "Network Settings",
        "Security Settings",
        "Application Settings"
    };

    // Thread pool names
    private static final String SETTINGS_THREAD_NAME = "SettingsManager";
    private static final String PREFERENCE_THREAD_NAME = "PreferenceManager";

    // Runtime state
    private static final AtomicBoolean isRunning = new AtomicBoolean(true);
    private static final Random randomGenerator = new Random();

    // Executor services for scheduled tasks
    private static ScheduledExecutorService settingsExecutor;
    private static ScheduledExecutorService preferenceExecutor;

    // Settings components
    private static Context mockContext;
    private static Map<String, Object> preferences;

    /**
     * Main entry point for the Settings stub application.
     *
     * <p>Initializes the process environment, starts simulation threads, and keeps the
     * application running until interrupted. The application simulates various settings
     * operations to provide a realistic testing environment.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        logStartup();

        try {
            initializeProcessEnvironment();
            initializeAndroidEnvironment();
            initializeSettingsComponents();
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
     * Initializes settings components for the application.
     *
     * <p>Creates the preferences map and populates it with initial settings data
     * to provide a realistic testing environment.
     */
    private static void initializeSettingsComponents() {
        try {
            System.out.println("[" + PROCESS_NAME + "] Initializing settings components");

            // Initialize preferences
            preferences = new HashMap<>();

            // Populate with initial settings data
            populateInitialSettingsData();

            System.out.println("[" + PROCESS_NAME + "] Settings components initialized successfully");
        } catch (Exception e) {
            logError("Failed to initialize settings components", e);
        }
    }

    /**
     * Populates the preferences with initial settings data.
     *
     * <p>Creates sample preference entries to simulate a realistic settings environment.
     */
    private static void populateInitialSettingsData() {
        try {
            // System settings
            preferences.put("system.language", "en_US");
            preferences.put("system.timezone", "UTC");
            preferences.put("system.auto_update", true);
            preferences.put("system.debug_mode", false);

            // Display settings
            preferences.put("display.brightness", 75);
            preferences.put("display.auto_rotate", true);
            preferences.put("display.night_mode", false);
            preferences.put("display.font_size", "normal");

            // Sound settings
            preferences.put("sound.volume_ring", 80);
            preferences.put("sound.volume_media", 70);
            preferences.put("sound.volume_alarm", 90);
            preferences.put("sound.vibrate", true);

            // Network settings
            preferences.put("network.wifi_enabled", true);
            preferences.put("network.mobile_data", true);
            preferences.put("network.bluetooth", false);
            preferences.put("network.airplane_mode", false);

            // Security settings
            preferences.put("security.screen_lock", true);
            preferences.put("security.fingerprint", false);
            preferences.put("security.face_unlock", false);
            preferences.put("security.unknown_sources", false);

            System.out.println("[" + PROCESS_NAME + "] Initial settings data populated");
        } catch (Exception e) {
            logError("Failed to populate initial settings data", e);
        }
    }

    /**
     * Starts all simulation threads for settings operations.
     *
     * <p>Creates and starts two separate scheduled executor services:
     * <ul>
     *   <li>Settings manager simulation</li>
     *   <li>Preference manager simulation</li>
     * </ul>
     *
     * Each simulation runs at different intervals to provide realistic timing behavior.
     */
    private static void startSimulations() {
        settingsExecutor = createScheduledExecutor(SETTINGS_THREAD_NAME);
        preferenceExecutor = createScheduledExecutor(PREFERENCE_THREAD_NAME);

        scheduleSettingsSimulation();
        schedulePreferenceSimulation();
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
     * Schedules the settings manager simulation.
     *
     * <p>The settings simulation runs every 8 seconds after an initial 2-second delay,
     * simulating settings category operations and management.
     */
    private static void scheduleSettingsSimulation() {
        settingsExecutor.scheduleAtFixedRate(
            SettingStub::simulateSettingsManager,
            SETTINGS_INITIAL_DELAY_SEC,
            SETTINGS_INTERVAL_SEC,
            TimeUnit.SECONDS
        );
    }

    /**
     * Schedules the preference manager simulation.
     *
     * <p>The preference simulation runs every 6 seconds after an initial 4-second delay,
     * simulating preference operations and updates.
     */
    private static void schedulePreferenceSimulation() {
        preferenceExecutor.scheduleAtFixedRate(
            SettingStub::simulatePreferenceManager,
            PREFERENCE_INITIAL_DELAY_SEC,
            PREFERENCE_INTERVAL_SEC,
            TimeUnit.SECONDS
        );
    }

    /**
     * Simulates the settings manager operations.
     *
     * <p>This method simulates the typical settings manager operations
     * that would be called in a real settings application.
     */
    private static void simulateSettingsManager() {
        try {
            String selectedCategory = selectRandomSettingsCategory();
            logInfo("Settings manager operation: " + selectedCategory);

            // Simulate settings operations
            if (randomGenerator.nextBoolean()) {
                logInfo("Settings category would be opened: " + selectedCategory);
            } else {
                logInfo("Settings category would be refreshed: " + selectedCategory);
            }
        } catch (Exception e) {
            logError("Settings manager simulation failed", e);
        }
    }

    /**
     * Simulates the preference manager operations.
     *
     * <p>This method simulates preference operations including reading,
     * writing, and updating preference values.
     */
    private static void simulatePreferenceManager() {
        try {
            String preferenceKey = selectRandomPreferenceKey();
            Object currentValue = preferences.get(preferenceKey);
            logInfo("Preference manager operation: " + preferenceKey + " = " + currentValue);

            // Simulate preference operations
            if (randomGenerator.nextBoolean()) {
                // Update preference value
                Object newValue = generateRandomPreferenceValue(preferenceKey);
                preferences.put(preferenceKey, newValue);
                logInfo("Preference updated: " + preferenceKey + " = " + newValue);
            } else {
                // Read preference value
                logInfo("Preference read: " + preferenceKey + " = " + currentValue);
            }
        } catch (Exception e) {
            logError("Preference manager simulation failed", e);
        }
    }

    /**
     * Selects a random settings category from the predefined categories array.
     *
     * @return a randomly selected settings category string
     * @throws IllegalArgumentException if the settings categories array is empty or null
     */
    private static String selectRandomSettingsCategory() {
        if (SETTINGS_CATEGORIES.length == 0) {
            throw new IllegalArgumentException("Settings categories array cannot be null or empty");
        }

        int randomIndex = randomGenerator.nextInt(SETTINGS_CATEGORIES.length);
        return SETTINGS_CATEGORIES[randomIndex];
    }

    /**
     * Selects a random preference key from the preferences map.
     *
     * @return a randomly selected preference key string
     */
    private static String selectRandomPreferenceKey() {
        if (preferences.isEmpty()) {
            return "unknown.preference";
        }

        List<String> keys = new ArrayList<>(preferences.keySet());
        int randomIndex = randomGenerator.nextInt(keys.size());
        return keys.get(randomIndex);
    }

    /**
     * Generates a random preference value based on the preference key.
     *
     * @param key the preference key
     * @return a randomly generated preference value
     */
    private static Object generateRandomPreferenceValue(String key) {
        if (key.endsWith("_enabled") || key.endsWith("_mode") || key.endsWith("_lock") ||
            key.endsWith("_vibrate") || key.endsWith("_rotate") || key.endsWith("_update")) {
            return randomGenerator.nextBoolean();
        } else if (key.endsWith("_volume") || key.endsWith("_brightness")) {
            return randomGenerator.nextInt(100);
        } else {
            return "random_value_" + randomGenerator.nextInt(1000);
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
        logInfo("Shutting down Settings stub application");

        shutdownExecutorService(settingsExecutor, SETTINGS_THREAD_NAME);
        shutdownExecutorService(preferenceExecutor, PREFERENCE_THREAD_NAME);

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
