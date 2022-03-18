package com.android.settings.development.tare;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.window.R;
/* loaded from: classes.dex */
public class AlarmManagerFragment extends Fragment {
    @Override // android.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.tare_policy_fragment, (ViewGroup) null);
        ExpandableListView expandableListView = (ExpandableListView) inflate.findViewById(R.id.factor_list);
        final SavedTabsListAdapter savedTabsListAdapter = new SavedTabsListAdapter();
        expandableListView.setGroupIndicator(null);
        expandableListView.setAdapter(savedTabsListAdapter);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() { // from class: com.android.settings.development.tare.AlarmManagerFragment.1
            @Override // android.widget.ExpandableListView.OnChildClickListener
            public boolean onChildClick(ExpandableListView expandableListView2, View view, int i, int i2, long j) {
                Toast.makeText(AlarmManagerFragment.this.getActivity(), (String) savedTabsListAdapter.getChild(i, i2), 0).show();
                return true;
            }
        });
        return inflate;
    }

    /* loaded from: classes.dex */
    public class SavedTabsListAdapter extends BaseExpandableListAdapter {
        private String[][] mChildren;
        private String[] mGroups;
        private final LayoutInflater mInflater;
        private Resources mResources;

        @Override // android.widget.ExpandableListAdapter
        public long getChildId(int i, int i2) {
            return i2;
        }

        @Override // android.widget.ExpandableListAdapter
        public long getGroupId(int i) {
            return i;
        }

        @Override // android.widget.ExpandableListAdapter
        public boolean hasStableIds() {
            return true;
        }

        @Override // android.widget.ExpandableListAdapter
        public boolean isChildSelectable(int i, int i2) {
            return true;
        }

        public SavedTabsListAdapter() {
            Resources resources = AlarmManagerFragment.this.getActivity().getResources();
            this.mResources = resources;
            this.mGroups = new String[]{resources.getString(R.string.tare_max_circulation), this.mResources.getString(R.string.tare_max_satiated_balance), this.mResources.getString(R.string.tare_min_satiated_balance), this.mResources.getString(R.string.tare_modifiers), this.mResources.getString(R.string.tare_actions), this.mResources.getString(R.string.tare_rewards)};
            this.mChildren = new String[][]{new String[0], new String[0], this.mResources.getStringArray(R.array.tare_min_satiated_balance_subfactors), this.mResources.getStringArray(R.array.tare_modifiers_subfactors), this.mResources.getStringArray(R.array.tare_alarm_manager_actions), this.mResources.getStringArray(R.array.tare_rewards_subfactors)};
            this.mInflater = LayoutInflater.from(AlarmManagerFragment.this.getActivity());
        }

        @Override // android.widget.ExpandableListAdapter
        public int getGroupCount() {
            return this.mGroups.length;
        }

        @Override // android.widget.ExpandableListAdapter
        public int getChildrenCount(int i) {
            return this.mChildren[i].length;
        }

        @Override // android.widget.ExpandableListAdapter
        public Object getGroup(int i) {
            return this.mGroups[i];
        }

        @Override // android.widget.ExpandableListAdapter
        public Object getChild(int i, int i2) {
            return this.mChildren[i][i2];
        }

        @Override // android.widget.ExpandableListAdapter
        public View getGroupView(int i, boolean z, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = this.mInflater.inflate(17367043, viewGroup, false);
            }
            ((TextView) view.findViewById(16908308)).setText(getGroup(i).toString());
            return view;
        }

        @Override // android.widget.ExpandableListAdapter
        public View getChildView(int i, int i2, boolean z, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = this.mInflater.inflate(R.layout.tare_child_item, (ViewGroup) null);
            }
            ((TextView) view.findViewById(R.id.factor)).setText(getChild(i, i2).toString());
            ((TextView) view.findViewById(R.id.factor_number)).setText("500");
            return view;
        }
    }
}
