package com.earlier.yma.settings;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.earlier.yma.R;

public class InfoFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_info);

        PackageManager packageManager = getActivity().getPackageManager();
        PackageInfo info = null;
        try {
            info = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        findPreference(getString(R.string.pref_application_version_key))
                .setSummary(info != null ? info.versionName : null);
    }

}
