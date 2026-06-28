package com.qinggan.app.launcher;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Java stub application for com.qinggan.app.launcher.
 * Provides realistic Java environment for Frida agent testing with weather simulation,
 * app management, and media control functionality.
 *
 * <p>This application simulates a main launcher that performs periodic operations:
 * <ul>
 *   <li>Weather API requests every 10 seconds</li>
 *   <li>App launch simulations every 15 seconds</li>
 *   <li>Media source updates every 20 seconds</li>
 * </ul>
 *
 * <p>The application is designed to be a target for Frida dynamic instrumentation
 * and testing frameworks.
 */
public class LauncherStub {
    // Process and configuration constants
    private static final String PROCESS_NAME = "com.qinggan.app.launcher";
    private static final String WEATHER_API_URL =
        "http://api.example.com/cp/weather/weather-live-info?latitude=55.75&longitude=37.62";
    private static final String JVM_PROPERTY_LOCAL_HOSTNAME = "java.rmi.server.useLocalHostname";

    // Timing constants (in seconds and milliseconds)
    private static final int MAIN_LOOP_SLEEP_MS = 1000;
    private static final long WEATHER_INITIAL_DELAY_SEC = 2L;
    private static final long WEATHER_PERIOD_SEC = 10L;
    private static final long APP_LAUNCH_INITIAL_DELAY_SEC = 4L;
    private static final long APP_LAUNCH_PERIOD_SEC = 15L;
    private static final long MEDIA_INITIAL_DELAY_SEC = 6L;
    private static final long MEDIA_PERIOD_SEC = 20L;
    private static final int HTTP_CONNECT_TIMEOUT_MS = 5000;
    private static final int HTTP_READ_TIMEOUT_MS = 5000;

    // Test application packages for simulation
    private static final String[] TEST_PACKAGES = {
        "com.example.app1",
        "com.example.app2",
        "com.example.app3"
    };

    // Thread-safe state management
    private static final AtomicBoolean running = new AtomicBoolean(true);
    private static final Random random = new Random();

    // Executor services for background simulations
    private static ScheduledExecutorService weatherExecutor;
    private static ScheduledExecutorService appExecutor;
    private static ScheduledExecutorService mediaExecutor;

    // Singleton instance
    private static final LauncherStub instance = new LauncherStub();

    /**
     * Main entry point for the LauncherStub application.
     *
     * <p>Initializes the application, starts background simulation threads,
     * and keeps the process running until interrupted.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("[" + PROCESS_NAME + "] Starting Java stub application");

        // Set process name using JVM argument
        System.setProperty(JVM_PROPERTY_LOCAL_HOSTNAME, "true");

        // Initialize mock Android environment
        initializeAndroidEnvironment();

        // Initialize and start background simulations
        initializeExecutors();
        startSimulations();

        System.out.println("[" + PROCESS_NAME + "] Ready for Frida injection");
        System.out.println("[" + PROCESS_NAME + "] Test with: frida -n java -l agent.js");

        // Keep process running until interrupted
        runMainLoop();

        // Cleanup on shutdown
        shutdownExecutors();
    }

    /**
     * Gets the singleton instance of LauncherStub.
     *
     * @return the LauncherStub instance
     */
    public static LauncherStub getInstance() {
        return instance;
    }

