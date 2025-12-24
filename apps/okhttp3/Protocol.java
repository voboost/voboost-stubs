package okhttp3;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock Protocol class for Frida agent testing
 */
public class Protocol {
    private final String protocol;

    private Protocol(String protocol) {
        this.protocol = protocol;
        System.out.println("[Protocol] Protocol created: " + protocol);
    }

    // Cache for protocol instances
    private static final Map<String, Protocol> protocolCache = new HashMap<>();

    static {
        protocolCache.put("http/1.0", new Protocol("HTTP_1_0"));
        protocolCache.put("http/1.1", new Protocol("HTTP_1_1"));
        protocolCache.put("h2", new Protocol("HTTP_2"));
        protocolCache.put("spdy/3.1", new Protocol("SPDY_3_1"));
        protocolCache.put("quic", new Protocol("QUIC"));
    }

    /**
     * Gets a protocol instance
     */
    public static Protocol get(String protocol) {
        System.out.println("[Protocol] get called with: " + protocol);
        Protocol result = protocolCache.get(protocol.toLowerCase());
        if (result == null) {
            // Create a new protocol if not found in cache
            result = new Protocol(protocol.toUpperCase().replace("-", "_"));
            protocolCache.put(protocol.toLowerCase(), result);
        }
        return result;
    }

    /**
     * Gets the HTTP 1.0 protocol
     */
    public static Protocol HTTP_1_0() {
        return get("http/1.0");
    }

    /**
     * Gets the HTTP 1.1 protocol
     */
    public static Protocol HTTP_1_1() {
        return get("http/1.1");
    }

    /**
     * Gets the HTTP 2 protocol
     */
    public static Protocol HTTP_2() {
        return get("h2");
    }

    /**
     * Gets the SPDY 3.1 protocol
     */
    public static Protocol SPDY_3_1() {
        return get("spdy/3.1");
    }

    /**
     * Gets the QUIC protocol
     */
    public static Protocol QUIC() {
        return get("quic");
    }

    /**
     * Gets the string representation
     */
    @Override
    public String toString() {
        // Convert back to the standard format
        if ("HTTP_1_0".equals(protocol)) return "http/1.0";
        if ("HTTP_1_1".equals(protocol)) return "http/1.1";
        if ("HTTP_2".equals(protocol)) return "h2";
        if ("SPDY_3_1".equals(protocol)) return "spdy/3.1";
        if ("QUIC".equals(protocol)) return "quic";
        return protocol.toLowerCase().replace("_", "-");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Protocol protocol1 = (Protocol) obj;
        return protocol != null ? protocol.equals(protocol1.protocol) : protocol1.protocol == null;
    }

    @Override
    public int hashCode() {
        return protocol != null ? protocol.hashCode() : 0;
    }
}
