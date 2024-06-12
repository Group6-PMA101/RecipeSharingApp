package com.ph41626.pma101_recipesharingapplication.Services;

import android.content.Context;

import com.ph41626.pma101_recipesharingapplication.Model.Recipe;

public interface MainActivityEventListener {
    void onSavedRecipe(Context context, Recipe recipe);
}
