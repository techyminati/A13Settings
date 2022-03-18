package com.android.settings.accessibility;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import androidx.window.R;
/* loaded from: classes.dex */
public class ShortcutPreference extends Preference {
    private OnClickCallback mClickCallback = null;
    private boolean mChecked = false;
    private boolean mSettingsEditable = true;

    /* loaded from: classes.dex */
    public interface OnClickCallback {
        void onSettingsClicked(ShortcutPreference shortcutPreference);

        void onToggleClicked(ShortcutPreference shortcutPreference);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ShortcutPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutResource(R.layout.accessibility_shortcut_secondary_action);
        setWidgetLayoutResource(R.layout.preference_widget_primary_switch);
        setIconSpaceReserved(false);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(16843534, typedValue, true);
        LinearLayout linearLayout = (LinearLayout) preferenceViewHolder.itemView.findViewById(R.id.main_frame);
        int i = 0;
        if (linearLayout != null) {
            linearLayout.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.accessibility.ShortcutPreference$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ShortcutPreference.this.lambda$onBindViewHolder$0(view);
                }
            });
            linearLayout.setClickable(this.mSettingsEditable);
            linearLayout.setFocusable(this.mSettingsEditable);
            linearLayout.setBackgroundResource(this.mSettingsEditable ? typedValue.resourceId : 0);
        }
        Switch r1 = (Switch) preferenceViewHolder.itemView.findViewById(R.id.switchWidget);
        if (r1 != null) {
            r1.setOnTouchListener(ShortcutPreference$$ExternalSyntheticLambda3.INSTANCE);
            r1.setContentDescription(getContext().getText(R.string.accessibility_shortcut_settings));
            r1.setChecked(this.mChecked);
            r1.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.accessibility.ShortcutPreference$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ShortcutPreference.this.lambda$onBindViewHolder$2(view);
                }
            });
            r1.setClickable(this.mSettingsEditable);
            r1.setFocusable(this.mSettingsEditable);
            r1.setBackgroundResource(this.mSettingsEditable ? typedValue.resourceId : 0);
        }
        View findViewById = preferenceViewHolder.itemView.findViewById(R.id.divider);
        if (findViewById != null) {
            if (!this.mSettingsEditable) {
                i = 8;
            }
            findViewById.setVisibility(i);
        }
        preferenceViewHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.accessibility.ShortcutPreference$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ShortcutPreference.this.lambda$onBindViewHolder$3(view);
            }
        });
        preferenceViewHolder.itemView.setClickable(!this.mSettingsEditable);
        preferenceViewHolder.itemView.setFocusable(!this.mSettingsEditable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$0(View view) {
        callOnSettingsClicked();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$onBindViewHolder$1(View view, MotionEvent motionEvent) {
        return motionEvent.getActionMasked() == 2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$2(View view) {
        callOnToggleClicked();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$3(View view) {
        callOnToggleClicked();
    }

    public void setChecked(boolean z) {
        if (this.mChecked != z) {
            this.mChecked = z;
            notifyChanged();
        }
    }

    public boolean isChecked() {
        return this.mChecked;
    }

    public void setSettingsEditable(boolean z) {
        if (this.mSettingsEditable != z) {
            this.mSettingsEditable = z;
            notifyChanged();
        }
    }

    public boolean isSettingsEditable() {
        return this.mSettingsEditable;
    }

    public void setOnClickCallback(OnClickCallback onClickCallback) {
        this.mClickCallback = onClickCallback;
    }

    private void callOnSettingsClicked() {
        OnClickCallback onClickCallback = this.mClickCallback;
        if (onClickCallback != null) {
            onClickCallback.onSettingsClicked(this);
        }
    }

    private void callOnToggleClicked() {
        setChecked(!this.mChecked);
        OnClickCallback onClickCallback = this.mClickCallback;
        if (onClickCallback != null) {
            onClickCallback.onToggleClicked(this);
        }
    }
}
