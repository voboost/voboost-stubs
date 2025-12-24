package android.net;

/**
 * Mock Uri class for Frida agent testing
 */
public class Uri {
    private String uriString;

    /**
     * Default constructor
     */
    public Uri() {
        this.uriString = "";
        System.out.println("[Uri] Uri created");
    }

    /**
     * Constructor with string
     */
    private Uri(String uriString) {
        this.uriString = uriString;
        System.out.println("[Uri] Uri created with: " + uriString);
    }

    /**
     * Parses a string into a Uri
     */
    public static Uri parse(String uriString) {
        System.out.println("[Uri] parse called with: " + uriString);
        return new Uri(uriString);
    }

    /**
     * Creates an empty Uri
     */
    public static Uri EMPTY() {
        System.out.println("[Uri] EMPTY called");
        return new Uri("");
    }

    /**
     * Gets the string representation
     */
    public String toString() {
        return uriString;
    }

    /**
     * Gets the scheme
     */
    public String getScheme() {
        System.out.println("[Uri] getScheme called");
        if (uriString.contains("://")) {
            return uriString.substring(0, uriString.indexOf("://"));
        }
        return "";
    }

    /**
     * Gets the scheme-specific part
     */
    public String getSchemeSpecificPart() {
        System.out.println("[Uri] getSchemeSpecificPart called");
        if (uriString.contains("://")) {
            return uriString.substring(uriString.indexOf("://") + 3);
        }
        return uriString;
    }

    /**
     * Gets the authority
     */
    public String getAuthority() {
        System.out.println("[Uri] getAuthority called");
        String ssp = getSchemeSpecificPart();
        if (ssp.contains("/")) {
            return ssp.substring(0, ssp.indexOf("/"));
        }
        return ssp;
    }

    /**
     * Gets the path
     */
    public String getPath() {
        System.out.println("[Uri] getPath called");
        String ssp = getSchemeSpecificPart();
        if (ssp.contains("/")) {
            return ssp.substring(ssp.indexOf("/"));
        }
        return "";
    }

    /**
     * Gets the query
     */
    public String getQuery() {
        System.out.println("[Uri] getQuery called");
        String path = getPath();
        if (path.contains("?")) {
            return path.substring(path.indexOf("?") + 1);
        }
        return "";
    }

    /**
     * Gets the fragment
     */
    public String getFragment() {
        System.out.println("[Uri] getFragment called");
        String path = getPath();
        if (path.contains("#")) {
            return path.substring(path.indexOf("#") + 1);
        }
        return "";
    }

    /**
     * Checks if the Uri is hierarchical
     */
    public boolean isHierarchical() {
        System.out.println("[Uri] isHierarchical called");
        return uriString.contains("://") || uriString.startsWith("/");
    }

    /**
     * Checks if the Uri is relative
     */
    public boolean isRelative() {
        System.out.println("[Uri] isRelative called");
        return !uriString.contains(":");
    }

    /**
     * Checks if the Uri is absolute
     */
    public boolean isAbsolute() {
        System.out.println("[Uri] isAbsolute called");
        return !isRelative();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Uri uri = (Uri) obj;
        return uriString != null ? uriString.equals(uri.uriString) : uri.uriString == null;
    }

    @Override
    public int hashCode() {
        return uriString != null ? uriString.hashCode() : 0;
    }
}
