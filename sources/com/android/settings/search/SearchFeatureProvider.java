package com.android.settings.search;

import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.window.R;
import com.android.settings.Utils;
import com.android.settings.activityembedding.ActivityEmbeddingRulesController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.search.SearchIndexableResources;
import com.google.android.setupcompat.util.WizardManagerHelper;
import java.util.List;
/* loaded from: classes.dex */
public interface SearchFeatureProvider {
    Intent buildSearchIntent(Context context, int i);

    SearchIndexableResources getSearchIndexableResources();

    void verifyLaunchSearchResultPageCaller(Context context, ComponentName componentName) throws SecurityException, IllegalArgumentException;

    default String getSettingsIntelligencePkgName(Context context) {
        return context.getString(R.string.config_settingsintelligence_package_name);
    }

    default void initSearchToolbar(final FragmentActivity fragmentActivity, Toolbar toolbar, final int i) {
        if (fragmentActivity != null && toolbar != null) {
            if (!WizardManagerHelper.isDeviceProvisioned(fragmentActivity) || !Utils.isPackageEnabled(fragmentActivity, getSettingsIntelligencePkgName(fragmentActivity)) || WizardManagerHelper.isAnySetupWizard(fragmentActivity.getIntent())) {
                ViewGroup viewGroup = (ViewGroup) toolbar.getParent();
                if (viewGroup != null) {
                    viewGroup.setVisibility(8);
                    return;
                }
                return;
            }
            View navigationView = toolbar.getNavigationView();
            navigationView.setClickable(false);
            navigationView.setImportantForAccessibility(2);
            navigationView.setBackground(null);
            final Context applicationContext = fragmentActivity.getApplicationContext();
            final Intent addFlags = buildSearchIntent(applicationContext, i).addFlags(67108864);
            List<ResolveInfo> queryIntentActivities = fragmentActivity.getPackageManager().queryIntentActivities(addFlags, 65536);
            if (!queryIntentActivities.isEmpty()) {
                ComponentName componentName = queryIntentActivities.get(0).getComponentInfo().getComponentName();
                addFlags.setComponent(componentName);
                ActivityEmbeddingRulesController.registerTwoPanePairRuleForSettingsHome(applicationContext, componentName, addFlags.getAction(), false, true, false);
                toolbar.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.search.SearchFeatureProvider$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        SearchFeatureProvider.lambda$initSearchToolbar$0(applicationContext, i, fragmentActivity, addFlags, view);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static /* synthetic */ void lambda$initSearchToolbar$0(Context context, int i, FragmentActivity fragmentActivity, Intent intent, View view) {
        FeatureFactory.getFactory(context).getSlicesFeatureProvider().indexSliceDataAsync(context);
        FeatureFactory.getFactory(context).getMetricsFeatureProvider().logSettingsTileClick("homepage_search_bar", i);
        fragmentActivity.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(fragmentActivity, new Pair[0]).toBundle());
    }
}
