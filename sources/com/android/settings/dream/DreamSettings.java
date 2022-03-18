package com.android.settings.dream;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.recyclerview.widget.RecyclerView;
import androidx.window.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.dream.DreamBackend;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class DreamSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.dream_fragment_overview) { // from class: com.android.settings.dream.DreamSettings.1
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return DreamSettings.buildPreferenceControllers(context);
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getDreamSettingDescriptionResId(int i) {
        return i != 0 ? i != 1 ? i != 2 ? R.string.screensaver_settings_summary_never : R.string.screensaver_settings_summary_either_long : R.string.screensaver_settings_summary_dock : R.string.screensaver_settings_summary_sleep;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getKeyFromSetting(int i) {
        return i != 0 ? i != 1 ? i != 2 ? "never" : "either_charging_or_docked" : "while_docked_only" : "while_charging_only";
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R.string.help_url_screen_saver;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "DreamSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 47;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.dream_fragment_overview;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static int getSettingFromPrefKey(String str) {
        char c;
        switch (str.hashCode()) {
            case -1592701525:
                if (str.equals("while_docked_only")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -294641318:
                if (str.equals("either_charging_or_docked")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 104712844:
                if (str.equals("never")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 1019349036:
                if (str.equals("while_charging_only")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        if (c == 0) {
            return 0;
        }
        if (c != 1) {
            return c != 2 ? 3 : 2;
        }
        return 1;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context);
    }

    public static CharSequence getSummaryTextWithDreamName(Context context) {
        return getSummaryTextFromBackend(DreamBackend.getInstance(context), context);
    }

    static CharSequence getSummaryTextFromBackend(DreamBackend dreamBackend, Context context) {
        if (!dreamBackend.isEnabled()) {
            return context.getString(R.string.screensaver_settings_summary_off);
        }
        return dreamBackend.getActiveDreamName();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new DreamPickerController(context));
        arrayList.add(new WhenToDreamPreferenceController(context));
        return arrayList;
    }

    @Override // androidx.preference.PreferenceFragmentCompat
    public RecyclerView onCreateRecyclerView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        ViewGroup viewGroup2 = (ViewGroup) getActivity().findViewById(16908290);
        final Button button = (Button) getActivity().getLayoutInflater().inflate(R.layout.dream_preview_button, viewGroup2, false);
        viewGroup2.addView(button);
        final DreamBackend instance = DreamBackend.getInstance(getContext());
        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.dream.DreamSettings$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                DreamSettings.lambda$onCreateRecyclerView$0(DreamBackend.this, view);
            }
        });
        final RecyclerView onCreateRecyclerView = super.onCreateRecyclerView(layoutInflater, viewGroup, bundle);
        button.post(new Runnable() { // from class: com.android.settings.dream.DreamSettings$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                DreamSettings.lambda$onCreateRecyclerView$1(RecyclerView.this, button);
            }
        });
        return onCreateRecyclerView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onCreateRecyclerView$0(DreamBackend dreamBackend, View view) {
        dreamBackend.preview(dreamBackend.getActiveDream());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onCreateRecyclerView$1(RecyclerView recyclerView, Button button) {
        recyclerView.setPadding(0, 0, 0, button.getMeasuredHeight());
    }
}
