package com.ph41626.pma101_recipesharingapplication.Model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

public class ViewModel extends androidx.lifecycle.ViewModel {
    private MutableLiveData<ArrayList<Recipe>> liveDataRecipes = new MutableLiveData<>();
    private MutableLiveData<User> currentUser = new MutableLiveData<>();
    public void changeDateRecipes(ArrayList<Recipe> recipes) {liveDataRecipes.setValue(recipes);}
    public void changeCurrentUser(User user) {currentUser.setValue(user);}
    public LiveData<ArrayList<Recipe>>getChangeDataRecipes() {return liveDataRecipes;}
    public LiveData<User> getChangeDateCurrentUser() {return currentUser;}


}
