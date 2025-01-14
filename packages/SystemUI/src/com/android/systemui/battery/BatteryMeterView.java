/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.systemui.battery;

import static java.lang.annotation.RetentionPolicy.SOURCE;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.annotation.IntDef;
import android.annotation.IntRange;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.StyleRes;
import androidx.annotation.VisibleForTesting;

import com.android.settingslib.graph.CircleBatteryDrawable;
import com.android.settingslib.graph.FullCircleBatteryDrawable;
import com.android.settingslib.graph.RLandscapeBatteryDrawable;
import com.android.settingslib.graph.LandscapeBatteryDrawable;
import com.android.settingslib.graph.RLandscapeBatteryDrawableStyleA;
import com.android.settingslib.graph.LandscapeBatteryDrawableStyleA;
import com.android.settingslib.graph.RLandscapeBatteryDrawableStyleB;
import com.android.settingslib.graph.LandscapeBatteryDrawableStyleB;
import com.android.settingslib.graph.LandscapeBatteryDrawableBuddy;
import com.android.settingslib.graph.LandscapeBatteryDrawableLine;
import com.android.settingslib.graph.LandscapeBatteryDrawableSignal;
import com.android.settingslib.graph.LandscapeBatteryDrawableMusku;
import com.android.settingslib.graph.LandscapeBatteryDrawablePill;
import com.android.settingslib.graph.LandscapeBatteryA;
import com.android.settingslib.graph.LandscapeBatteryB;
import com.android.settingslib.graph.LandscapeBatteryC;
import com.android.settingslib.graph.LandscapeBatteryD;
import com.android.settingslib.graph.LandscapeBatteryE;
import com.android.settingslib.graph.LandscapeBatteryF;
import com.android.settingslib.graph.LandscapeBatteryG;
import com.android.settingslib.graph.LandscapeBatteryH;
import com.android.settingslib.graph.LandscapeBatteryI;
import com.android.settingslib.graph.LandscapeBatteryJ;
import com.android.settingslib.graph.LandscapeBatteryK;
import com.android.settingslib.graph.LandscapeBatteryL;
import com.android.settingslib.graph.LandscapeBatteryM;
import com.android.settingslib.graph.LandscapeBatteryN;
import com.android.settingslib.graph.LandscapeBatteryO;
import com.android.systemui.DualToneHandler;
import com.android.systemui.R;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.plugins.DarkIconDispatcher.DarkReceiver;
import com.android.systemui.statusbar.policy.BatteryController;

import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.text.NumberFormat;
import java.util.ArrayList;

public class BatteryMeterView extends LinearLayout implements DarkReceiver {

    protected static final int BATTERY_STYLE_PORTRAIT = 0;
    protected static final int BATTERY_STYLE_CIRCLE = 1;
    protected static final int BATTERY_STYLE_DOTTED_CIRCLE = 2;
    protected static final int BATTERY_STYLE_FULL_CIRCLE = 3;
    protected static final int BATTERY_STYLE_TEXT = 4;
    protected static final int BATTERY_STYLE_HIDDEN = 5;
    protected static final int BATTERY_STYLE_RLANDSCAPE = 6;
    protected static final int BATTERY_STYLE_LANDSCAPE = 7;
    protected static final int BATTERY_STYLE_BIG_CIRCLE = 8;
    protected static final int BATTERY_STYLE_BIG_DOTTED_CIRCLE = 9;
    protected static final int BATTERY_STYLE_LANDSCAPE_BUDDY = 10;
    protected static final int BATTERY_STYLE_LANDSCAPE_LINE = 11;
    protected static final int BATTERY_STYLE_LANDSCAPE_MUSKU = 12;
    protected static final int BATTERY_STYLE_LANDSCAPE_PILL = 13;
    protected static final int BATTERY_STYLE_LANDSCAPE_SIGNAL = 14;
    protected static final int BATTERY_STYLE_RLANDSCAPE_STYLE_A = 15;
    protected static final int BATTERY_STYLE_LANDSCAPE_STYLE_A = 16;
    protected static final int BATTERY_STYLE_RLANDSCAPE_STYLE_B = 17;
    protected static final int BATTERY_STYLE_LANDSCAPE_STYLE_B = 18;
    protected static final int BATTERY_STYLE_LANDSCAPEA = 19;
    protected static final int BATTERY_STYLE_LANDSCAPEB = 20;
    protected static final int BATTERY_STYLE_LANDSCAPEC = 21;
    protected static final int BATTERY_STYLE_LANDSCAPED = 22;
    protected static final int BATTERY_STYLE_LANDSCAPEE = 23;
    protected static final int BATTERY_STYLE_LANDSCAPEF = 24;
    protected static final int BATTERY_STYLE_LANDSCAPEG = 25;
    protected static final int BATTERY_STYLE_LANDSCAPEH = 26;
    protected static final int BATTERY_STYLE_LANDSCAPEI = 27;
    protected static final int BATTERY_STYLE_LANDSCAPEJ = 28;
    protected static final int BATTERY_STYLE_LANDSCAPEK = 29;
    protected static final int BATTERY_STYLE_LANDSCAPEL = 30;
    protected static final int BATTERY_STYLE_LANDSCAPEM = 31;
    protected static final int BATTERY_STYLE_LANDSCAPEN = 32;
    protected static final int BATTERY_STYLE_LANDSCAPEO = 33;

    @Retention(SOURCE)
    @IntDef({MODE_DEFAULT, MODE_ON, MODE_OFF, MODE_ESTIMATE})
    public @interface BatteryPercentMode {}
    public static final int MODE_DEFAULT = 0;
    public static final int MODE_ON = 1;
    public static final int MODE_OFF = 2;
    public static final int MODE_ESTIMATE = 3;

