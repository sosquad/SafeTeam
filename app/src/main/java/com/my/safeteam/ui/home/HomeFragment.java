package com.my.safeteam.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.safeteam.DB.Grupo;
import com.my.safeteam.R;
import com.my.safeteam.globals.LogedUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {
    View root;
    DatabaseReference dR;
    ImageView groupAvatar;
    TextView groupName;
    TextView groupOrganitation;
    TextView time_elapsed;
    LinearLayout groupContainer;
    CardView cardView;

    List<Grupo> grupos = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        groupContainer = root.findViewById(R.id.group_container);
        LogedUser lu = LogedUser.getInstance();
        grupos.clear();
        dR = FirebaseDatabase.getInstance().getReference("USERS/" + lu.getCurrentUserUid() + "/GRUPOS");
        dR.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Grupo grupo = snapshot.getValue(Grupo.class);
                        grupo.setuId(snapshot.getKey());
                        grupos.add(grupo);
                    }
                }
                showGroups();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return root;
    }

    private void showGroups() {
        groupContainer.removeAllViews();
        if (grupos.size() > 0) {
            for (Grupo grupo : grupos) {
                final Grupo selectedGroup = grupo;
                final LayoutInflater layoutInflater = (LayoutInflater) root.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout groupRow = (LinearLayout) layoutInflater.inflate(R.layout.group_link, null);
                cardView = groupRow.findViewById(R.id.nav_send);
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("GroupUid", selectedGroup);
                        NavController navController = Navigation.findNavController(v);
                        navController.navigate(R.id.nav_send, bundle);
                    }
                });
                groupAvatar = groupRow.findViewById(R.id.group_avatar_row);
                groupName = groupRow.findViewById(R.id.group_name);
                groupOrganitation = groupRow.findViewById(R.id.group_organitation);
                time_elapsed = groupRow.findViewById(R.id.creado_hace);
                Glide.with(getContext().getApplicationContext()).load(grupo.getAvatar()).apply(RequestOptions.circleCropTransform()).into(groupAvatar);
                groupName.setText("Grupo : " + grupo.getNombre());
                groupOrganitation.setText("Organizacion : " + grupo.getContexto());
                System.out.println(grupo.getCreated_at());
                time_elapsed.setText(getDate(grupo.getCreated_at()));
                groupContainer.addView(groupRow);
            }


        }

    }

    private String getDate(long milliSeconds) {
        long currentDate = System.currentTimeMillis();
        long elapsed_time = TimeUnit.MILLISECONDS.toMinutes(currentDate - milliSeconds);
        double hour = 0;
        double days = 0;
        if ((elapsed_time / 60) >= 1) {
            hour = Math.floor(elapsed_time / 60);
            if (hour / 24 >= 1) {
                days = Math.floor(hour / 24);
                return "Creado hace " + (int) (days) + " dias y " + Math.floor(hour % 24) + " horas.";
            } else {
                return "Creado hace " + (int) (hour) + " horas y " + Math.floor((elapsed_time % 60)) + " minutos.";
            }
        } else {
            return "Creado hace " + (elapsed_time) + " minutos.";
        }
    }
}