package com.qinggan.audiopolicy;

public class AudioPolicyInfo {

    public AudioPolicyInfo() {
        System.out.println("[AudioPolicyInfo] AudioPolicyInfo constructor called");
    }

    public String getPackageName() {
        System.out.println("[AudioPolicyInfo] getPackageName called");
        try {
            String packageName = "";
            System.out.println("[AudioPolicyInfo] getPackageName returning: " + packageName);
            return packageName;
        } catch (Exception e) {
            System.out.println("[AudioPolicyInfo] Error in getPackageName: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }
}
