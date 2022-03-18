package com.android.settings.support.actionbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.android.settingslib.HelpUtils;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnCreateOptionsMenu;
/* loaded from: classes.dex */
public class HelpMenuController implements LifecycleObserver, OnCreateOptionsMenu {
    private final Fragment mHost;

    @Override // com.android.settingslib.core.lifecycle.events.OnCreateOptionsMenu
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        int i;
        Bundle arguments = this.mHost.getArguments();
        if (arguments == null || !arguments.containsKey("help_uri_resource")) {
            Fragment fragment = this.mHost;
            i = fragment instanceof HelpResourceProvider ? ((HelpResourceProvider) fragment).getHelpResource() : 0;
        } else {
            i = arguments.getInt("help_uri_resource");
        }
        String str = null;
        if (i != 0) {
            str = this.mHost.getContext().getString(i);
        }
        FragmentActivity activity = this.mHost.getActivity();
        if (str != null && activity != null) {
            HelpUtils.prepareHelpMenuItem(activity, menu, str, this.mHost.getClass().getName());
        }
    }
}
