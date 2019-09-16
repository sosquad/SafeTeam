package com.my.safeteam.utils;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.my.safeteam.DB.User;
import com.my.safeteam.R;

import java.util.ArrayList;
import java.util.List;

public class SearchLayout extends AppCompatActivity {
    ListView listView;
    SearchListAdapter adapter;
    List<User> dinamicList = new ArrayList<>();
    List<User> values = new ArrayList<>();
    private int RESULT_OK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_overlay_layout);
        listView = findViewById(R.id.lista_de_busqueda);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout(width, height / 2);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.TOP;
        params.x = 0;
        params.y = -20;
        params.alpha = 0.8f;
        getWindow().setAttributes(params);
        //selectedUsers = (ArrayList<User>) getIntent().getExtras().get("selectedUsers");
        SearchView sv = findViewById(R.id.buscar_usuarios);

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.equals("")) {
                    dinamicList.clear();
                    adapter = new SearchListAdapter(getApplicationContext(), dinamicList, true);
                    listView.setAdapter(adapter);
                } else {
                    Query query = FirebaseDatabase.getInstance().getReference("USERS")
                            .orderByChild("email")
                            .startAt(s)
                            .endAt(s + "\uf8ff")
                            .limitToFirst(3);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            dinamicList.clear();
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    User user = data.getValue(User.class);
                                    System.out.println(user);
                                    if (user.getName() != null) {
                                        user.setuId(data.getKey());
                                        addWithoutRepeat(user);
                                    }
                                }
                                adapter = new SearchListAdapter(getApplicationContext(), dinamicList, true);
                                listView.setAdapter(adapter);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }


                return false;
            }
        });
        listView.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listView.requestLayout();

                User user = (User) parent.getItemAtPosition(position);
                Intent intent = new Intent();
                intent.putExtra("selectedUser", user);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void addWithoutRepeat(User user) {
        boolean found = false;
        for (User inlist : dinamicList) {
            if (inlist.equals(user)) {
                found = true;
            }
        }
        if (!found) {
            dinamicList.add(user);
        }
    }
}