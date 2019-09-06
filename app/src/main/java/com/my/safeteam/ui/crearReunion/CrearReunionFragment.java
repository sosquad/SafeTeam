package com.my.safeteam.ui.crearReunion;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.safeteam.DB.User;
import com.my.safeteam.R;
import com.my.safeteam.utils.UserAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CrearReunionFragment extends Fragment implements OnMapReadyCallback {
    ListView userList;
    UserAdapter adapter;
    SearchView editsearch;
    List<User> values = new ArrayList<>();
    private View root;
    private GoogleMap mMap;
    private CrearReunionViewModel crearReunionViewModel;
    private int TAG_CODE_PERMISSION_LOCATION;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_crear_reunion, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        textView.setText("Holi");
        //getCurrentPosition(root);
        setMaps();
        setAutoComplete();
        manageSearchView();
        return root;
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

    public void manageSearchView() {
        FirebaseDatabase.getInstance().getReference("USERS").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    values.add(user);
                }
                adapter = new UserAdapter(root.getContext(), values);
                userList = root.findViewById(R.id.lista_usuarios);
                userList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        SearchView searchView = root.findViewById(R.id.unir_a_grupo);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                System.out.println(s);
                return false;
            }
        });

    }

    public void manageListView() {

    }


}