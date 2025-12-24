package okhttp3;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Mock ResponseBody class for Frida agent testing
 */
public class ResponseBody {
    private final MediaType mediaType;
    private final String content;

    private ResponseBody(MediaType mediaType, String content) {
        this.mediaType = mediaType;
        this.content = content;
        System.out.println("[ResponseBody] ResponseBody created with content length: " + content.length());
    }

    /**
     * Creates a ResponseBody
     */
    public static ResponseBody create(MediaType mediaType, String content) {
        System.out.println("[ResponseBody] create called with mediaType: " + mediaType + ", content: " + content);
        return new ResponseBody(mediaType, content);
    }

    /**
     * Creates a ResponseBody with bytes
     */
    public static ResponseBody create(MediaType mediaType, byte[] content) {
        String contentStr = new String(content, StandardCharsets.UTF_8);
        System.out.println("[ResponseBody] create called with mediaType: " + mediaType + ", bytes length: " + content.length);
        return new ResponseBody(mediaType, contentStr);
    }

    /**
     * Gets the content as string
     */
    public String string() throws IOException {
        System.out.println("[ResponseBody] string() called, returning: " + content);
        return content;
    }

    /**
     * Gets the content as bytes
     */
    public byte[] bytes() throws IOException {
        System.out.println("[ResponseBody] bytes() called");
        return content.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Gets the input stream
     */
    public InputStream byteStream() {
        System.out.println("[ResponseBody] byteStream() called");
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Gets the character stream
     */
    public java.io.Reader charStream() {
        System.out.println("[ResponseBody] charStream() called");
        return new java.io.StringReader(content);
    }

    /**
     * Gets the content length
     */
    public long contentLength() {
        System.out.println("[ResponseBody] contentLength() called");
        return content.length();
    }

    /**
     * Gets the media type
     */
    public MediaType contentType() {
        System.out.println("[ResponseBody] contentType() called");
        return mediaType;
    }

    /**
     * Closes the response body
     */
    public void close() {
        System.out.println("[ResponseBody] close() called");
    }
}
