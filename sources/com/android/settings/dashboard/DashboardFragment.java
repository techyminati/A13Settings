package com.android.settings.dashboard;

import android.app.admin.DevicePolicyManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import androidx.window.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.CategoryMixin;
import com.android.settings.core.PreferenceControllerListHelper;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.PrimarySwitchPreference;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.drawer.DashboardCategory;
import com.android.settingslib.drawer.ProviderTile;
import com.android.settingslib.drawer.Tile;
import com.android.settingslib.utils.ThreadUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;
/* loaded from: classes.dex */
public abstract class DashboardFragment extends SettingsPreferenceFragment implements CategoryMixin.CategoryListener, PreferenceGroup.OnExpandButtonClickListener, BasePreferenceController.UiBlockListener {
    public static final String CATEGORY = "category";
    private static final String TAG = "DashboardFragment";
    UiBlockerController mBlockerController;
    private DashboardFeatureProvider mDashboardFeatureProvider;
    private DevicePolicyManager mDevicePolicyManager;
    private boolean mListeningToCategoryChange;
    private DashboardTilePlaceholderPreferenceController mPlaceholderPreferenceController;
    private List<String> mSuppressInjectedTileKeys;
    final ArrayMap<String, List<DynamicDataObserver>> mDashboardTilePrefKeys = new ArrayMap<>();
    private final Map<Class, List<AbstractPreferenceController>> mPreferenceControllers = new ArrayMap();
    private final List<DynamicDataObserver> mRegisteredObservers = new ArrayList();
    private final List<AbstractPreferenceController> mControllers = new ArrayList();

    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract String getLogTag();

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public abstract int getPreferenceScreenResId();

    protected boolean isParalleledControllers() {
        return false;
    }

