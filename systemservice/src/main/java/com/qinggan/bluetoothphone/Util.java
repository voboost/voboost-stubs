package com.qinggan.bluetoothphone.util;

public class Util {

    public static String getAmendNumber(Object number) {
        System.out.println("[Util] getAmendNumber called with number: " + (number != null ? number.toString() : "null"));
        try {
            if (number == null) {
                System.out.println("[Util] getAmendNumber received null number, returning empty string");
                return "";
            }
            String result = number.toString().trim();
            System.out.println("[Util] getAmendNumber returning: " + result);
            return result;
        } catch (Exception e) {
            System.out.println("[Util] Error in getAmendNumber: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }
}
