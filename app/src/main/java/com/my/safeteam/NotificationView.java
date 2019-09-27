package com.my.safeteam;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.safeteam.DB.BasicUser;
import com.my.safeteam.DB.Grupo;
import com.my.safeteam.DB.InvitacionGrupo;
import com.my.safeteam.DB.ReferenciaAlGrupo;
import com.my.safeteam.globals.LogedUser;
import com.my.safeteam.utils.DebouncedOnClickListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class NotificationView extends AppCompatActivity {
    ArrayList<InvitacionGrupo> invitaciones;
    LinearLayout notificationContainer;
    LogedUser lu = LogedUser.getInstance();
    final int RESULT_OK = 1;

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
                            if (dataSnapshot.exists()) {
                                final Grupo grupo = dataSnapshot.getValue(Grupo.class);
                                Glide.with(NotificationView.this).load(grupo.getAvatar()).apply(RequestOptions.circleCropTransform()).into(groupAvatar);
                                groupName.setText("Grupo " + grupo.getNombre());
                                groupLeader.setText("Te invitó " + grupo.getLider().getName());
                                invited_at.setText(getDateFromMilis(invitation.getFecha_invitacion()));
                                accept_btn.setOnClickListener(new DebouncedOnClickListener(1000) {
                                    @Override
                                    public void onDebouncedClick(View v) {
                                        if(invitation.isAceptado() == false){
                                            invitation.setAceptado(true);
                                            responseToInvitation(2, invitation, grupo, invitationCard);
                                        }
                                    }
                                });
                                cancel_btn.setOnClickListener(new DebouncedOnClickListener(1000) {
                                    @Override
                                    public void onDebouncedClick(View v) {
                                        responseToInvitation(0, invitation, grupo, invitationCard);
                                    }
                                });
                                notificationContainer.addView(invitationCard);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


        }
    }

    public void responseToInvitation(final int response, final InvitacionGrupo invitation, final Grupo grupo, final CardView view) {
        final String acceptInvitationPath = "USERS/" + invitation.getLider() + "/GRUPOS/" + invitation.getIDGrupo() + "/usuariosEnGrupo";
        FirebaseDatabase.getInstance()
                .getReference(acceptInvitationPath)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                BasicUser user = data.getValue(BasicUser.class);
                                if (user.getuId().equals(lu.getCurrentUserUid())) {
                                    FirebaseDatabase.getInstance().getReference(acceptInvitationPath + "/" + data.getKey())
                                            .child("estado").setValue(response);
                                    notificationContainer.removeView(view);

                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        FirebaseDatabase.getInstance().getReference("USERS/" + lu.getCurrentUserUid() + "/INVITACIONES/GRUPO/")
                .child(invitation.getIDGrupo()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (response == 2) {
                    createReferenceToGroup(invitation, grupo);
                    Toast.makeText(NotificationView.this, "Invitación a " + grupo.getNombre() + " aceptada!", Toast.LENGTH_LONG).show();
                } else if (response == 0) {
                    Toast.makeText(NotificationView.this, "Invitación a " + grupo.getNombre() + " rechazada!", Toast.LENGTH_LONG).show();
                }
                invitaciones.remove(invitation);
                Intent returnData = new Intent();
                returnData.putExtra("result", invitaciones);
                setResult(RESULT_OK, returnData);
                if (invitaciones.size() == 0) {
                    finish();
                }
            }
        });
    }

    public void createReferenceToGroup(final InvitacionGrupo invitation, final Grupo grupo) {
        String urlGrupo = "USERS/" + invitation.getLider() + "/GRUPOS/" + invitation.getIDGrupo();
        FirebaseDatabase.getInstance().getReference("USERS/" + lu.getCurrentUserUid() + "/PARTICIPANTE")
                .push().setValue(new ReferenciaAlGrupo(urlGrupo, System.currentTimeMillis()));
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
                return "Hace " + (int) (days) + " dias y " + (int) Math.floor(hour % 24) + " horas.";
            } else {
                return "Hace " + (int) (hour) + " horas y " + (int) Math.floor((elapsed_time % 60)) + " minutos.";
            }
        } else {
            return "Hace " + (elapsed_time) + " minutos.";
        }
    }
}
