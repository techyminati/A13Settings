package com.android.settings.notification.zen;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import androidx.window.R;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
/* loaded from: classes.dex */
public class ZenModeScheduleDaysSelection extends ScrollView {
    private final SimpleDateFormat mDayFormat = new SimpleDateFormat("EEEE");
    private final SparseBooleanArray mDays = new SparseBooleanArray();
    private final LinearLayout mLayout;

    protected void onChanged(int[] iArr) {
    }

    public ZenModeScheduleDaysSelection(Context context, int[] iArr) {
        super(context);
        LinearLayout linearLayout = new LinearLayout(((ScrollView) this).mContext);
        this.mLayout = linearLayout;
        int dimensionPixelSize = context.getResources().getDimensionPixelSize(R.dimen.zen_schedule_day_margin);
        linearLayout.setPadding(dimensionPixelSize, 0, dimensionPixelSize, 0);
        addView(linearLayout);
        if (iArr != null) {
            for (int i : iArr) {
                this.mDays.put(i, true);
            }
        }
        this.mLayout.setOrientation(1);
        Calendar instance = Calendar.getInstance();
        int[] daysOfWeekForLocale = getDaysOfWeekForLocale(instance);
        LayoutInflater from = LayoutInflater.from(context);
        for (final int i2 : daysOfWeekForLocale) {
            CheckBox checkBox = (CheckBox) from.inflate(R.layout.zen_schedule_rule_day, (ViewGroup) this, false);
            instance.set(7, i2);
            checkBox.setText(this.mDayFormat.format(instance.getTime()));
            checkBox.setChecked(this.mDays.get(i2));
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.notification.zen.ZenModeScheduleDaysSelection.1
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    ZenModeScheduleDaysSelection.this.mDays.put(i2, z);
                    ZenModeScheduleDaysSelection zenModeScheduleDaysSelection = ZenModeScheduleDaysSelection.this;
                    zenModeScheduleDaysSelection.onChanged(zenModeScheduleDaysSelection.getDays());
                }
            });
            this.mLayout.addView(checkBox);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int[] getDays() {
        SparseBooleanArray sparseBooleanArray = new SparseBooleanArray(this.mDays.size());
        for (int i = 0; i < this.mDays.size(); i++) {
            int keyAt = this.mDays.keyAt(i);
            if (this.mDays.valueAt(i)) {
                sparseBooleanArray.put(keyAt, true);
            }
        }
        int size = sparseBooleanArray.size();
        int[] iArr = new int[size];
        for (int i2 = 0; i2 < size; i2++) {
            iArr[i2] = sparseBooleanArray.keyAt(i2);
        }
        Arrays.sort(iArr);
        return iArr;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int[] getDaysOfWeekForLocale(Calendar calendar) {
        int[] iArr = new int[7];
        int firstDayOfWeek = calendar.getFirstDayOfWeek();
        for (int i = 0; i < 7; i++) {
            if (firstDayOfWeek > 7) {
                firstDayOfWeek = 1;
            }
            iArr[i] = firstDayOfWeek;
            firstDayOfWeek++;
        }
        return iArr;
    }
}
