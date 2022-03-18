package com.android.settings.slices;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.IntentFilter;
import android.widget.Toast;
import androidx.window.R;
/* loaded from: classes.dex */
public interface Sliceable {
    default void copy() {
    }

    default Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return null;
    }

    default IntentFilter getIntentFilter() {
        return null;
    }

    default int getSliceHighlightMenuRes() {
        return 0;
    }

    default boolean hasAsyncUpdate() {
        return false;
    }

    default boolean isCopyableSlice() {
        return false;
    }

    default boolean isPublicSlice() {
        return false;
    }

    default boolean isSliceable() {
        return false;
    }

    default boolean useDynamicSliceSummary() {
        return false;
    }

    static void setCopyContent(Context context, CharSequence charSequence, CharSequence charSequence2) {
        ((ClipboardManager) context.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("text", charSequence));
        Toast.makeText(context, context.getString(R.string.copyable_slice_toast, charSequence2), 0).show();
    }
}
