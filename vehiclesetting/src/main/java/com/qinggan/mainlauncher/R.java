package com.qinggan.mainlauncher;

/**
 * Mock R class for Android resource IDs
 */
public class R {
    public static class id {
        public static int screen_up_item_package = 0x7f080001;
        public static int screen_up_item_app_sub_type = 0x7f080002;

        static {
            System.out.println("[R.id] Resource IDs initialized");
            System.out.println("[R.id] screen_up_item_package = " + screen_up_item_package);
            System.out.println("[R.id] screen_up_item_app_sub_type = " + screen_up_item_app_sub_type);
        }
    }

    public static class drawable {
        public static int ic_launcher = 0x7f020001;

        static {
            System.out.println("[R.drawable] Drawable resources initialized");
            System.out.println("[R.drawable] ic_launcher = " + ic_launcher);
        }
    }

    public static class layout {
        public static int activity_main = 0x7f030001;

        static {
            System.out.println("[R.layout] Layout resources initialized");
            System.out.println("[R.layout] activity_main = " + activity_main);
        }
    }

    public static class string {
        public static int app_name = 0x7f040001;

        static {
            System.out.println("[R.string] String resources initialized");
            System.out.println("[R.string] app_name = " + app_name);
        }
    }

    static {
        System.out.println("[R] R class loaded with all resource constants");
    }
}
