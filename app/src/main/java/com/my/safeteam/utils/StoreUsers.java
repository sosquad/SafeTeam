package com.my.safeteam.utils;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.safeteam.DB.User;

public class StoreUsers {

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    public boolean storeUser(User user, String id) {
        final User fUser = user;
        final String uId = id;
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("USERS");
        myRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    System.out.println("SAD THINGS HAPPEN :(");
                } else {
                    System.out.println("HAPPINESS");
                    myRef.child(uId).setValue(fUser);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return true;
    }
}
