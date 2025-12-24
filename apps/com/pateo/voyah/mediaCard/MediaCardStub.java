package com.pateo.voyah.mediaCard;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

import com.pateo.voyah.mediaCard.home.fragment.MediaCardHomeFragment;
import com.pateo.voyah.mediaCard.home.inter.MediaBeanInter;
import com.pateo.iloader.common.bean.SourceBean;
import android.content.Context;

/**
 * Java stub application for com.pateo.voyah.mediaCard.
 *
 * <p>This class provides a realistic Java environment for Frida agent testing by simulating
 * media card functionality including media source management, fragment lifecycle,
 * and media operations.
 *
 * <p>The application runs continuously, simulating various media operations at
 * different intervals to provide a realistic testing environment for dynamic instrumentation.
 */
public class MediaCardStub {
    // Process and application constants
    private static final String PROCESS_NAME = "com.pateo.voyah.mediaCard";
    private static final String FRIDA_TEST_COMMAND = "frida -n " + PROCESS_NAME + " -l agent.js";

    // Timing constants (in seconds)
    private static final long MAIN_LOOP_SLEEP_MS = 1000L;
    private static final long FRAGMENT_INITIAL_DELAY_SEC = 2L;
    private static final long FRAGMENT_INTERVAL_SEC = 10L;
    private static final long MEDIA_INITIAL_DELAY_SEC = 4L;
    private static final long MEDIA_INTERVAL_SEC = 6L;

    // Media source constants
    private static final String[] MEDIA_SOURCES = {
        "Bluetooth Audio",
        "USB Audio",
        "Radio",
        "Spotify",
        "Apple Music"
    };

    // Thread pool names
    private static final String FRAGMENT_THREAD_NAME = "MediaCardFragment";
    private static final String MEDIA_THREAD_NAME = "MediaSource";

    // Runtime state
    private static final AtomicBoolean isRunning = new AtomicBoolean(true);
    private static final Random randomGenerator = new Random();

    // Executor services for scheduled tasks
    private static ScheduledExecutorService fragmentExecutor;
    private static ScheduledExecutorService mediaExecutor;

    // Media components
    private static MediaCardHomeFragment mediaCardHomeFragment;
    private static Context mockContext;

    /**
     * Main entry point for the Media Card stub application.
     *
     * <p>Initializes the process environment, starts simulation threads, and keeps the
     * application running until interrupted. The application simulates various media
     * operations to provide a realistic testing environment.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        logStartup();

        try {
            initializeProcessEnvironment();
            initializeAndroidEnvironment();
            initializeMediaComponents();
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
     * Initializes media components for the application.
     *
     * <p>Creates the MediaCardHomeFragment and populates it with initial media data
     * to provide a realistic testing environment.
     */
    private static void initializeMediaComponents() {
        try {
            System.out.println("[" + PROCESS_NAME + "] Initializing media components");

            // Create the main fragment
            mediaCardHomeFragment = new MediaCardHomeFragment(mockContext);

            // Populate with initial media data
            populateInitialMediaData();

            System.out.println("[" + PROCESS_NAME + "] Media components initialized successfully");
        } catch (Exception e) {
            logError("Failed to initialize media components", e);
        }
    }

