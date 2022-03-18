package com.android.settings.wifi;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import androidx.window.R;
import com.android.settingslib.Utils;
import com.android.settingslib.wifi.WifiUtils;
import com.android.wifitrackerlib.WifiEntry;
/* loaded from: classes.dex */
public class WifiEntryPreference extends Preference implements WifiEntry.WifiEntryCallback, View.OnClickListener {
    private CharSequence mContentDescription;
    private final StateListDrawable mFrictionSld;
    private final WifiUtils.InternetIconInjector mIconInjector;
    private int mLevel;
    private OnButtonClickListener mOnButtonClickListener;
    private boolean mShowX;
    private WifiEntry mWifiEntry;
    private static final int[] STATE_SECURED = {R.attr.state_encrypted};
    private static final int[] FRICTION_ATTRS = {R.attr.wifi_friction};
    private static final int[] WIFI_CONNECTION_STRENGTH = {R.string.accessibility_no_wifi, R.string.accessibility_wifi_one_bar, R.string.accessibility_wifi_two_bars, R.string.accessibility_wifi_three_bars, R.string.accessibility_wifi_signal_full};

    /* loaded from: classes.dex */
    public interface OnButtonClickListener {
        void onButtonClick(WifiEntryPreference wifiEntryPreference);
    }

    public WifiEntryPreference(Context context, WifiEntry wifiEntry) {
        this(context, wifiEntry, new WifiUtils.InternetIconInjector(context));
    }

    WifiEntryPreference(Context context, WifiEntry wifiEntry, WifiUtils.InternetIconInjector internetIconInjector) {
        super(context);
        this.mLevel = -1;
        setLayoutResource(R.layout.preference_access_point);
        setWidgetLayoutResource(R.layout.access_point_friction_widget);
        this.mFrictionSld = getFrictionStateListDrawable();
        this.mWifiEntry = wifiEntry;
        wifiEntry.setListener(this);
        this.mIconInjector = internetIconInjector;
        refresh();
    }

    public WifiEntry getWifiEntry() {
        return this.mWifiEntry;
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        Drawable icon = getIcon();
        if (icon != null) {
            icon.setLevel(this.mLevel);
        }
        preferenceViewHolder.itemView.setContentDescription(this.mContentDescription);
        preferenceViewHolder.findViewById(R.id.two_target_divider).setVisibility(4);
        ImageButton imageButton = (ImageButton) preferenceViewHolder.findViewById(R.id.icon_button);
        ImageView imageView = (ImageView) preferenceViewHolder.findViewById(R.id.friction_icon);
        if (this.mWifiEntry.getHelpUriString() == null || this.mWifiEntry.getConnectedState() != 0) {
            imageButton.setVisibility(8);
            if (imageView != null) {
                imageView.setVisibility(0);
                bindFrictionImage(imageView);
                return;
            }
            return;
        }
        Drawable drawable = getDrawable(R.drawable.ic_help);
        drawable.setTintList(Utils.getColorAttr(getContext(), 16843817));
        imageButton.setImageDrawable(drawable);
        imageButton.setVisibility(0);
        imageButton.setOnClickListener(this);
        imageButton.setContentDescription(getContext().getText(R.string.help_label));
        if (imageView != null) {
            imageView.setVisibility(8);
        }
    }

    public void refresh() {
        setTitle(this.mWifiEntry.getTitle());
        int level = this.mWifiEntry.getLevel();
        boolean shouldShowXLevelIcon = this.mWifiEntry.shouldShowXLevelIcon();
        if (!(level == this.mLevel && shouldShowXLevelIcon == this.mShowX)) {
            this.mLevel = level;
            this.mShowX = shouldShowXLevelIcon;
            updateIcon(shouldShowXLevelIcon, level);
            notifyChanged();
        }
        setSummary(this.mWifiEntry.getSummary(false));
        this.mContentDescription = buildContentDescription();
    }

    @Override // com.android.wifitrackerlib.WifiEntry.WifiEntryCallback
    public void onUpdated() {
        refresh();
    }

    protected int getIconColorAttr() {
        return this.mWifiEntry.hasInternetAccess() && this.mWifiEntry.getConnectedState() == 2 ? 16843829 : 16843817;
    }

    private void updateIcon(boolean z, int i) {
        if (i == -1) {
            setIcon((Drawable) null);
            return;
        }
        Drawable icon = this.mIconInjector.getIcon(z, i);
        if (icon != null) {
            icon.setTint(Utils.getColorAttrDefaultColor(getContext(), getIconColorAttr()));
            setIcon(icon);
            return;
        }
        setIcon((Drawable) null);
    }

    private StateListDrawable getFrictionStateListDrawable() {
        TypedArray typedArray;
        try {
            typedArray = getContext().getTheme().obtainStyledAttributes(FRICTION_ATTRS);
        } catch (Resources.NotFoundException unused) {
            typedArray = null;
        }
        if (typedArray != null) {
            return (StateListDrawable) typedArray.getDrawable(0);
        }
        return null;
    }

    private void bindFrictionImage(ImageView imageView) {
        if (imageView != null && this.mFrictionSld != null) {
            if (!(this.mWifiEntry.getSecurity() == 0 || this.mWifiEntry.getSecurity() == 4)) {
                this.mFrictionSld.setState(STATE_SECURED);
            }
            imageView.setImageDrawable(this.mFrictionSld.getCurrent());
        }
    }

    CharSequence buildContentDescription() {
        String str;
        Context context = getContext();
        CharSequence title = getTitle();
        CharSequence summary = getSummary();
        if (!TextUtils.isEmpty(summary)) {
            title = TextUtils.concat(title, ",", summary);
        }
        int level = this.mWifiEntry.getLevel();
        if (level >= 0) {
            int[] iArr = WIFI_CONNECTION_STRENGTH;
            if (level < iArr.length) {
                title = TextUtils.concat(title, ",", context.getString(iArr[level]));
            }
        }
        CharSequence[] charSequenceArr = new CharSequence[3];
        charSequenceArr[0] = title;
        charSequenceArr[1] = ",";
        if (this.mWifiEntry.getSecurity() == 0) {
            str = context.getString(R.string.accessibility_wifi_security_type_none);
        } else {
            str = context.getString(R.string.accessibility_wifi_security_type_secured);
        }
        charSequenceArr[2] = str;
        return TextUtils.concat(charSequenceArr);
    }

    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        this.mOnButtonClickListener = onButtonClickListener;
        notifyChanged();
    }

    public void onClick(View view) {
        OnButtonClickListener onButtonClickListener;
        if (view.getId() == R.id.icon_button && (onButtonClickListener = this.mOnButtonClickListener) != null) {
            onButtonClickListener.onButtonClick(this);
        }
    }

    private Drawable getDrawable(int i) {
        try {
            return getContext().getDrawable(i);
        } catch (Resources.NotFoundException unused) {
            return null;
        }
    }
}
