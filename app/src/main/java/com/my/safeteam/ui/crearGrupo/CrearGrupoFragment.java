package com.my.safeteam.ui.crearGrupo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.my.safeteam.DB.BasicUser;
import com.my.safeteam.DB.Grupo;
import com.my.safeteam.DB.InvitacionGrupo;
import com.my.safeteam.DB.User;
import com.my.safeteam.R;
import com.my.safeteam.globals.LogedUser;
import com.my.safeteam.utils.Animaciones;
import com.my.safeteam.utils.SearchLayout;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CrearGrupoFragment extends Fragment {
    final LogedUser lu = LogedUser.getInstance();
    private Animaciones anim = new Animaciones();
    private View root;
    CircleImageView imagenGrupoContainer;
    private Button agregarAlGrupo, crearGrupo;
    private EditText agregarNombre, agregarOrganizacion;
    private List<User> selectedList = new ArrayList<>();
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private final int SEARCH_USERS = 3;
    private LinearLayout container;
    private StorageReference mStorageRef;
    private Uri contentURI;
    private ProgressBar progressBar;
    ScrollView containerCrearGrupo;
    List<BasicUser> BasicUser;
    LottieAnimationView lottieAnimationView;
    UploadTask ut;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_crear_grupo, container, false);
        inflateLeader(lu.getUser());
        Button buscarImagen = root.findViewById(R.id.agregar_imagen_grupo);
        containerCrearGrupo = root.findViewById(R.id.content_crear_grupo);
        imagenGrupoContainer = root.findViewById(R.id.imagen_grupo_container);
        lottieAnimationView = root.findViewById(R.id.animation_view);
        buscarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestRead();
            }
        });
        agregarAlGrupo = root.findViewById(R.id.agregar_miembro_grupo);
        agregarAlGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchSearch();
            }
        });

        agregarNombre = root.findViewById(R.id.agregar_nombre_grupo);
        agregarOrganizacion = root.findViewById(R.id.agregar_nombre_organizacion);
        progressBar = root.findViewById(R.id.progressBar);

        crearGrupo = root.findViewById(R.id.crear_grupo);
        crearGrupo.setOnClickListener((View v) -> {
                if (agregarNombre.getText().toString().equals("")) {
                    Toast.makeText(root.getContext(), "Debes agregar un nombre al grupo!", Toast.LENGTH_SHORT).show();
                } else {
                    if (agregarOrganizacion.getText().toString().equals("")) {
                        new AlertDialog.Builder(root.getContext())
                                .setIcon(R.drawable.ic_sms_failed)
                                .setTitle("Nombre organización")
                                .setMessage("¿Desea continuar sin ingresar un nombre a su organización?")
                                .setPositiveButton("Si", (DialogInterface dialog, int which) -> crearGrupo())
                                .setNegativeButton("No", null)
                                .show();
                    } else {
                        crearGrupo();
                    }
                }
        });
        return root;
    }

    private void addWithoutRepeat(User user) {
        boolean found = false;
        if (user.getuId().equals(lu.getUser().getuId())) {
            found = true;
            Toast.makeText(root.getContext(), "Eres el lider de este grupo ! ", Toast.LENGTH_SHORT).show();

        } else {
            for (User inlist : selectedList) {
                if (inlist.getuId().equals(user.getuId())) {
                    found = true;
                }
            }
        }

        if (!found) {
            selectedList.add(user);
            inflateSelectedUsers(user);
            Toast.makeText(root.getContext(), "Se ha enviado un mensaje a " + user.getEmail() + "!", Toast.LENGTH_SHORT).show();
        }
    }

    private void launchSearch() {
        Intent searchLayout = new Intent(root.getContext(), SearchLayout.class);
        startActivityForResult(searchLayout, SEARCH_USERS);
    }

    private void takePhotoFromCamera() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(getContext(), this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (data != null) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode != CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    contentURI = result.getUri();
                    imagenGrupoContainer.setImageURI(contentURI);
                    imagenGrupoContainer.setVisibility(View.VISIBLE);
                } else {
                    Exception error = result.getError();
                }
            }
        }
        if (requestCode == SEARCH_USERS) {
            if (resultCode == 1) {
                if (data.getExtras().get("selectedUser") != null) {
                    User incomingUser = (User) data.getExtras().get("selectedUser");
                    System.out.println("INCOMING USER : " + incomingUser);
                    addWithoutRepeat(incomingUser);
                }
            }
        }
    }

    public void inflateSelectedUsers(User user) {
        final User thumbUser = user;
        LayoutInflater inflater = (LayoutInflater) root.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        container = root.findViewById(R.id.personas_grupo);
        if (user != null) {
            final LinearLayout clickeableColumn = (LinearLayout) inflater.inflate(R.layout.user_selected_team, null);
            clickeableColumn.setOnClickListener((View view) -> onClickClickeableColumn(thumbUser, clickeableColumn));
            ImageView selectedUserPhoto = clickeableColumn.findViewById(R.id.member_avatar_selected);
            TextView userName = clickeableColumn.findViewById(R.id.member_name_selected);
            TextView userEmail = clickeableColumn.findViewById(R.id.member_email_selected);
            userName.setText(thumbUser.getName());
            userEmail.setText(thumbUser.getEmail());
            Glide.with(getContext().getApplicationContext()).load(thumbUser.getPhotoUri()).apply(RequestOptions.circleCropTransform()).into(selectedUserPhoto);
            container.addView(clickeableColumn);
        }
    }

    public void inflateLeader(User user) {
        LayoutInflater inflater = (LayoutInflater) root.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        container = root.findViewById(R.id.personas_grupo);
        if (user != null) {
            final LinearLayout clickeableColumn = (LinearLayout) inflater.inflate(R.layout.user_list_team, null);
            ImageView selectedUserPhoto = clickeableColumn.findViewById(R.id.member_avatar);
            TextView userName = clickeableColumn.findViewById(R.id.member_name);
            TextView userEmail = clickeableColumn.findViewById(R.id.member_email);

            userName.setText(user.getName() + " | LIDER");
            userEmail.setVisibility(View.GONE);
            Glide.with(getContext().getApplicationContext()).load(user.getPhotoUri()).apply(RequestOptions.circleCropTransform()).into(selectedUserPhoto);
            container.addView(clickeableColumn);
        }
    }

    public void onClickClickeableColumn(User user, LinearLayout view) {
        final LinearLayout finalView = view;
        final User finalUser = user;
        AnimationSet animationSet = anim.slideFadeAnimation(600, 0, 0, 0, 0, 1.0f, 0.0f);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Toast.makeText(root.getContext(), finalUser.getName() + " removed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                selectedList.remove(finalUser);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.startAnimation(animationSet);
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        container.removeView(finalView);
                    }
                },
                600);
    }

    public void requestRead() {
        if (ContextCompat.checkSelfPermission(root.getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            takePhotoFromCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhotoFromCamera();
            } else {
                // Permission Denied
                Toast.makeText(root.getContext(), "Permiso denegado", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    public void crearGrupo() {
        User leader = lu.getUser();
        BasicUser = new ArrayList<>();
        BasicUser lider = new BasicUser(leader.getuId(), leader.getName(), leader.getPhotoUri(), leader.getEmail(), 3);
        for (User user : selectedList) {
            BasicUser.add(new BasicUser(user.getuId(), user.getName(), user.getPhotoUri(), user.getEmail(), 1));
        }
        Grupo newGroup = new Grupo(agregarNombre.getText().toString(), agregarOrganizacion.getText().toString(), BasicUser, lider, getCurrentTimestamp());
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("USERS/" + lu.getUser().getuId() + "/GRUPOS");
        final String uniqueKey = ref.push().getKey();
        ref.child(uniqueKey).setValue(newGroup);
        mStorageRef = FirebaseStorage.getInstance().getReference("USERS/" + lu.getUser().getuId() + "/" + uniqueKey + "/AVATAR");
        if (contentURI != null) {
            ut = mStorageRef.child("avatar.jpg").putFile(contentURI);
            ut.addOnSuccessListener((UploadTask.TaskSnapshot success)->{
                Handler handler = new Handler();
                handler.postDelayed(()->{
                    progressBar.setProgress(0);
                    progressBar.setVisibility(View.INVISIBLE);
                }, 1000);
                Toast.makeText(root.getContext(), "Grupo creado con exito!", Toast.LENGTH_SHORT).show();
                success.getMetadata().getReference().getDownloadUrl().addOnSuccessListener((Uri uri) -> {
                    ref.child(uniqueKey).child("avatar").setValue(uri.toString());
                    onSuccessHandler(uniqueKey);
                });
            })
                    .addOnFailureListener((@NonNull Exception e) -> Toast.makeText(root.getContext(), "Hubo un error al guardar la imagen", Toast.LENGTH_SHORT).show())
                    .addOnProgressListener((@NonNull UploadTask.TaskSnapshot taskSnapshot) ->{
                        progressBar.setVisibility(View.VISIBLE);
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressBar.setProgress((int) progress);
                    });
        }else {
            ref.child(uniqueKey).child("avatar").setValue("https://firebasestorage.googleapis.com/v0/b/safe-team.appspot.com/o/default%2Fdefault.jpg?alt=media&token=1fea854c-a96d-46eb-b4e0-eac2eca3874a");
            onSuccessHandler(uniqueKey);
        }


    }

    public void sendInvitation(String uniqueKey) {
        for (BasicUser u : BasicUser) {
            InvitacionGrupo ig = new InvitacionGrupo(uniqueKey, lu.getUser().getuId(), false, false, getCurrentTimestamp());
            FirebaseDatabase.getInstance().getReference("USERS/" + u.getuId() + "/INVITACIONES/GRUPO/" + uniqueKey)
                    .setValue(ig);
        }
    }

    private void onSuccessHandler(String uniqueKey){
        sendInvitation(uniqueKey);
        AnimationSet animationSet = anim.slideFadeAnimation(600, 0, 0, 0, 0, 1.0f, 0.0f);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                containerCrearGrupo.setVisibility(View.GONE);
                crearGrupo.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                lottieAnimationView.setVisibility(View.VISIBLE);
                lottieAnimationView.playAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        Handler handler = new Handler();
        handler.postDelayed(()->{
            NavController navController = Navigation.findNavController(root);
            navController.navigate(R.id.nav_home);
        }, 2600);
        containerCrearGrupo.setAnimation(animationSet);
        crearGrupo.setAnimation(animationSet);
        progressBar.setAnimation(animationSet);

    }
}