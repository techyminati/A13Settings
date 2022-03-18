package com.android.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import androidx.window.R;
import com.android.internal.util.Preconditions;
import com.android.settings.R$styleable;
/* loaded from: classes.dex */
public class LabeledSeekBarPreference extends SeekBarPreference {
    private final int mIconEndContentDescriptionId;
    private final int mIconEndId;
    private final int mIconStartContentDescriptionId;
    private final int mIconStartId;
    private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener;
    private Preference.OnPreferenceChangeListener mStopListener;
    private CharSequence mSummary;
    private final int mTextEndId;
    private final int mTextStartId;
    private final int mTickMarkId;

    public LabeledSeekBarPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        setLayoutResource(R.layout.preference_labeled_slider);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.LabeledSeekBarPreference);
        boolean z = false;
        this.mTextStartId = obtainStyledAttributes.getResourceId(5, 0);
        this.mTextEndId = obtainStyledAttributes.getResourceId(4, 0);
        this.mTickMarkId = obtainStyledAttributes.getResourceId(6, 0);
        int resourceId = obtainStyledAttributes.getResourceId(2, 0);
        this.mIconStartId = resourceId;
        int resourceId2 = obtainStyledAttributes.getResourceId(0, 0);
        this.mIconEndId = resourceId2;
        int resourceId3 = obtainStyledAttributes.getResourceId(3, 0);
        this.mIconStartContentDescriptionId = resourceId3;
        Preconditions.checkArgument(resourceId3 == 0 || resourceId != 0, "The resource of the iconStart attribute may be invalid or not set, you should set the iconStart attribute and have the valid resource.");
        int resourceId4 = obtainStyledAttributes.getResourceId(1, 0);
        this.mIconEndContentDescriptionId = resourceId4;
        Preconditions.checkArgument((resourceId4 == 0 || resourceId2 != 0) ? true : z, "The resource of the iconEnd attribute may be invalid or not set, you should set the iconEnd attribute and have the valid resource.");
        this.mSummary = obtainStyledAttributes.getText(7);
        obtainStyledAttributes.recycle();
    }

    public LabeledSeekBarPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, TypedArrayUtils.getAttr(context, R.attr.seekBarPreferenceStyle, 17957081), 0);
    }

    @Override // com.android.settings.widget.SeekBarPreference, com.android.settingslib.RestrictedPreference, com.android.settingslib.widget.TwoTargetPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        TextView textView = (TextView) preferenceViewHolder.findViewById(16908308);
        int i = this.mTextStartId;
        if (i > 0) {
            textView.setText(i);
        }
        TextView textView2 = (TextView) preferenceViewHolder.findViewById(16908309);
        int i2 = this.mTextEndId;
        if (i2 > 0) {
            textView2.setText(i2);
        }
        preferenceViewHolder.findViewById(R.id.label_frame).setVisibility(this.mTextStartId > 0 || this.mTextEndId > 0 ? 0 : 8);
        SeekBar seekBar = (SeekBar) preferenceViewHolder.findViewById(16909454);
        if (this.mTickMarkId != 0) {
            seekBar.setTickMark(getContext().getDrawable(this.mTickMarkId));
        }
        TextView textView3 = (TextView) preferenceViewHolder.findViewById(16908304);
        CharSequence charSequence = this.mSummary;
        if (charSequence != null) {
            textView3.setText(charSequence);
            textView3.setVisibility(0);
        } else {
            textView3.setText((CharSequence) null);
            textView3.setVisibility(8);
        }
        updateIconStartIfNeeded((ViewGroup) preferenceViewHolder.findViewById(R.id.icon_start_frame), (ImageView) preferenceViewHolder.findViewById(R.id.icon_start), seekBar);
        updateIconEndIfNeeded((ViewGroup) preferenceViewHolder.findViewById(R.id.icon_end_frame), (ImageView) preferenceViewHolder.findViewById(R.id.icon_end), seekBar);
    }

    public void setOnPreferenceChangeStopListener(Preference.OnPreferenceChangeListener onPreferenceChangeListener) {
        this.mStopListener = onPreferenceChangeListener;
    }

    @Override // com.android.settings.widget.SeekBarPreference, android.widget.SeekBar.OnSeekBarChangeListener
    public void onStartTrackingTouch(SeekBar seekBar) {
        super.onStartTrackingTouch(seekBar);
        SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = this.mSeekBarChangeListener;
        if (onSeekBarChangeListener != null) {
            onSeekBarChangeListener.onStartTrackingTouch(seekBar);
        }
    }

    @Override // com.android.settings.widget.SeekBarPreference, android.widget.SeekBar.OnSeekBarChangeListener
    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        super.onProgressChanged(seekBar, i, z);
        SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = this.mSeekBarChangeListener;
        if (onSeekBarChangeListener != null) {
            onSeekBarChangeListener.onProgressChanged(seekBar, i, z);
        }
    }

    @Override // com.android.settings.widget.SeekBarPreference, android.widget.SeekBar.OnSeekBarChangeListener
    public void onStopTrackingTouch(SeekBar seekBar) {
        super.onStopTrackingTouch(seekBar);
        SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = this.mSeekBarChangeListener;
        if (onSeekBarChangeListener != null) {
            onSeekBarChangeListener.onStopTrackingTouch(seekBar);
        }
        Preference.OnPreferenceChangeListener onPreferenceChangeListener = this.mStopListener;
        if (onPreferenceChangeListener != null) {
            onPreferenceChangeListener.onPreferenceChange(this, Integer.valueOf(seekBar.getProgress()));
        }
        notifyChanged();
    }

    @Override // androidx.preference.Preference
    public void setSummary(CharSequence charSequence) {
        super.setSummary(charSequence);
        this.mSummary = charSequence;
        notifyChanged();
    }

    @Override // androidx.preference.Preference
    public void setSummary(int i) {
        super.setSummary(i);
        this.mSummary = getContext().getText(i);
        notifyChanged();
    }

    @Override // com.android.settings.widget.SeekBarPreference, androidx.preference.Preference
    public CharSequence getSummary() {
        return this.mSummary;
    }

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener onSeekBarChangeListener) {
        this.mSeekBarChangeListener = onSeekBarChangeListener;
    }

    private void updateIconStartIfNeeded(ViewGroup viewGroup, ImageView imageView, SeekBar seekBar) {
        if (this.mIconStartId != 0) {
            if (imageView.getDrawable() == null) {
                imageView.setImageResource(this.mIconStartId);
            }
            if (this.mIconStartContentDescriptionId != 0) {
                viewGroup.setContentDescription(viewGroup.getContext().getString(this.mIconStartContentDescriptionId));
            }
            viewGroup.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.widget.LabeledSeekBarPreference$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    LabeledSeekBarPreference.this.lambda$updateIconStartIfNeeded$0(view);
                }
            });
            boolean z = false;
            viewGroup.setVisibility(0);
            if (seekBar.getProgress() > 0) {
                z = true;
            }
            setIconViewAndFrameEnabled(imageView, z);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateIconStartIfNeeded$0(View view) {
        int progress = getProgress();
        if (progress > 0) {
            setProgress(progress - 1);
        }
    }

    private void updateIconEndIfNeeded(ViewGroup viewGroup, ImageView imageView, SeekBar seekBar) {
        if (this.mIconEndId != 0) {
            if (imageView.getDrawable() == null) {
                imageView.setImageResource(this.mIconEndId);
            }
            if (this.mIconEndContentDescriptionId != 0) {
                viewGroup.setContentDescription(viewGroup.getContext().getString(this.mIconEndContentDescriptionId));
            }
            viewGroup.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.widget.LabeledSeekBarPreference$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    LabeledSeekBarPreference.this.lambda$updateIconEndIfNeeded$1(view);
                }
            });
            boolean z = false;
            viewGroup.setVisibility(0);
            if (seekBar.getProgress() < seekBar.getMax()) {
                z = true;
            }
            setIconViewAndFrameEnabled(imageView, z);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateIconEndIfNeeded$1(View view) {
        int progress = getProgress();
        if (progress < getMax()) {
            setProgress(progress + 1);
        }
    }

    private static void setIconViewAndFrameEnabled(View view, boolean z) {
        view.setEnabled(z);
        ((ViewGroup) view.getParent()).setEnabled(z);
    }
}
