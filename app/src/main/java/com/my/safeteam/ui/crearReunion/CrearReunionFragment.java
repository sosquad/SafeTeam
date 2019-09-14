package com.my.safeteam.ui.crearReunion;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.my.safeteam.ChooseMembers;
import com.my.safeteam.DB.User;
import com.my.safeteam.R;
import com.my.safeteam.dialog.DataPickerFragment;
import com.my.safeteam.dialog.TimePickerFragment;
import com.my.safeteam.utils.Animaciones;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CrearReunionFragment extends Fragment implements OnMapReadyCallback {

    private Animaciones anim = new Animaciones();
    private View root;
    private GoogleMap mMap;
    private int TAG_CODE_PERMISSION_LOCATION;
    private int CHOOSE_MEMBERS_CODE = 5;
    private List<User> selectedUsers = new ArrayList<>();
    private EditText etPlannedDate;
    private EditText etPlannedTime;
    private LinearLayout container;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_crear_reunion, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        etPlannedDate = root.findViewById(R.id.etPlannedDate);
        etPlannedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        etPlannedTime = root.findViewById(R.id.etPlannedTime);
        etPlannedTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(etPlannedTime);
            }
        });
        textView.setText("Creador de reuniones");
        //getCurrentPosition(root);
        bottonBehavior();
        setMaps();
        setAutoComplete();
        return root;
    }

    private void bottonBehavior() {
        Button btn = root.findViewById(R.id.add_to_group);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(root.getContext(), ChooseMembers.class);
                intent.putExtra("selectedUsers", (Serializable) selectedUsers);
                startActivityForResult(intent, CHOOSE_MEMBERS_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int index = 1;
        if (requestCode == CHOOSE_MEMBERS_CODE) {
            if (resultCode == 1) {
                if (((ArrayList<User>) data.getSerializableExtra("result")).size() != 0) {
                    ArrayList<User> incomingUsers = (ArrayList<User>) data.getSerializableExtra("result");
                    for (User user : incomingUsers) {
                        boolean found = true;
                        for (User u : selectedUsers) {
                            if (u.getuId().equals(user.getuId())) {
                                found = false;
                            }
                        }
                        if (found) {
                            inflateSelectedUsers(user, index);
                            index++;
                        }
                    }
                }
            }
        }
    }

    public void inflateSelectedUsers(User user, int index) {
        final User thumbUser = user;
        selectedUsers.add(user);
        LayoutInflater inflater = (LayoutInflater) root.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        container = root.findViewById(R.id.casts_container);
        int size = selectedUsers.size();
        if (user != null) {
            final LinearLayout clickeableColumn = (LinearLayout) inflater.inflate(R.layout.simple_user_for_horizontal_list, null);
            clickeableColumn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickClickeableColumn(thumbUser, clickeableColumn);
                }
            });
            ImageView selectedUserPhoto = clickeableColumn.findViewById(R.id.thumbnail_image);
            TextView userName = clickeableColumn.findViewById(R.id.thumbnail_title);
            userName.setText(user.getName());
            Glide.with(root).load(user.getPhotoUri()).apply(RequestOptions.circleCropTransform()).into(selectedUserPhoto);
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
                selectedUsers.remove(finalUser);
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

    private void setMaps() {
        FragmentManager fragmentManager = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager
                .findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);
    }

    private void setAutoComplete() {
        if (!Places.isInitialized()) {
            Places.initialize(root.getContext(), getString(R.string.google_maps_key), Locale.US);
        }
        FragmentManager fragmentManager = getChildFragmentManager();
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                fragmentManager.findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                LatLng ubicacion = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(ubicacion).title(place.getName()).title(place.getName()));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 15.0f));
            }

            @Override
            public void onError(@NonNull Status status) {

            }

        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(root.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(root.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    TAG_CODE_PERMISSION_LOCATION);
        }
        // Add a marker in Sydney, Australia, and move the camera.
        LatLng Santiago = new LatLng(-33.447487, -70.673676);
        mMap.addMarker(new MarkerOptions().position(Santiago).title("Santiago de Chile"));
        mMap.setMinZoomPreference(10);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Santiago));
    }

    private void showDatePickerDialog() {
        DataPickerFragment newFragment = DataPickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                final String selectedDate = day + " / " + (month + 1) + " / " + year;

                etPlannedDate.setText(selectedDate);
            }
        });

        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(EditText v) {
        DialogFragment newFragment = TimePickerFragment.newInstance(v);
        // Mostrar el datePicker
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }
}