    /**
     * Populates the fragment with initial media data.
     *
     * <p>Creates sample MediaBeanInter and SourceBean objects to simulate
     * a realistic media environment.
     */
    private static void populateInitialMediaData() {
        try {
            // Create sample MediaBeanInter objects
            List<MediaBeanInter> mediaBeanInterList = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                MediaBeanInter mediaBean = new MediaBeanInter();
                mediaBean.setPageName("Page " + i);
                mediaBean.setName("Media " + i);
                mediaBean.setIcon("icon_" + i);
                mediaBean.setAutoPlay(i % 2 == 0);
                mediaBeanInterList.add(mediaBean);
            }
            mediaCardHomeFragment.setMediaBeanInterList(mediaBeanInterList);

            // Create sample SourceBean objects
            List<SourceBean> sourceBeanList = new ArrayList<>();
            for (int i = 0; i < MEDIA_SOURCES.length; i++) {
                SourceBean sourceBean = new SourceBean();
                sourceBean.setPkgName("com.example.media" + i);
                sourceBean.setClassName("MediaActivity" + i);
                sourceBean.setSourceName(MEDIA_SOURCES[i]);
                sourceBean.setSourceIcon("media_icon_" + i);
                sourceBean.setSourceId("source_" + i);
                sourceBean.setSourceType(i);
                sourceBeanList.add(sourceBean);
            }
            mediaCardHomeFragment.setSourceBeanList(sourceBeanList);

            System.out.println("[" + PROCESS_NAME + "] Initial media data populated");
        } catch (Exception e) {
            logError("Failed to populate initial media data", e);
        }
    }

    /**
     * Starts all simulation threads for media operations.
     *
     * <p>Creates and starts two separate scheduled executor services:
     * <ul>
     *   <li>Media card fragment simulation</li>
     *   <li>Media source simulation</li>
     * </ul>
     *
     * Each simulation runs at different intervals to provide realistic timing behavior.
     */
    private static void startSimulations() {
        fragmentExecutor = createScheduledExecutor(FRAGMENT_THREAD_NAME);
        mediaExecutor = createScheduledExecutor(MEDIA_THREAD_NAME);

        scheduleFragmentSimulation();
        scheduleMediaSimulation();
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
     * Schedules the media card fragment simulation.
     *
     * <p>The fragment simulation runs every 10 seconds after an initial 2-second delay,
     * simulating the lifecycle of a media card fragment.
     */
    private static void scheduleFragmentSimulation() {
        fragmentExecutor.scheduleAtFixedRate(
            MediaCardStub::simulateMediaCardFragment,
            FRAGMENT_INITIAL_DELAY_SEC,
            FRAGMENT_INTERVAL_SEC,
            TimeUnit.SECONDS
        );
    }

    /**
     * Schedules the media source simulation.
     *
     * <p>The media simulation runs every 6 seconds after an initial 4-second delay,
     * simulating media source operations and updates.
     */
    private static void scheduleMediaSimulation() {
        mediaExecutor.scheduleAtFixedRate(
            MediaCardStub::simulateMediaSource,
            MEDIA_INITIAL_DELAY_SEC,
            MEDIA_INTERVAL_SEC,
            TimeUnit.SECONDS
        );
    }

    /**
     * Simulates the media card fragment lifecycle.
     *
     * <p>This method simulates the typical Android fragment lifecycle methods
     * that would be called in a real media card application.
     */
    private static void simulateMediaCardFragment() {
        try {
            logInfo("Simulated media card fragment lifecycle");
            logInfo("Fragment: onCreate() -> onCreateView() -> onViewCreated() -> onResume()");

            // Simulate fragment operations
            if (mediaCardHomeFragment != null) {
                List<MediaBeanInter> mediaBeans = mediaCardHomeFragment.getMediaBeanInterList();
                List<SourceBean> sourceBeans = mediaCardHomeFragment.getSourceBeanList();
                logInfo("Fragment contains " + mediaBeans.size() + " media beans and " + sourceBeans.size() + " source beans");
            }
        } catch (Exception e) {
            logError("Media card fragment simulation failed", e);
        }
    }

    /**
     * Simulates media source operations and updates.
     *
     * <p>This method simulates the behavior of media source management,
     * including source selection and media playback operations.
     */
    private static void simulateMediaSource() {
        try {
            String selectedSource = selectRandomMediaSource();
            logInfo("Media source operation: " + selectedSource);

            // Simulate media operations
            if (randomGenerator.nextBoolean()) {
                logInfo("Media playback would start for: " + selectedSource);
            } else {
                logInfo("Media source would be switched to: " + selectedSource);
            }
        } catch (Exception e) {
            logError("Media source simulation failed", e);
        }
    }

    /**
     * Selects a random media source from the predefined media sources array.
     *
     * @return a randomly selected media source string
     * @throws IllegalArgumentException if the media sources array is empty or null
     */
    private static String selectRandomMediaSource() {
        if (MEDIA_SOURCES.length == 0) {
            throw new IllegalArgumentException("Media sources array cannot be null or empty");
        }

        int randomIndex = randomGenerator.nextInt(MEDIA_SOURCES.length);
        return MEDIA_SOURCES[randomIndex];
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
        logInfo("Shutting down Media Card stub application");

        shutdownExecutorService(fragmentExecutor, FRAGMENT_THREAD_NAME);
        shutdownExecutorService(mediaExecutor, MEDIA_THREAD_NAME);

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
