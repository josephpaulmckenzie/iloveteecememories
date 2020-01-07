package com.josephpaulmckenzie.iloveteeceememories;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.josephpaulmckenzie.iloveteeceememories.constants.NavigationDrawerConstants;
import com.josephpaulmckenzie.iloveteeceememories.fragments.ChatRoomFragment;
import com.josephpaulmckenzie.iloveteeceememories.fragments.GalleryFragment;
import com.josephpaulmckenzie.iloveteeceememories.fragments.HomeFragment;
import com.josephpaulmckenzie.iloveteeceememories.fragments.SettingsFragment;
import com.josephpaulmckenzie.iloveteeceememories.fragments.UploadFileFragment;
import com.josephpaulmckenzie.iloveteeceememories.fragments.VideosFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Arrays;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.centerCropTransform;
import static com.josephpaulmckenzie.iloveteeceememories.MyFirebaseMessagingService.sendRegistrationToServer;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private View navHeader;
    public boolean connectionStatus;
    private FirebaseUser mFirebaseUser;
    private String mUsername;
    private FirebaseAuth fbAuth;
    private FirebaseUser fbUser;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public Boolean connectionStatus() {
        return connectionStatus;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navHeader = navigationView.getHeaderView(0);
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//        final int REQUEST_IMAGE = 1;
        // Start sign in/sign up activity
        FirebaseUser username = FirebaseAuth.getInstance().getCurrentUser();
//        String displayName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());
//                                    new AuthUI.IdpConfig.GitHubBuilder().build());
//                new AuthUI.IdpConfig.FacebookBuilder().build(),
//                new AuthUI.IdpConfig.TwitterBuilder().build());
        if (username == null) {
            // Start sign in/sign up activity
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setLogo(R.drawable.newfiebig)
                            .build(),
                    554
            );
        }

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.i("Logged In","Yes");
                    FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                @Override
                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                    if (!task.isSuccessful()) {
                                        Log.w("FIREBASE", "getInstanceId failed", task.getException());
                                        return;
                                    }

                                    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
                                    mFirebaseUser = mFirebaseAuth.getCurrentUser();
                                    mUsername = mFirebaseUser.getDisplayName();
                                    String token = task.getResult().getToken();
                                    SharedPreferences sharedPref = getSharedPreferences("FCM_TOKEN", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    String userName = sharedPref.getString("userName","");
                                    String fcmtoken = sharedPref.getString("fcm_token","");

                                    // If either our username or fcm token has been updated and does not match what is in our shared prefs (or is newly instantiated ) we will send it to the database
                                    if (!userName.equals(mUsername) || !fcmtoken.equals(token)){
                                     editor.putString("fcm_token", token);
                                     editor.putString("userName",mUsername);
                                     editor.apply();
                                     editor.commit();
                                     MyFirebaseMessagingService.sendRegistrationToServer(mUsername,token);
                                     Log.i("FCM","Updating");
                                 } else {
                                        Log.i("FCM","Nothing to update");
                                    }


                                }
                            });
                } else {
                    Log.i("Logged in","No");
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Teecee Love", "1 Love sent");
                // Was harcoded to one love per click, we now can set the number of LPC's in settings view
                final SharedPreferences sharedPref = getSharedPreferences("loves", Context.MODE_PRIVATE);
                // Will check and see if we have any LPC's set and use its value and if not will default to 1
                final int currentLoveIntervals = sharedPref.getInt("loves", 1);
                getTeeceeLoves(currentLoveIntervals);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d("FIREBASE", "Key: " + key + " Value: " + value);

            }
        }
        // [END handle_data_extras]






//         Loading profile image
        ImageView profileImage = navHeader.findViewById(R.id.profileImage);
        Glide.with(this)
                .load(R.drawable.teeceee)
                .apply(new RequestOptions()
                        .circleCropTransform()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                ).thumbnail(0.5f)
                .into(profileImage);

        //Loading background image
        ImageView navBackground = navHeader.findViewById(R.id.img_header_bg);
        Glide.with(this).load(NavigationDrawerConstants.BACKGROUND_URL)
                .apply(new RequestOptions()
                        .circleCropTransform()
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .thumbnail(0.5f)
                .into(navBackground);

        //Select Home fragment to display and load by default
        navigationView.setCheckedItem(R.id.nav_home);
        Fragment fragment = new HomeFragment();
        displaySelectedFragment(fragment);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            AuthUI.getInstance()
                .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.i("LOGOUT", "Log out button clicked");
                            Toast.makeText(getApplicationContext(),
                                    "Successfully logged out",
                                    Toast.LENGTH_LONG)
                                    .show();
                            // Start sign in/sign up activity
                            FirebaseUser username = FirebaseAuth.getInstance().getCurrentUser();
                            // String displayName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                            List<AuthUI.IdpConfig> providers = Arrays.asList(
                                    new AuthUI.IdpConfig.EmailBuilder().build(),
                                    new AuthUI.IdpConfig.PhoneBuilder().build(),
                                    new AuthUI.IdpConfig.GoogleBuilder().build());
