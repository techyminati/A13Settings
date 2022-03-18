package com.android.settings.dream;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.RecyclerView;
import androidx.window.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.dream.DreamPickerController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.dream.DreamBackend;
import com.android.settingslib.widget.LayoutPreference;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class DreamPickerController extends BasePreferenceController {
    private static final String KEY = "dream_picker";
    private DreamBackend.DreamInfo mActiveDream;
    private DreamAdapter mAdapter;
    private final DreamBackend mBackend;
    private final List<DreamBackend.DreamInfo> mDreamInfos;
    private final MetricsFeatureProvider mMetricsFeatureProvider;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return KEY;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public DreamPickerController(Context context) {
        this(context, DreamBackend.getInstance(context));
    }

    public DreamPickerController(Context context, DreamBackend dreamBackend) {
        super(context, KEY);
        this.mBackend = dreamBackend;
        List<DreamBackend.DreamInfo> dreamInfos = dreamBackend.getDreamInfos();
        this.mDreamInfos = dreamInfos;
        this.mActiveDream = getActiveDreamInfo(dreamInfos);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mDreamInfos.size() > 0 ? 0 : 2;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mAdapter = new DreamAdapter((List) this.mDreamInfos.stream().map(new Function() { // from class: com.android.settings.dream.DreamPickerController$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                DreamPickerController.DreamItem lambda$displayPreference$0;
                lambda$displayPreference$0 = DreamPickerController.this.lambda$displayPreference$0((DreamBackend.DreamInfo) obj);
                return lambda$displayPreference$0;
            }
        }).collect(Collectors.toList()));
        LayoutPreference layoutPreference = (LayoutPreference) preferenceScreen.findPreference(getPreferenceKey());
        if (layoutPreference != null) {
            RecyclerView recyclerView = (RecyclerView) layoutPreference.findViewById(R.id.dream_list);
            recyclerView.setLayoutManager(new AutoFitGridLayoutManager(this.mContext));
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(this.mContext, R.dimen.dream_preference_card_padding));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(this.mAdapter);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ DreamItem lambda$displayPreference$0(DreamBackend.DreamInfo dreamInfo) {
        return new DreamItem(dreamInfo);
    }

    private static DreamBackend.DreamInfo getActiveDreamInfo(List<DreamBackend.DreamInfo> list) {
        return list.stream().filter(DreamPickerController$$ExternalSyntheticLambda1.INSTANCE).findFirst().orElse(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class DreamItem implements IDreamItem {
        DreamBackend.DreamInfo mDreamInfo;

        DreamItem(DreamBackend.DreamInfo dreamInfo) {
            this.mDreamInfo = dreamInfo;
        }

        @Override // com.android.settings.dream.IDreamItem
        public CharSequence getTitle() {
            return this.mDreamInfo.caption;
        }

        @Override // com.android.settings.dream.IDreamItem
        public Drawable getIcon() {
            return this.mDreamInfo.icon;
        }

        @Override // com.android.settings.dream.IDreamItem
        public void onItemClicked() {
            DreamPickerController.this.mActiveDream = this.mDreamInfo;
            DreamPickerController.this.mBackend.setActiveDream(this.mDreamInfo.componentName);
            DreamPickerController.this.mMetricsFeatureProvider.action(((AbstractPreferenceController) DreamPickerController.this).mContext, 1788, this.mDreamInfo.componentName.flattenToString());
        }

        @Override // com.android.settings.dream.IDreamItem
        public void onCustomizeClicked() {
            DreamPickerController.this.mBackend.launchSettings(((AbstractPreferenceController) DreamPickerController.this).mContext, this.mDreamInfo);
        }

        @Override // com.android.settings.dream.IDreamItem
        public Drawable getPreviewImage() {
            return this.mDreamInfo.previewImage;
        }

        @Override // com.android.settings.dream.IDreamItem
        public boolean isActive() {
            if (DreamPickerController.this.mActiveDream == null) {
                return false;
            }
            return this.mDreamInfo.componentName.equals(DreamPickerController.this.mActiveDream.componentName);
        }

        @Override // com.android.settings.dream.IDreamItem
        public boolean allowCustomization() {
            return isActive() && this.mDreamInfo.settingsComponentName != null;
        }
    }
}
