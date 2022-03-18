package com.android.settings.dream;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.window.R;
import com.android.settings.dream.DreamAdapter;
import com.android.settingslib.Utils;
import java.util.List;
/* loaded from: classes.dex */
public class DreamAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<IDreamItem> mItemList;
    private int mLastSelectedPos = -1;

    /* loaded from: classes.dex */
    private class DreamViewHolder extends RecyclerView.ViewHolder {
        private final Context mContext;
        private final Button mCustomizeButton;
        private final ImageView mIconView;
        private final ImageView mPreviewPlaceholderView;
        private final ImageView mPreviewView;
        private final TextView mTitleView;

        DreamViewHolder(View view, Context context) {
            super(view);
            this.mContext = context;
            this.mPreviewView = (ImageView) view.findViewById(R.id.preview);
            this.mPreviewPlaceholderView = (ImageView) view.findViewById(R.id.preview_placeholder);
            this.mIconView = (ImageView) view.findViewById(R.id.icon);
            this.mTitleView = (TextView) view.findViewById(R.id.title_text);
            this.mCustomizeButton = (Button) view.findViewById(R.id.customize_button);
        }

        public void bindView(final IDreamItem iDreamItem, final int i) {
            Drawable drawable;
            this.mTitleView.setText(iDreamItem.getTitle());
            Drawable previewImage = iDreamItem.getPreviewImage();
            int i2 = 8;
            if (previewImage != null) {
                this.mPreviewView.setImageDrawable(previewImage);
                this.mPreviewView.setClipToOutline(true);
                this.mPreviewPlaceholderView.setVisibility(8);
            }
            if (iDreamItem.isActive()) {
                drawable = this.mContext.getDrawable(R.drawable.ic_dream_check_circle);
            } else {
                drawable = iDreamItem.getIcon();
            }
            if (drawable instanceof VectorDrawable) {
                drawable.setTint(Utils.getColorAttrDefaultColor(this.mContext, 17956901));
            }
            this.mIconView.setImageDrawable(drawable);
            if (iDreamItem.isActive()) {
                DreamAdapter.this.mLastSelectedPos = i;
                this.itemView.setSelected(true);
            } else {
                this.itemView.setSelected(false);
            }
            this.mCustomizeButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.dream.DreamAdapter$DreamViewHolder$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    IDreamItem.this.onCustomizeClicked();
                }
            });
            Button button = this.mCustomizeButton;
            if (iDreamItem.allowCustomization()) {
                i2 = 0;
            }
            button.setVisibility(i2);
            this.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.dream.DreamAdapter$DreamViewHolder$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    DreamAdapter.DreamViewHolder.this.lambda$bindView$1(iDreamItem, i, view);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$bindView$1(IDreamItem iDreamItem, int i, View view) {
            iDreamItem.onItemClicked();
            if (DreamAdapter.this.mLastSelectedPos > -1 && DreamAdapter.this.mLastSelectedPos != i) {
                DreamAdapter dreamAdapter = DreamAdapter.this;
                dreamAdapter.notifyItemChanged(dreamAdapter.mLastSelectedPos);
            }
            DreamAdapter.this.notifyItemChanged(i);
        }
    }

    public DreamAdapter(List<IDreamItem> list) {
        this.mItemList = list;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new DreamViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dream_preference_layout, viewGroup, false), viewGroup.getContext());
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        ((DreamViewHolder) viewHolder).bindView(this.mItemList.get(i), i);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.mItemList.size();
    }
}
