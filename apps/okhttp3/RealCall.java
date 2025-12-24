package okhttp3;

import java.io.IOException;

/**
 * Mock RealCall class for Frida agent testing
 */
public class RealCall {
    private final OkHttpClient client;
    private final Request originalRequest;

    /**
     * Constructor
     */
    public RealCall(OkHttpClient client, Request originalRequest) {
        this.client = client;
        this.originalRequest = originalRequest;
        System.out.println("[RealCall] RealCall created");
    }

    /**
     * Gets the original request
     */
    public Request request() {
        System.out.println("[RealCall] request() called");
        return originalRequest;
    }

    /**
     * Executes the call synchronously
     */
    public Response execute() throws IOException {
        System.out.println("[RealCall] execute() called");
        return new Response.Builder()
            .request(originalRequest)
            .protocol(Protocol.get("http/1.1"))
            .code(200)
            .message("OK")
            .body(ResponseBody.create(MediaType.parse("application/json"), "{}"))
            .build();
    }

    /**
     * Enqueues the call for asynchronous execution
     */
    public void enqueue(Callback callback) {
        System.out.println("[RealCall] enqueue() called");
        try {
            Response response = execute();
            callback.onResponse(this, response);
        } catch (IOException e) {
            callback.onFailure(this, e);
        }
    }

    /**
     * Cancels the call
     */
    public void cancel() {
        System.out.println("[RealCall] cancel() called");
    }

    /**
     * Checks if the call is executed
     */
    public boolean isExecuted() {
        System.out.println("[RealCall] isExecuted() called");
        return false;
    }

    /**
     * Checks if the call is canceled
     */
    public boolean isCanceled() {
        System.out.println("[RealCall] isCanceled() called");
        return false;
    }

    /**
     * Mock Callback interface
     */
    public interface Callback {
        void onFailure(RealCall call, IOException e);
        void onResponse(RealCall call, Response response);
    }
}
