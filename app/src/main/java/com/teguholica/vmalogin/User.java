package com.teguholica.vmalogin;

import android.content.Context;
import android.content.SharedPreferences;

import com.teguholica.vmalogin.utils.Hasher;

public class User {

    private static String USER_PREF = "User";
    private static String USERNAME_PREF = "username";
    private static String PASSWORD_PREF = "password";

    private Context context;

    private String username;
    private String password;

    private SharedPreferences userPref;

    public User(Context context) {
        this.context = context;
        userPref = context.getSharedPreferences(USER_PREF, 0);
        this.username = userPref.getString(USERNAME_PREF, "");
        this.password = userPref.getString(PASSWORD_PREF, "");
    }

    public User(Context context, String username, String password) {
        userPref = context.getSharedPreferences(USER_PREF, 0);
        SharedPreferences.Editor userPrefEdit = userPref.edit();
        userPrefEdit.putString(USERNAME_PREF, username);
        userPrefEdit.putString(PASSWORD_PREF, password);
        userPrefEdit.apply();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEncyrptPassword(String capId, String capChallenge) {
        return Hasher.hashMD5(capId + password + capChallenge);
    }

    public Boolean isEmpty() {
        if (username.isEmpty() && password.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public void clear() {
        userPref = context.getSharedPreferences(USER_PREF, 0);
        SharedPreferences.Editor userPrefEdit = userPref.edit();
        userPrefEdit.clear();
        userPrefEdit.apply();
    }
}
