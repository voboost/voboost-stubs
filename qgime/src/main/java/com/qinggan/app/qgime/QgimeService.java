package com.qinggan.app.qgime;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

/**
 * Foreground service that keeps the {@code com.qinggan.app.qgime} process
 * alive so the voboost-inject daemon can spawn-gate / attach Frida agents to it.
 *
 * <p>The service runs in the foreground with a persistent low-priority
 * notification. The actual QG IME business logic is intentionally minimal here:
 * the daemon injects Frida agents that patch the real classes at runtime, so the
 * host process only needs to stay alive.
 */
public class QgimeService extends Service {
    /** Log tag. */
    private static final String TAG = "QgimeService";

    /** Notification channel id (low importance, no sound). */
    private static final String CHANNEL_ID = "voboost_qgime_foreground";

    /** Notification id used for {@link #startForeground(int, Notification)}. */
    private static final int NOTIFICATION_ID = 1;

    /** Human-readable channel name shown in system settings. */
    private static final String CHANNEL_NAME = "QG IME stub foreground service";

    /** Loop sleep interval (ms) for the keep-alive worker thread. */
    private static final long KEEP_ALIVE_SLEEP_MS = 60_000L;

    /** Background keep-alive thread. */
    private Thread keepAliveThread;

    /** Flag controlling the keep-alive loop. */
    private volatile boolean running = false;

    @Override
    public final void onCreate() {
        super.onCreate();
        createNotificationChannel();
        Notification notification = buildNotification();
        startForeground(NOTIFICATION_ID, notification);
        startKeepAliveLoop();
        Log.i(TAG, "QG IME foreground service started");
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        // Sticky restart: if the process is killed, Android restarts the service
        // with a null intent, keeping the process alive for the inject daemon.
        return START_STICKY;
    }

    @Override
    public IBinder onBind(final Intent intent) {
        // Not a bound service.
        return null;
    }

    @Override
    public void onDestroy() {
        running = false;
        if (keepAliveThread != null) {
            keepAliveThread.interrupt();
        }
        Log.i(TAG, "QG IME foreground service destroyed");
        super.onDestroy();
    }

    /** Creates the low-importance notification channel required on API 26+. */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Keeps the QG IME process alive for Frida injection");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    /** Builds the persistent foreground notification. */
    private Notification buildNotification() {
        return new Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("QG IME stub")
            .setContentText("Running (voboost injection target)")
            .setSmallIcon(android.R.drawable.ic_menu_search)
            .setOngoing(true)
            .build();
    }

    /**
     * Starts a daemon thread that sleeps in a loop. The thread itself does no
     * real work: its only purpose is to keep the process address space mapped
     * so the daemon can inject into it.
     */
    private void startKeepAliveLoop() {
        running = true;
        keepAliveThread = new Thread(() -> {
            while (running && !Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(KEEP_ALIVE_SLEEP_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "QgimeKeepAlive");
        keepAliveThread.setDaemon(true);
        keepAliveThread.start();
    }

    /**
     * Convenience helper to start the foreground service from a context.
     *
     * @param context context used to start the service
     */
    public static void start(final Context context) {
        Intent intent = new Intent(context, QgimeService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }
}
