package com.qinggan.app.systemservice;

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
 * Java stub application for com.qinggan.app.systemservice.
 *
 * <p>This class provides a realistic Java environment for Frida agent testing by simulating
 * system service functionality including service management, system operations,
 * and background task handling.
 *
 * <p>The application runs continuously, simulating various system service operations at
 * different intervals to provide a realistic testing environment for dynamic instrumentation.
 */
public class SystemServiceStub {
    // Process and application constants
    private static final String PROCESS_NAME = "com.qinggan.app.systemservice";
    private static final String FRIDA_TEST_COMMAND = "frida -n " + PROCESS_NAME + " -l agent.js";

    // Timing constants (in seconds)
    private static final long MAIN_LOOP_SLEEP_MS = 1000L;
    private static final long SERVICE_INITIAL_DELAY_SEC = 2L;
    private static final long SERVICE_INTERVAL_SEC = 12L;
    private static final long TASK_INITIAL_DELAY_SEC = 4L;
    private static final long TASK_INTERVAL_SEC = 7L;

    // System service types
    private static final String[] SERVICE_TYPES = {
        "Background Service",
        "Foreground Service",
        "Bound Service",
        "Intent Service",
        "Job Service"
    };

    // System task types
    private static final String[] TASK_TYPES = {
        "System Update",
        "Cache Cleanup",
        "Log Collection",
        "Performance Monitoring",
        "Security Scan",
        "Data Sync"
    };

    // Thread pool names
    private static final String SERVICE_THREAD_NAME = "ServiceManager";
    private static final String TASK_THREAD_NAME = "TaskManager";

    // Runtime state
    private static final AtomicBoolean isRunning = new AtomicBoolean(true);
    private static final Random randomGenerator = new Random();

    // Executor services for scheduled tasks
    private static ScheduledExecutorService serviceExecutor;
    private static ScheduledExecutorService taskExecutor;

    // System service components
    private static Context mockContext;
    private static List<SystemService> runningServices;
    private static Map<String, SystemTask> activeTasks;

    /**
     * Main entry point for the System Service stub application.
     *
     * <p>Initializes the process environment, starts simulation threads, and keeps the
     * application running until interrupted. The application simulates various system
     * service operations to provide a realistic testing environment.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        logStartup();

        try {
            initializeProcessEnvironment();
            initializeAndroidEnvironment();
            initializeSystemServiceComponents();
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
     * Initializes system service components for the application.
     *
     * <p>Creates the service list and task map and populates them with initial data
     * to provide a realistic testing environment.
     */
    private static void initializeSystemServiceComponents() {
        try {
            System.out.println("[" + PROCESS_NAME + "] Initializing system service components");

            // Initialize components
            runningServices = new ArrayList<>();
            activeTasks = new HashMap<>();

            // Populate with initial data
            populateInitialSystemServiceData();

            System.out.println("[" + PROCESS_NAME + "] System service components initialized successfully");
        } catch (Exception e) {
            logError("Failed to initialize system service components", e);
        }
    }

    /**
     * Populates the system service components with initial data.
     *
     * <p>Creates sample system services and tasks to simulate
     * a realistic system service environment.
     */
    private static void populateInitialSystemServiceData() {
        try {
            // Create sample system services
            for (int i = 0; i < 4; i++) {
                SystemService service = new SystemService();
                service.name = "SystemService" + i;
                service.type = SERVICE_TYPES[i % SERVICE_TYPES.length];
                service.priority = i + 1;
                service.isRunning = i % 2 == 0;
                service.memoryUsage = 50 + (i * 25);
                runningServices.add(service);
            }

            // Create sample system tasks
            for (int i = 0; i < 3; i++) {
                SystemTask task = new SystemTask();
                task.id = "task_" + i;
                task.name = TASK_TYPES[i % TASK_TYPES.length];
                task.status = i == 0 ? "Running" : "Pending";
                task.progress = i * 30;
                task.priority = (i % 3) + 1;
                activeTasks.put(task.id, task);
            }

            System.out.println("[" + PROCESS_NAME + "] Initial system service data populated");
        } catch (Exception e) {
            logError("Failed to populate initial system service data", e);
        }
    }

