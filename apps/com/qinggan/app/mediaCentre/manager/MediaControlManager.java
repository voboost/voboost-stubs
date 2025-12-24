package com.qinggan.app.mediaCentre.manager;

import com.qinggan.media.helper.MediaEnum;
import com.qinggan.app.qgime.FieldWrapper;

/**
 * Stub implementation of MediaControlManager for testing purposes
 */
public class MediaControlManager {

    private MediaBrowserHelper mediaBrowserHelper;
    private MediaEnum currentMediaType;
    private boolean connected;

    public MediaControlManager() {
        this.connected = false;
    }

    public MediaEnum getMediaType() {
        return currentMediaType;
    }

    public void setMediaType(MediaEnum mediaType) {
        this.currentMediaType = mediaType;
    }

    public MediaBrowserHelper getMediaBrowserHelper() {
        if (mediaBrowserHelper == null) {
            mediaBrowserHelper = new MediaBrowserHelper();
        }
        return mediaBrowserHelper;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void play() {
        // Stub implementation
        System.out.println("[MediaControlManager] play() called");
    }

    /**
     * Inner class for MediaBrowserHelper
     */
    public static class MediaBrowserHelper {
        public FieldWrapper<String> mMediaType;
        public FieldWrapper<String> mMediaServicePackage;
        public FieldWrapper<String> mMediaServiceClass;

        public MediaBrowserHelper() {
            this.mMediaType = new FieldWrapper<>("");
            this.mMediaServicePackage = new FieldWrapper<>("");
            this.mMediaServiceClass = new FieldWrapper<>("");
        }

        public void onStop() {
            // Stub implementation
            System.out.println("[MediaBrowserHelper] onStop() called");
        }

        public void onStart() {
            // Stub implementation
            System.out.println("[MediaBrowserHelper] onStart() called");
        }
    }
}
