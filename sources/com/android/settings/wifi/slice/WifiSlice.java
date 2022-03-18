package com.android.settings.wifi.slice;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import androidx.window.R;
import com.android.settings.SubSettings;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.network.NetworkProviderSettings;
import com.android.settings.network.ProviderModelSliceHelper$$ExternalSyntheticLambda1;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.CustomSliceable;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.slices.SliceBuilderUtils;
import com.android.settings.wifi.WifiDialogActivity;
import com.android.settings.wifi.details.WifiNetworkDetailsFragment;
import com.android.settingslib.Utils;
import com.android.settingslib.wifi.WifiUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class WifiSlice implements CustomSliceable {
    static final int DEFAULT_EXPANDED_ROW_COUNT = 3;
    protected final Context mContext;
    protected final WifiManager mWifiManager;

    @Override // com.android.settings.slices.CustomSliceable, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R.string.menu_key_network;
    }

    protected boolean isApRowCollapsed() {
        return false;
    }

    public WifiSlice(Context context) {
        this.mContext = context;
        this.mWifiManager = (WifiManager) context.getSystemService(WifiManager.class);
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Uri getUri() {
        return CustomSliceRegistry.WIFI_SLICE_URI;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Slice getSlice() {
        boolean isWifiEnabled = isWifiEnabled();
        List<WifiSliceItem> list = null;
        ListBuilder listBuilder = getListBuilder(isWifiEnabled, null);
        if (!isWifiEnabled) {
            return listBuilder.build();
        }
        WifiScanWorker wifiScanWorker = (WifiScanWorker) SliceBackgroundWorker.getInstance(getUri());
        if (wifiScanWorker != null) {
            list = wifiScanWorker.getResults();
        }
        int size = list == null ? 0 : list.size();
        if (size > 0 && list.get(0).getConnectedState() != 0) {
            listBuilder = getListBuilder(true, list.get(0));
        }
        if (isApRowCollapsed()) {
            return listBuilder.build();
        }
        CharSequence text = this.mContext.getText(R.string.summary_placeholder);
        for (int i = 0; i < 3; i++) {
            if (i < size) {
                listBuilder.addRow(getWifiSliceItemRow(list.get(i)));
            } else if (i == size) {
                listBuilder.addRow(getLoadingRow(text));
            } else {
                listBuilder.addRow(new ListBuilder.RowBuilder().setTitle(text).setSubtitle(text));
            }
        }
        return listBuilder.build();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ListBuilder.RowBuilder getHeaderRow(boolean z, WifiSliceItem wifiSliceItem) {
        IconCompat createWithResource = IconCompat.createWithResource(this.mContext, R.drawable.ic_settings_wireless);
        String string = this.mContext.getString(R.string.wifi_settings);
        return new ListBuilder.RowBuilder().setTitle(string).setPrimaryAction(SliceAction.createDeeplink(getPrimaryAction(), createWithResource, 0, string));
    }

    private ListBuilder getListBuilder(boolean z, WifiSliceItem wifiSliceItem) {
        return new ListBuilder(this.mContext, getUri(), -1L).setAccentColor(-1).setKeywords(getKeywords()).addRow(getHeaderRow(z, wifiSliceItem)).addAction(SliceAction.createToggle(getBroadcastIntent(this.mContext), null, z));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ListBuilder.RowBuilder getWifiSliceItemRow(WifiSliceItem wifiSliceItem) {
        String title = wifiSliceItem.getTitle();
        IconCompat wifiSliceItemLevelIcon = getWifiSliceItemLevelIcon(wifiSliceItem);
        ListBuilder.RowBuilder primaryAction = new ListBuilder.RowBuilder().setTitleItem(wifiSliceItemLevelIcon, 0).setTitle(title).setSubtitle(wifiSliceItem.getSummary()).setContentDescription(wifiSliceItem.getContentDescription()).setPrimaryAction(getWifiEntryAction(wifiSliceItem, wifiSliceItemLevelIcon, title));
        IconCompat endIcon = getEndIcon(wifiSliceItem);
        if (endIcon != null) {
            primaryAction.addEndItem(endIcon, 0);
        }
        return primaryAction;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public IconCompat getWifiSliceItemLevelIcon(WifiSliceItem wifiSliceItem) {
        int i;
        if (wifiSliceItem.getConnectedState() == 2) {
            i = Utils.getColorAccentDefaultColor(this.mContext);
        } else if (wifiSliceItem.getConnectedState() == 0) {
            i = Utils.getColorAttrDefaultColor(this.mContext, 16843817);
        } else {
            Context context = this.mContext;
            i = Utils.getDisabled(context, Utils.getColorAttrDefaultColor(context, 16843817));
        }
        Drawable drawable = this.mContext.getDrawable(WifiUtils.getInternetIconResource(wifiSliceItem.getLevel(), wifiSliceItem.shouldShowXLevelIcon()));
        drawable.setTint(i);
        return com.android.settings.Utils.createIconWithDrawable(drawable);
    }

    protected IconCompat getEndIcon(WifiSliceItem wifiSliceItem) {
        if (wifiSliceItem.getConnectedState() != 0) {
            return IconCompat.createWithResource(this.mContext, R.drawable.ic_settings_24dp);
        }
        if (wifiSliceItem.getSecurity() != 0) {
            return IconCompat.createWithResource(this.mContext, R.drawable.ic_friction_lock_closed);
        }
        return null;
    }

    protected SliceAction getWifiEntryAction(WifiSliceItem wifiSliceItem, IconCompat iconCompat, CharSequence charSequence) {
        int hashCode = wifiSliceItem.getKey().hashCode();
        if (wifiSliceItem.getConnectedState() != 0) {
            Bundle bundle = new Bundle();
            bundle.putString("key_chosen_wifientry_key", wifiSliceItem.getKey());
            return getActivityAction(hashCode, new SubSettingLauncher(this.mContext).setTitleRes(R.string.pref_title_network_details).setDestination(WifiNetworkDetailsFragment.class.getName()).setArguments(bundle).setSourceMetricsCategory(103).toIntent(), iconCompat, charSequence);
        } else if (wifiSliceItem.shouldEditBeforeConnect()) {
            return getActivityAction(hashCode, new Intent(this.mContext, WifiDialogActivity.class).putExtra("key_chosen_wifientry_key", wifiSliceItem.getKey()), iconCompat, charSequence);
        } else {
            return getBroadcastAction(hashCode, new Intent(this.mContext, ConnectToWifiHandler.class).putExtra("key_chosen_wifientry_key", wifiSliceItem.getKey()).putExtra("key_wifi_slice_uri", getUri()), iconCompat, charSequence);
        }
    }

    private SliceAction getActivityAction(int i, Intent intent, IconCompat iconCompat, CharSequence charSequence) {
        return SliceAction.createDeeplink(PendingIntent.getActivity(this.mContext, i, intent, 67108864), iconCompat, 0, charSequence);
    }

    private SliceAction getBroadcastAction(int i, Intent intent, IconCompat iconCompat, CharSequence charSequence) {
        intent.addFlags(268435456);
        return SliceAction.create(PendingIntent.getBroadcast(this.mContext, i, intent, 201326592), iconCompat, 0, charSequence);
    }

    private ListBuilder.RowBuilder getLoadingRow(CharSequence charSequence) {
        return new ListBuilder.RowBuilder().setTitleItem(com.android.settings.Utils.createIconWithDrawable(new ColorDrawable(0)), 0).setTitle(charSequence).setSubtitle(this.mContext.getText(R.string.wifi_empty_list_wifi_on));
    }

    @Override // com.android.settings.slices.CustomSliceable
    public void onNotifyChange(Intent intent) {
        this.mWifiManager.setWifiEnabled(intent.getBooleanExtra("android.app.slice.extra.TOGGLE_STATE", this.mWifiManager.isWifiEnabled()));
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Intent getIntent() {
        String charSequence = this.mContext.getText(R.string.wifi_settings).toString();
        return SliceBuilderUtils.buildSearchResultPageIntent(this.mContext, NetworkProviderSettings.class.getName(), "main_toggle_wifi", charSequence, 603, this).setClassName(this.mContext.getPackageName(), SubSettings.class.getName()).setData(new Uri.Builder().appendPath("wifi").build());
    }

    private boolean isWifiEnabled() {
        int wifiState = this.mWifiManager.getWifiState();
        return wifiState == 2 || wifiState == 3;
    }

    private PendingIntent getPrimaryAction() {
        return PendingIntent.getActivity(this.mContext, 0, getIntent(), 67108864);
    }

    private Set<String> getKeywords() {
        return (Set) Arrays.asList(TextUtils.split(this.mContext.getString(R.string.keywords_wifi), ",")).stream().map(ProviderModelSliceHelper$$ExternalSyntheticLambda1.INSTANCE).collect(Collectors.toSet());
    }

    @Override // com.android.settings.slices.Sliceable
    public Class getBackgroundWorkerClass() {
        return WifiScanWorker.class;
    }
}
