package com.qingang.asgard.media.general.src;

import com.qinggan.media.helper.FieldWrapper;
import com.qinggan.media.helper.MediaEnum;

public class MediaResEnum {
    public FieldWrapper mediaEnum;

    public MediaResEnum() {
        System.out.println("[MediaResEnum] MediaResEnum constructor called");
        try {
            mediaEnum = new FieldWrapper(new MediaEnum());
            System.out.println("[MediaResEnum] MediaResEnum initialized successfully");
        } catch (Exception e) {
            System.out.println("[MediaResEnum] Error initializing MediaResEnum: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
