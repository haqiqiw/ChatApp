package com.neogeekscamp.workshop2.manager;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by M. Asrof Bayhaqqi on 11/26/2016.
 */

public class AppPrefManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    private int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "Workshop2";
    private static final String IS_LOGGED_IN = "IsLoggedIn";

    public AppPrefManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(IS_LOGGED_IN, isLoggedIn);
        editor.commit();
    }

    public boolean getIsLoggedIn() {
        return pref.getBoolean(IS_LOGGED_IN, false);
    }

    public void setUser(String username) {
        editor.putString("username", username);
        editor.apply();
    }

    public HashMap<String, String> getUser() {
        HashMap<String, String> dataUser = new HashMap<String, String>();
        dataUser.put("username", pref.getString("username", ""));
        return dataUser;
    }

    public void logout() {
        editor.remove(IS_LOGGED_IN);
        editor.remove("username");
        editor.apply();
    }


}
