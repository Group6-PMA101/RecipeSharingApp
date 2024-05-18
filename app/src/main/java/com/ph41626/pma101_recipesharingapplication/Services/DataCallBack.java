package com.ph41626.pma101_recipesharingapplication.Services;

public interface DataCallBack<T> {
    void onSuccess(T data);
    void onFailure(String errorMessage);
}
