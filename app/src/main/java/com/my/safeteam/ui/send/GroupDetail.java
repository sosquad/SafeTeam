package com.my.safeteam.ui.send;

import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.my.safeteam.DB.BasicUser;
import com.my.safeteam.DB.Grupo;
import com.my.safeteam.R;
import com.my.safeteam.globals.LogedUser;
import com.my.safeteam.utils.MySnackBar;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GroupDetail extends Fragment {
    private MySnackBar SB = new MySnackBar();
    private ImageView groupdetailavatar;
    private TextView groupname, grouporganization, created_at;
    private LinearLayout userinvitedcontainer;
    private RelativeLayout avatar_container;
    private View root;
    Grupo grupo;
    FloatingActionButton dynamic_fab;
    Snackbar message;
    LogedUser lu = LogedUser.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_group_detail, container, false);
        dynamic_fab = root.findViewById(R.id.fab_dynamic);
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
        invitedUserName.setText(lider.getName());
        invitedUserEmail.setText("Lider");
        userinvitedcontainer.addView(userInvited);
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
        if (grupo.getLider().getuId().equals(lu.getCurrentUserUid())) {
            dynamic_fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_edit));
            dynamic_fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (message == null) {
                        message = SB.snackBar("Aqui despues se podra editar :3", v, "action", null);
                    } else {
                        if (!message.isShown()) {
                            message.show();
                        } else {
                            message.dismiss();
                        }
                    }

                }
            });
        } else {
            dynamic_fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (message == null) {
                        message = SB.snackBar("¿Quieres contactar al lider?", v, "Contactar", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                    } else {
                        if (!message.isShown()) {
                            message.show();
                        } else {
                            message.dismiss();
                        }
                    }

                }
            });
        }
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