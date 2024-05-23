package com.ph41626.pma101_recipesharingapplication.Services;

import com.ph41626.pma101_recipesharingapplication.Model.Ingredient;
import com.ph41626.pma101_recipesharingapplication.Model.Instruction;

public interface OnItemIngredientListener {
    void removeItemIngredient(Ingredient ingredient, int position);
}
