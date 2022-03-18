package com.android.settings.slices;

import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceScreen;
import androidx.slice.Slice;
import androidx.slice.widget.SliceLiveData;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
/* loaded from: classes.dex */
public class SlicePreferenceController extends BasePreferenceController implements LifecycleObserver, OnStart, OnStop, Observer<Slice> {
    private static final String TAG = "SlicePreferenceController";
    LiveData<Slice> mLiveData;
    SlicePreference mSlicePreference;
    private Uri mUri;

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public SlicePreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mSlicePreference = (SlicePreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mUri != null ? 0 : 3;
    }

    public void setSliceUri(final Uri uri) {
        this.mUri = uri;
        LiveData<Slice> fromUri = SliceLiveData.fromUri(this.mContext, uri, new SliceLiveData.OnErrorListener() { // from class: com.android.settings.slices.SlicePreferenceController$$ExternalSyntheticLambda0
            @Override // androidx.slice.widget.SliceLiveData.OnErrorListener
            public final void onSliceError(int i, Throwable th) {
                SlicePreferenceController.lambda$setSliceUri$0(uri, i, th);
            }
        });
        this.mLiveData = fromUri;
        fromUri.removeObserver(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$setSliceUri$0(Uri uri, int i, Throwable th) {
        Log.w(TAG, "Slice may be null. uri = " + uri + ", error = " + i);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        LiveData<Slice> liveData = this.mLiveData;
        if (liveData != null) {
            liveData.observeForever(this);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        LiveData<Slice> liveData = this.mLiveData;
        if (liveData != null) {
            liveData.removeObserver(this);
        }
    }

    public void onChanged(Slice slice) {
        this.mSlicePreference.onSliceUpdated(slice);
    }
}
