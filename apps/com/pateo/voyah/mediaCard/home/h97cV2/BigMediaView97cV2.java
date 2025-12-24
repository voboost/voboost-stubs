package com.pateo.voyah.mediaCard.home.h97cV2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.pateo.voyah.mediaCard.home.inter.MediaBeanInter;
import com.qinggan.media.helper.FieldWrapper;

public class BigMediaView97cV2 extends View {
    public FieldWrapper mediaInfoHelper;
    public FieldWrapper binding;

    public BigMediaView97cV2(Context context) {
        super(context);
        initializeFields();
    }

    public BigMediaView97cV2(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeFields();
    }

    public BigMediaView97cV2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeFields();
    }

    private void initializeFields() {
        mediaInfoHelper = new FieldWrapper(null);
        binding = new FieldWrapper(null);
    }

    public void updateTitleUI(MediaBeanInter mediaBeanInter) {
        // Stub implementation
    }

    public void openMediaPage() {
        // Stub implementation
    }
}
