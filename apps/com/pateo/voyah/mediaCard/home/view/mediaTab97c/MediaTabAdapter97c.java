package com.pateo.voyah.mediaCard.home.view.mediaTab97c;

import com.pateo.voyah.mediaCard.home.inter.MediaBeanInter;
import android.widget.TextView;
import android.widget.ImageView;
import com.qinggan.media.helper.FieldWrapper;

/**
 * Stub implementation of MediaTabAdapter97c for testing purposes
 */
public class MediaTabAdapter97c {

    /**
     * Inner class for MediaTabHolder
     */
    public static class MediaTabHolder {
        public FieldWrapper mediaBean;
        public FieldWrapper tvName;
        public FieldWrapper ivIcon;

        public MediaTabHolder() {
            // Initialize with wrapped fields
            mediaBean = new FieldWrapper(null);
            tvName = new FieldWrapper(null);
            ivIcon = new FieldWrapper(null);
        }

        public void bindView(int dataIndex) {
            // Stub implementation
        }
    }
}
