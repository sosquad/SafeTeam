package com.my.safeteam;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.safeteam.DB.Grupo;
import com.my.safeteam.DB.InvitacionGrupo;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class NotificationView extends AppCompatActivity {
    ArrayList<InvitacionGrupo> invitaciones;
    LinearLayout notificationContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_view);
        notificationContainer = findViewById(R.id.notification_container);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .9), (int) (height * .7));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.x = 0;
        params.y = -5;
        getWindow().setAttributes(params);
        invitaciones = (ArrayList<InvitacionGrupo>) getIntent().getExtras().get("invitaciones");
        createList();
    }

    private void createList() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (final InvitacionGrupo invitation : invitaciones) {
            final CardView invitationCard = (CardView) inflater.inflate(R.layout.notification_view, null);
            final ImageView groupAvatar = invitationCard.findViewById(R.id.avatar_group);
            final TextView groupName = invitationCard.findViewById(R.id.inv_group_name);
            final TextView groupLeader = invitationCard.findViewById(R.id.leader_name);
            final TextView invited_at = invitationCard.findViewById(R.id.invited_at);
            final Button accept_btn = invitationCard.findViewById(R.id.accept_group);
            final Button cancel_btn = invitationCard.findViewById(R.id.cancel_group);
            FirebaseDatabase.getInstance().getReference("USERS/" + invitation.getLider() + "/GRUPOS/" + invitation.getIDGrupo())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            System.out.println(dataSnapshot);
                            if (dataSnapshot.exists()) {
                                Grupo grupo = dataSnapshot.getValue(Grupo.class);
                                Glide.with(NotificationView.this).load(grupo.getAvatar()).apply(RequestOptions.circleCropTransform()).into(groupAvatar);
                                groupName.setText("Grupo " + grupo.getNombre());
                                groupLeader.setText("Te invitó " + grupo.getLider().getName());
                                invited_at.setText(getDateFromMilis(invitation.getFecha_invitacion()));
                                notificationContainer.addView(invitationCard);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


        }
    }


    public String getDateFromMilis(long milis) {
        long currentDate = System.currentTimeMillis();
        long elapsed_time = TimeUnit.MILLISECONDS.toMinutes(currentDate - milis);
        double hour = 0;
        double days = 0;
        if ((elapsed_time / 60) >= 1) {
            hour = Math.floor(elapsed_time / 60);
            if (hour / 24 >= 1) {
                days = Math.floor(hour / 24);
                return "Hace " + (int) (days) + " dias y " + Math.floor(hour % 24) + " horas.";
            } else {
                return "Hace " + (int) (hour) + " horas y " + Math.floor((elapsed_time % 60)) + " minutos.";
            }
        } else {
            return "Hace " + (elapsed_time) + " minutos.";
        }
    }
}
