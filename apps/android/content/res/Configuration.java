package android.content.res;

import java.util.Locale;

/**
 * Stub implementation of Configuration for testing
 */
public class Configuration {
    public int densityDpi;
    public int orientation;
    public Locale locale;

    public Configuration() {
        this.densityDpi = 240; // Default DPI
        this.orientation = 2; // Landscape
        this.locale = Locale.US;
    }

    public Configuration(Configuration other) {
        this.densityDpi = other.densityDpi;
        this.orientation = other.orientation;
        this.locale = other.locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }

    // Orientation constants
    public static final int ORIENTATION_PORTRAIT = 1;
    public static final int ORIENTATION_LANDSCAPE = 2;

    @Override
    public String toString() {
        return "Configuration{densityDpi=" + densityDpi +
               ", orientation=" + orientation +
               ", locale=" + locale + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Configuration that = (Configuration) obj;
        return densityDpi == that.densityDpi &&
               orientation == that.orientation &&
               locale.equals(that.locale);
    }

    @Override
    public int hashCode() {
        int result = densityDpi;
        result = 31 * result + orientation;
        result = 31 * result + locale.hashCode();
        return result;
    }
}
