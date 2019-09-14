package com.my.safeteam;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.safeteam.DB.User;
import com.my.safeteam.utils.UserAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChooseMembers extends AppCompatActivity {
    ListView userList;
    UserAdapter adapter;
    List<User> selectedUsers;
    List<User> values = new ArrayList<>();
    private int RESULT_OK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_members);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .7));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;
        getWindow().setAttributes(params);
        selectedUsers = (ArrayList<User>) getIntent().getExtras().get("selectedUsers");
        manageSearchView();
    }

    public void manageSearchView() {
        FirebaseDatabase.getInstance().getReference("USERS").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    user.setuId(snapshot.getKey());
                    values.add(user);
                }
                populateView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        SearchView searchView = findViewById(R.id.unir_a_grupo);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchByValue(s);
                return false;
            }
        });

    }

    public void populateView() {
        if (selectedUsers != null) {
            for (User user : selectedUsers) {
                for (int i = 0; i < values.size(); i++) {
                    if (user.getuId().equals(values.get(i).getuId())) {
                        values.get(i).setSelected(values.get(i).isSelected());
                        values.remove(i);
                    }
                }
            }
        }

        userList = findViewById(R.id.lista_usuarios);
        adapter = new UserAdapter(getApplicationContext(), values, true);
        userList.setAdapter(adapter);
    }


    private void searchByValue(String s) {
        final String searchText = s;
        FirebaseDatabase.getInstance().getReference("USERS").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                values.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user.getName().contains(searchText) | user.getEmail().contains(searchText)) {
                        user.setuId(snapshot.getKey());
                        values.add(user);
                        ((UserAdapter) userList.getAdapter()).update(values);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public Serializable getResult() {
        ArrayList<User> userSelected = new ArrayList<>();
        for (int i = 0; i < (userList.getAdapter()).getCount(); i++) {
            if (((UserAdapter) userList.getAdapter()).isChecked(i)) {
                User user = new User(i, ((UserAdapter) userList.getAdapter()).getItem(i).getName(), ((UserAdapter) userList.getAdapter()).getItem(i).getPhotoUri(), ((UserAdapter) userList.getAdapter()).getItem(i).getEmail());
                user.setuId(((UserAdapter) userList.getAdapter()).getItem(i).getuId());
                userSelected.add(user);
            }
        }
        return userSelected;
    }

    public void onClickOk(View view) {
        Intent returnData = new Intent();
        returnData.putExtra("result", getResult());
        setResult(RESULT_OK, returnData);
        finish();
    }

    public void onClickCancel(View view) {
        finish();
    }
}
