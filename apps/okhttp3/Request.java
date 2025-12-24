package okhttp3;

import android.net.Uri;

/**
 * Mock Request class for Frida agent testing
 */
public class Request {
    private final String url;
    private final String method;
    private final Object tag;

    private Request(Builder builder) {
        this.url = builder.url;
        this.method = builder.method;
        this.tag = builder.tag;
        System.out.println("[Request] Request created with URL: " + url);
    }

    /**
     * Gets the URL
     */
    public Uri url() {
        System.out.println("[Request] url() called, returning: " + url);
        return Uri.parse(url);
    }

    /**
     * Gets the URL as string
     */
    public String urlString() {
        System.out.println("[Request] urlString() called, returning: " + url);
        return url;
    }

    /**
     * Gets the method
     */
    public String method() {
        System.out.println("[Request] method() called, returning: " + method);
        return method;
    }

    /**
     * Gets the tag
     */
    public Object tag() {
        System.out.println("[Request] tag() called");
        return tag;
    }

    /**
     * Builder class for Request
     */
    public static class Builder {
        private String url = "";
        private String method = "GET";
        private Object tag = null;

        public Builder() {
            System.out.println("[Request.Builder] Builder created");
        }

        public Builder url(String url) {
            this.url = url;
            System.out.println("[Request.Builder] url called with: " + url);
            return this;
        }

        public Builder url(Uri url) {
            this.url = url.toString();
            System.out.println("[Request.Builder] url called with Uri: " + url);
            return this;
        }

        public Builder get() {
            this.method = "GET";
            System.out.println("[Request.Builder] get called");
            return this;
        }

        public Builder post(Object body) {
            this.method = "POST";
            System.out.println("[Request.Builder] post called");
            return this;
        }

        public Builder put(Object body) {
            this.method = "PUT";
            System.out.println("[Request.Builder] put called");
            return this;
        }

        public Builder delete() {
            this.method = "DELETE";
            System.out.println("[Request.Builder] delete called");
            return this;
        }

        public Builder addHeader(String name, String value) {
            System.out.println("[Request.Builder] addHeader called: " + name + " = " + value);
            return this;
        }

        public Builder tag(Object tag) {
            this.tag = tag;
            System.out.println("[Request.Builder] tag called");
            return this;
        }

        public Request build() {
            System.out.println("[Request.Builder] build called");
            return new Request(this);
        }
    }
}
