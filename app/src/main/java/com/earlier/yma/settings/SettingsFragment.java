package com.earlier.yma.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import com.afollestad.materialdialogs.MaterialDialog;
import com.earlier.yma.R;
import com.earlier.yma.data.Meal;
import com.earlier.yma.searchschool.SearchSchoolActivity;
import com.earlier.yma.utilities.RealmString;
import io.realm.Realm;

public class SettingsFragment extends PreferenceFragmentCompat
    implements Preference.OnPreferenceClickListener,
    SharedPreferences.OnSharedPreferenceChangeListener {

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    addPreferencesFromResource(R.xml.pref_settings);

    PreferenceScreen preferenceScreen = getPreferenceScreen();
    SharedPreferences sharedPreferences = preferenceScreen.getSharedPreferences();
    int count = preferenceScreen.getPreferenceCount();

    for (int i = 0; i < count; i++) {
      Preference p = preferenceScreen.getPreference(i);
      p.setOnPreferenceClickListener(this);

      if (!(p instanceof CheckBoxPreference)) {
        String value = sharedPreferences.getString(p.getKey(), "");
        setPreferenceSummary(p, value);
      }
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getPreferenceScreen().getSharedPreferences()
        .registerOnSharedPreferenceChangeListener(this);
  }

  @Override
  public void onStop() {
    super.onStop();
    getPreferenceScreen().getSharedPreferences()
        .unregisterOnSharedPreferenceChangeListener(this);
  }

  @Override
  public boolean onPreferenceClick(Preference preference) {
    if (preference.getKey().equals(getString(R.string.pref_reset_key))) {
      new MaterialDialog.Builder(getActivity())
          .title(R.string.pref_reset)
          .content(R.string.dialog_reset_content)
          .positiveText(android.R.string.ok)
          .onPositive((dialog, which) -> {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(realm1 -> {
              realm1.where(Meal.class).findAll().deleteAllFromRealm();
              realm1.where(RealmString.class).findAll().deleteAllFromRealm();
            });
            realm.close();

            dialog.dismiss();

            Toast.makeText(getContext(), R.string.toast_reset, Toast.LENGTH_SHORT).show();
            ;
          })
          .negativeText(android.R.string.cancel)
          .show();
    } else if (preference.getKey().equals(getString(R.string.pref_school_setting_key))) {
      Intent intent = new Intent(getContext(), SearchSchoolActivity.class);
      startActivity(intent);
      getActivity().finish();
    }
    return true;
  }

  private void setPreferenceSummary(Preference preference, String value) {
    if (preference instanceof ListPreference) {
      ListPreference listPreference = (ListPreference) preference;
      int prefIndex = listPreference.findIndexOfValue(value);
      if (prefIndex >= 0) {
        preference.setSummary(listPreference.getEntries()[prefIndex]);
      }
    } else {
      preference.setSummary(value);
    }
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    Preference preference = findPreference(key);
    if (null != preference) {
      if (!(preference instanceof CheckBoxPreference)) {
        setPreferenceSummary(preference, sharedPreferences.getString(key, ""));
      }
    }
  }
}