    protected boolean shouldForceRoundedIcon() {
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mSuppressInjectedTileKeys = Arrays.asList(context.getResources().getStringArray(R.array.config_suppress_injected_tile_keys));
        this.mDashboardFeatureProvider = FeatureFactory.getFactory(context).getDashboardFeatureProvider(context);
        List<AbstractPreferenceController> createPreferenceControllers = createPreferenceControllers(context);
        List<BasePreferenceController> filterControllers = PreferenceControllerListHelper.filterControllers(PreferenceControllerListHelper.getPreferenceControllersFromXml(context, getPreferenceScreenResId()), createPreferenceControllers);
        if (createPreferenceControllers != null) {
            this.mControllers.addAll(createPreferenceControllers);
        }
        this.mControllers.addAll(filterControllers);
        final Lifecycle settingsLifecycle = getSettingsLifecycle();
        filterControllers.forEach(new Consumer() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda10
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DashboardFragment.lambda$onAttach$0(Lifecycle.this, (BasePreferenceController) obj);
            }
        });
        final int metricsCategory = getMetricsCategory();
        this.mControllers.forEach(new Consumer() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DashboardFragment.lambda$onAttach$1(metricsCategory, (AbstractPreferenceController) obj);
            }
        });
        DashboardTilePlaceholderPreferenceController dashboardTilePlaceholderPreferenceController = new DashboardTilePlaceholderPreferenceController(context);
        this.mPlaceholderPreferenceController = dashboardTilePlaceholderPreferenceController;
        this.mControllers.add(dashboardTilePlaceholderPreferenceController);
        for (AbstractPreferenceController abstractPreferenceController : this.mControllers) {
            addPreferenceController(abstractPreferenceController);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onAttach$0(Lifecycle lifecycle, BasePreferenceController basePreferenceController) {
        if (basePreferenceController instanceof LifecycleObserver) {
            lifecycle.addObserver((LifecycleObserver) basePreferenceController);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onAttach$1(int i, AbstractPreferenceController abstractPreferenceController) {
        if (abstractPreferenceController instanceof BasePreferenceController) {
            ((BasePreferenceController) abstractPreferenceController).setMetricsCategory(i);
        }
    }

    void checkUiBlocker(List<AbstractPreferenceController> list) {
        final ArrayList arrayList = new ArrayList();
        list.forEach(new Consumer() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda9
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DashboardFragment.this.lambda$checkUiBlocker$2(arrayList, (AbstractPreferenceController) obj);
            }
        });
        if (!arrayList.isEmpty()) {
            UiBlockerController uiBlockerController = new UiBlockerController(arrayList);
            this.mBlockerController = uiBlockerController;
            uiBlockerController.start(new Runnable() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    DashboardFragment.this.lambda$checkUiBlocker$3();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkUiBlocker$2(List list, AbstractPreferenceController abstractPreferenceController) {
        if ((abstractPreferenceController instanceof BasePreferenceController.UiBlocker) && abstractPreferenceController.isAvailable()) {
            ((BasePreferenceController) abstractPreferenceController).setUiBlockListener(this);
            list.add(abstractPreferenceController.getPreferenceKey());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkUiBlocker$3() {
        updatePreferenceVisibility(this.mPreferenceControllers);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mDevicePolicyManager = (DevicePolicyManager) getSystemService(DevicePolicyManager.class);
        getPreferenceManager().setPreferenceComparisonCallback(new PreferenceManager.SimplePreferenceComparisonCallback());
        if (bundle != null) {
            updatePreferenceStates();
        }
    }

    @Override // com.android.settings.core.CategoryMixin.CategoryListener
    public void onCategoriesChanged(Set<String> set) {
        String categoryKey = getCategoryKey();
        if (this.mDashboardFeatureProvider.getTilesForCategory(categoryKey) != null) {
            if (set == null) {
                refreshDashboardTiles(getLogTag());
            } else if (set.contains(categoryKey)) {
                Log.i(TAG, "refresh tiles for " + categoryKey);
                refreshDashboardTiles(getLogTag());
            }
        }
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        checkUiBlocker(this.mControllers);
        refreshAllPreferences(getLogTag());
        this.mControllers.stream().map(new Function() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda11
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Preference lambda$onCreatePreferences$4;
                lambda$onCreatePreferences$4 = DashboardFragment.this.lambda$onCreatePreferences$4((AbstractPreferenceController) obj);
                return lambda$onCreatePreferences$4;
            }
        }).filter(DashboardFragment$$ExternalSyntheticLambda13.INSTANCE).forEach(new Consumer() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda5
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DashboardFragment.this.lambda$onCreatePreferences$5((Preference) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Preference lambda$onCreatePreferences$4(AbstractPreferenceController abstractPreferenceController) {
        return findPreference(abstractPreferenceController.getPreferenceKey());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreatePreferences$5(Preference preference) {
        preference.getExtras().putInt(CATEGORY, getMetricsCategory());
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        if (this.mDashboardFeatureProvider.getTilesForCategory(getCategoryKey()) != null) {
            FragmentActivity activity = getActivity();
            if (activity instanceof CategoryMixin.CategoryHandler) {
                this.mListeningToCategoryChange = true;
                ((CategoryMixin.CategoryHandler) activity).getCategoryMixin().addCategoryListener(this);
            }
            final ContentResolver contentResolver = getContentResolver();
            this.mDashboardTilePrefKeys.values().stream().filter(DashboardFragment$$ExternalSyntheticLambda14.INSTANCE).flatMap(DashboardFragment$$ExternalSyntheticLambda12.INSTANCE).forEach(new Consumer() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda6
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    DashboardFragment.this.lambda$onStart$6(contentResolver, (DynamicDataObserver) obj);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onStart$6(ContentResolver contentResolver, DynamicDataObserver dynamicDataObserver) {
        if (!this.mRegisteredObservers.contains(dynamicDataObserver)) {
            lambda$registerDynamicDataObservers$8(contentResolver, dynamicDataObserver);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updatePreferenceStates();
        writeElapsedTimeMetric(1729, "isParalleledControllers:" + isParalleledControllers());
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        for (List<AbstractPreferenceController> list : this.mPreferenceControllers.values()) {
            for (AbstractPreferenceController abstractPreferenceController : list) {
                if (abstractPreferenceController.handlePreferenceTreeClick(preference)) {
                    writePreferenceClickMetric(preference);
                    return true;
                }
            }
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        unregisterDynamicDataObservers(new ArrayList(this.mRegisteredObservers));
        if (this.mListeningToCategoryChange) {
            FragmentActivity activity = getActivity();
            if (activity instanceof CategoryMixin.CategoryHandler) {
                ((CategoryMixin.CategoryHandler) activity).getCategoryMixin().removeCategoryListener(this);
            }
            this.mListeningToCategoryChange = false;
        }
    }

    public void onExpandButtonClick() {
        this.mMetricsFeatureProvider.action(0, 834, getMetricsCategory(), null, 0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public <T extends AbstractPreferenceController> T use(Class<T> cls) {
        List<AbstractPreferenceController> list = this.mPreferenceControllers.get(cls);
        if (list == null) {
            return null;
        }
        if (list.size() > 1) {
            Log.w(TAG, "Multiple controllers of Class " + cls.getSimpleName() + " found, returning first one.");
        }
        return (T) list.get(0);
    }

    protected void addPreferenceController(AbstractPreferenceController abstractPreferenceController) {
        if (this.mPreferenceControllers.get(abstractPreferenceController.getClass()) == null) {
            this.mPreferenceControllers.put(abstractPreferenceController.getClass(), new ArrayList());
        }
        this.mPreferenceControllers.get(abstractPreferenceController.getClass()).add(abstractPreferenceController);
    }

    public String getCategoryKey() {
        return DashboardFragmentRegistry.PARENT_TO_CATEGORY_KEY_MAP.get(getClass().getName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean displayTile(Tile tile) {
        if (this.mSuppressInjectedTileKeys == null || !tile.hasKey()) {
            return true;
        }
        return !this.mSuppressInjectedTileKeys.contains(tile.getKey(getContext()));
    }

    private void displayResourceTiles() {
        int preferenceScreenResId = getPreferenceScreenResId();
        if (preferenceScreenResId > 0) {
            addPreferencesFromResource(preferenceScreenResId);
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            preferenceScreen.setOnExpandButtonClickListener(this);
            displayResourceTilesToScreen(preferenceScreen);
        }
    }

    protected void displayResourceTilesToScreen(final PreferenceScreen preferenceScreen) {
        this.mPreferenceControllers.values().stream().flatMap(DashboardFragment$$ExternalSyntheticLambda12.INSTANCE).forEach(new Consumer() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda4
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((AbstractPreferenceController) obj).displayPreference(PreferenceScreen.this);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Collection<List<AbstractPreferenceController>> getPreferenceControllers() {
        return this.mPreferenceControllers.values();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updatePreferenceStates() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        for (List<AbstractPreferenceController> list : this.mPreferenceControllers.values()) {
            for (AbstractPreferenceController abstractPreferenceController : list) {
                if (abstractPreferenceController.isAvailable()) {
                    String preferenceKey = abstractPreferenceController.getPreferenceKey();
                    if (TextUtils.isEmpty(preferenceKey)) {
                        Log.d(TAG, String.format("Preference key is %s in Controller %s", preferenceKey, abstractPreferenceController.getClass().getSimpleName()));
                    } else {
                        Preference findPreference = preferenceScreen.findPreference(preferenceKey);
                        if (findPreference == null) {
                            Log.d(TAG, String.format("Cannot find preference with key %s in Controller %s", preferenceKey, abstractPreferenceController.getClass().getSimpleName()));
                        } else {
                            abstractPreferenceController.updateState(findPreference);
                        }
                    }
                }
            }
        }
    }

    void updatePreferenceStatesInParallel() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        Collection<List<AbstractPreferenceController>> values = this.mPreferenceControllers.values();
        ArrayList<ControllerFutureTask> arrayList = new ArrayList();
        for (List<AbstractPreferenceController> list : values) {
            for (AbstractPreferenceController abstractPreferenceController : list) {
                ControllerFutureTask controllerFutureTask = new ControllerFutureTask(new ControllerTask(abstractPreferenceController, preferenceScreen, this.mMetricsFeatureProvider, getMetricsCategory()), null);
                arrayList.add(controllerFutureTask);
                ThreadUtils.postOnBackgroundThread(controllerFutureTask);
            }
        }
        for (ControllerFutureTask controllerFutureTask2 : arrayList) {
            try {
                controllerFutureTask2.get();
            } catch (InterruptedException | ExecutionException e) {
                Log.w(TAG, controllerFutureTask2.getController().getPreferenceKey() + " " + e.getMessage());
            }
        }
    }

    private void refreshAllPreferences(String str) {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            preferenceScreen.removeAll();
        }
        displayResourceTiles();
        refreshDashboardTiles(str);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            Log.d(str, "All preferences added, reporting fully drawn");
            activity.reportFullyDrawn();
        }
        updatePreferenceVisibility(this.mPreferenceControllers);
    }

    void updatePreferenceVisibility(Map<Class, List<AbstractPreferenceController>> map) {
        UiBlockerController uiBlockerController;
        if (!(getPreferenceScreen() == null || map == null || (uiBlockerController = this.mBlockerController) == null)) {
            boolean isBlockerFinished = uiBlockerController.isBlockerFinished();
            for (List<AbstractPreferenceController> list : map.values()) {
                for (AbstractPreferenceController abstractPreferenceController : list) {
                    Preference findPreference = findPreference(abstractPreferenceController.getPreferenceKey());
                    if (findPreference != null) {
                        findPreference.setVisible(isBlockerFinished && abstractPreferenceController.isAvailable());
                    }
                }
            }
        }
    }

    private void refreshDashboardTiles(String str) {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        DashboardCategory tilesForCategory = this.mDashboardFeatureProvider.getTilesForCategory(getCategoryKey());
        if (tilesForCategory == null) {
            Log.d(str, "NO dashboard tiles for " + str);
            return;
        }
        List<Tile> tiles = tilesForCategory.getTiles();
        if (tiles == null) {
            Log.d(str, "tile list is empty, skipping category " + tilesForCategory.key);
            return;
        }
        ArrayMap arrayMap = new ArrayMap(this.mDashboardTilePrefKeys);
        boolean shouldForceRoundedIcon = shouldForceRoundedIcon();
        for (Tile tile : tiles) {
            String dashboardKeyForTile = this.mDashboardFeatureProvider.getDashboardKeyForTile(tile);
            if (TextUtils.isEmpty(dashboardKeyForTile)) {
                Log.d(str, "tile does not contain a key, skipping " + tile);
            } else if (displayTile(tile)) {
                if (this.mDashboardTilePrefKeys.containsKey(dashboardKeyForTile)) {
                    this.mDashboardFeatureProvider.bindPreferenceToTileAndGetObservers(getActivity(), this, shouldForceRoundedIcon, preferenceScreen.findPreference(dashboardKeyForTile), tile, dashboardKeyForTile, this.mPlaceholderPreferenceController.getOrder());
                } else {
                    Preference createPreference = createPreference(tile);
                    List<DynamicDataObserver> bindPreferenceToTileAndGetObservers = this.mDashboardFeatureProvider.bindPreferenceToTileAndGetObservers(getActivity(), this, shouldForceRoundedIcon, createPreference, tile, dashboardKeyForTile, this.mPlaceholderPreferenceController.getOrder());
                    preferenceScreen.addPreference(createPreference);
                    registerDynamicDataObservers(bindPreferenceToTileAndGetObservers);
                    this.mDashboardTilePrefKeys.put(dashboardKeyForTile, bindPreferenceToTileAndGetObservers);
                }
                arrayMap.remove(dashboardKeyForTile);
            }
        }
        for (Map.Entry entry : arrayMap.entrySet()) {
            String str2 = (String) entry.getKey();
            this.mDashboardTilePrefKeys.remove(str2);
            Preference findPreference = preferenceScreen.findPreference(str2);
            if (findPreference != null) {
                preferenceScreen.removePreference(findPreference);
            }
            unregisterDynamicDataObservers((List) entry.getValue());
        }
    }

    @Override // com.android.settings.core.BasePreferenceController.UiBlockListener
    public void onBlockerWorkFinished(BasePreferenceController basePreferenceController) {
        this.mBlockerController.countDown(basePreferenceController.getPreferenceKey());
    }

    protected Preference createPreference(Tile tile) {
        if (tile instanceof ProviderTile) {
            return new SwitchPreference(getPrefContext());
        }
        if (tile.hasSwitch()) {
            return new PrimarySwitchPreference(getPrefContext());
        }
        return new Preference(getPrefContext());
    }

    void registerDynamicDataObservers(List<DynamicDataObserver> list) {
        if (list != null && !list.isEmpty()) {
            final ContentResolver contentResolver = getContentResolver();
            list.forEach(new Consumer() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda8
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    DashboardFragment.this.lambda$registerDynamicDataObservers$8(contentResolver, (DynamicDataObserver) obj);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: registerDynamicDataObserver */
    public void lambda$registerDynamicDataObservers$8(ContentResolver contentResolver, DynamicDataObserver dynamicDataObserver) {
        Log.d(TAG, "register observer: @" + Integer.toHexString(dynamicDataObserver.hashCode()) + ", uri: " + dynamicDataObserver.getUri());
        contentResolver.registerContentObserver(dynamicDataObserver.getUri(), false, dynamicDataObserver);
        this.mRegisteredObservers.add(dynamicDataObserver);
    }

    private void unregisterDynamicDataObservers(List<DynamicDataObserver> list) {
        if (list != null && !list.isEmpty()) {
            final ContentResolver contentResolver = getContentResolver();
            list.forEach(new Consumer() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda7
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    DashboardFragment.this.lambda$unregisterDynamicDataObservers$9(contentResolver, (DynamicDataObserver) obj);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$unregisterDynamicDataObservers$9(ContentResolver contentResolver, DynamicDataObserver dynamicDataObserver) {
        Log.d(TAG, "unregister observer: @" + Integer.toHexString(dynamicDataObserver.hashCode()) + ", uri: " + dynamicDataObserver.getUri());
        this.mRegisteredObservers.remove(dynamicDataObserver);
        contentResolver.unregisterContentObserver(dynamicDataObserver);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void replaceEnterpriseStringTitle(String str, String str2, final int i) {
        Preference findPreference = findPreference(str);
        if (findPreference == null) {
            Log.d(TAG, "Could not find enterprise preference " + str);
            return;
        }
        findPreference.setTitle(this.mDevicePolicyManager.getString(str2, new Callable() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda1
            @Override // java.util.concurrent.Callable
            public final Object call() {
                String lambda$replaceEnterpriseStringTitle$10;
                lambda$replaceEnterpriseStringTitle$10 = DashboardFragment.this.lambda$replaceEnterpriseStringTitle$10(i);
                return lambda$replaceEnterpriseStringTitle$10;
            }
        }));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$replaceEnterpriseStringTitle$10(int i) throws Exception {
        return getString(i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void replaceEnterpriseStringSummary(String str, String str2, final int i) {
        Preference findPreference = findPreference(str);
        if (findPreference == null) {
            Log.d(TAG, "Could not find enterprise preference " + str);
            return;
        }
        findPreference.setSummary(this.mDevicePolicyManager.getString(str2, new Callable() { // from class: com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda2
            @Override // java.util.concurrent.Callable
            public final Object call() {
                String lambda$replaceEnterpriseStringSummary$11;
                lambda$replaceEnterpriseStringSummary$11 = DashboardFragment.this.lambda$replaceEnterpriseStringSummary$11(i);
                return lambda$replaceEnterpriseStringSummary$11;
            }
        }));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$replaceEnterpriseStringSummary$11(int i) throws Exception {
        return getString(i);
    }
}
