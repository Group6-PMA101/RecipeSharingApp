package com.ph41626.pma101_recipesharingapplication.Services;

import android.content.Context;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.ph41626.pma101_recipesharingapplication.Model.Ingredient;
import com.ph41626.pma101_recipesharingapplication.Model.Instruction;
import com.ph41626.pma101_recipesharingapplication.Model.Media;
import com.ph41626.pma101_recipesharingapplication.R;

import java.util.ArrayList;
import java.util.UUID;

public class Services {
    public static String RandomID() {return UUID.randomUUID().toString();}
    public static Ingredient CreateNewIngredient() {
        return new Ingredient(RandomID().toString(),"",0);
    }
    public static Instruction CreateNewInstruction() {
        return new Instruction(RandomID().toString(),"","",new ArrayList<>(),0);
    }
    public static <T> T findObjectById(ArrayList<T> list, String id) {
        for (T item : list) {
            if (item instanceof Media) {
                Media media = (Media) item;
                if (media.getId().equals(id)) {
                    return item;
                }
            } else if (item instanceof Ingredient) {
                Ingredient ingredient = (Ingredient) item;
                if (ingredient.getId().equals(id)) {
                    return item;
                }
            } else if (item instanceof Instruction) {
                Instruction instruction = (Instruction) item;
                if (instruction.getId().equals(id)) {
                    return item;
                }
            }
        }
        return null;
    }
}
