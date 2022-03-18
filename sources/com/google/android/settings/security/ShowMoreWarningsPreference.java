package com.google.android.settings.security;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import androidx.window.R;
import com.google.android.material.card.MaterialCardView;
/* loaded from: classes2.dex */
public class ShowMoreWarningsPreference extends Preference {
    private int mBackgroundColor;

    public ShowMoreWarningsPreference(Context context) {
        this(context, null);
    }

    public ShowMoreWarningsPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutResource(R.layout.preference_security_show_warnings);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        ((TextView) preferenceViewHolder.findViewById(R.id.show_more_title)).setText(getTitle());
        ((MaterialCardView) preferenceViewHolder.itemView).setCardBackgroundColor(this.mBackgroundColor);
    }

    public void setCardBackgroundColor(int i) {
        this.mBackgroundColor = i;
        notifyChanged();
    }
}
