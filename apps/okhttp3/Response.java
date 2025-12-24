package okhttp3;

import java.io.IOException;

/**
 * Mock Response class for Frida agent testing
 */
public class Response {
    private final Request request;
    private final Protocol protocol;
    private final int code;
    private final String message;
    private final ResponseBody body;

    private Response(Builder builder) {
        this.request = builder.request;
        this.protocol = builder.protocol;
        this.code = builder.code;
        this.message = builder.message;
        this.body = builder.body;
        System.out.println("[Response] Response created with code: " + code);
    }

    /**
     * Gets the request
     */
    public Request request() {
        System.out.println("[Response] request() called");
        return request;
    }

    /**
     * Gets the protocol
     */
    public Protocol protocol() {
        System.out.println("[Response] protocol() called");
        return protocol;
    }

    /**
     * Gets the response code
     */
    public int code() {
        System.out.println("[Response] code() called, returning: " + code);
        return code;
    }

    /**
     * Gets the response message
     */
    public String message() {
        System.out.println("[Response] message() called, returning: " + message);
        return message;
    }

    /**
     * Gets the response body
     */
    public ResponseBody body() {
        System.out.println("[Response] body() called");
        return body;
    }

    /**
     * Checks if the response is successful
     */
    public boolean isSuccessful() {
        boolean successful = code >= 200 && code < 300;
        System.out.println("[Response] isSuccessful() called, returning: " + successful);
        return successful;
    }

    /**
     * Closes the response
     */
    public void close() {
        System.out.println("[Response] close() called");
        if (body != null) {
            body.close();
        }
    }

    /**
     * Builder class for Response
     */
    public static class Builder {
        private Request request;
        private Protocol protocol;
        private int code = 200;
        private String message = "OK";
        private ResponseBody body;

        public Builder() {
            System.out.println("[Response.Builder] Builder created");
        }

        public Builder request(Request request) {
            this.request = request;
            System.out.println("[Response.Builder] request called");
            return this;
        }

        public Builder protocol(Protocol protocol) {
            this.protocol = protocol;
            System.out.println("[Response.Builder] protocol called");
            return this;
        }

        public Builder code(int code) {
            this.code = code;
            System.out.println("[Response.Builder] code called with: " + code);
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            System.out.println("[Response.Builder] message called with: " + message);
            return this;
        }

        public Builder body(ResponseBody body) {
            this.body = body;
            System.out.println("[Response.Builder] body called");
            return this;
        }

        public Response build() {
            System.out.println("[Response.Builder] build called");
            return new Response(this);
        }
    }
}
