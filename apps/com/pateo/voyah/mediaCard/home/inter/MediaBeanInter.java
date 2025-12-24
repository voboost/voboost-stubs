package com.pateo.voyah.mediaCard.home.inter;

import com.qinggan.media.helper.MediaEnum;

public class MediaBeanInter {
    private String pageName;
    private String name;
    private String icon;
    private boolean autoPlay;

    public MediaBeanInter() {
        // Stub implementation
    }

    public MediaEnum getMediaEnum() {
        // Return a default enum value for testing
        return null;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    public String getPageName() {
        return pageName;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public boolean isAutoPlay() {
        return autoPlay;
    }
}
