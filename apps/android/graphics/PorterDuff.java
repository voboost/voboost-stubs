package android.graphics;

/**
 * Mock PorterDuff class for Frida agent testing
 */
public class PorterDuff {
    public PorterDuff() {
        System.out.println("[PorterDuff] PorterDuff created");
    }

    public enum Mode {
        CLEAR,
        SRC,
        DST,
        SRC_OVER,
        DST_OVER,
        SRC_IN,
        DST_IN,
        SRC_OUT,
        DST_OUT,
        SRC_ATOP,
        DST_ATOP,
        XOR,
        DARKEN,
        LIGHTEN,
        MULTIPLY,
        SCREEN
    }

    public static Mode modeFromInt(int value) {
        System.out.println("[PorterDuff] modeFromInt called with: " + value);
        switch (value) {
            case 0: return Mode.CLEAR;
            case 1: return Mode.SRC;
            case 2: return Mode.DST;
            case 3: return Mode.SRC_OVER;
            case 4: return Mode.DST_OVER;
            case 5: return Mode.SRC_IN;
            case 6: return Mode.DST_IN;
            case 7: return Mode.SRC_OUT;
            case 8: return Mode.DST_OUT;
            case 9: return Mode.SRC_ATOP;
            case 10: return Mode.DST_ATOP;
            case 11: return Mode.XOR;
            case 12: return Mode.DARKEN;
            case 13: return Mode.LIGHTEN;
            case 14: return Mode.MULTIPLY;
            case 15: return Mode.SCREEN;
            default: return Mode.SRC_OVER;
        }
    }
}
