package com.android.settings.datausage;

import android.content.Context;
import android.widget.AdapterView;
import com.android.settings.Utils;
import com.android.settingslib.net.NetworkCycleData;
import com.android.settingslib.widget.SettingsSpinnerAdapter;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public class CycleAdapter extends SettingsSpinnerAdapter<CycleItem> {
    private final AdapterView.OnItemSelectedListener mListener;
    private final SpinnerInterface mSpinner;

    /* loaded from: classes.dex */
    public interface SpinnerInterface {
        Object getSelectedItem();

        void setAdapter(CycleAdapter cycleAdapter);

        void setOnItemSelectedListener(AdapterView.OnItemSelectedListener onItemSelectedListener);

        void setSelection(int i);
    }

    public CycleAdapter(Context context, SpinnerInterface spinnerInterface, AdapterView.OnItemSelectedListener onItemSelectedListener) {
        super(context);
        this.mSpinner = spinnerInterface;
        this.mListener = onItemSelectedListener;
        spinnerInterface.setAdapter(this);
        spinnerInterface.setOnItemSelectedListener(onItemSelectedListener);
    }

    public int findNearestPosition(CycleItem cycleItem) {
        if (cycleItem == null) {
            return 0;
        }
        for (int count = getCount() - 1; count >= 0; count--) {
            if (getItem(count).compareTo(cycleItem) >= 0) {
                return count;
            }
        }
        return 0;
    }

    public boolean updateCycleList(List<? extends NetworkCycleData> list) {
        CycleItem cycleItem = (CycleItem) this.mSpinner.getSelectedItem();
        clear();
        Context context = getContext();
        for (NetworkCycleData networkCycleData : list) {
            add(new CycleItem(context, networkCycleData.getStartTime(), networkCycleData.getEndTime()));
        }
        if (getCount() <= 0) {
            return true;
        }
        int findNearestPosition = findNearestPosition(cycleItem);
        this.mSpinner.setSelection(findNearestPosition);
        if (Objects.equals(getItem(findNearestPosition), cycleItem)) {
            return true;
        }
        this.mListener.onItemSelected(null, null, findNearestPosition, 0L);
        return false;
    }

    /* loaded from: classes.dex */
    public static class CycleItem implements Comparable<CycleItem> {
        public long end;
        public CharSequence label;
        public long start;

        public CycleItem(Context context, long j, long j2) {
            this.label = Utils.formatDateRange(context, j, j2);
            this.start = j;
            this.end = j2;
        }

        public String toString() {
            return this.label.toString();
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof CycleItem)) {
                return false;
            }
            CycleItem cycleItem = (CycleItem) obj;
            return this.start == cycleItem.start && this.end == cycleItem.end;
        }

        public int compareTo(CycleItem cycleItem) {
            return Long.compare(this.start, cycleItem.start);
        }
    }
}
