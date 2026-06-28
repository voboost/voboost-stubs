package com.qinggan.app.qgime;

import android.graphics.drawable.Drawable;

public class SoftKeyToggle extends SoftKey {
    public ToggleState mToggleState;

    public SoftKeyToggle() {
        super();
        mToggleState = null;
    }

    public ToggleState getToggleState() {
        return mToggleState;
    }

    public void setToggleStates(ToggleState state) {
        mToggleState = state;
    }

    public ToggleState createToggleState() {
        return new ToggleState();
    }

    public static class ToggleState {
        public int mKeyCode = 0;
        public String mKeyLabel = null;
        public Drawable mKeyIcon = null;
        public Drawable mKeyIconPopup = null;
        public Object mKeyType = null;
        public ToggleState mNextState = null;

        private int stateId;
        private boolean repeat;
        private boolean balloon;

        public void setStateId(int id) {
            stateId = id;
        }

        public void setStateFlags(boolean repeat, boolean balloon) {
            this.repeat = repeat;
            this.balloon = balloon;
        }
    }
}
