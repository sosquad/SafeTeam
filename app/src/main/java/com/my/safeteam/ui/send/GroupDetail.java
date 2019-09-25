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
    private TextView groupname, grouporganization, groupleader, created_at;
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
        if (grupo.getLider().getuId().equals(lu.getCurrentUserUid())) {
            dynamic_fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_edit));
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
        if (grupo.getUsuariosEnGrupo() != null) {
            if (grupo.getUsuariosEnGrupo().size() > 0) {
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
        }


    }

    private String getDate() {
        SimpleDateFormat simple = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date(grupo.getCreated_at());
        return simple.format(date);
    }
}