    private final AccessorizedBatteryDrawable mAccessorizedDrawable;
    private final CircleBatteryDrawable mCircleDrawable;
    private final FullCircleBatteryDrawable mFullCircleDrawable;
    private final RLandscapeBatteryDrawable mRLandscapeDrawable;
    private final LandscapeBatteryDrawable mLandscapeDrawable;
    private final RLandscapeBatteryDrawableStyleA mRLandscapeDrawableStyleA;
    private final LandscapeBatteryDrawableStyleA mLandscapeDrawableStyleA;
    private final RLandscapeBatteryDrawableStyleB mRLandscapeDrawableStyleB;
    private final LandscapeBatteryDrawableStyleB mLandscapeDrawableStyleB;
    private final LandscapeBatteryDrawableBuddy mLandscapeDrawableBuddy;
    private final LandscapeBatteryDrawableLine mLandscapeDrawableLine;
    private final LandscapeBatteryDrawableMusku mLandscapeDrawableMusku;
    private final LandscapeBatteryDrawablePill mLandscapeDrawablePill;
    private final LandscapeBatteryDrawableSignal mLandscapeDrawableSignal;
    private final LandscapeBatteryA mLandscapeBatteryA;
    private final LandscapeBatteryB mLandscapeBatteryB;
    private final LandscapeBatteryC mLandscapeBatteryC;
    private final LandscapeBatteryD mLandscapeBatteryD;
    private final LandscapeBatteryE mLandscapeBatteryE;
    private final LandscapeBatteryF mLandscapeBatteryF;
    private final LandscapeBatteryG mLandscapeBatteryG;
    private final LandscapeBatteryH mLandscapeBatteryH;
    private final LandscapeBatteryI mLandscapeBatteryI;
    private final LandscapeBatteryJ mLandscapeBatteryJ;
    private final LandscapeBatteryK mLandscapeBatteryK;
    private final LandscapeBatteryL mLandscapeBatteryL;
    private final LandscapeBatteryM mLandscapeBatteryM;
    private final LandscapeBatteryN mLandscapeBatteryN;
    private final LandscapeBatteryO mLandscapeBatteryO;
    private final ImageView mBatteryIconView;
    private TextView mBatteryPercentView;

    private final @StyleRes int mPercentageStyleId;
    private int mTextColor;
    private int mLevel;
    private int mShowPercentMode = MODE_DEFAULT;
    private String mEstimateText = null;
    private boolean mCharging;
    private boolean mIsOverheated;
    private boolean mDisplayShieldEnabled;
    // Error state where we know nothing about the current battery state
    private boolean mBatteryStateUnknown;
    // Lazily-loaded since this is expected to be a rare-if-ever state
    private Drawable mUnknownStateDrawable;

    private int mBatteryStyle = BATTERY_STYLE_PORTRAIT;
    private int mShowBatteryPercent;
    private boolean mBatteryPercentCharging;

    private DualToneHandler mDualToneHandler;

    private final ArrayList<BatteryMeterViewCallbacks> mCallbacks = new ArrayList<>();

    private int mNonAdaptedSingleToneColor;
    private int mNonAdaptedForegroundColor;
    private int mNonAdaptedBackgroundColor;

    private BatteryEstimateFetcher mBatteryEstimateFetcher;

