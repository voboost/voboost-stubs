package com.pateo.iloader.common.bean;

/**
 * Mock SourceBean class for Frida agent testing
 */
public class SourceBean {
    public String pkgName;
    public String className;
    public String sourceName;
    public String sourceIcon;
    public String sourceId;
    public int sourceType;

    public SourceBean() {
        System.out.println("[SourceBean] SourceBean created");
    }

    public String getPkgName() {
        System.out.println("[SourceBean] getPkgName called, returning: " + pkgName);
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        System.out.println("[SourceBean] setPkgName called with: " + pkgName);
        this.pkgName = pkgName;
    }

    public String getClassName() {
        System.out.println("[SourceBean] getClassName called, returning: " + className);
        return className;
    }

    public void setClassName(String className) {
        System.out.println("[SourceBean] setClassName called with: " + className);
        this.className = className;
    }

    public String getSourceName() {
        System.out.println("[SourceBean] getSourceName called, returning: " + sourceName);
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        System.out.println("[SourceBean] setSourceName called with: " + sourceName);
        this.sourceName = sourceName;
    }

    public String getSourceIcon() {
        System.out.println("[SourceBean] getSourceIcon called, returning: " + sourceIcon);
        return sourceIcon;
    }

    public void setSourceIcon(String sourceIcon) {
        System.out.println("[SourceBean] setSourceIcon called with: " + sourceIcon);
        this.sourceIcon = sourceIcon;
    }

    public String getSourceId() {
        System.out.println("[SourceBean] getSourceId called, returning: " + sourceId);
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        System.out.println("[SourceBean] setSourceId called with: " + sourceId);
        this.sourceId = sourceId;
    }

    public int getSourceType() {
        System.out.println("[SourceBean] getSourceType called, returning: " + sourceType);
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        System.out.println("[SourceBean] setSourceType called with: " + sourceType);
        this.sourceType = sourceType;
    }
}
