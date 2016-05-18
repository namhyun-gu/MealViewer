package com.earlier.yma.ui.fragment;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.earlier.yma.R;

/**
 * Created by namhyun on 2016-03-12.
 */
public class InfoFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.action_information);
        addPreferencesFromResource(R.xml.pref_info);
        showAppVersion(getString(R.string.pref_application_version_key));
    }

    private void showAppVersion(String preferenceKey) {
        PackageManager manager = getActivity().getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        findPreference(preferenceKey).setSummary(info != null ? info.versionName : null);
    }
}
