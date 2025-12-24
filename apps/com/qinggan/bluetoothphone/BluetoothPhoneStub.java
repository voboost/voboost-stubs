package com.qinggan.bluetoothphone;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Java stub application for com.qinggan.bluetoothphone.
 *
 * <p>This application simulates Bluetooth phone functionality for testing purposes.
 * It generates simulated phone calls at regular intervals and provides a target
 * for Frida injection testing.</p>
 *
 * <p>The application runs continuously until interrupted, generating phone call
 * simulations every 8 seconds after an initial 2-second delay.</p>
 */
public class BluetoothPhoneStub {
    /** Process name used for logging and identification. */
    private static final String PROCESS_NAME = "com.qinggan.bluetoothphone";

    /** Initial delay before starting phone simulations (in seconds). */
    private static final long INITIAL_DELAY_SECONDS = 2L;

    /** Interval between phone simulations (in seconds). */
    private static final long SIMULATION_INTERVAL_SECONDS = 8L;

    /** Sleep interval for main loop (in milliseconds). */
    private static final long MAIN_LOOP_SLEEP_MS = 1000L;

    /** Russian phone number prefix. */
    private static final String PHONE_PREFIX = "+7";

    /** Base phone number for random generation (9000000000L). */
    private static final long PHONE_NUMBER_BASE = 9000000000L;

    /** Range for random phone number generation (1000000000L). */
    private static final long PHONE_NUMBER_RANGE = 1000000000L;

    /** Thread-safe flag to control application running state. */
    private static final AtomicBoolean running = new AtomicBoolean(true);

    /** Shared random instance for phone number generation. */
    private static final Random random = new Random();

    /** Executor service for scheduled phone simulations. */
    private static ScheduledExecutorService executorService;

    /**
     * Main entry point for the Bluetooth phone stub application.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("[" + PROCESS_NAME + "] Starting Java stub application");

        try {
            startSimulations();
            System.out.println("[" + PROCESS_NAME + "] Ready for Frida injection");
            runMainLoop();
        } catch (Exception e) {
            System.err.println("[" + PROCESS_NAME + "] Unexpected error in main loop: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    /**
     * Starts the scheduled phone call simulations.
     *
     * <p>Creates a single-threaded executor that schedules phone call
     * simulations at regular intervals.</p>
     */
    private static void startSimulations() {
        executorService = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "PhoneSimulation-" + PROCESS_NAME);
            thread.setDaemon(true);
            return thread;
        });

        executorService.scheduleAtFixedRate(
            BluetoothPhoneStub::simulatePhoneCall,
            INITIAL_DELAY_SECONDS,
            SIMULATION_INTERVAL_SECONDS,
            TimeUnit.SECONDS
        );
    }

    /**
     * Simulates an incoming phone call with a randomly generated phone number.
     *
     * <p>Generates a Russian phone number in the format +7XXXXXXXXXX
     * where X represents a random digit.</p>
     */
    private static void simulatePhoneCall() {
        try {
            String phoneNumber = generatePhoneNumber();
            System.out.println("[" + PROCESS_NAME + "] Simulated phone call from: " + phoneNumber);
        } catch (Exception e) {
            System.err.println("[" + PROCESS_NAME + "] Phone simulation error: " + e.getMessage());
        }
    }

    /**
     * Generates a random Russian phone number.
     *
     * @return a formatted phone number string in the format +7XXXXXXXXXX
     */
    private static String generatePhoneNumber() {
        long randomNumber = PHONE_NUMBER_BASE + (long)(random.nextDouble() * PHONE_NUMBER_RANGE);
        return PHONE_PREFIX + randomNumber;
    }

    /**
     * Runs the main application loop, keeping the process alive.
     *
     * <p>The loop continues until the running flag is set to false,
     * sleeping for the configured interval between iterations.</p>
     */
    private static void runMainLoop() {
        while (running.get()) {
            try {
                Thread.sleep(MAIN_LOOP_SLEEP_MS);
            } catch (InterruptedException e) {
                System.out.println("[" + PROCESS_NAME + "] Interrupted, shutting down");
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Gracefully shuts down the application and its resources.
     */
    private static void shutdown() {
        running.set(false);

        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        logInfo("Shutdown completed");
    }

    /**
     * Logs informational messages with process name prefix.
     *
     * @param message the message to log
     */
    private static void logInfo(String message) {
        System.out.println("[" + PROCESS_NAME + "] " + message);
    }

    // Static initialization block for shutdown hook
    static {
        Runtime.getRuntime().addShutdownHook(
            new Thread(() -> {
                logInfo("Shutdown hook called");
                shutdown();
            }, "ShutdownHook-" + PROCESS_NAME)
        );
    }
}
