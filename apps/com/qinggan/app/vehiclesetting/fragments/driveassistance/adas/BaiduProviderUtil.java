package com.qinggan.app.vehiclesetting.fragments.driveassistance.adas;

/**
 * Mock BaiduProviderUtil for Frida agent testing.
 * Used by: ADAS-activation-mod
 */
public class BaiduProviderUtil {

    /**
     * Queries subscription info.
     *
     * @return subscription info JSON string
     */
    public static String doQuerySubscribeInfo() {
        // Original returns subscription info
        // Agent hooks this to return activated status
        System.out.println("[BaiduProviderUtil] doQuerySubscribeInfo() called");
        return "{\"expireStatus\":\"1\",\"isMqtt\":false,\"remainDays\":\"0\",\"subscriptionStatus\":\"0\"}";
    }

    /**
     * Queries NOA learn info.
     *
     * @return NOA learn info string
     */
    public static String doQueryNOALearnInfo() {
        System.out.println("[BaiduProviderUtil] doQueryNOALearnInfo() called");
        return "0";
    }

    /**
     * Additional method that might be called by agents.
     *
     * @return mock activation status
     */
    public static boolean isActivated() {
        System.out.println("[BaiduProviderUtil] isActivated() called");
        return false; // Default to not activated, agents can hook this
    }

    /**
     * Additional method for subscription status check.
     *
     * @return subscription status
     */
    public static int getSubscriptionStatus() {
        System.out.println("[BaiduProviderUtil] getSubscriptionStatus() called");
        return 0; // Default to inactive
    }
}
