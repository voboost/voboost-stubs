package com.qinggan.launcher.base.allapp;

import java.util.ArrayList;

/**
 * Mock AllAppDataManager for Frida agent testing
 */
public class AllAppDataManager {
    private static final AllAppDataManager instance = new AllAppDataManager();

    // Public fields for Frida agent access
    public ArrayList<Object> mMainAllApps = new ArrayList<>();
    public ArrayList<Object> mAllAppDataListeners = new ArrayList<>();

    // Field wrapper classes for .value access pattern
    public static class FieldWrapper<T> {
        public T value;
        public FieldWrapper(T value) {
            this.value = value;
        }
    }

    public FieldWrapper<ArrayList<Object>> mMainAllAppsWrapper = new FieldWrapper<>(mMainAllApps);
    public FieldWrapper<ArrayList<Object>> mAllAppDataListenersWrapper = new FieldWrapper<>(mAllAppDataListeners);

    /**
     * Gets the singleton instance of AllAppDataManager.
     *
     * @return the AllAppDataManager instance
     */
    public static AllAppDataManager getInstance() {
        return instance;
    }

    /**
     * Gets all apps for a specific screen ID.
     *
     * @param screenId the screen ID to get apps for
     * @return list of all apps
     */
    public ArrayList<Object> getAllApps(int screenId) {
        System.out.println("[AllAppDataManager] getAllApps called for screenId: " + screenId);
        return mMainAllApps;
    }

    /**
     * Adds an app to the main apps list.
     *
     * @param app the app to add
     */
    public void addApp(Object app) {
        mMainAllApps.add(app);
        System.out.println("[AllAppDataManager] Added app, total count: " + mMainAllApps.size());
    }

    /**
     * Adds a listener to the data listeners list.
     *
     * @param listener the listener to add
     */
    public void addDataListener(Object listener) {
        mAllAppDataListeners.add(listener);
        System.out.println("[AllAppDataManager] Added data listener, total count: " + mAllAppDataListeners.size());
    }
}
