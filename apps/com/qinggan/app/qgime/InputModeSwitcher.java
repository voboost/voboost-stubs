package com.qinggan.app.qgime;

/**
 * Mock InputModeSwitcher class for Frida agent testing
 */
public class InputModeSwitcher {
    public static final FieldWrapper<Integer> MODE_SKB_ENGLISH_LOWER = new FieldWrapper<>(1);
    public static final FieldWrapper<Integer> MODE_SKB_ENGLISH_UPPER = new FieldWrapper<>(2);
    public static final FieldWrapper<Integer> MODE_SKB_ENGLISH_FIRST = new FieldWrapper<>(3);
    public static final FieldWrapper<Integer> MODE_HKB_ENGLISH = new FieldWrapper<>(4);
    public static final FieldWrapper<Integer> MODE_SKB_SYMBOL1_EN = new FieldWrapper<>(5);
    public static final FieldWrapper<Integer> MODE_SKB_SYMBOL2_EN = new FieldWrapper<>(6);

    private static InputModeSwitcher instance;

    // Fields
    public int mInputMode = 3; // Default to MODE_SKB_ENGLISH_FIRST
    public Object mInputIcon = null;
    public Object mImeService = null;

    public InputModeSwitcher() {
        System.out.println("[InputModeSwitcher] InputModeSwitcher created");
    }

    public static InputModeSwitcher getInstance() {
        System.out.println("[InputModeSwitcher] getInstance called");
        if (instance == null) {
            instance = new InputModeSwitcher();
        }
        return instance;
    }

    public int getTooggleStateForCnCand() {
        System.out.println("[InputModeSwitcher] getTooggleStateForCnCand called");
        return 0;
    }

    public int[] getToggleStates() {
        System.out.println("[InputModeSwitcher] getToggleStates called");
        return new int[]{1, 2, 3};
    }

    public boolean isQwertyFirstMode() {
        System.out.println("[InputModeSwitcher] isQwertyFirstMode called");
        return true;
    }

    public int switchModeForUserKey(int keyCode, boolean z) {
        System.out.println("[InputModeSwitcher] switchModeForUserKey called with keyCode: " + keyCode + ", z: " + z);
        return keyCode;
    }

    public void saveInputMode(int mode) {
        System.out.println("[InputModeSwitcher] saveInputMode called with mode: " + mode);
    }
}
