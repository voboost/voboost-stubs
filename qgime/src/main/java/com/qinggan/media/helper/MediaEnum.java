package com.qinggan.media.helper;

public class MediaEnum {
    public static final MediaEnum WECAR_FLOW = new MediaEnum();
    public static final MediaEnum XMLA_MUSIC = new MediaEnum();
    public static final MediaEnum RADIO_YUNTING = new MediaEnum();

    public MediaService service;

    public static class MediaService {
        public FieldWrapper pageName;
        public FieldWrapper servicePageName;
        public FieldWrapper serviceName;
        public FieldWrapper clientId;

        public MediaService() {
            pageName = new FieldWrapper("");
            servicePageName = new FieldWrapper("");
            serviceName = new FieldWrapper("");
            clientId = new FieldWrapper("");
        }
    }

    @Override
    public String toString() {
        return "MediaEnum";
    }
}
