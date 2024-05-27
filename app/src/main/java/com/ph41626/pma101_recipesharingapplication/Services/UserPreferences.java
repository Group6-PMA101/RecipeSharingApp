package com.ph41626.pma101_recipesharingapplication.Services;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.ph41626.pma101_recipesharingapplication.Model.User;

public class UserPreferences {
    private static final String USER_PREFS = "USER";
    private static final String USER_KEY = "USER";

    public static void SaveUser(Context context, User user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_PREFS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString(USER_KEY,json);
        editor.apply();
    }

    public static User GetUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_PREFS,Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(USER_KEY,null);
        return gson.fromJson(json,User.class);
    }

    public static void ClearUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_PREFS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(USER_KEY);
        editor.apply();
    }
}
