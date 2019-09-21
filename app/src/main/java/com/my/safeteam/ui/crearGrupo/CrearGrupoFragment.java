package com.my.safeteam.ui.crearGrupo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.my.safeteam.DB.BasicUser;
import com.my.safeteam.DB.Grupo;
import com.my.safeteam.DB.User;
import com.my.safeteam.R;
import com.my.safeteam.globals.LogedUser;
import com.my.safeteam.utils.Animaciones;
import com.my.safeteam.utils.SearchLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    private final int CAMERA = 0;
    private final int GALLERY = 1;
    private final int RESULT_CANCELED = 2;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private final int SEARCH_USERS = 3;
    private LinearLayout container;
    private byte[] path;
    private StorageReference mStorageRef;
    private Uri contentURI;
    private ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_crear_grupo, container, false);

        inflateLeader(lu.getUser());
        Button buscarImagen = root.findViewById(R.id.agregar_imagen_grupo);
        imagenGrupoContainer = root.findViewById(R.id.imagen_grupo_container);
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
        crearGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User leader = lu.getUser();
                List<BasicUser> BasicUser = new ArrayList<>();
                BasicUser lider = new BasicUser(leader.getuId(), leader.getName(), leader.getPhotoUri(), leader.getEmail(), 3);
                for (User user : selectedList) {
                    BasicUser.add(new BasicUser(user.getuId(), user.getName(), user.getPhotoUri(), user.getEmail(), 1));
                }
                Grupo newGroup = new Grupo(agregarNombre.getText().toString(), agregarOrganizacion.getText().toString(), BasicUser, lider, getCurrentTimestamp());
                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("USERS/" + lu.getUser().getuId() + "/GRUPOS");
                final String uniqueKey = ref.push().getKey();
                ref.child(uniqueKey).setValue(newGroup);
                mStorageRef = FirebaseStorage.getInstance().getReference("USERS/" + lu.getUser().getuId() + "/" + uniqueKey + "/AVATAR");
                if (path != null) {
                    mStorageRef.child("avatar.jpg")
                            .putBytes(path)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setProgress(0);
                                            progressBar.setVisibility(View.INVISIBLE);
                                        }
                                    }, 1000);
                                    Toast.makeText(root.getContext(), "Grupo creado con exito!", Toast.LENGTH_SHORT).show();
                                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            ref.child(uniqueKey).child("avatar").setValue(uri.toString());
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(root.getContext(), "Hubo un error al guardar la imagen", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                    progressBar.setProgress((int) progress);
                                }
                            });
                } else {
                    Toast.makeText(root.getContext(), "Selecciona una imagen!", Toast.LENGTH_SHORT).show();
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

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(root.getContext());
        pictureDialog.setTitle("Selecciona una acción");
        String[] pictureDialogItems = {
                "Buscar en galeria",
                "Tomar una foto"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    private void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(root.getContext().getContentResolver(), contentURI);
                    path = saveImage(bitmap);
                    Toast.makeText(root.getContext(), "Imagen Guardada!", Toast.LENGTH_SHORT).show();
                    imagenGrupoContainer.setImageBitmap(bitmap);
                    imagenGrupoContainer.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(root.getContext(), "Hubo un error! " + e, Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            try {
                if (data != null) {
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    path = saveImage(thumbnail);
                    imagenGrupoContainer.setImageBitmap(thumbnail);
                    imagenGrupoContainer.setVisibility(View.VISIBLE);
                    Toast.makeText(root.getContext(), "Imagen Guardada!", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(root.getContext(), "Foto cancelada! ", Toast.LENGTH_SHORT).show();
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

    public byte[] saveImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }


    public void inflateSelectedUsers(User user) {
        final User thumbUser = user;
        LayoutInflater inflater = (LayoutInflater) root.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        container = root.findViewById(R.id.personas_grupo);
        if (user != null) {
            final LinearLayout clickeableColumn = (LinearLayout) inflater.inflate(R.layout.user_selected_team, null);
            clickeableColumn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickClickeableColumn(thumbUser, clickeableColumn);
                }
            });
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
        AnimationSet animationSet = anim.slideFadeAnimation(view, 600, 0, -100, 0, 0, 1.0f, 0.0f);
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
            showPictureDialog();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showPictureDialog();
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

}