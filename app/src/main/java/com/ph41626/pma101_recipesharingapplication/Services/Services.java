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
import com.ph41626.pma101_recipesharingapplication.Model.User;
import com.ph41626.pma101_recipesharingapplication.R;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.UUID;

public class Services {
    public static String RandomID() {return UUID.randomUUID().toString();}
    public static Ingredient CreateNewIngredient() {
        return new Ingredient(RandomID().toString(),"","",0);
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
            } else if (item instanceof User) {
                User user = (User) item;
                if (user.getId().equals(id)) {
                    return item;
                }
            }
        }
        return null;
    }
    public static boolean isVideoMimeType(String mimeType) {
        return mimeType.startsWith("video/");
    }
    public static String getMimeType(String url) throws IOException {
        URLConnection connection = new URL(url).openConnection();
        connection.connect();
        String mimeType = connection.getContentType();
        return mimeType;
    }
}
