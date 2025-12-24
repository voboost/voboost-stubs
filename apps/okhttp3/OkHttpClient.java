package okhttp3;

/**
 * Mock OkHttpClient class for Frida agent testing
 */
public class OkHttpClient {
    private int connectTimeout = 10000;
    private int readTimeout = 10000;
    private int writeTimeout = 10000;

    /**
     * Default constructor
     */
    public OkHttpClient() {
        System.out.println("[OkHttpClient] OkHttpClient created");
    }

    /**
     * Creates a new call
     */
    public RealCall newCall(Request request) {
        System.out.println("[OkHttpClient] newCall called");
        return new RealCall(this, request);
    }

    /**
     * Builder class for OkHttpClient
     */
    public static class Builder {
        private int connectTimeout = 10000;
        private int readTimeout = 10000;
        private int writeTimeout = 10000;

        public Builder() {
            System.out.println("[OkHttpClient.Builder] Builder created");
        }

        public Builder connectTimeout(long timeout, java.util.concurrent.TimeUnit unit) {
            this.connectTimeout = (int) timeout;
            System.out.println("[OkHttpClient.Builder] connectTimeout called with: " + timeout);
            return this;
        }

        public Builder readTimeout(long timeout, java.util.concurrent.TimeUnit unit) {
            this.readTimeout = (int) timeout;
            System.out.println("[OkHttpClient.Builder] readTimeout called with: " + timeout);
            return this;
        }

        public Builder writeTimeout(long timeout, java.util.concurrent.TimeUnit unit) {
            this.writeTimeout = (int) timeout;
            System.out.println("[OkHttpClient.Builder] writeTimeout called with: " + timeout);
            return this;
        }

        public OkHttpClient build() {
            System.out.println("[OkHttpClient.Builder] build called");
            return new OkHttpClient();
        }
    }
}
