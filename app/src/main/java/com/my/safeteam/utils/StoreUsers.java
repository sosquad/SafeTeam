package com.my.safeteam.utils;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.my.safeteam.DB.User;

public class StoreUsers {

    FirebaseDatabase database;
    DatabaseReference myRef;

    public void storeUser(User user, String id) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("USERS");
        myRef.child(id).setValue(user);
    }

    public boolean searchRegisteredUsers(String id) {
        boolean found = false;

        return found;
    }

}
