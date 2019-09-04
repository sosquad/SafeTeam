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

    public void storeUser(User user, String id) {
        final User fUser = user;
        final String uId = id;
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("USERS");

        ValueEventListener responseListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(uId)) {
                    System.out.println("Valor ya existe");
                } else {
                    System.out.println("Valor aun no existe");
                    myRef.child(uId).setValue(fUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        myRef.addListenerForSingleValueEvent(responseListener);
    }
}