    /**
     * Initializes all executor services for background simulations.
     */
    private static void initializeExecutors() {
        weatherExecutor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "WeatherSimulation");
            thread.setDaemon(true);
            return thread;
        });

        appExecutor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "AppLaunchSimulation");
            thread.setDaemon(true);
            return thread;
        });

        mediaExecutor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "MediaSourceSimulation");
            thread.setDaemon(true);
            return thread;
        });
    }

    /**
     * Initializes mock Android environment for testing.
     */
    private static void initializeAndroidEnvironment() {
        // Create mock instances for Frida agents to find
        try {
            // Initialize AllAppDataManager with some mock data
            com.qinggan.launcher.base.allapp.AllAppDataManager dataManager =
                com.qinggan.launcher.base.allapp.AllAppDataManager.getInstance();

            // Create mock NavigationBar
            com.qinggan.mainlauncher.navigation.NavigationBar navBar =
                new com.qinggan.mainlauncher.navigation.NavigationBar();
            navBar.mScreenId.value = 0;

            System.out.println("[" + PROCESS_NAME + "] Android environment initialized");
        } catch (Exception e) {
            System.err.println("[" + PROCESS_NAME + "] Failed to initialize Android environment: " + e.getMessage());
        }
    }

    /**
     * Starts all background simulation tasks with their respective schedules.
     */
    private static void startSimulations() {
        // Weather request simulation
        weatherExecutor.scheduleAtFixedRate(() -> {
            try {
                simulateWeatherRequest();
            } catch (RuntimeException e) {
                System.err.println("[" + PROCESS_NAME + "] Weather simulation error: " + e.getMessage());
            }
        }, WEATHER_INITIAL_DELAY_SEC, WEATHER_PERIOD_SEC, TimeUnit.SECONDS);

        // App launch simulation
        appExecutor.scheduleAtFixedRate(() -> {
            try {
                simulateAppLaunch();
            } catch (RuntimeException e) {
                System.err.println("[" + PROCESS_NAME + "] App launch simulation error: " + e.getMessage());
            }
        }, APP_LAUNCH_INITIAL_DELAY_SEC, APP_LAUNCH_PERIOD_SEC, TimeUnit.SECONDS);

        // Media source simulation
        mediaExecutor.scheduleAtFixedRate(() -> {
            try {
                simulateMediaSourceChange();
            } catch (RuntimeException e) {
                System.err.println("[" + PROCESS_NAME + "] Media simulation error: " + e.getMessage());
            }
        }, MEDIA_INITIAL_DELAY_SEC, MEDIA_PERIOD_SEC, TimeUnit.SECONDS);
    }

    /**
     * Runs the main application loop, keeping the process alive.
     */
    private static void runMainLoop() {
        try {
            while (running.get()) {
                Thread.sleep(MAIN_LOOP_SLEEP_MS);
            }
        } catch (InterruptedException e) {
            System.err.println("[" + PROCESS_NAME + "] Interrupted, shutting down");
            // Restore the interrupted status
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Simulates an HTTP request to a weather API.
     *
     * <p>This method demonstrates proper HTTP connection management using
     * try-with-resources to prevent resource leaks.
     *
     * @throws RuntimeException if the HTTP request fails due to network issues
     */
    private static void simulateWeatherRequest() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(WEATHER_API_URL);
            connection = (HttpURLConnection) url.openConnection();

            // Configure connection
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(HTTP_CONNECT_TIMEOUT_MS);
            connection.setReadTimeout(HTTP_READ_TIMEOUT_MS);

            // Make the request
            int responseCode = connection.getResponseCode();
            System.out.println("[" + PROCESS_NAME + "] Weather request completed: " + responseCode);

            // Read response to fully consume the connection
            java.io.InputStream inputStream = (responseCode >= 400) ?
                connection.getErrorStream() : connection.getInputStream();

            if (inputStream != null) {
                try {
                    // Consume the response body to ensure connection can be reused
                    byte[] buffer = new byte[1024];
                    while (inputStream.read(buffer) != -1) {
                        // Discard data
                    }
                } finally {
                    inputStream.close();
                }
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid weather API URL: " + WEATHER_API_URL, e);
        } catch (IOException e) {
            throw new RuntimeException("Weather request failed: " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Simulates launching a random test application.
     *
     * <p>Selects a random package from the predefined test packages array
     * and logs the simulated launch event.
     */
    private static void simulateAppLaunch() {
        String packageName = TEST_PACKAGES[random.nextInt(TEST_PACKAGES.length)];
        System.out.println("[" + PROCESS_NAME + "] Simulated app launch: " + packageName);
    }

    /**
     * Simulates a media source change event.
     *
     * <p>This method represents media control functionality that would
     * typically be found in a launcher application.
     */
    private static void simulateMediaSourceChange() {
        System.out.println("[" + PROCESS_NAME + "] Simulated media source update");
    }

    /**
     * Properly shuts down all executor services.
     *
     * <p>This method attempts graceful shutdown first, then forces shutdown
     * if necessary. It waits for tasks to complete before returning.
     */
    private static void shutdownExecutors() {
        System.out.println("[" + PROCESS_NAME + "] Shutting down executor services");

        shutdownExecutor(weatherExecutor, "Weather");
        shutdownExecutor(appExecutor, "AppLaunch");
        shutdownExecutor(mediaExecutor, "Media");
    }

    /**
     * Shuts down a single executor service gracefully.
     *
     * @param executor the executor service to shutdown
     * @param name the name of the executor for logging purposes
     */
    private static void shutdownExecutor(ScheduledExecutorService executor, String name) {
        if (executor != null && !executor.isShutdown()) {
            try {
                // Disable new tasks from being submitted
                executor.shutdown();

                // Wait a while for existing tasks to terminate
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    // Cancel currently executing tasks
                    executor.shutdownNow();

                    // Wait a while for tasks to respond to being cancelled
                    if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                        System.err.println("[" + PROCESS_NAME + "] " + name + " executor did not terminate");
                    }
                }
            } catch (InterruptedException e) {
                // (Re-)Cancel if current thread also interrupted
                executor.shutdownNow();
                // Preserve interrupt status
                Thread.currentThread().interrupt();
            }
        }
    }

    // Add shutdown hook for graceful cleanup
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            running.set(false);
            System.out.println("[" + PROCESS_NAME + "] Shutdown hook called");
            shutdownExecutors();
        }, "ShutdownHook"));
    }
}
