package com.qinggan.app.qgime;

import android.graphics.drawable.Drawable;

/**
 * Mock SoftKey class for Frida agent testing
 */
public class SoftKey {
    public int mKeyCode;
    public String mKeyLabel;
    public Drawable mKeyIcon;
    public Drawable mKeyIconPopup;
    public boolean mRepeat;
    public boolean mBalloon;

    public SoftKey() {
        System.out.println("[SoftKey] SoftKey created");
        this.mKeyCode = 0;
        this.mKeyLabel = "";
        this.mKeyIcon = null;
        this.mKeyIconPopup = null;
        this.mRepeat = false;
        this.mBalloon = false;
    }

    public int getKeyCode() {
        System.out.println("[SoftKey] getKeyCode called, returning: " + mKeyCode);
        return mKeyCode;
    }

    public void setKeyAttribute(int keyCode, String label, boolean repeat, boolean balloon) {
        System.out.println("[SoftKey] setKeyAttribute called with keyCode: " + keyCode + ", label: " + label);
        this.mKeyCode = keyCode;
        this.mKeyLabel = label;
        this.mRepeat = repeat;
        this.mBalloon = balloon;
    }

    public void setKeyType(Object keyType, Drawable icon, Drawable iconPopup) {
        System.out.println("[SoftKey] setKeyType called");
        this.mKeyIcon = icon;
        this.mKeyIconPopup = iconPopup;
    }

    public void setKeyDimensions(float x, float y, float positionX, float positionY) {
        System.out.println("[SoftKey] setKeyDimensions called");
    }

    public void setSkbCoreSize(int width, int height) {
        System.out.println("[SoftKey] setSkbCoreSize called with width: " + width + ", height: " + height);
    }

    public void changeCase(boolean isUpper) {
        System.out.println("[SoftKey] changeCase called with isUpper: " + isUpper);
        if (mKeyLabel != null) {
            mKeyLabel = isUpper ? mKeyLabel.toUpperCase() : mKeyLabel.toLowerCase();
        }
    }

    public void setPopupSkbId(int popupSkbId) {
        System.out.println("[SoftKey] setPopupSkbId called with: " + popupSkbId);
    }

    public String getKeyLabel() {
        System.out.println("[SoftKey] getKeyLabel called, returning: " + mKeyLabel);
        return mKeyLabel;
    }
}

