package com.android.settings.core;

import android.R;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.SetupWizardUtils;
import com.android.settings.SubSettings;
import com.android.settings.core.CategoryMixin;
import com.android.settingslib.core.lifecycle.HideNonSystemOverlayMixin;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.resources.TextAppearanceConfig;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.google.android.setupdesign.util.ThemeHelper;
/* loaded from: classes.dex */
public class SettingsBaseActivity extends FragmentActivity implements CategoryMixin.CategoryHandler {
    protected AppBarLayout mAppBarLayout;
    protected CategoryMixin mCategoryMixin;
    protected CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Toolbar mToolbar;

    protected boolean isToolbarEnabled() {
        return true;
    }

    @Override // com.android.settings.core.CategoryMixin.CategoryHandler
    public CategoryMixin getCategoryMixin() {
        return this.mCategoryMixin;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (!isFinishing()) {
            if (isLockTaskModePinned() && !isSettingsRunOnTop()) {
                Log.w("SettingsBaseActivity", "Devices lock task mode pinned.");
                finish();
            }
            System.currentTimeMillis();
            getLifecycle().addObserver(new HideNonSystemOverlayMixin(this));
            TextAppearanceConfig.setShouldLoadFontSynchronously(true);
            this.mCategoryMixin = new CategoryMixin(this);
            getLifecycle().addObserver(this.mCategoryMixin);
            if (!getTheme().obtainStyledAttributes(R.styleable.Theme).getBoolean(38, false)) {
                requestWindowFeature(1);
            }
            boolean isAnySetupWizard = WizardManagerHelper.isAnySetupWizard(getIntent());
            if (isAnySetupWizard && (this instanceof SubSettings)) {
                if (ThemeHelper.trySetDynamicColor(this)) {
                    setTheme(ThemeHelper.isSetupWizardDayNightEnabled(this) ? androidx.window.R.style.SudDynamicColorThemeSettings_SetupWizard_DayNight : androidx.window.R.style.SudDynamicColorThemeSettings_SetupWizard);
                } else {
                    setTheme(SetupWizardUtils.getTheme(this, getIntent()));
                }
            }
            if (!isToolbarEnabled() || isAnySetupWizard) {
                super.setContentView(androidx.window.R.layout.settings_base_layout);
            } else {
                super.setContentView(androidx.window.R.layout.collapsing_toolbar_base_layout);
                this.mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(androidx.window.R.id.collapsing_toolbar);
                this.mAppBarLayout = (AppBarLayout) findViewById(androidx.window.R.id.app_bar);
                CollapsingToolbarLayout collapsingToolbarLayout = this.mCollapsingToolbarLayout;
                if (collapsingToolbarLayout != null) {
                    collapsingToolbarLayout.setLineSpacingMultiplier(1.1f);
                }
                disableCollapsingToolbarLayoutScrollingBehavior();
            }
            Toolbar toolbar = (Toolbar) findViewById(androidx.window.R.id.action_bar);
            if (!isToolbarEnabled() || isAnySetupWizard) {
                toolbar.setVisibility(8);
            } else {
                setActionBar(toolbar);
            }
        }
    }

    @Override // android.app.Activity
    public void setActionBar(Toolbar toolbar) {
        super.setActionBar(toolbar);
        this.mToolbar = toolbar;
    }

    @Override // android.app.Activity
    public boolean onNavigateUp() {
        if (super.onNavigateUp()) {
            return true;
        }
        finishAfterTransition();
        return true;
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void startActivityForResult(Intent intent, int i, Bundle bundle) {
        int transitionType = getTransitionType(intent);
        super.startActivityForResult(intent, i, bundle);
        if (transitionType == 1) {
            overridePendingTransition(androidx.window.R.anim.sud_slide_next_in, androidx.window.R.anim.sud_slide_next_out);
        } else if (transitionType == 2) {
            overridePendingTransition(17432576, androidx.window.R.anim.sud_stay);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        if (getTransitionType(getIntent()) == 2) {
            overridePendingTransition(androidx.window.R.anim.sud_stay, 17432577);
        }
        super.onPause();
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void setContentView(int i) {
        ViewGroup viewGroup = (ViewGroup) findViewById(androidx.window.R.id.content_frame);
        if (viewGroup != null) {
            viewGroup.removeAllViews();
        }
        LayoutInflater.from(this).inflate(i, viewGroup);
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void setContentView(View view) {
        ((ViewGroup) findViewById(androidx.window.R.id.content_frame)).addView(view);
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void setContentView(View view, ViewGroup.LayoutParams layoutParams) {
        ((ViewGroup) findViewById(androidx.window.R.id.content_frame)).addView(view, layoutParams);
    }

    @Override // android.app.Activity
    public void setTitle(CharSequence charSequence) {
        super.setTitle(charSequence);
        CollapsingToolbarLayout collapsingToolbarLayout = this.mCollapsingToolbarLayout;
        if (collapsingToolbarLayout != null) {
            collapsingToolbarLayout.setTitle(charSequence);
        }
    }

    @Override // android.app.Activity
    public void setTitle(int i) {
        super.setTitle(getText(i));
        CollapsingToolbarLayout collapsingToolbarLayout = this.mCollapsingToolbarLayout;
        if (collapsingToolbarLayout != null) {
            collapsingToolbarLayout.setTitle(getText(i));
        }
    }

    private boolean isLockTaskModePinned() {
        return ((ActivityManager) getApplicationContext().getSystemService(ActivityManager.class)).getLockTaskModeState() == 2;
    }

    private boolean isSettingsRunOnTop() {
        return TextUtils.equals(getPackageName(), ((ActivityManager) getApplicationContext().getSystemService(ActivityManager.class)).getRunningTasks(1).get(0).baseActivity.getPackageName());
    }

    public boolean setTileEnabled(ComponentName componentName, boolean z) {
        PackageManager packageManager = getPackageManager();
        int componentEnabledSetting = packageManager.getComponentEnabledSetting(componentName);
        if ((componentEnabledSetting == 1) == z && componentEnabledSetting != 0) {
            return false;
        }
        if (z) {
            this.mCategoryMixin.removeFromDenylist(componentName);
        } else {
            this.mCategoryMixin.addToDenylist(componentName);
        }
        packageManager.setComponentEnabledSetting(componentName, z ? 1 : 2, 1);
        return true;
    }

    private void disableCollapsingToolbarLayoutScrollingBehavior() {
        AppBarLayout appBarLayout = this.mAppBarLayout;
        if (appBarLayout != null) {
            AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
            behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() { // from class: com.android.settings.core.SettingsBaseActivity.1
                @Override // com.google.android.material.appbar.AppBarLayout.BaseBehavior.BaseDragCallback
                public boolean canDrag(AppBarLayout appBarLayout2) {
                    return false;
                }
            });
            ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).setBehavior(behavior);
        }
    }

    private int getTransitionType(Intent intent) {
        return intent.getIntExtra("page_transition_type", -1);
    }
}
