package com.ph41626.pma101_recipesharingapplication.Services;

import com.ph41626.pma101_recipesharingapplication.Model.Ingredient;
import com.ph41626.pma101_recipesharingapplication.Model.Instruction;
import com.ph41626.pma101_recipesharingapplication.Model.Media;
import com.ph41626.pma101_recipesharingapplication.Model.User;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Services {
    private static final List<String> VIDEO_EXTENSIONS = Arrays.asList(
            ".mp4", ".avi", ".mkv", ".flv", ".webm", ".ogg", ".3gp", ".3g2"
    );
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
    public static boolean isVideo(String url) {
        if (url == null) {
            return false;
        }
        String urlLowerCase = url.toLowerCase();
        for (String extension : VIDEO_EXTENSIONS) {
            if (urlLowerCase.contains(extension)) {
                return true;
            }
        }
        return false;
    }
    public static String getMimeType(String url) throws IOException {
        URLConnection connection = new URL(url).openConnection();
        connection.connect();
        String mimeType = connection.getContentType();
        return mimeType;
    }
}
