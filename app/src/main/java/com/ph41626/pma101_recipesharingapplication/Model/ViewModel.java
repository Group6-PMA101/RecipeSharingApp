package com.ph41626.pma101_recipesharingapplication.Model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

public class ViewModel extends androidx.lifecycle.ViewModel {
    private MutableLiveData<ArrayList<Recipe>> liveDataRecipes = new MutableLiveData<>();
    private MutableLiveData<User> currentUser = new MutableLiveData<>();
    private MutableLiveData<ArrayList<User>> users = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Recipe>> allRecipeByChef = new MutableLiveData<>();


    public void changeDateRecipes(ArrayList<Recipe> recipes) {this.liveDataRecipes.setValue(recipes);}
    public void changeCurrentUser(User user) {currentUser.setValue(user);}
    public void changeUsers(ArrayList<User> users) {this.users.setValue(users);}
    public void changeAllRecipeByChef(ArrayList<Recipe> recipes) {this.allRecipeByChef.setValue(recipes);}

    public LiveData<ArrayList<Recipe>>getChangeDataRecipes() {return this.liveDataRecipes;}
    public LiveData<User> getChangeDateCurrentUser() {return this.currentUser;}
    public LiveData<ArrayList<User>> getChangeDateUsers() {return this.users;}
    public LiveData<ArrayList<Recipe>> getAllRecipeByChef() {return this.allRecipeByChef;}
}
