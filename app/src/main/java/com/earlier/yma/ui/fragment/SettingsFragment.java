package com.earlier.yma.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.Log;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.earlier.yma.BaseApplication;
import com.earlier.yma.R;
import com.earlier.yma.data.model.preference.Time;
import com.earlier.yma.ui.preference.TimePickerPreference;
import com.earlier.yma.util.IabConfig;
import com.earlier.yma.util.iab.IabHelper;
import com.earlier.yma.util.iab.IabResult;
import com.earlier.yma.util.iab.Inventory;
import com.earlier.yma.util.iab.Purchase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by namhyun on 2016-03-12.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    private final String TAG = getClass().getSimpleName();
    private IabHelper mIabHelper;
    private IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set title
        getActivity().setTitle(R.string.action_settings);

        // Add resource
        addPreferencesFromResource(R.xml.pref_settings);

        // Bind Preference
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_default_menu_key)));

        // Set click listener
        final Preference removeAdsPreference = findPreference(getString(R.string.pref_remove_ads_key));
        removeAdsPreference.setOnPreferenceClickListener(this);
        setPriceToPreference(removeAdsPreference, "Unknown");
        Preference resetPreference = findPreference(getString(R.string.pref_reset_key));
        resetPreference.setOnPreferenceClickListener(this);

        // Set time picker listener
        TimePickerPreference breakfastPreference =
                (TimePickerPreference) findPreference(getString(R.string.pref_time_breakfast_key));
        breakfastPreference.setOnTimeSetListener(new TimePickerPreference.OnTimeSetListener() {
            @Override
            public boolean onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Time lunchTime = getTimeFromPreference(R.string.pref_time_lunch_key);
                Time currentTime = new Time(hourOfDay, minute);
                if (currentTime.compareTo(lunchTime) < 0)
                    return true;
                Toast.makeText(getActivity(),
                        getString(R.string.toast_cannot_save_time), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        TimePickerPreference lunchPreference =
                (TimePickerPreference) findPreference(getString(R.string.pref_time_lunch_key));
        lunchPreference.setOnTimeSetListener(new TimePickerPreference.OnTimeSetListener() {
            @Override
            public boolean onTimeSet(TimePicker view, int hourOfDay, int minute) {
                boolean enableBreakfastChanged =
                        getBooleanFromPreference(R.string.pref_breakfast_enable_key, false);
                Time breakfastTime = getTimeFromPreference(R.string.pref_time_breakfast_key);
                Time dinnerTime = getTimeFromPreference(R.string.pref_time_dinner_key);
                Time currentTime = new Time(hourOfDay, minute);

                boolean validation;
                if (enableBreakfastChanged) {
                    validation = (currentTime.compareTo(breakfastTime) > 0
                            && currentTime.compareTo(dinnerTime) < 0);
                } else {
                    validation = currentTime.compareTo(dinnerTime) < 0;
                }

                if (!validation) {
                    Toast.makeText(getActivity(), getString(R.string.toast_cannot_save_time),
                            Toast.LENGTH_SHORT).show();
                }
                return validation;
            }
        });

        TimePickerPreference dinnerPreference =
                (TimePickerPreference) findPreference(getString(R.string.pref_time_dinner_key));
        dinnerPreference.setOnTimeSetListener(new TimePickerPreference.OnTimeSetListener() {
            @Override
            public boolean onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Time lunchTime = getTimeFromPreference(R.string.pref_time_lunch_key);
                Time currentTime = new Time(hourOfDay, minute);
                if (currentTime.compareTo(lunchTime) > 0)
                    return true;
                Toast.makeText(getActivity(),
                        getString(R.string.toast_cannot_save_time), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Initialize iab
        final IabHelper.QueryInventoryFinishedListener queryInventoryFinishedListener
                = new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                boolean hasPurchaseRemoveAd = inv.hasPurchase(IabConfig.SKU_REMOVE_AD);
                if (!hasPurchaseRemoveAd) {
                    removeAdsPreference.setSummary(getString(R.string.pref_remove_ads_not_summary));
                    removeAdsPreference.setEnabled(true);
                } else {
                    removeAdsPreference.setSummary(getString(R.string.pref_remove_ads_already_summary));
                    removeAdsPreference.setEnabled(false);
                }
                String price = inv.getSkuDetails(IabConfig.SKU_REMOVE_AD).getPrice();
                setPriceToPreference(removeAdsPreference, price);
            }
        };

        final IabHelper.OnIabSetupFinishedListener setupFinishedListener
                = new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (result.isFailure()) {
                    Log.e(TAG, "Problem setting up In-app Billing: " + result);
                    return;
                }
                List additionalSkuList = new ArrayList();
                additionalSkuList.add(IabConfig.SKU_REMOVE_AD);
                mIabHelper.queryInventoryAsync(true, additionalSkuList, queryInventoryFinishedListener);
            }
        };

        mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
            @Override
            public void onIabPurchaseFinished(IabResult result, Purchase info) {
                if (result.isFailure()) {
                    Log.d(TAG, "Error purchasing: " + result);
                    return;
                }
                List additionalSkuList = new ArrayList();
                additionalSkuList.add(IabConfig.SKU_REMOVE_AD);
                mIabHelper.queryInventoryAsync(true, additionalSkuList, queryInventoryFinishedListener);
            }
        };

        mIabHelper = new IabHelper(getActivity(), ((BaseApplication) getActivity().getApplication()).base64publicKey);
        mIabHelper.startSetup(setupFinishedListener);
    }

    private void setPriceToPreference(Preference preference, String price) {
        preference.setTitle(String.format(getString(R.string.pref_remove_ads), price));
    }

    private boolean getBooleanFromPreference(@StringRes int preferenceKey, boolean defaultValue) {
        return PreferenceManager
                .getDefaultSharedPreferences(getActivity()).getBoolean(getString(preferenceKey), defaultValue);
    }

    private Time getTimeFromPreference(@StringRes int preferenceKey) {
        String value = PreferenceManager
                .getDefaultSharedPreferences(getActivity()).getString(getString(preferenceKey), null);
        return new Gson().fromJson(value, Time.class);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals(getString(R.string.pref_reset_key))) {
            new MaterialDialog.Builder(getActivity())
                    .title(R.string.pref_reset)
                    .content(R.string.dialog_reset_content)
                    .positiveText(android.R.string.ok)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().clear().apply();
                            getActivity().finish();
                            dialog.dismiss();
                        }
                    })
                    .negativeText(android.R.string.cancel)
                    .show();
        } else if (preference.getKey().equals(getString(R.string.pref_remove_ads_key))) {
            mIabHelper.launchPurchaseFlow(getActivity(), IabConfig.SKU_REMOVE_AD, 10001,
                    mPurchaseFinishedListener, "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
        }
        return true;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mIabHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mIabHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }
}