    /**
     * Starts all simulation threads for system service operations.
     *
     * <p>Creates and starts two separate scheduled executor services:
     * <ul>
     *   <li>Service manager simulation</li>
     *   <li>Task manager simulation</li>
     * </ul>
     *
     * Each simulation runs at different intervals to provide realistic timing behavior.
     */
    private static void startSimulations() {
        serviceExecutor = createScheduledExecutor(SERVICE_THREAD_NAME);
        taskExecutor = createScheduledExecutor(TASK_THREAD_NAME);

        scheduleServiceSimulation();
        scheduleTaskSimulation();
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
     * Schedules the service manager simulation.
     *
     * <p>The service simulation runs every 12 seconds after an initial 2-second delay,
     * simulating service lifecycle and management operations.
     */
    private static void scheduleServiceSimulation() {
        serviceExecutor.scheduleAtFixedRate(
            SystemServiceStub::simulateServiceManager,
            SERVICE_INITIAL_DELAY_SEC,
            SERVICE_INTERVAL_SEC,
            TimeUnit.SECONDS
        );
    }

    /**
     * Schedules the task manager simulation.
     *
     * <p>The task simulation runs every 7 seconds after an initial 4-second delay,
     * simulating task execution and state changes.
     */
    private static void scheduleTaskSimulation() {
        taskExecutor.scheduleAtFixedRate(
            SystemServiceStub::simulateTaskManager,
            TASK_INITIAL_DELAY_SEC,
            TASK_INTERVAL_SEC,
            TimeUnit.SECONDS
        );
    }

    /**
     * Simulates the service manager operations.
     *
     * <p>This method simulates the typical service manager operations
     * that would be called in a real system service application.
     */
    private static void simulateServiceManager() {
        try {
            logInfo("Service manager operation: " + runningServices.size() + " services registered");

            // Simulate service operations
            if (randomGenerator.nextBoolean() && !runningServices.isEmpty()) {
                SystemService service = runningServices.get(randomGenerator.nextInt(runningServices.size()));
                service.isRunning = !service.isRunning;
                service.memoryUsage = 50 + randomGenerator.nextInt(100);
                logInfo("Service state updated: " + service.name + " -> " + (service.isRunning ? "Running" : "Stopped"));
            } else {
                logInfo("Service discovery would be performed");
            }
        } catch (Exception e) {
            logError("Service manager simulation failed", e);
        }
    }

    /**
     * Simulates the task manager operations.
     *
     * <p>This method simulates task operations including task creation,
     * execution, and completion.
     */
    private static void simulateTaskManager() {
        try {
            logInfo("Task manager operation: " + activeTasks.size() + " active tasks");

            // Simulate task operations
            if (randomGenerator.nextBoolean()) {
                if (activeTasks.size() < 5) {
                    // Create new task
                    SystemTask newTask = new SystemTask();
                    newTask.id = "task_" + System.currentTimeMillis();
                    newTask.name = TASK_TYPES[randomGenerator.nextInt(TASK_TYPES.length)];
                    newTask.status = "Pending";
                    newTask.progress = 0;
                    newTask.priority = (randomGenerator.nextInt(3) + 1);
                    activeTasks.put(newTask.id, newTask);
                    logInfo("New task created: " + newTask.name);
                } else {
                    // Update existing task
                    String taskId = new ArrayList<>(activeTasks.keySet()).get(randomGenerator.nextInt(activeTasks.size()));
                    SystemTask task = activeTasks.get(taskId);
                    task.progress = Math.min(100, task.progress + randomGenerator.nextInt(30));
                    if (task.progress >= 100) {
                        task.status = "Completed";
                    } else if (task.status.equals("Pending")) {
                        task.status = "Running";
                    }
                    logInfo("Task updated: " + task.name + " -> " + task.status + " (" + task.progress + "%)");
                }
            } else {
                logInfo("Task queue would be processed");
            }
        } catch (Exception e) {
            logError("Task manager simulation failed", e);
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
        logInfo("Shutting down System Service stub application");

        shutdownExecutorService(serviceExecutor, SERVICE_THREAD_NAME);
        shutdownExecutorService(taskExecutor, TASK_THREAD_NAME);

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

    // Inner classes for system service simulation
    private static class SystemService {
        String name;
        String type;
        int priority;
        boolean isRunning;
        int memoryUsage;
    }

    private static class SystemTask {
        String id;
        String name;
        String status;
        int progress;
        int priority;
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
