package okhttp3;

/**
 * Mock MediaType class for Frida agent testing
 */
public class MediaType {
    private final String mediaType;

    private MediaType(String mediaType) {
        this.mediaType = mediaType;
        System.out.println("[MediaType] MediaType created: " + mediaType);
    }

    /**
     * Parses a media type string
     */
    public static MediaType parse(String mediaType) {
        System.out.println("[MediaType] parse called with: " + mediaType);
        return new MediaType(mediaType);
    }

    /**
     * Gets the string representation
     */
    public String toString() {
        return mediaType;
    }

    /**
     * Gets the type (e.g., "application" from "application/json")
     */
    public String type() {
        if (mediaType != null && mediaType.contains("/")) {
            return mediaType.split("/")[0];
        }
        return mediaType;
    }

    /**
     * Gets the subtype (e.g., "json" from "application/json")
     */
    public String subtype() {
        if (mediaType != null && mediaType.contains("/")) {
            String[] parts = mediaType.split("/");
            if (parts.length > 1) {
                String subtype = parts[1];
                if (subtype.contains(";")) {
                    return subtype.split(";")[0];
                }
                return subtype;
            }
        }
        return mediaType;
    }

    /**
     * Gets the charset (e.g., "utf-8" from "application/json; charset=utf-8")
     */
    public String charset() {
        if (mediaType != null && mediaType.contains("charset=")) {
            String[] parts = mediaType.split("charset=");
            if (parts.length > 1) {
                String charset = parts[1].trim();
                if (charset.endsWith(";")) {
                    charset = charset.substring(0, charset.length() - 1);
                }
                return charset;
            }
        }
        return "utf-8"; // Default charset
    }
}
