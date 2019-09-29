package com.my.safeteam.ui.send;

import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.snackbar.Snackbar;
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
import com.my.safeteam.utils.MySnackBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GroupDetail extends Fragment {
    private MySnackBar SB = new MySnackBar();
    private ImageView groupdetailavatar;
    private TextView groupname, grouporganization, created_at;
    private LinearLayout userinvitedcontainer;
    private RelativeLayout avatar_container;
    private View root;
    Grupo grupo;
    FloatingActionMenu dynamic_fab;
    LogedUser lu = LogedUser.getInstance();
    LayoutInflater inflater;
    Button addMeet;

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_group_detail, container, false);
        dynamic_fab = root.findViewById(R.id.fab_dynamic);
        addMeet = root.findViewById(R.id.add_meet_btn);
        userinvitedcontainer = root.findViewById(R.id.invited_people);
        if (getArguments() != null) {
            grupo = (Grupo) getArguments().getSerializable("GroupUid");
            settingDetails();
        }
        return root;
    }

    private void settingDetails() {
        settingEnviroment(grupo.getLider().getuId().equals(lu.getCurrentUserUid()));
        settingFAB();
        groupdetailavatar = root.findViewById(R.id.group_detail_avatar);
        groupname = root.findViewById(R.id.nombre_grupo);
        grouporganization = root.findViewById(R.id.nombre_organizacion);
        created_at = root.findViewById(R.id.created_at);
        avatar_container = root.findViewById(R.id.avatar_group_container);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        avatar_container.getLayoutParams().height = (int) (display.getHeight() * 0.5);
        Glide.with(getContext().getApplicationContext()).load(grupo.getAvatar()).into(groupdetailavatar);
        created_at.setText("Creado en : " + getDate());
        groupname.setText(grupo.getNombre());
        grouporganization.setText("Organización : " + grupo.getContexto());
        LayoutInflater inflater = (LayoutInflater) root.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addLeaderToList(inflater, grupo.getLider());
        if (grupo.getUsuariosEnGrupo() != null) {
            if (grupo.getUsuariosEnGrupo().size() > 0) {
                for (BasicUser user : grupo.getUsuariosEnGrupo()) {
                    final LinearLayout userInvited = (LinearLayout) inflater.inflate(R.layout.user_selected_team, null);
                    ImageView invitedUserAvatar = userInvited.findViewById(R.id.member_avatar_selected);
                    TextView invitedUserName = userInvited.findViewById(R.id.member_name_selected);
                    TextView invitedUserEmail = userInvited.findViewById(R.id.member_email_selected);
                    Glide.with(getContext().getApplicationContext()).load(user.getPhotoUri()).apply(RequestOptions.circleCropTransform()).into(invitedUserAvatar);
                    if (user.getuId().equals(lu.getCurrentUserUid())) {
                        invitedUserName.setText("Tú");
                        invitedUserEmail.setVisibility(View.GONE);
                    } else {
                        setStatus(user.getEstado(), user, invitedUserEmail, invitedUserName);
                    }
                    userinvitedcontainer.addView(userInvited);
                }
            }
        }
    }

    private void addLeaderToList(LayoutInflater inflater, BasicUser lider) {
        final LinearLayout userInvited = (LinearLayout) inflater.inflate(R.layout.user_selected_team, null);
        ImageView invitedUserAvatar = userInvited.findViewById(R.id.member_avatar_selected);
        TextView invitedUserName = userInvited.findViewById(R.id.member_name_selected);
        TextView invitedUserEmail = userInvited.findViewById(R.id.member_email_selected);
        Glide.with(getContext().getApplicationContext()).load(lider.getPhotoUri()).apply(RequestOptions.circleCropTransform()).into(invitedUserAvatar);
        invitedUserName.setText(grupo.getLider().getuId().equals(lu.getCurrentUserUid()) ? "Tú" : lider.getName());
        invitedUserEmail.setText("Lider");
        userinvitedcontainer.addView(userInvited);
        addMeet.setOnClickListener((View v) ->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.nav_gallery);
        });
    }

    private void settingEnviroment(boolean isLeaderOrNot) {
        ImageView theresNoMeet = root.findViewById(R.id.if_theres_no_meet);

        if (isLeaderOrNot) {
            theresNoMeet.setImageDrawable(getResources().getDrawable(R.drawable.agregar_reu));
        } else {
            theresNoMeet.setImageDrawable(getResources().getDrawable(R.drawable.no_hay_reu));
        }
    }

    private void settingFAB() {
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (grupo.getLider().getuId().equals(lu.getCurrentUserUid())) {
            addMeet.setVisibility(View.VISIBLE);
            FloatingActionButton botonDelete = (FloatingActionButton) inflater.inflate(R.layout.item_fab, null);
            botonDelete.setImageDrawable(getResources().getDrawable(R.drawable.ic_delete));
            botonDelete.setLabelText("Eliminar");
            dynamic_fab.setClosedOnTouchOutside(true);
            dynamic_fab.addMenuButton(botonDelete);
            dynamic_fab.getMenuIconView().setImageDrawable(getResources().getDrawable(R.drawable.ic_action_edit));
            botonDelete.setOnClickListener((View v) -> SB.snackBar("¿Desea borrar este grupo? ", v, "BORRAR", (View view) -> borrarGrupo(view)).show());
        } else {
            dynamic_fab.setOnClickListener((View v)-> SB.snackBar("¿Quieres contactar al lider?", v, "Contactar",null));
        }
    }

    private void borrarGrupo(View v) {
        String referencia = "USERS/"+grupo.getLider().getuId()+"/GRUPOS/"+grupo.getuId();
        for(BasicUser g : grupo.getUsuariosEnGrupo()){
            if(g.getEstado() == 2) {
                FirebaseDatabase.getInstance().getReference("USERS/" + g.getuId() + "/PARTICIPANTE")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    if (data.getValue(ReferenciaAlGrupo.class).getUrlGrupo().equals(referencia)) {
                                        FirebaseDatabase.getInstance().getReference("USERS/" + g.getuId() + "/PARTICIPANTE/" + data.getKey()).removeValue();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }else if(g.getEstado() == 1){
                FirebaseDatabase.getInstance().getReference("USERS/" + g.getuId() + "/INVITACIONES/GRUPO/"+grupo.getuId())
                        .removeValue();
            }

        }
        FirebaseDatabase.getInstance().getReference("USERS/"+lu.getCurrentUserUid()+"/GRUPOS/"+grupo.getuId())
                .removeValue((@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) -> {
                    Toast.makeText(root.getContext(),"Grupo eliminado",Toast.LENGTH_LONG).show();
                    NavController navController = Navigation.findNavController(root);
                    navController.navigate(R.id.nav_home);
                });
    }

    private String getDate() {
        SimpleDateFormat simple = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date(grupo.getCreated_at());
        return simple.format(date);
    }

    private void setStatus(int i, BasicUser user, TextView invitedUserEmail, TextView invitedUserName) {
        switch (i) {
            case 0:
                invitedUserName.setText(user.getEmail());
                invitedUserEmail.setText("Estado : Rechazado");
                break;
            case 1:
                invitedUserName.setText(user.getEmail());
                invitedUserEmail.setText("Estado : En espera");
                break;
            case 2:
                invitedUserName.setText(user.getName());
                invitedUserEmail.setText(user.getEmail());
                break;
        }
    }
}