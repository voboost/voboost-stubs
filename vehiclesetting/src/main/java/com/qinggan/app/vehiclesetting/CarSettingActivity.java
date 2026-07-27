package com.qinggan.app.vehiclesetting;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Stub for the real com.qinggan.app.vehiclesetting.CarSettingActivity settings
 * menu. Mirrors the real activity_car_setting.xml structure closely enough
 * for the voboost-script settings-menu agent to exercise the same view-tree
 * (menuContainer -&gt; itemsLayout(LinearLayout) -&gt; [feature-row group,
 * mainMenuItemSystemSetting]) and clone code paths on the emulator, so
 * layout/positioning bugs are catchable without the real device. Ground
 * truth (268dp row width, 30x30dp chevron, 26dp marginRight) is from the
 * decompiled activity_car_setting.xml, not guessed.
 */
public class CarSettingActivity extends Activity {
    public CarSettingActivityBinding carSettingBinding;

    private static final String[] MENU_LABELS = {
        "Quick app", "CHG & DISC", "Lighting", "Door & Win",
        "Drv pref", "Drv assist", "SFTY maint", "Vehicle health",
    };

    private static final int ROW_WIDTH = 268;
    private static final int ROW_HEIGHT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        carSettingBinding = new CarSettingActivityBinding(this);
        setContentView(carSettingBinding.menuContainer);
    }

    /** Mirrors the real ActivityCarSettingBinding: a menuContainer (single-child
     * scroll wrapper) and the mainMenuItemSystemSetting row field. */
    public static class CarSettingActivityBinding {
        public final ViewGroup menuContainer;
        public final RelativeLayout mainMenuItemSystemSetting;

        public CarSettingActivityBinding(Activity activity) {
            LinearLayout itemsLayout = new LinearLayout(activity);
            itemsLayout.setOrientation(LinearLayout.VERTICAL);
            itemsLayout.addView(buildFeatureRowGroup(activity));

            mainMenuItemSystemSetting = buildSystemSettingRow(activity);
            itemsLayout.addView(mainMenuItemSystemSetting);

            // menuContainer (real OverScrollView) is a single-child scroll
            // wrapper around itemsLayout; a plain ScrollView reproduces both
            // the "one child" shape AND actual scroll behavior (needed to
            // reach rows below the fold on the emulator's screen), without
            // needing the real vendor class.
            ScrollView scrollWrapper = new ScrollView(activity);
            scrollWrapper.addView(itemsLayout);
            menuContainer = scrollWrapper;
        }

        /** The scrolling group of regular feature rows (real: QGRadioGroup of
         * BoldRadioButton), ending in "Vehicle health" — the row the
         * settings-menu agent clones as its Voboost entry's basis. */
        private static RadioGroup buildFeatureRowGroup(Activity activity) {
            RadioGroup group = new RadioGroup(activity);
            group.setOrientation(RadioGroup.VERTICAL);
            for (String text : MENU_LABELS) {
                RadioButton row = new RadioButton(activity);
                row.setText(text);
                row.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ROW_WIDTH, ROW_HEIGHT);
                lp.topMargin = 40;
                row.setLayoutParams(lp);
                group.addView(row);
            }
            return group;
        }

        /** The pinned Settings row: RelativeLayout + centered label + a
         * right-edge chevron ImageView (background, not src — matching the
         * real icon_menu_more_switch_setting state-list-as-background). */
        private static RelativeLayout buildSystemSettingRow(Activity activity) {
            RelativeLayout row = new RelativeLayout(activity);
            LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(ROW_WIDTH, ROW_HEIGHT);
            rowLp.leftMargin = 30;
            rowLp.topMargin = 40;
            rowLp.bottomMargin = 15;
            row.setLayoutParams(rowLp);

            TextView label = new TextView(activity);
            label.setText("Settings");
            label.setTextColor(Color.WHITE);
            label.setGravity(Gravity.CENTER);
            RelativeLayout.LayoutParams labelLp = new RelativeLayout.LayoutParams(ROW_WIDTH, 80);
            labelLp.topMargin = 20;
            label.setLayoutParams(labelLp);
            row.addView(label);

            ImageView chevron = new ImageView(activity);
            chevron.setBackgroundResource(android.R.drawable.ic_menu_more);
            RelativeLayout.LayoutParams chevronLp = new RelativeLayout.LayoutParams(30, 30);
            chevronLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            chevronLp.topMargin = 25;
            chevronLp.rightMargin = 26;
            chevron.setLayoutParams(chevronLp);
            row.addView(chevron);

            return row;
        }
    }
}
