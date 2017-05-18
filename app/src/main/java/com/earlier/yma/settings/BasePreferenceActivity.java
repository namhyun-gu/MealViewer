package com.earlier.yma.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.Toolbar;

import com.earlier.yma.R;

public class BasePreferenceActivity extends AppCompatActivity {

    public static String BUNDLE_TYPE = "preference_type";

    public static int PREFERENCE_TYPE_NONE = 0x0000;
    public static int TYPE_SETTINGS = 0x00001;
    public static int TYPE_INFORMATION = 0x0002;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_preference);

        // Initial Toolbar
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            PreferenceFragmentCompat fragment = null;
            int preferenceType = getIntent().getIntExtra(BUNDLE_TYPE, PREFERENCE_TYPE_NONE);
            if (preferenceType == TYPE_INFORMATION) {
                setTitle(getString(R.string.action_information));
                fragment = new InfoFragment();
            } else if (preferenceType == TYPE_SETTINGS) {
                setTitle(getString(R.string.action_settings));
                fragment = new SettingsFragment();
            } else {
                throw new UnsupportedOperationException("Not receive type bundle");
            }
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        android.app.FragmentManager fragmentManager = getFragmentManager();
        android.app.Fragment fragment = fragmentManager.findFragmentById(R.id.container);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
