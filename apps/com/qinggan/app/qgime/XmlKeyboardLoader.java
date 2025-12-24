package com.qinggan.app.qgime;

public class XmlKeyboardLoader {
    public SoftKeyboard loadKeyboard(int xmlId, int width, int height) {
        // Return a mock soft keyboard
        return new SoftKeyboard(xmlId, null, width, height);
    }
}
