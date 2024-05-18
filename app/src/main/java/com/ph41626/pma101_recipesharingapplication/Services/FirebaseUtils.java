package com.ph41626.pma101_recipesharingapplication.Services;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseUtils {
    private FirebaseDatabase database;

    public FirebaseUtils() {
        database = FirebaseDatabase.getInstance();
    }
    public <T> void getDataFromFirebase(String path, ValueEventListener listener) {
        DatabaseReference reference = database.getReference(path);
        reference.addListenerForSingleValueEvent(listener);
    }
}
