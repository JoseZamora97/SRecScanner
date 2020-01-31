package com.josezamora.tcscanner.Preferences;

import android.app.Activity;
import android.content.SharedPreferences;

import com.josezamora.tcscanner.AppGlobals;

import static android.content.Context.MODE_PRIVATE;

public class PreferencesController {

    private Activity context;
    private SharedPreferences preferences;

    public PreferencesController(Activity context) {
        this.context = context;
        this.preferences = this.context.getSharedPreferences(AppGlobals.PREFERENCES_NAME, MODE_PRIVATE);
    }

    public void connectedToSRec(String ip, String port) {
        this.preferences
                .edit()
                .putString("IP", ip)
                .putString("PORT", port)
                .apply();
    }

    public void clearSRecConnection() {
        this.preferences.edit()
                .remove("IP")
                .remove("PORT")
                .apply();
    }

    public String[] getConnectionDetailsSRec() {
        String[] ip_port = new String[2];

        ip_port[0] = this.preferences.getString("IP", null);
        ip_port[1] = this.preferences.getString("PORT", null);

        return ip_port;
    }
}
