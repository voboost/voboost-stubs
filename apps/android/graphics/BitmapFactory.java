package android.graphics;

import android.content.res.Resources;
import java.io.InputStream;

public class BitmapFactory {
    public static Bitmap decodeResource(Resources res, int id) {
        // Mock implementation - return a simple bitmap
        return Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
    }

    public static Bitmap decodeStream(InputStream is) {
        // Mock implementation - return a simple bitmap
        return Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
    }

    public static Bitmap decodeByteArray(byte[] data, int offset, int length) {
        // Mock implementation - return a simple bitmap
        return Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
    }

    public static class Options {
        public boolean inJustDecodeBounds = false;
        public int inSampleSize = 1;
        public int outWidth = 0;
        public int outHeight = 0;
        public String outMimeType = null;
    }
}
