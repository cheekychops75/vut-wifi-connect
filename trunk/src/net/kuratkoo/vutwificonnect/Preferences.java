package net.kuratkoo.vutwificonnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Preferences extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    private static final String TAG = "VUTWifiConnect|MainActivity";
    private EditTextPreference username;
    private EditTextPreference password;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.preferences);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        username = (EditTextPreference) getPreferenceScreen().findPreference("username");
        password = (EditTextPreference) getPreferenceScreen().findPreference("password");

        username.setSummary(username.getText());
        password.setSummary(password.getText());
    }

    public static String getString(Context context, String name, String defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(name, defaultValue);
    }

    public static void setString(Context context, String name, String value) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(name, value);
        editor.commit();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("username")) {
            username.setSummary(sharedPreferences.getString(key, ""));
        }
        if (key.equals("password")) {
            password.setSummary(sharedPreferences.getString(key, ""));
        }
    }
}
