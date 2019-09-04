package com.my.safeteam;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.my.safeteam.DB.User;

public class MainActivity extends AppCompatActivity {
    //NAVBAR INITALITATION
    public Toolbar toolbar;
    public DrawerLayout drawerLayout;
    public NavController navController;
    public NavigationView navigationView;

    //GOOGLE THINGS
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount account;
    ImageView avatar;
    TextView displayName;
    TextView displayEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        setupNavigation();
        getinGoogleUser();

    }

    private void getinGoogleUser(){
        View hView = navigationView.getHeaderView(0);
        account = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
        User user = new User(account.getDisplayName(), account.getPhotoUrl(), account.getEmail());
        avatar = hView.findViewById(R.id.userPhotoAvatar);
        Glide.with(this).load(user.getPhotoUri()).apply(RequestOptions.circleCropTransform()).into(avatar);
        displayName = hView.findViewById(R.id.userNameDisplay);
        displayName.setText(user.getName());
        displayEmail = hView.findViewById(R.id.userEmailDisplay);
        displayEmail.setText(user.getEmail());
    }

    //Un cambio random para el commit
    // Setting Up One Time Navigation
    private void setupNavigation() {
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = findViewById(R.id.navigationView);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);

        NavigationUI.setupWithNavController(navigationView, navController);
    }

    public void logOut(MenuItem view) {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseAuth.getInstance().signOut();
                        goToLogin();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(Navigation.findNavController(this, R.id.nav_host_fragment), drawerLayout);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void goToLogin() {
        Intent LoginView = new Intent(this, LoginActivity.class);
        LoginView.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(LoginView);
    }
}

