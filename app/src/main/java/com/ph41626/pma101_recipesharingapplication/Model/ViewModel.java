package com.ph41626.pma101_recipesharingapplication.Model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewModel extends androidx.lifecycle.ViewModel {
    private MutableLiveData<ArrayList<Recipe>> liveDataRecipes = new MutableLiveData<>();
    private MutableLiveData<User> currentUser = new MutableLiveData<>();
    private MutableLiveData<ArrayList<User>> users = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Recipe>> allRecipeByChef = new MutableLiveData<>();
    private MutableLiveData<Recipe> recipe = new MutableLiveData<>();
    private MutableLiveData<ArrayList<RecipeCollection>> recipeCollections = new MutableLiveData<>();
    private MutableLiveData<HashMap<String,ArrayList<Recipe>>> recipeForRecipeCollection = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Recipe>> manageRecipe = new MutableLiveData<>();
    private MutableLiveData<HashMap<String,User>> manageUser = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Notification>> notifications = new MutableLiveData<>();


    public void changeDateRecipes(ArrayList<Recipe> recipes) {this.liveDataRecipes.setValue(recipes);}
    public void changeCurrentUser(User user) {currentUser.setValue(user);}
    public void changeUsers(ArrayList<User> users) {this.users.setValue(users);}
    public void changeAllRecipeByChef(ArrayList<Recipe> recipes) {this.allRecipeByChef.setValue(recipes);}
    public void addRecipe(Recipe recipe) {this.recipe.setValue(recipe);}
    public void changeRecipeCollectionForUser(ArrayList<RecipeCollection> recipeCollections) {this.recipeCollections.setValue(recipeCollections);}
    public void changeRecipeForRecipeCollection(HashMap<String,ArrayList<Recipe>> recipes) {this.recipeForRecipeCollection.setValue(recipes);}
    public void changeDataManageRecipe(ArrayList<Recipe> recipes) {this.manageRecipe.setValue(recipes);}
    public void changeDataManageUser(HashMap<String,User> users) {this.manageUser.setValue(users);}
    public void changeNotificationForUser(ArrayList<Notification> notifications) {this.notifications.setValue(notifications);}


    public LiveData<ArrayList<Recipe>>getChangeDataRecipes() {return this.liveDataRecipes;}
    public LiveData<User> getChangeDateCurrentUser() {return this.currentUser;}
    public LiveData<ArrayList<User>> getChangeDateUsers() {return this.users;}
    public LiveData<ArrayList<Recipe>> getAllRecipeByChef() {return this.allRecipeByChef;}
    public LiveData<Recipe> getRecipe() {return this.recipe;}
    public LiveData<ArrayList<RecipeCollection>> getRecipeCollectionForUser() {return this.recipeCollections;}
    public LiveData<HashMap<String,ArrayList<Recipe>>> getRecipeForRecipeCollection() {return this.recipeForRecipeCollection;}
    public LiveData<ArrayList<Recipe>> getManageRecipe() {return this.manageRecipe;}
    public LiveData<HashMap<String, User>> getManageUser() {return this.manageUser;}
    public LiveData<ArrayList<Notification>> getChangeNotification() {return this.notifications;}
}
