package com.ursinepaw.publictransport;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
 
public class Preferences extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	
	public static final String KEY_UPDATE_INVERVAL = "update_interval";		
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences); 

            ListPreference updateIntervalPreference = (ListPreference)getPreferenceScreen().findPreference(KEY_UPDATE_INVERVAL);
            updateIntervalPreference.setSummary(updateIntervalPreference.getEntry());
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);    
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Preference preference = findPreference(key);

	    if (preference instanceof ListPreference) {
	        ListPreference listPreference = (ListPreference) preference;
	        preference.setSummary(listPreference.getEntry());
	    }
	}
}