    public BatteryMeterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BatteryMeterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL | Gravity.START);

        TypedArray atts = context.obtainStyledAttributes(attrs, R.styleable.BatteryMeterView,
                defStyle, 0);
        final int frameColor = atts.getColor(R.styleable.BatteryMeterView_frameColor,
                context.getColor(R.color.meter_background_color));
        mPercentageStyleId = atts.getResourceId(R.styleable.BatteryMeterView_textAppearance, 0);
        mAccessorizedDrawable = new AccessorizedBatteryDrawable(context, frameColor);
        mCircleDrawable = new CircleBatteryDrawable(context, frameColor);
        mFullCircleDrawable = new FullCircleBatteryDrawable(context, frameColor);
        mRLandscapeDrawable = new RLandscapeBatteryDrawable(context, frameColor);
        mLandscapeDrawable = new LandscapeBatteryDrawable(context, frameColor);
        mRLandscapeDrawableStyleA = new RLandscapeBatteryDrawableStyleA(context, frameColor);
        mLandscapeDrawableStyleA = new LandscapeBatteryDrawableStyleA(context, frameColor);
        mRLandscapeDrawableStyleB = new RLandscapeBatteryDrawableStyleB(context, frameColor);
        mLandscapeDrawableStyleB = new LandscapeBatteryDrawableStyleB(context, frameColor);
        mLandscapeDrawableBuddy = new LandscapeBatteryDrawableBuddy(context, frameColor);
        mLandscapeDrawableLine = new LandscapeBatteryDrawableLine(context, frameColor);
        mLandscapeDrawableMusku = new LandscapeBatteryDrawableMusku(context, frameColor);
        mLandscapeDrawablePill = new LandscapeBatteryDrawablePill(context, frameColor);
        mLandscapeDrawableSignal = new LandscapeBatteryDrawableSignal(context, frameColor);
        mLandscapeBatteryA = new LandscapeBatteryA(context, frameColor);
        mLandscapeBatteryB = new LandscapeBatteryB(context, frameColor);
        mLandscapeBatteryC = new LandscapeBatteryC(context, frameColor);
        mLandscapeBatteryD = new LandscapeBatteryD(context, frameColor);
        mLandscapeBatteryE = new LandscapeBatteryE(context, frameColor);
        mLandscapeBatteryF = new LandscapeBatteryF(context, frameColor);
        mLandscapeBatteryG = new LandscapeBatteryG(context, frameColor);
        mLandscapeBatteryH = new LandscapeBatteryH(context, frameColor);
        mLandscapeBatteryI = new LandscapeBatteryI(context, frameColor);
        mLandscapeBatteryJ = new LandscapeBatteryJ(context, frameColor);
        mLandscapeBatteryK = new LandscapeBatteryK(context, frameColor);
        mLandscapeBatteryL = new LandscapeBatteryL(context, frameColor);
        mLandscapeBatteryM = new LandscapeBatteryM(context, frameColor);
        mLandscapeBatteryN = new LandscapeBatteryN(context, frameColor);
        mLandscapeBatteryO = new LandscapeBatteryO(context, frameColor);
        atts.recycle();

        setupLayoutTransition();

        mBatteryIconView = new ImageView(context);
        mBatteryStyle = Settings.System.getIntForUser(mContext.getContentResolver(),
                Settings.System.STATUS_BAR_BATTERY_STYLE, BATTERY_STYLE_PORTRAIT, UserHandle.USER_CURRENT);
        updateDrawable();

        int batteryHeight = mBatteryStyle == BATTERY_STYLE_CIRCLE || mBatteryStyle == BATTERY_STYLE_DOTTED_CIRCLE
                || mBatteryStyle == BATTERY_STYLE_FULL_CIRCLE ?
                getResources().getDimensionPixelSize(R.dimen.status_bar_battery_icon_circle_width) :
                getResources().getDimensionPixelSize(R.dimen.status_bar_battery_icon_height);
        int batteryWidth = mBatteryStyle == BATTERY_STYLE_CIRCLE || mBatteryStyle == BATTERY_STYLE_DOTTED_CIRCLE
                || mBatteryStyle == BATTERY_STYLE_FULL_CIRCLE ?
                getResources().getDimensionPixelSize(R.dimen.status_bar_battery_icon_circle_width) :
                getResources().getDimensionPixelSize(R.dimen.status_bar_battery_icon_width);

        final MarginLayoutParams mlp = new MarginLayoutParams(batteryWidth, batteryHeight);
        mlp.setMargins(0, 0, 0,
                getResources().getDimensionPixelOffset(R.dimen.battery_margin_bottom));
        addView(mBatteryIconView, mlp);

        updatePercentView();
        updateVisibility();

        mDualToneHandler = new DualToneHandler(context);
        // Init to not dark at all.
        onDarkChanged(new ArrayList<Rect>(), 0, DarkIconDispatcher.DEFAULT_ICON_TINT);

        setClipChildren(false);
        setClipToPadding(false);
    }

    private void setupLayoutTransition() {
        LayoutTransition transition = new LayoutTransition();
        transition.setDuration(200);

        // Animates appearing/disappearing of the battery percentage text using fade-in/fade-out
        // and disables all other animation types
        ObjectAnimator appearAnimator = ObjectAnimator.ofFloat(null, "alpha", 0f, 1f);
        transition.setAnimator(LayoutTransition.APPEARING, appearAnimator);
        transition.setInterpolator(LayoutTransition.APPEARING, Interpolators.ALPHA_IN);

        ObjectAnimator disappearAnimator = ObjectAnimator.ofFloat(null, "alpha", 1f, 0f);
        transition.setInterpolator(LayoutTransition.DISAPPEARING, Interpolators.ALPHA_OUT);
        transition.setAnimator(LayoutTransition.DISAPPEARING, disappearAnimator);

        transition.setAnimator(LayoutTransition.CHANGE_APPEARING, null);
        transition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, null);
        transition.setAnimator(LayoutTransition.CHANGING, null);

        setLayoutTransition(transition);
    }

    public int getBatteryStyle() {
        return mBatteryStyle;
    }

    public void setBatteryStyle(int batteryStyle) {
        if (batteryStyle == mBatteryStyle) return;
        mBatteryStyle = batteryStyle;
        updateBatteryStyle();
    }

    protected void updateBatteryStyle() {
        updateDrawable();
        scaleBatteryMeterViews();
        updatePercentView();
        updateVisibility();
    }

    public void setBatteryPercent(int showBatteryPercent) {
        if (showBatteryPercent == mShowBatteryPercent) return;
        mShowBatteryPercent = showBatteryPercent;
        updatePercentView();
    }

    protected void setBatteryPercentCharging(boolean batteryPercentCharging) {
        if (batteryPercentCharging == mBatteryPercentCharging) return;
        mBatteryPercentCharging = batteryPercentCharging;
        updatePercentView();
    }

    public void setForceShowPercent(boolean show) {
        setPercentShowMode(show ? MODE_ON : MODE_DEFAULT);
    }

    /**
     * Force a particular mode of showing percent
     *
     * 0 - No preference
     * 1 - Force on
     * 2 - Force off
     * 3 - Estimate
     * @param mode desired mode (none, on, off)
     */
    public void setPercentShowMode(@BatteryPercentMode int mode) {
        if (mode == mShowPercentMode) return;
        mShowPercentMode = mode;
        updateShowPercent();
        updatePercentText();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateBatteryStyle();
        mAccessorizedDrawable.notifyDensityChanged();
    }

    public void setColorsFromContext(Context context) {
        if (context == null) {
            return;
        }

        mDualToneHandler.setColorsFromContext(context);
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    /**
     * Update battery level
     *
     * @param level     int between 0 and 100 (representing percentage value)
     * @param pluggedIn whether the device is plugged in or not
     */
    public void onBatteryLevelChanged(@IntRange(from = 0, to = 100) int level, boolean pluggedIn) {
        if (mLevel != level) {
            mLevel = level;
            mAccessorizedDrawable.setBatteryLevel(mLevel);
            mCircleDrawable.setBatteryLevel(mLevel);
            mFullCircleDrawable.setBatteryLevel(mLevel);
            mRLandscapeDrawable.setBatteryLevel(mLevel);
            mLandscapeDrawable.setBatteryLevel(mLevel);
            mRLandscapeDrawableStyleA.setBatteryLevel(mLevel);
            mLandscapeDrawableStyleA.setBatteryLevel(mLevel);
            mRLandscapeDrawableStyleB.setBatteryLevel(mLevel);
            mLandscapeDrawableStyleB.setBatteryLevel(mLevel);
            mLandscapeDrawableBuddy.setBatteryLevel(mLevel);
            mLandscapeDrawableLine.setBatteryLevel(mLevel);
            mLandscapeDrawableMusku.setBatteryLevel(mLevel);
            mLandscapeDrawablePill.setBatteryLevel(mLevel);
            mLandscapeDrawableSignal.setBatteryLevel(mLevel);
            mLandscapeBatteryA.setBatteryLevel(mLevel);
            mLandscapeBatteryB.setBatteryLevel(mLevel);
            mLandscapeBatteryC.setBatteryLevel(mLevel);
            mLandscapeBatteryD.setBatteryLevel(mLevel);
            mLandscapeBatteryE.setBatteryLevel(mLevel);
            mLandscapeBatteryF.setBatteryLevel(mLevel);
            mLandscapeBatteryG.setBatteryLevel(mLevel);
            mLandscapeBatteryH.setBatteryLevel(mLevel);
            mLandscapeBatteryI.setBatteryLevel(mLevel);
            mLandscapeBatteryJ.setBatteryLevel(mLevel);
            mLandscapeBatteryK.setBatteryLevel(mLevel);
            mLandscapeBatteryL.setBatteryLevel(mLevel);
            mLandscapeBatteryM.setBatteryLevel(mLevel);
            mLandscapeBatteryN.setBatteryLevel(mLevel);
            mLandscapeBatteryO.setBatteryLevel(mLevel);
            updatePercentText();
        }
        if (mCharging != pluggedIn) {
            mCharging = pluggedIn;
            mAccessorizedDrawable.setCharging(mCharging);
            mCircleDrawable.setCharging(mCharging);
            mFullCircleDrawable.setCharging(mCharging);
            mRLandscapeDrawable.setCharging(mCharging);
            mLandscapeDrawable.setCharging(mCharging);
            mRLandscapeDrawableStyleA.setCharging(mCharging);
            mLandscapeDrawableStyleA.setCharging(mCharging);
            mRLandscapeDrawableStyleB.setCharging(mCharging);
            mLandscapeDrawableStyleB.setCharging(mCharging);
            mLandscapeDrawableBuddy.setCharging(mCharging);
            mLandscapeDrawableLine.setCharging(mCharging);
            mLandscapeDrawableMusku.setCharging(mCharging);
            mLandscapeDrawablePill.setCharging(mCharging);
            mLandscapeDrawableSignal.setCharging(mCharging);
            mLandscapeBatteryA.setCharging(mCharging);
            mLandscapeBatteryB.setCharging(mCharging);
            mLandscapeBatteryC.setCharging(mCharging);
            mLandscapeBatteryD.setCharging(mCharging);
            mLandscapeBatteryE.setCharging(mCharging);
            mLandscapeBatteryF.setCharging(mCharging);
            mLandscapeBatteryG.setCharging(mCharging);
            mLandscapeBatteryH.setCharging(mCharging);
            mLandscapeBatteryI.setCharging(mCharging);
            mLandscapeBatteryJ.setCharging(mCharging);
            mLandscapeBatteryK.setCharging(mCharging);
            mLandscapeBatteryL.setCharging(mCharging);
            mLandscapeBatteryM.setCharging(mCharging);
            mLandscapeBatteryN.setCharging(mCharging);
            mLandscapeBatteryO.setCharging(mCharging);
            updateShowPercent();
            updatePercentText();
        }
    }

    void onPowerSaveChanged(boolean isPowerSave) {
        mAccessorizedDrawable.setPowerSaveEnabled(isPowerSave);
        mCircleDrawable.setPowerSaveEnabled(isPowerSave);
        mFullCircleDrawable.setPowerSaveEnabled(isPowerSave);
        mRLandscapeDrawable.setPowerSaveEnabled(isPowerSave);
        mLandscapeDrawable.setPowerSaveEnabled(isPowerSave);
        mRLandscapeDrawableStyleA.setPowerSaveEnabled(isPowerSave);
        mLandscapeDrawableStyleA.setPowerSaveEnabled(isPowerSave);
        mRLandscapeDrawableStyleB.setPowerSaveEnabled(isPowerSave);
        mLandscapeDrawableStyleB.setPowerSaveEnabled(isPowerSave);
        mLandscapeDrawableBuddy.setPowerSaveEnabled(isPowerSave);
        mLandscapeDrawableLine.setPowerSaveEnabled(isPowerSave);
        mLandscapeDrawableMusku.setPowerSaveEnabled(isPowerSave);
        mLandscapeDrawablePill.setPowerSaveEnabled(isPowerSave);
        mLandscapeDrawableSignal.setPowerSaveEnabled(isPowerSave);
        mLandscapeBatteryA.setPowerSaveEnabled(isPowerSave);
        mLandscapeBatteryB.setPowerSaveEnabled(isPowerSave);
        mLandscapeBatteryC.setPowerSaveEnabled(isPowerSave);
        mLandscapeBatteryD.setPowerSaveEnabled(isPowerSave);
        mLandscapeBatteryE.setPowerSaveEnabled(isPowerSave);
        mLandscapeBatteryF.setPowerSaveEnabled(isPowerSave);
        mLandscapeBatteryG.setPowerSaveEnabled(isPowerSave);
        mLandscapeBatteryH.setPowerSaveEnabled(isPowerSave);
        mLandscapeBatteryI.setPowerSaveEnabled(isPowerSave);
        mLandscapeBatteryJ.setPowerSaveEnabled(isPowerSave);
        mLandscapeBatteryK.setPowerSaveEnabled(isPowerSave);
        mLandscapeBatteryL.setPowerSaveEnabled(isPowerSave);
        mLandscapeBatteryM.setPowerSaveEnabled(isPowerSave);
        mLandscapeBatteryN.setPowerSaveEnabled(isPowerSave);
        mLandscapeBatteryO.setPowerSaveEnabled(isPowerSave);
    }

    void onIsOverheatedChanged(boolean isOverheated) {
        boolean valueChanged = mIsOverheated != isOverheated;
        mIsOverheated = isOverheated;
        if (valueChanged) {
            updateContentDescription();
            // The battery drawable is a different size depending on whether it's currently
            // overheated or not, so we need to re-scale the view when overheated changes.
            scaleBatteryMeterViews();
        }
    }

    private TextView loadPercentView() {
        return (TextView) LayoutInflater.from(getContext())
                .inflate(R.layout.battery_percentage_view, null);
    }

    /**
     * Updates percent view by removing old one and reinflating if necessary
     */
    public void updatePercentView() {
        if (mBatteryPercentView != null) {
            removeView(mBatteryPercentView);
            mBatteryPercentView = null;
        }
        updateShowPercent();
    }

    /**
     * Sets the fetcher that should be used to get the estimated time remaining for the user's
     * battery.
     */
    void setBatteryEstimateFetcher(BatteryEstimateFetcher fetcher) {
        mBatteryEstimateFetcher = fetcher;
    }

    void setDisplayShieldEnabled(boolean displayShieldEnabled) {
        mDisplayShieldEnabled = displayShieldEnabled;
    }

    void updatePercentText() {
        if (mBatteryStateUnknown) {
            return;
        }

        if (mBatteryPercentView != null) {
            setPercentTextAtCurrentLevel();
        } else {
            updateContentDescription();
        }
    }

    private void setPercentTextAtCurrentLevel() {
        String text = NumberFormat.getPercentInstance().format(mLevel / 100f);
        // Use the high voltage symbol ⚡ (u26A1 unicode) but prevent the system
        // to load its emoji colored variant with the uFE0E flag
        String bolt = "\u26A1";
        CharSequence mChargeIndicator = mCharging && (mBatteryStyle == BATTERY_STYLE_HIDDEN ||
                    mBatteryStyle == BATTERY_STYLE_TEXT) ? (bolt + " ") : "";
        String percentText = mChargeIndicator + text;
        // Setting text actually triggers a layout pass (because the text view is set to
        // wrap_content width and TextView always relayouts for this). Avoid needless
        // relayout if the text didn't actually change.
        if (!TextUtils.equals(mBatteryPercentView.getText(), percentText)) {
            mBatteryPercentView.setText(percentText);
        }
        updateContentDescription();
    }

    private void updateContentDescription() {
        Context context = getContext();

        String contentDescription;
        if (mBatteryStateUnknown) {
            contentDescription = context.getString(R.string.accessibility_battery_unknown);
        } else if (mIsOverheated) {
            contentDescription =
                    context.getString(R.string.accessibility_battery_level_charging_paused, mLevel);
        } else if (mCharging) {
            contentDescription =
                    context.getString(R.string.accessibility_battery_level_charging, mLevel);
        } else {
            contentDescription = context.getString(R.string.accessibility_battery_level, mLevel);
        }

        setContentDescription(contentDescription);
    }

    private void removeBatteryPercentView() {
        if (mBatteryPercentView != null) {
            removeView(mBatteryPercentView);
            mBatteryPercentView = null;
        }
    }

    void updateShowPercent() {
        boolean drawPercentInside = mShowBatteryPercent == 1
                                    && !mCharging && !mBatteryStateUnknown;
        boolean showPercent = mShowBatteryPercent >= 2
                                    || mBatteryStyle == BATTERY_STYLE_TEXT
                                    || mShowPercentMode == MODE_ON;
        showPercent = showPercent && !mBatteryStateUnknown
                      && mBatteryStyle != BATTERY_STYLE_LANDSCAPEL
                                    && mBatteryStyle != BATTERY_STYLE_HIDDEN;

        mAccessorizedDrawable.showPercent(drawPercentInside);
        mCircleDrawable.setShowPercent(drawPercentInside);
        mFullCircleDrawable.setShowPercent(drawPercentInside);
        mRLandscapeDrawable.setShowPercent(drawPercentInside);
        mLandscapeDrawable.setShowPercent(drawPercentInside);
        mRLandscapeDrawableStyleA.setShowPercent(drawPercentInside);
        mLandscapeDrawableStyleA.setShowPercent(drawPercentInside);
        mRLandscapeDrawableStyleB.setShowPercent(drawPercentInside);
        mLandscapeDrawableStyleB.setShowPercent(drawPercentInside);
        mLandscapeDrawableBuddy.setShowPercent(drawPercentInside);
        mLandscapeDrawableLine.setShowPercent(drawPercentInside);
        mLandscapeDrawableMusku.setShowPercent(drawPercentInside);
        mLandscapeDrawablePill.setShowPercent(drawPercentInside);
        mLandscapeDrawableSignal.setShowPercent(drawPercentInside);
        mLandscapeBatteryA.setShowPercent(drawPercentInside);
        mLandscapeBatteryB.setShowPercent(drawPercentInside);
        mLandscapeBatteryC.setShowPercent(drawPercentInside);
        mLandscapeBatteryD.setShowPercent(drawPercentInside);
        mLandscapeBatteryE.setShowPercent(drawPercentInside);
        mLandscapeBatteryF.setShowPercent(drawPercentInside);
        mLandscapeBatteryG.setShowPercent(drawPercentInside);
        mLandscapeBatteryH.setShowPercent(drawPercentInside);
        mLandscapeBatteryI.setShowPercent(drawPercentInside);
        mLandscapeBatteryJ.setShowPercent(drawPercentInside);
        mLandscapeBatteryK.setShowPercent(drawPercentInside);
        mLandscapeBatteryL.setShowPercent(drawPercentInside);
        mLandscapeBatteryM.setShowPercent(drawPercentInside);
        mLandscapeBatteryN.setShowPercent(drawPercentInside);
        mLandscapeBatteryO.setShowPercent(drawPercentInside);

        if (showPercent || (mBatteryPercentCharging && mCharging)) {
            if (mBatteryPercentView == null) {
                mBatteryPercentView = loadPercentView();
                if (mPercentageStyleId != 0) { // Only set if specified as attribute
                    mBatteryPercentView.setTextAppearance(mPercentageStyleId);
                }
                if (mTextColor != 0) mBatteryPercentView.setTextColor(mTextColor);
                updatePercentText();
                addView(mBatteryPercentView, new LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.MATCH_PARENT));
            }
            if (mBatteryStyle == BATTERY_STYLE_HIDDEN 
                || mBatteryStyle == BATTERY_STYLE_TEXT) {
                mBatteryPercentView.setPaddingRelative(0, 0, 0, 0);
            } else {
                Resources res = getContext().getResources();
                mBatteryPercentView.setPaddingRelative(
                        res.getDimensionPixelSize(R.dimen.battery_level_padding_start), 0, 0, 0);
                setLayoutDirection(mShowBatteryPercent > 2 ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
            }

        } else {
            removeBatteryPercentView();
        }
    }

    private void updateVisibility() {
        if (mBatteryStyle == BATTERY_STYLE_HIDDEN || mBatteryStyle == BATTERY_STYLE_TEXT) {
            mBatteryIconView.setVisibility(View.GONE);
            mBatteryIconView.setImageDrawable(null);
        } else {
            mBatteryIconView.setVisibility(View.VISIBLE);
            scaleBatteryMeterViews();
        }
        for (int i = 0; i < mCallbacks.size(); i++) {
            mCallbacks.get(i).onHiddenBattery(mBatteryStyle == BATTERY_STYLE_HIDDEN);
        }
    }

    private Drawable getUnknownStateDrawable() {
        if (mUnknownStateDrawable == null) {
            mUnknownStateDrawable = mContext.getDrawable(R.drawable.ic_battery_unknown);
            mUnknownStateDrawable.setTint(mTextColor);
        }

        return mUnknownStateDrawable;
    }

    void onBatteryUnknownStateChanged(boolean isUnknown) {
        if (mBatteryStateUnknown == isUnknown) {
            return;
        }

        mBatteryStateUnknown = isUnknown;
        updateContentDescription();

        if (mBatteryStateUnknown) {
            mBatteryIconView.setImageDrawable(getUnknownStateDrawable());
        } else {
            updateDrawable();
        }

        updateShowPercent();
    }

    /**
     * Looks up the scale factor for status bar icons and scales the battery view by that amount.
     */
    void scaleBatteryMeterViews() {
        if (mBatteryIconView == null) {
            return;
        }
        Resources res = getContext().getResources();
        TypedValue typedValue = new TypedValue();

        res.getValue(R.dimen.status_bar_icon_scale_factor, typedValue, true);
        float iconScaleFactor = typedValue.getFloat();

        int batteryHeight = mBatteryStyle == BATTERY_STYLE_CIRCLE || mBatteryStyle == BATTERY_STYLE_DOTTED_CIRCLE
                || mBatteryStyle == BATTERY_STYLE_FULL_CIRCLE ?
                res.getDimensionPixelSize(R.dimen.status_bar_battery_icon_circle_width) :
                res.getDimensionPixelSize(R.dimen.status_bar_battery_icon_height);
        batteryHeight = mBatteryStyle == BATTERY_STYLE_LANDSCAPE || mBatteryStyle == BATTERY_STYLE_RLANDSCAPE 
        || mBatteryStyle == BATTERY_STYLE_RLANDSCAPE_STYLE_A || mBatteryStyle == BATTERY_STYLE_LANDSCAPE_STYLE_A 
        || mBatteryStyle == BATTERY_STYLE_RLANDSCAPE_STYLE_B || mBatteryStyle == BATTERY_STYLE_LANDSCAPE_STYLE_B ?
                res.getDimensionPixelSize(R.dimen.status_bar_battery_icon_height_landscape) : batteryHeight;
        batteryHeight = mBatteryStyle == BATTERY_STYLE_BIG_CIRCLE || mBatteryStyle == BATTERY_STYLE_BIG_DOTTED_CIRCLE ?
                res.getDimensionPixelSize(R.dimen.status_bar_battery_icon_big_circle_height) : batteryHeight;
        batteryHeight = mBatteryStyle == BATTERY_STYLE_LANDSCAPE_SIGNAL ?
                res.getDimensionPixelSize(R.dimen.status_bar_battery_icon_height_landscape_signal) : batteryHeight;
        batteryHeight = mBatteryStyle == BATTERY_STYLE_LANDSCAPE_LINE ?
                res.getDimensionPixelSize(R.dimen.status_bar_battery_icon_height_landscape_line) : batteryHeight;
        batteryHeight = mBatteryStyle == BATTERY_STYLE_LANDSCAPE_PILL || mBatteryStyle == BATTERY_STYLE_LANDSCAPE_MUSKU ?
                res.getDimensionPixelSize(R.dimen.status_bar_battery_icon_height_landscape_pill_musku) : batteryHeight;
        batteryHeight = mBatteryStyle == BATTERY_STYLE_LANDSCAPE_BUDDY ?
                res.getDimensionPixelSize(R.dimen.status_bar_battery_icon_height_landscape_buddy) : batteryHeight;
        int batteryWidth = mBatteryStyle == BATTERY_STYLE_CIRCLE || mBatteryStyle == BATTERY_STYLE_DOTTED_CIRCLE
                || mBatteryStyle == BATTERY_STYLE_FULL_CIRCLE ?
                res.getDimensionPixelSize(R.dimen.status_bar_battery_icon_circle_width) :
                res.getDimensionPixelSize(R.dimen.status_bar_battery_icon_width);
        batteryWidth = mBatteryStyle == BATTERY_STYLE_LANDSCAPE || mBatteryStyle == BATTERY_STYLE_RLANDSCAPE 
        || mBatteryStyle == BATTERY_STYLE_RLANDSCAPE_STYLE_A || mBatteryStyle == BATTERY_STYLE_LANDSCAPE_STYLE_A 
        || mBatteryStyle == BATTERY_STYLE_RLANDSCAPE_STYLE_B || mBatteryStyle == BATTERY_STYLE_LANDSCAPE_STYLE_B ?
                res.getDimensionPixelSize(R.dimen.status_bar_battery_icon_width_landscape) : batteryWidth;
        batteryWidth = mBatteryStyle == BATTERY_STYLE_BIG_CIRCLE || mBatteryStyle == BATTERY_STYLE_BIG_DOTTED_CIRCLE ?
                res.getDimensionPixelSize(R.dimen.status_bar_battery_icon_big_circle_width) : batteryWidth;
        batteryWidth = mBatteryStyle == BATTERY_STYLE_LANDSCAPE_SIGNAL ?
                res.getDimensionPixelSize(R.dimen.status_bar_battery_icon_width_landscape_signal): batteryWidth;      
         batteryWidth = mBatteryStyle == BATTERY_STYLE_LANDSCAPE_LINE ?
                res.getDimensionPixelSize(R.dimen.status_bar_battery_icon_width_landscape_line) : batteryWidth;      
         batteryWidth = mBatteryStyle == BATTERY_STYLE_LANDSCAPE_PILL || mBatteryStyle == BATTERY_STYLE_LANDSCAPE_MUSKU ?
                res.getDimensionPixelSize(R.dimen.status_bar_battery_icon_width_landscape_pill_musku) : batteryWidth;      
         batteryWidth = mBatteryStyle == BATTERY_STYLE_LANDSCAPE_BUDDY ?
                res.getDimensionPixelSize(R.dimen.status_bar_battery_icon_width_landscape_buddy) : batteryWidth; 
        float mainBatteryHeight = batteryHeight * iconScaleFactor;
        float mainBatteryWidth = batteryWidth * iconScaleFactor;

        mainBatteryHeight = mBatteryStyle == BATTERY_STYLE_LANDSCAPEA ||
                	mBatteryStyle == BATTERY_STYLE_LANDSCAPEB ||
                	mBatteryStyle == BATTERY_STYLE_LANDSCAPEC ||
                	mBatteryStyle == BATTERY_STYLE_LANDSCAPED ||
                	mBatteryStyle == BATTERY_STYLE_LANDSCAPEE ||
                	mBatteryStyle == BATTERY_STYLE_LANDSCAPEF ||
                	mBatteryStyle == BATTERY_STYLE_LANDSCAPEG ||
                	mBatteryStyle == BATTERY_STYLE_LANDSCAPEH ||
                	mBatteryStyle == BATTERY_STYLE_LANDSCAPEI ||
                	mBatteryStyle == BATTERY_STYLE_LANDSCAPEJ ||
                	mBatteryStyle == BATTERY_STYLE_LANDSCAPEK ||
               		mBatteryStyle == BATTERY_STYLE_LANDSCAPEL ||
                	mBatteryStyle == BATTERY_STYLE_LANDSCAPEM ||
                	mBatteryStyle == BATTERY_STYLE_LANDSCAPEN ||
                	mBatteryStyle == BATTERY_STYLE_LANDSCAPEO ?
        res.getDimensionPixelSize(R.dimen.status_bar_battery_icon_height_landscape_a_o) : mainBatteryHeight;

        mainBatteryWidth = mBatteryStyle == BATTERY_STYLE_LANDSCAPEA ||
                	mBatteryStyle == BATTERY_STYLE_LANDSCAPEB ||
                	mBatteryStyle == BATTERY_STYLE_LANDSCAPEC ||
                	mBatteryStyle == BATTERY_STYLE_LANDSCAPED ||
                	mBatteryStyle == BATTERY_STYLE_LANDSCAPEE ||
                	mBatteryStyle == BATTERY_STYLE_LANDSCAPEF ||
                	mBatteryStyle == BATTERY_STYLE_LANDSCAPEG ||
                	mBatteryStyle == BATTERY_STYLE_LANDSCAPEH ||
                	mBatteryStyle == BATTERY_STYLE_LANDSCAPEI ||
                	mBatteryStyle == BATTERY_STYLE_LANDSCAPEJ ||
                	mBatteryStyle == BATTERY_STYLE_LANDSCAPEK ||
               		mBatteryStyle == BATTERY_STYLE_LANDSCAPEL ||
                	mBatteryStyle == BATTERY_STYLE_LANDSCAPEM ||
                	mBatteryStyle == BATTERY_STYLE_LANDSCAPEN ||
                	mBatteryStyle == BATTERY_STYLE_LANDSCAPEO ?
        res.getDimensionPixelSize(R.dimen.status_bar_battery_icon_width_landscape_a_o) : mainBatteryWidth;

        // If the battery is marked as overheated, we should display a shield indicating that the
        // battery is being "defended".
        boolean displayShield = mDisplayShieldEnabled && mIsOverheated;
        float fullBatteryIconHeight =
                BatterySpecs.getFullBatteryHeight(mainBatteryHeight, displayShield);
        float fullBatteryIconWidth =
                BatterySpecs.getFullBatteryWidth(mainBatteryWidth, displayShield);

        int marginTop;
        if (displayShield) {
            // If the shield is displayed, we need some extra marginTop so that the bottom of the
            // main icon is still aligned with the bottom of all the other system icons.
            int shieldHeightAddition = Math.round(fullBatteryIconHeight - mainBatteryHeight);
            // However, the other system icons have some embedded bottom padding that the battery
            // doesn't have, so we shouldn't move the battery icon down by the full amount.
            // See b/258672854.
            marginTop = shieldHeightAddition
                    - res.getDimensionPixelSize(R.dimen.status_bar_battery_extra_vertical_spacing);
        } else {
            marginTop = 0;
        }

        int marginBottom = res.getDimensionPixelSize(R.dimen.battery_margin_bottom);

        LinearLayout.LayoutParams scaledLayoutParams = new LinearLayout.LayoutParams(
                Math.round(fullBatteryIconWidth),
                Math.round(fullBatteryIconHeight));
        scaledLayoutParams.setMargins(0, marginTop, 0, marginBottom);

        mAccessorizedDrawable.setDisplayShield(displayShield);
        if (mBatteryIconView != null) {
            mBatteryIconView.setLayoutParams(scaledLayoutParams);
        }
        mBatteryIconView.invalidateDrawable(mAccessorizedDrawable);
    }

    private void updateDrawable() {
        switch (mBatteryStyle) {
            case BATTERY_STYLE_PORTRAIT:
                mBatteryIconView.setImageDrawable(mAccessorizedDrawable);
                break;
            case BATTERY_STYLE_RLANDSCAPE:
                mBatteryIconView.setImageDrawable(mRLandscapeDrawable);
                break;
            case BATTERY_STYLE_LANDSCAPE:
                mBatteryIconView.setImageDrawable(mLandscapeDrawable);
                break;
            case BATTERY_STYLE_RLANDSCAPE_STYLE_A:
                mBatteryIconView.setImageDrawable(mRLandscapeDrawableStyleA);
                mBatteryIconView.setVisibility(View.VISIBLE);
                scaleBatteryMeterViews();
                break;
            case BATTERY_STYLE_LANDSCAPE_STYLE_A:
                mBatteryIconView.setImageDrawable(mLandscapeDrawableStyleA);
                mBatteryIconView.setVisibility(View.VISIBLE);
                scaleBatteryMeterViews();
                break;
            case BATTERY_STYLE_RLANDSCAPE_STYLE_B:
                 mBatteryIconView.setImageDrawable(mRLandscapeDrawableStyleB);
                 mBatteryIconView.setVisibility(View.VISIBLE);
                 scaleBatteryMeterViews();
                 break;
            case BATTERY_STYLE_LANDSCAPE_STYLE_B:
                 mBatteryIconView.setImageDrawable(mLandscapeDrawableStyleB);
                 mBatteryIconView.setVisibility(View.VISIBLE);
                 scaleBatteryMeterViews();
                 break;
            case BATTERY_STYLE_LANDSCAPE_BUDDY:
                 mBatteryIconView.setImageDrawable(mLandscapeDrawableBuddy);
                 mBatteryIconView.setVisibility(View.VISIBLE);
                 scaleBatteryMeterViews();
                 break;
            case BATTERY_STYLE_LANDSCAPE_LINE:
                 mBatteryIconView.setImageDrawable(mLandscapeDrawableLine);
                 mBatteryIconView.setVisibility(View.VISIBLE);
                 scaleBatteryMeterViews();
                 break;
            case BATTERY_STYLE_LANDSCAPE_MUSKU:
                 mBatteryIconView.setImageDrawable(mLandscapeDrawableMusku);
                 mBatteryIconView.setVisibility(View.VISIBLE);
                 scaleBatteryMeterViews();
                 break;
            case BATTERY_STYLE_LANDSCAPE_PILL:
                 mBatteryIconView.setImageDrawable(mLandscapeDrawablePill);
                 mBatteryIconView.setVisibility(View.VISIBLE);
                 scaleBatteryMeterViews();
                 break;
            case BATTERY_STYLE_LANDSCAPE_SIGNAL:
                 mBatteryIconView.setImageDrawable(mLandscapeDrawableSignal);
                 mBatteryIconView.setVisibility(View.VISIBLE);
                 scaleBatteryMeterViews();
                 break;
            case BATTERY_STYLE_FULL_CIRCLE:
                mBatteryIconView.setImageDrawable(mFullCircleDrawable);
                break;
            case BATTERY_STYLE_CIRCLE:
            case BATTERY_STYLE_DOTTED_CIRCLE:
            case BATTERY_STYLE_BIG_CIRCLE:
            case BATTERY_STYLE_BIG_DOTTED_CIRCLE:
                mCircleDrawable.setMeterStyle(mBatteryStyle);
                mBatteryIconView.setImageDrawable(mCircleDrawable);
                break;
            case BATTERY_STYLE_LANDSCAPEA:
                mBatteryIconView.setImageDrawable(mLandscapeBatteryA);
                mBatteryIconView.setVisibility(View.VISIBLE);
                break;
            case BATTERY_STYLE_LANDSCAPEB:
                mBatteryIconView.setImageDrawable(mLandscapeBatteryB);
                mBatteryIconView.setVisibility(View.VISIBLE);
                break;
            case BATTERY_STYLE_LANDSCAPEC:
                mBatteryIconView.setImageDrawable(mLandscapeBatteryC);
                mBatteryIconView.setVisibility(View.VISIBLE);
                break;
            case BATTERY_STYLE_LANDSCAPED:
                mBatteryIconView.setImageDrawable(mLandscapeBatteryD);
                mBatteryIconView.setVisibility(View.VISIBLE);
                break;
            case BATTERY_STYLE_LANDSCAPEE:
                mBatteryIconView.setImageDrawable(mLandscapeBatteryE);
                mBatteryIconView.setVisibility(View.VISIBLE);
                break;
            case BATTERY_STYLE_LANDSCAPEF:
                mBatteryIconView.setImageDrawable(mLandscapeBatteryF);
                mBatteryIconView.setVisibility(View.VISIBLE);
                break;
            case BATTERY_STYLE_LANDSCAPEG:
                mBatteryIconView.setImageDrawable(mLandscapeBatteryG);
                mBatteryIconView.setVisibility(View.VISIBLE);
                break;
            case BATTERY_STYLE_LANDSCAPEH:
                mBatteryIconView.setImageDrawable(mLandscapeBatteryH);
                mBatteryIconView.setVisibility(View.VISIBLE);
                break;
            case BATTERY_STYLE_LANDSCAPEI:
                mBatteryIconView.setImageDrawable(mLandscapeBatteryI);
                mBatteryIconView.setVisibility(View.VISIBLE);
                break;
            case BATTERY_STYLE_LANDSCAPEJ:
                mBatteryIconView.setImageDrawable(mLandscapeBatteryJ);
                mBatteryIconView.setVisibility(View.VISIBLE);
                break;
            case BATTERY_STYLE_LANDSCAPEK:
                mBatteryIconView.setImageDrawable(mLandscapeBatteryK);
                break;
            case BATTERY_STYLE_LANDSCAPEL:
                mBatteryIconView.setImageDrawable(mLandscapeBatteryL);
                break;
            case BATTERY_STYLE_LANDSCAPEM:
                mBatteryIconView.setImageDrawable(mLandscapeBatteryM);
                break;
            case BATTERY_STYLE_LANDSCAPEN:
                mBatteryIconView.setImageDrawable(mLandscapeBatteryN);
                break;
            case BATTERY_STYLE_LANDSCAPEO:
                mBatteryIconView.setImageDrawable(mLandscapeBatteryO);
                break;
            case BATTERY_STYLE_HIDDEN:
            case BATTERY_STYLE_TEXT:
                return;
            default:
        }
    }

    @Override
    public void onDarkChanged(ArrayList<Rect> areas, float darkIntensity, int tint) {
        float intensity = DarkIconDispatcher.isInAreas(areas, this) ? darkIntensity : 0;
        mNonAdaptedSingleToneColor = mDualToneHandler.getSingleColor(intensity);
        mNonAdaptedForegroundColor = mDualToneHandler.getFillColor(intensity);
        mNonAdaptedBackgroundColor = mDualToneHandler.getBackgroundColor(intensity);

        updateColors(mNonAdaptedForegroundColor, mNonAdaptedBackgroundColor,
                mNonAdaptedSingleToneColor);
    }

    /**
     * Sets icon and text colors. This will be overridden by {@code onDarkChanged} events,
     * if registered.
     *
     * @param foregroundColor
     * @param backgroundColor
     * @param singleToneColor
     */
    public void updateColors(int foregroundColor, int backgroundColor, int singleToneColor) {
        mAccessorizedDrawable.setColors(foregroundColor, backgroundColor, singleToneColor);
        mCircleDrawable.setColors(foregroundColor, backgroundColor, singleToneColor);
        mFullCircleDrawable.setColors(foregroundColor, backgroundColor, singleToneColor);
        mRLandscapeDrawable.setColors(foregroundColor, backgroundColor, singleToneColor);
        mLandscapeDrawable.setColors(foregroundColor, backgroundColor, singleToneColor);
        mRLandscapeDrawableStyleA.setColors(foregroundColor, backgroundColor, singleToneColor);
        mLandscapeDrawableStyleA.setColors(foregroundColor, backgroundColor, singleToneColor);
        mRLandscapeDrawableStyleB.setColors(foregroundColor, backgroundColor, singleToneColor);
        mLandscapeDrawableStyleB.setColors(foregroundColor, backgroundColor, singleToneColor);
        mLandscapeDrawableBuddy.setColors(foregroundColor, backgroundColor, singleToneColor);
        mLandscapeDrawableLine.setColors(foregroundColor, backgroundColor, singleToneColor);
        mLandscapeDrawableMusku.setColors(foregroundColor, backgroundColor, singleToneColor);
        mLandscapeDrawablePill.setColors(foregroundColor, backgroundColor, singleToneColor);
        mLandscapeDrawableSignal.setColors(foregroundColor, backgroundColor, singleToneColor);
        mLandscapeBatteryA.setColors(foregroundColor, backgroundColor, singleToneColor);
        mLandscapeBatteryB.setColors(foregroundColor, backgroundColor, singleToneColor);
        mLandscapeBatteryC.setColors(foregroundColor, backgroundColor, singleToneColor);
        mLandscapeBatteryD.setColors(foregroundColor, backgroundColor, singleToneColor);
        mLandscapeBatteryE.setColors(foregroundColor, backgroundColor, singleToneColor);
        mLandscapeBatteryF.setColors(foregroundColor, backgroundColor, singleToneColor);
        mLandscapeBatteryG.setColors(foregroundColor, backgroundColor, singleToneColor);
        mLandscapeBatteryH.setColors(foregroundColor, backgroundColor, singleToneColor);
        mLandscapeBatteryI.setColors(foregroundColor, backgroundColor, singleToneColor);
        mLandscapeBatteryJ.setColors(foregroundColor, backgroundColor, singleToneColor);
        mLandscapeBatteryK.setColors(foregroundColor, backgroundColor, singleToneColor);
        mLandscapeBatteryL.setColors(foregroundColor, backgroundColor, singleToneColor);
        mLandscapeBatteryM.setColors(foregroundColor, backgroundColor, singleToneColor);
        mLandscapeBatteryN.setColors(foregroundColor, backgroundColor, singleToneColor);
        mLandscapeBatteryO.setColors(foregroundColor, backgroundColor, singleToneColor);
        mTextColor = singleToneColor;
        if (mBatteryPercentView != null) {
            mBatteryPercentView.setTextColor(singleToneColor);
        }

        if (mUnknownStateDrawable != null) {
            mUnknownStateDrawable.setTint(singleToneColor);
        }
    }

    public void dump(PrintWriter pw, String[] args) {
        String powerSave = mAccessorizedDrawable == null ?
                null : mAccessorizedDrawable.getPowerSaveEnabled() + "";
        CharSequence percent = mBatteryPercentView == null ? null : mBatteryPercentView.getText();
        pw.println("  BatteryMeterView:");
        pw.println("    getPowerSave: " + powerSave);
        pw.println("    mBatteryPercentView.getText(): " + percent);
        pw.println("    mTextColor: #" + Integer.toHexString(mTextColor));
        pw.println("    mBatteryStateUnknown: " + mBatteryStateUnknown);
        pw.println("    mLevel: " + mLevel);
        pw.println("    mMode: " + mShowPercentMode);
    }

    @VisibleForTesting
    CharSequence getBatteryPercentViewText() {
        return mBatteryPercentView.getText();
    }

    /** An interface that will fetch the estimated time remaining for the user's battery. */
    public interface BatteryEstimateFetcher {
        void fetchBatteryTimeRemainingEstimate(
                BatteryController.EstimateFetchCompletion completion);
    }

    public interface BatteryMeterViewCallbacks {
        default void onHiddenBattery(boolean hidden) {}
    }

    public void addCallback(BatteryMeterViewCallbacks callback) {
        mCallbacks.add(callback);
    }

    public void removeCallback(BatteryMeterViewCallbacks callbacks) {
        mCallbacks.remove(callbacks);
    }
}

