package com.my.safeteam.ui.send;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.my.safeteam.DB.BasicUser;
import com.my.safeteam.DB.Grupo;
import com.my.safeteam.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GroupDetail extends Fragment {
    private ImageView groupdetailavatar;
    private TextView groupname, grouporganization, groupleader, created_at;
    private LinearLayout userinvitedcontainer;
    private View root;
    Grupo grupo;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_group_detail, container, false);
        userinvitedcontainer = root.findViewById(R.id.invited_people);
        if (getArguments() != null) {
            grupo = (Grupo) getArguments().getSerializable("GroupUid");
            settingDetails();
        }
        return root;
    }

    private void settingDetails() {
        groupdetailavatar = root.findViewById(R.id.group_detail_avatar);
        groupname = root.findViewById(R.id.nombre_grupo);
        grouporganization = root.findViewById(R.id.nombre_organizacion);
        groupleader = root.findViewById(R.id.name_leader);
        created_at = root.findViewById(R.id.created_at);
        Glide.with(getContext().getApplicationContext()).load(grupo.getAvatar()).into(groupdetailavatar);
        created_at.setText("Creado en : " + getDate());
        groupname.setText(grupo.getNombre());
        grouporganization.setText("Organización : " + grupo.getContexto());
        if (grupo.getLider() != null) {
            groupleader.setText("Lider : " + grupo.getLider().getName() + " | " + grupo.getLider().getEmail());
        } else {
            groupleader.setVisibility(View.INVISIBLE);
        }
        LayoutInflater inflater = (LayoutInflater) root.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (BasicUser user : grupo.getUsuariosEnGrupo()) {
            final LinearLayout userInvited = (LinearLayout) inflater.inflate(R.layout.user_selected_team, null);
            ImageView invitedUserAvatar = userInvited.findViewById(R.id.member_avatar_selected);
            TextView invitedUserName = userInvited.findViewById(R.id.member_name_selected);
            TextView invitedUserEmail = userInvited.findViewById(R.id.member_email_selected);
            Glide.with(getContext().getApplicationContext()).load(user.getPhotoUri()).apply(RequestOptions.circleCropTransform()).into(invitedUserAvatar);
            invitedUserName.setText(user.getName());
            invitedUserEmail.setText(user.getEmail());
            userinvitedcontainer.addView(userInvited);
        }


    }

    private String getDate() {
        SimpleDateFormat simple = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date(grupo.getCreated_at());
        return simple.format(date);
    }
}