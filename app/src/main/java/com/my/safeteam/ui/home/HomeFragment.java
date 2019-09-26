package com.my.safeteam.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.safeteam.DB.Grupo;
import com.my.safeteam.DB.ReferenciaAlGrupo;
import com.my.safeteam.R;
import com.my.safeteam.globals.LogedUser;
import com.my.safeteam.utils.Animaciones;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {
    View root;
    Animaciones anim = new Animaciones();
    DatabaseReference dR;
    ImageView groupAvatar;
    TextView groupName;
    TextView groupOrganitation;
    TextView time_elapsed;
    LinearLayout groupContainer;
    CardView cardView;

    List<Grupo> grupos = new ArrayList<>();
    List<Grupo> externalGroup = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        AnimationSet animationSet = anim.slideFadeAnimation(1000, 0, 0, 0, 0, 0.0f, 1.0f);
        root.setAnimation(animationSet);

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
                    showGroups();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseDatabase.getInstance().getReference("USERS/" + lu.getCurrentUserUid() + "/PARTICIPANTE")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                ReferenciaAlGrupo referenciaAlGrupo = data.getValue(ReferenciaAlGrupo.class);
                                System.out.println(referenciaAlGrupo.getUrlGrupo());
                                SearchingGroupByReference(referenciaAlGrupo.getUrlGrupo());
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        return root;
    }

    private void oneByOne(Grupo grupo) {
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
        Glide.with(root.getContext().getApplicationContext()).load(grupo.getAvatar()).apply(RequestOptions.circleCropTransform()).into(groupAvatar);
        groupName.setText("Grupo : " + grupo.getNombre());
        groupOrganitation.setText("Organizacion : " + grupo.getContexto());
        System.out.println(grupo.getCreated_at());
        time_elapsed.setText(getDate(grupo.getCreated_at()));
        groupContainer.addView(groupRow);
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
                Glide.with(root.getContext().getApplicationContext()).load(grupo.getAvatar()).apply(RequestOptions.circleCropTransform()).into(groupAvatar);
                groupName.setText("Grupo : " + grupo.getNombre());
                groupOrganitation.setText("Organizacion : " + grupo.getContexto());
                System.out.println(grupo.getCreated_at());
                time_elapsed.setText(getDate(grupo.getCreated_at()));
                groupContainer.addView(groupRow);
            }
            ScrollView container_scroll = root.findViewById(R.id.scroll_current_groups);
            LottieAnimationView lottieAnimationView = root.findViewById(R.id.loading_view);
            lottieAnimationView.setVisibility(View.GONE);
            container_scroll.setVisibility(View.VISIBLE);

        } else {
            ScrollView container_scroll = root.findViewById(R.id.scroll_current_groups);
            LottieAnimationView lottieAnimationView = root.findViewById(R.id.loading_view);
            lottieAnimationView.setVisibility(View.GONE);
            container_scroll.setVisibility(View.VISIBLE);
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
                return "Creado hace " + (int) (days) + " dias y " + (int) Math.floor(hour % 24) + " horas.";
            } else {
                return "Creado hace " + (int) (hour) + " horas y " + (int) Math.floor((elapsed_time % 60)) + " minutos.";
            }
        } else {
            return "Creado hace " + (elapsed_time) + " minutos.";
        }
    }

    public void SearchingGroupByReference(String reference) {
        FirebaseDatabase.getInstance().getReference(reference)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Grupo grupo = dataSnapshot.getValue(Grupo.class);
                            grupo.setuId(dataSnapshot.getKey());
                            addWithoutRepeat(grupo);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void addWithoutRepeat(Grupo grupo) {
        boolean found = false;
        for (Grupo g : grupos) {
            if (g.getuId().equals(grupo.getuId())) {
                found = true;
            }
        }
        if (!found) {
            oneByOne(grupo);
        }
    }
}