package android.os;

/**
 * Stub implementation for Android Looper
 */
public class Looper {
    private static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<Looper>();

    public static void prepare() {
        // Stub implementation
    }

    public static void loop() {
        // Stub implementation
    }

    public static Looper myLooper() {
        return sThreadLocal.get();
    }

    public static Looper getMainLooper() {
        // Stub implementation
        return null;
    }

    public Thread getThread() {
        // Stub implementation
        return Thread.currentThread();
    }
}
