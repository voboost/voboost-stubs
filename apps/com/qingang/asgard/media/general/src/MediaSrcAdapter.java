package com.qingang.asgard.media.general.src;

import com.qinggan.media.helper.FieldWrapper;
import com.qinggan.media.helper.MediaEnum;

public class MediaSrcAdapter {
    public static class MediaSrcHolder {
        public FieldWrapper srcMediaBean;
        public FieldWrapper binding;

        public MediaSrcHolder() {
            srcMediaBean = new FieldWrapper(new SrcMediaBean());
            binding = new FieldWrapper(new MediaSrcBinding());
        }

        public void bindView(int position) {
            // Implementation stub
        }
    }

    public static class SrcMediaBean {
        public MediaEnum getMediaEnum() {
            return new MediaEnum();
        }
    }

    public static class MediaSrcBinding {
        public ValueWrapper tvName;
        public ValueWrapper ivMain;

        public MediaSrcBinding() {
            tvName = new ValueWrapper();
            ivMain = new ValueWrapper();
        }
    }

    public static class ValueWrapper {
        public Object value;

        public ValueWrapper() {
            this.value = new Object();
        }
    }
}
