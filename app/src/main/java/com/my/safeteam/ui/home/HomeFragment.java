package com.my.safeteam.ui.home;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.safeteam.DB.BasicUser;
import com.my.safeteam.DB.Grupo;
import com.my.safeteam.DB.ReferenciaAlGrupo;
import com.my.safeteam.R;
import com.my.safeteam.globals.LogedUser;
import com.my.safeteam.utils.Animaciones;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment {
    private View root;
    private Animaciones anim = new Animaciones();
    private DatabaseReference dR;
    private ImageView groupAvatar;
    private TextView groupName;
    private TextView groupOrganitation;
    private TextView time_elapsed;
    private LinearLayout groupContainer;
    private LinearLayout groupExtContainer;
    private CardView cardView;
    private List<Grupo> grupos = new ArrayList<>();
    private ScrollView home_scroll;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        AnimationSet animationSet = anim.slideFadeAnimation(1000, 0, 0, 0, 0, 0.0f, 1.0f);
        root.setAnimation(animationSet);
        groupContainer = root.findViewById(R.id.group_container);
        groupExtContainer = root.findViewById(R.id.group_ext_container);
        home_scroll = root.findViewById(R.id.home_container);
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
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        FirebaseDatabase.getInstance().getReference("USERS/" + lu.getCurrentUserUid() + "/PARTICIPANTE").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               FirebaseDatabase.getInstance().getReference(dataSnapshot.getValue(ReferenciaAlGrupo.class).getUrlGrupo())
                       .addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                               Grupo grupo = dataSnapshot.getValue(Grupo.class);
                               grupo.setuId(dataSnapshot.getKey());
                               oneByOne(grupo);
                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {

                           }
                       });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                ReferenciaAlGrupo ref = dataSnapshot.getValue(ReferenciaAlGrupo.class);
                System.out.println("REMOVIENDO "+ ref.getUrlGrupo());
                String[] url = ref.getUrlGrupo().split("/");
                for(int i = 0; i<groupExtContainer.getChildCount();i++){
                    if(groupExtContainer.getChildAt(i).getTag().equals(url[url.length - 1])){
                        groupExtContainer.removeView(groupExtContainer.getChildAt(i));
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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
        cardView.setOnClickListener((View v)->{
                Bundle bundle = new Bundle();
                bundle.putSerializable("GroupUid", selectedGroup);
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.nav_send, bundle);
        });
        groupAvatar = groupRow.findViewById(R.id.group_avatar_row);
        groupName = groupRow.findViewById(R.id.group_name);
        groupOrganitation = groupRow.findViewById(R.id.group_organitation);
        time_elapsed = groupRow.findViewById(R.id.creado_hace);
        Glide.with(root.getContext().getApplicationContext()).load(grupo.getAvatar()).apply(RequestOptions.circleCropTransform()).into(groupAvatar);
        groupName.setText("Grupo : " + grupo.getNombre());
        groupOrganitation.setText("Organizacion : " + grupo.getContexto());
        time_elapsed.setText(getDate(grupo.getCreated_at()));
        groupRow.setTag(grupo.getuId());
        groupExtContainer.addView(groupRow);
    }

    private void showGroups() {
        groupContainer.removeAllViews();
        if(grupos != null){
            if (grupos.size() > 0) {
                for (Grupo grupo : grupos) {
                    final Grupo selectedGroup = grupo;
                    final LayoutInflater layoutInflater = (LayoutInflater) root.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    LinearLayout groupRow = (LinearLayout) layoutInflater.inflate(R.layout.group_link, null);
                    cardView = groupRow.findViewById(R.id.nav_send);
                    cardView.setOnClickListener((View v) ->{
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("GroupUid", selectedGroup);
                            NavController navController = Navigation.findNavController(v);
                            navController.navigate(R.id.nav_send, bundle);
                    });
                    groupAvatar = groupRow.findViewById(R.id.group_avatar_row);
                    groupName = groupRow.findViewById(R.id.group_name);
                    groupOrganitation = groupRow.findViewById(R.id.group_organitation);
                    time_elapsed = groupRow.findViewById(R.id.creado_hace);
                    Glide.with(root.getContext().getApplicationContext()).load(grupo.getAvatar()).apply(RequestOptions.circleCropTransform()).into(groupAvatar);
                    groupName.setText("Grupo : " + grupo.getNombre());
                    groupOrganitation.setText("Organizacion : " + grupo.getContexto());
                    time_elapsed.setText(getDate(grupo.getCreated_at()));
                    groupContainer.addView(groupRow);
                }
                LinearLayout container_scroll = root.findViewById(R.id.scroll_current_groups);
                LinearLayout container_ext_scroll = root.findViewById(R.id.scroll_ext_groups);
                LottieAnimationView lottieAnimationView = root.findViewById(R.id.loading_view);
                lottieAnimationView.setVisibility(View.GONE);
                container_scroll.setVisibility(View.VISIBLE);
                container_ext_scroll.setVisibility(View.VISIBLE);
                home_scroll.setVisibility(View.VISIBLE);

            } else {
                LinearLayout container_scroll = root.findViewById(R.id.scroll_current_groups);
                LinearLayout container_ext_scroll = root.findViewById(R.id.scroll_ext_groups);
                LottieAnimationView lottieAnimationView = root.findViewById(R.id.loading_view);
                lottieAnimationView.setVisibility(View.GONE);
                container_scroll.setVisibility(View.VISIBLE);
                container_ext_scroll.setVisibility(View.VISIBLE);
                home_scroll.setVisibility(View.VISIBLE);

            }
        }else {
            LinearLayout container_scroll = root.findViewById(R.id.scroll_current_groups);
            LinearLayout container_ext_scroll = root.findViewById(R.id.scroll_ext_groups);
            LottieAnimationView lottieAnimationView = root.findViewById(R.id.loading_view);
            lottieAnimationView.setVisibility(View.GONE);
            container_scroll.setVisibility(View.VISIBLE);
            container_ext_scroll.setVisibility(View.VISIBLE);
            home_scroll.setVisibility(View.VISIBLE);
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
}