//                                  new AuthUI.IdpConfig.GitHubBuilder().build());
//                                  new AuthUI.IdpConfig.FacebookBuilder().build(),
//                                  new AuthUI.IdpConfig.TwitterBuilder().build());
                            if (username == null) {
                                // Start sign in/sign up activity
                                startActivityForResult(
                                        AuthUI.getInstance()
                                                .createSignInIntentBuilder()
                                                .setAvailableProviders(providers)
                                                .setLogo(R.drawable.newfiebig)
                                                .build(),
                                        554
                                );
                            }
                        }
                    });
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment;
        final SharedPreferences sharedPref = this.getSharedPreferences("accessCodes", Context.MODE_PRIVATE);

        if (id == R.id.nav_home) {
            fragment = new HomeFragment();
            displaySelectedFragment(fragment);
        } else if (id == R.id.nav_gallery) {
            fragment = new GalleryFragment();
            displaySelectedFragment(fragment);
        } else if (id == R.id.nav_videos) {
            fragment = new VideosFragment();
            displaySelectedFragment(fragment);
        } else if (id == R.id.nav_settings) {
            fragment = new SettingsFragment();
            displaySelectedFragment(fragment);
        } else if (id == R.id.nav_chat) {
            if (sharedPref.getString("accessCode", "").equals("Teecee2020")) {
                Log.i("AccessCode", "Valid");
                fragment = new ChatRoomFragment();
                displaySelectedFragment(fragment);
            } else {
                Log.i("AccessCode", "Invalid");
                ViewGroup view = findViewById(android.R.id.content);
                Snackbar mySnackbar = Snackbar.make(view, "Restricted Access", Snackbar.LENGTH_LONG);
                mySnackbar.show();
                mySnackbar.setAction("Enter or Request a code", new AccessCodeListener());
            }
        } else if (id == R.id.upload_file) {
            fragment = new UploadFileFragment();
            displaySelectedFragment(fragment);
        } else if (id == R.id.nav_visit_us) {
            //Open URL on click of Visit Us
            Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(NavigationDrawerConstants.SITE_URL));
            startActivity(urlIntent);
        } else if (id == R.id.nav_share) {
            //Display Share Via dialogue
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType(NavigationDrawerConstants.SHARE_TEXT_TYPE);
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, NavigationDrawerConstants.SHARE_TITLE);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, NavigationDrawerConstants.SHARE_MESSAGE);
            startActivity(Intent.createChooser(sharingIntent, NavigationDrawerConstants.SHARE_VIA));
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Loads the specified fragment to the frame
     *
     * @param fragment
     */
    private void displaySelectedFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }

    /**
     * Get current love(s) from our firebase database ( No limit on the love <3 )
     */
    private void getTeeceeLoves(final int newLoves) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Loves");
        myRef.keepSynced(true);

        // Read from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                try {
                    Long value = dataSnapshot.getValue(Long.class);
                    int currentLoves = Math.toIntExact(value);
                    int totalNewLoveCount = currentLoves + newLoves;
                    addNewLoves(totalNewLoveCount);
                } catch (Exception e) {
                    // This exception covers the case where we don't have any loves in the database yet. Wont happen very often
                    // but lets cover our bases.
                    int currentLoves = 1;
                    int totalNewLoveCount = currentLoves + newLoves;
                    addNewLoves(totalNewLoveCount);
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Error", "Failed to read value.", error.toException());
            }
        });
    }

    /**
     * Adds love(s) to the database to keep track of teecee love points ( No limit on the love <3 )
     */
    private void addNewLoves(int totalNewLoveCount) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Loves");
        Log.i("totalNewLoveCount", String.valueOf(totalNewLoveCount));
        myRef.setValue(totalNewLoveCount);
        ViewGroup view = findViewById(android.R.id.content);
        Snackbar.make(view, "Current Teecee Loves: " + totalNewLoveCount,
                Snackbar.LENGTH_LONG).show();
    }


    public class AccessCodeListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Fragment fragment = null;
            fragment = new SettingsFragment();
            displaySelectedFragment(fragment);
            Log.i("Access Code", "Access code requested");
        }
    }
}

