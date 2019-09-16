package com.my.safeteam.ui.crearGrupo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.my.safeteam.DB.User;
import com.my.safeteam.R;
import com.my.safeteam.utils.Animaciones;
import com.my.safeteam.utils.SearchLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CrearGrupoFragment extends Fragment {
    private Animaciones anim = new Animaciones();
    private View root;
    CircleImageView imagenGrupoContainer;
    private Button agregarAlGrupo, crearGrupo;
    private List<User> selectedList = new ArrayList<>();
    private final int CAMERA = 0;
    private final int GALLERY = 1;
    private final int RESULT_CANCELED = 2;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private final int SEARCH_USERS = 3;
    private LinearLayout container;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_crear_grupo, container, false);
        Button buscarImagen = root.findViewById(R.id.agregar_imagen_grupo);
        imagenGrupoContainer = root.findViewById(R.id.imagen_grupo_container);
        buscarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });
        agregarAlGrupo = root.findViewById(R.id.agregar_miembro_grupo);
        agregarAlGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchSearch();
            }
        });
        return root;
    }

    private void addWithoutRepeat(User user) {
        boolean found = false;
        for (User inlist : selectedList) {
            if (inlist.equals(user)) {
                found = true;
            }
        }
        if (!found) {
            selectedList.add(user);
        }
    }

    private void launchSearch() {
        Intent searchLayout = new Intent(root.getContext(), SearchLayout.class);
        startActivityForResult(searchLayout, SEARCH_USERS);
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(root.getContext());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
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
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(root.getContext().getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
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
                    imagenGrupoContainer.setImageBitmap(thumbnail);
                    imagenGrupoContainer.setVisibility(View.VISIBLE);
                    saveImage(thumbnail);
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
                    inflateSelectedUsers(incomingUser);
                    Toast.makeText(root.getContext(), "Se ha enviado un mensaje a " + incomingUser.getEmail() + "!", Toast.LENGTH_LONG).show();

                }
            }
        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + "SAFETEAM/IMAGES");
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(root.getContext(),
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
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
}