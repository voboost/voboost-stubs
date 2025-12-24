package com.pateo.voyah.mediaCard.home.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Fragment;
import android.content.Context;
import com.pateo.voyah.mediaCard.home.inter.MediaBeanInter;
import com.pateo.iloader.common.bean.SourceBean;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock MediaCardHomeFragment class for Frida agent testing
 */
public class MediaCardHomeFragment extends Fragment {
    private Context context;
    private List<MediaBeanInter> mediaBeanInterList;
    private List<SourceBean> sourceBeanList;

    public MediaCardHomeFragment() {
        System.out.println("[MediaCardHomeFragment] MediaCardHomeFragment created");
        this.mediaBeanInterList = new ArrayList<>();
        this.sourceBeanList = new ArrayList<>();
        this.context = null; // Will be set later
    }

    public MediaCardHomeFragment(Context context) {
        System.out.println("[MediaCardHomeFragment] MediaCardHomeFragment created with context");
        this.mediaBeanInterList = new ArrayList<>();
        this.sourceBeanList = new ArrayList<>();
        this.context = context;
        setContext(context);
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("[MediaCardHomeFragment] onCreateView called");
        return new View(context);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        System.out.println("[MediaCardHomeFragment] onViewCreated called");
    }

    public List<MediaBeanInter> getMediaBeanInterList() {
        System.out.println("[MediaCardHomeFragment] getMediaBeanInterList called, size: " + mediaBeanInterList.size());
        return mediaBeanInterList;
    }

    public void setMediaBeanInterList(List<MediaBeanInter> mediaBeanInterList) {
        System.out.println("[MediaCardHomeFragment] setMediaBeanInterList called with size: " + mediaBeanInterList.size());
        this.mediaBeanInterList = mediaBeanInterList;
    }

    public List<SourceBean> getSourceBeanList() {
        System.out.println("[MediaCardHomeFragment] getSourceBeanList called, size: " + sourceBeanList.size());
        return sourceBeanList;
    }

    public void setSourceBeanList(List<SourceBean> sourceBeanList) {
        System.out.println("[MediaCardHomeFragment] setSourceBeanList called with size: " + sourceBeanList.size());
        this.sourceBeanList = sourceBeanList;
    }

    public void addMediaBeanInter(MediaBeanInter mediaBeanInter) {
        System.out.println("[MediaCardHomeFragment] addMediaBeanInter called");
        this.mediaBeanInterList.add(mediaBeanInter);
    }

    public void addSourceBean(SourceBean sourceBean) {
        System.out.println("[MediaCardHomeFragment] addSourceBean called");
        this.sourceBeanList.add(sourceBean);
    }
}
