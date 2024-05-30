package com.ph41626.pma101_recipesharingapplication.Services;

import com.ph41626.pma101_recipesharingapplication.Model.User;

public interface RecipeDetailEventListener {
    void onFollowEvent(String userId,User user);
}
