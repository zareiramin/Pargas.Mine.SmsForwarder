package ir.pargasit.smsforwarder.ui.setting;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import ir.pargasit.smsforwarder.R;

public class SettingFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}