package com.android.settings.wifi.calling;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.window.R;
import com.android.internal.annotations.VisibleForTesting;
import java.util.List;
/* loaded from: classes.dex */
public class DisclaimerItemListAdapter extends RecyclerView.Adapter<DisclaimerItemViewHolder> {
    private List<DisclaimerItem> mDisclaimerItemList;

    public DisclaimerItemListAdapter(List<DisclaimerItem> list) {
        this.mDisclaimerItemList = list;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public DisclaimerItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new DisclaimerItemViewHolder(((LayoutInflater) viewGroup.getContext().getSystemService("layout_inflater")).inflate(R.layout.wfc_simple_disclaimer_item, (ViewGroup) null, false));
    }

    public void onBindViewHolder(DisclaimerItemViewHolder disclaimerItemViewHolder, int i) {
        disclaimerItemViewHolder.titleView.setText(this.mDisclaimerItemList.get(i).getTitleId());
        disclaimerItemViewHolder.descriptionView.setText(this.mDisclaimerItemList.get(i).getMessageId());
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.mDisclaimerItemList.size();
    }

    /* loaded from: classes.dex */
    public static class DisclaimerItemViewHolder extends RecyclerView.ViewHolder {
        @VisibleForTesting
        static final int ID_DISCLAIMER_ITEM_DESCRIPTION = 2131558868;
        @VisibleForTesting
        static final int ID_DISCLAIMER_ITEM_TITLE = 2131558870;
        public final TextView descriptionView;
        public final TextView titleView;

        public DisclaimerItemViewHolder(View view) {
            super(view);
            this.titleView = (TextView) view.findViewById(R.id.disclaimer_title);
            this.descriptionView = (TextView) view.findViewById(R.id.disclaimer_desc);
        }
    }
}
