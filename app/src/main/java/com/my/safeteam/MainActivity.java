package com.my.safeteam;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.my.safeteam.DB.InvitacionGrupo;
import com.my.safeteam.DB.User;
import com.my.safeteam.globals.LogedUser;
import com.my.safeteam.utils.DebouncedOnClickListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnSystemUiVisibilityChangeListener {
    //GSON
    private Gson gson = new Gson();
    //LOGED USER
    private User user;
    private View hView;
    //NAVBAR INITALITATION
    public Toolbar toolbar;
    public DrawerLayout drawerLayout;
    public NavController navController;
    public NavigationView navigationView;
    //GOOGLE THINGS
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    ImageView avatar;
    TextView displayName;
    TextView displayEmail;
    LogedUser lu = LogedUser.getInstance();
    FrameLayout redCircle;
    TextView countTextView;
    List<InvitacionGrupo> invitaciones = new ArrayList<>();
    Intent seeNotifications;
    LottieAnimationView notificationAlert;
    final int START_VIEW_NOTIFICATION = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lu.setCurrentUserUid(getIntent().getExtras().getString("userID"));
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        getinGoogleUser();

        if (Build.VERSION.SDK_INT > 26) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            MainActivity.this.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LOW_PROFILE
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            );
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

    }

    private void getinGoogleUser(){
        DatabaseReference fUserReference = FirebaseDatabase.getInstance().getReference("USERS").child(getIntent().getExtras().getString("userID"));
        fUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    user.setuId(dataSnapshot.getKey());
                    lu.setUser(user);
                    setupNavigation(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    // Setting Up One Time Navigation
    private void setupNavigation(User user) {
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = findViewById(R.id.navigationView);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        hView = navigationView.getHeaderView(0);
        avatar = hView.findViewById(R.id.userPhotoAvatar);
        Glide.with(getApplicationContext()).load(user.getPhotoUri()).apply(RequestOptions.circleCropTransform()).into(avatar);
        displayName = hView.findViewById(R.id.userNameDisplay);
        displayName.setText(user.getName());
        displayEmail = hView.findViewById(R.id.userEmailDisplay);
        displayEmail.setText(user.getEmail());
        if (Build.VERSION.SDK_INT > 26) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);

        NavigationUI.setupWithNavController(navigationView, navController);
    }

    public void logOut(MenuItem view) {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseAuth.getInstance().signOut();
                        LoginManager.getInstance().logOut();
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

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.notifications);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();
        redCircle = rootView.findViewById(R.id.view_alert_red_circle);
        countTextView = rootView.findViewById(R.id.view_alert_count_textview);
        notificationAlert = rootView.findViewById(R.id.notification_alert_animation);

        lookForNotifications();
        rootView.setOnClickListener(new DebouncedOnClickListener(1000) {
            @Override
            public void onDebouncedClick(View v) {
                onOptionsItemSelected(alertMenuItem);

            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    private void lookForNotifications() {
        FirebaseDatabase.getInstance().getReference("USERS/" + lu.getCurrentUserUid() + "/INVITACIONES/GRUPO")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                InvitacionGrupo invite = data.getValue(InvitacionGrupo.class);
                                addInvite(invite);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.notifications:
                seeNotifications = new Intent(this, NotificationView.class);
                seeNotifications.putExtra("invitaciones", (Serializable) invitaciones);
                if (invitaciones.size() > 0) {
                    startActivityForResult(seeNotifications, START_VIEW_NOTIFICATION);
                } else {
                    Toast.makeText(this, "No tienes notificaciones!", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.item1:
                Toast.makeText(this, "Holi desde item1", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void goToLogin() {
        Intent LoginView = new Intent(this, LoginActivity.class);
        LoginView.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(LoginView);
    }

    @Override
    public void onSystemUiVisibilityChange(int i) {
        int mUIFlag = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        getWindow().getDecorView()
                .setSystemUiVisibility(mUIFlag);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void addInvite(InvitacionGrupo invite) {
        boolean encontrado = false;
        for (InvitacionGrupo current : invitaciones) {
            if (current.getIDGrupo().equals(invite.getIDGrupo())) {
                encontrado = true;
            }
        }
        if (!encontrado) {
            invitaciones.add(invite);
            actualizarCounter();
        }
    }

    private void actualizarCounter() {
        int notificationCounter = 0;
        for (InvitacionGrupo current : invitaciones) {
            if (!current.isVisto()) {
                notificationCounter++;
            }
        }
        if (notificationCounter > 0) {
            countTextView.setText(String.valueOf(notificationCounter));
            redCircle.setVisibility(View.VISIBLE);
            notificationAlert.setVisibility(View.VISIBLE);
            notificationAlert.playAnimation();
        } else {
            redCircle.setVisibility(View.GONE);
            notificationAlert.cancelAnimation();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == START_VIEW_NOTIFICATION) {
            if (data != null) {
                invitaciones = (ArrayList<InvitacionGrupo>) data.getExtras().get("result");
                actualizarCounter();
            }else{
                invitaciones = new ArrayList<>();
                actualizarCounter();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}

