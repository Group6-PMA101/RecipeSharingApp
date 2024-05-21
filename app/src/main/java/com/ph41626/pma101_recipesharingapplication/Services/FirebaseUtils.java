package com.ph41626.pma101_recipesharingapplication.Services;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FirebaseUtils {
    private FirebaseDatabase database;

    public FirebaseUtils() {
        database = FirebaseDatabase.getInstance();
    }
    public void getDataFromFirebase(String path, ValueEventListener listener) {
        DatabaseReference reference = database.getReference(path);
        reference.addListenerForSingleValueEvent(listener);
    }
    public void getAllDataByKey(String path, String key, String value, ValueEventListener listener) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(path);
        Query query = reference.orderByChild(key).equalTo(value);
        query.addListenerForSingleValueEvent(listener);
    }
    public void getDataFromFirebaseById(String path,String id, ValueEventListener listener) {
        DatabaseReference reference = database.getReference(path).child(id);
        reference.addListenerForSingleValueEvent(listener);
    }
    public void getUserByEmail(String path, final String email, final ValueEventListener listener) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(path);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                    String noteEmail = noteSnapshot.child("email").getValue(String.class);
                    if (email.equals(noteEmail)) {
                        listener.onDataChange(noteSnapshot);
                        return;
                    }
                }
                listener.onDataChange(null);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onCancelled(databaseError);
            }
        });
    }
}
