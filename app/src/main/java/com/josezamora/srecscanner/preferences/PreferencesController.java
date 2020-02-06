package com.josezamora.srecscanner.preferences;

import android.app.Activity;
import android.content.SharedPreferences;

import com.josezamora.srecscanner.AppGlobals;

import static android.content.Context.MODE_PRIVATE;

/**
 * The type Preferences controller.
 */
public class PreferencesController {

    private SharedPreferences preferences;

    /**
     * Instantiates a new Preferences controller.
     *
     * @param context the context
     */
    public PreferencesController(Activity context) {
        this.preferences = context.getSharedPreferences(AppGlobals.PREFERENCES_NAME, MODE_PRIVATE);
    }

    /**
     * Connected to s rec.
     *
     * @param ip   the ip
     * @param port the port
     */
    public void connectedToSRec(String ip, String port) {
        this.preferences
                .edit()
                .putString("IP", ip)
                .putString("PORT", port)
                .apply();
    }

    /**
     * Clear s rec connection.
     */
    public void clearSRecConnection() {
        this.preferences.edit()
                .remove("IP")
                .remove("PORT")
                .apply();
    }

    /**
     * Get connection details s rec string [ ].
     *
     * @return the string [ ]
     */
    public String[] getConnectionDetailsSRec() {
        String[] ip_port = new String[2];

        ip_port[0] = this.preferences.getString("IP", null);
        ip_port[1] = this.preferences.getString("PORT", null);

        return ip_port;
    }
}
