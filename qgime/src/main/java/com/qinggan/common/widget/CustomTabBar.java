package com.qinggan.common.widget;

import android.content.Context;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock CustomTabBar class for Frida agent testing
 */
public class CustomTabBar extends ViewGroup {
    private List<CustomTab> tabs;
    private int selectedTabIndex;

    public CustomTabBar(Context context) {
        super(context);
        System.out.println("[CustomTabBar] CustomTabBar created");
        this.tabs = new ArrayList<>();
        this.selectedTabIndex = -1;
    }

    public void addTab(CustomTab tab) {
        System.out.println("[CustomTabBar] addTab called");
        this.tabs.add(tab);
    }

    public void removeTab(CustomTab tab) {
        System.out.println("[CustomTabBar] removeTab called");
        this.tabs.remove(tab);
    }

    public void removeTab(int index) {
        System.out.println("[CustomTabBar] removeTab called with index: " + index);
        if (index >= 0 && index < tabs.size()) {
            tabs.remove(index);
        }
    }

    public CustomTab getTab(int index) {
        System.out.println("[CustomTabBar] getTab called with index: " + index);
        if (index >= 0 && index < tabs.size()) {
            return tabs.get(index);
        }
        return null;
    }

    public int getTabCount() {
        System.out.println("[CustomTabBar] getTabCount called, returning: " + tabs.size());
        return tabs.size();
    }

    public int getSelectedTabIndex() {
        System.out.println("[CustomTabBar] getSelectedTabIndex called, returning: " + selectedTabIndex);
        return selectedTabIndex;
    }

    public void setSelectedTabIndex(int index) {
        System.out.println("[CustomTabBar] setSelectedTabIndex called with: " + index);

        // Deselect previous tab
        if (selectedTabIndex >= 0 && selectedTabIndex < tabs.size()) {
            tabs.get(selectedTabIndex).setSelected(false);
        }

        // Select new tab
        if (index >= 0 && index < tabs.size()) {
            tabs.get(index).setSelected(true);
            selectedTabIndex = index;
        } else {
            selectedTabIndex = -1;
        }
    }

    public void selectTab(CustomTab tab) {
        System.out.println("[CustomTabBar] selectTab called");
        int index = tabs.indexOf(tab);
        if (index >= 0) {
            setSelectedTabIndex(index);
        }
    }

    public List<CustomTab> getTabs() {
        System.out.println("[CustomTabBar] getTabs called, returning: " + tabs.size() + " tabs");
        return new ArrayList<>(tabs);
    }

    public void clearAllTabs() {
        System.out.println("[CustomTabBar] clearAllTabs called");
        tabs.clear();
        selectedTabIndex = -1;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        System.out.println("[CustomTabBar] onLayout called");
        // Simple layout: place tabs horizontally
        int x = 0;
        for (int i = 0; i < tabs.size(); i++) {
            CustomTab tab = tabs.get(i);
            if (tab != null) {
                tab.layout(x, 0, x + 100, bottom - top);
                x += 100;
            }
        }
    }
}
