package com.josephpaulmckenzie.iloveteeceememories;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.josephpaulmckenzie.iloveteeceememories.constants.NavigationDrawerConstants;
import com.josephpaulmckenzie.iloveteeceememories.fragments.ChatRoomFragment;
import com.josephpaulmckenzie.iloveteeceememories.fragments.GalleryFragment;
import com.josephpaulmckenzie.iloveteeceememories.fragments.HomeFragment;
import com.josephpaulmckenzie.iloveteeceememories.fragments.SettingsFragment;
import com.josephpaulmckenzie.iloveteeceememories.fragments.VideosFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private View navHeader;
    public boolean connectionStatus;


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

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    // Firebase check for connection to database
//        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
//
//        connectedRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                boolean connected = snapshot.getValue(Boolean.class);
//                if (connected) {
//                    connectionStatus = true;
//                    Log.d("Internet is ", "Connected");
//                } else {
//                    Log.d("Internet is ", "Disconnected");
//                    connectionStatus = false;
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.w("Cancelled", "Listener was cancelled");
//            }
//        });

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

        // Loading profile image
        // Attempt to first load image from url and if no connection to cloud storage will get locally
        ImageView profileImage = navHeader.findViewById(R.id.profileImage);
        Glide.with(this)
                .load(NavigationDrawerConstants.PROFILE_URL)
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

        //Select Home by default
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
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
        Log.i("@@@@@@@", sharedPref.getString("accessCode", ""));

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

        } else if (id == R.id.nav_share) {
            //Display Share Via dialogue
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType(NavigationDrawerConstants.SHARE_TEXT_TYPE);
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, NavigationDrawerConstants.SHARE_TITLE);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, NavigationDrawerConstants.SHARE_MESSAGE);
            startActivity(Intent.createChooser(sharingIntent, NavigationDrawerConstants.SHARE_VIA));

        } else if (id == R.id.nav_visit_us) {
            //Open URL on click of Visit Us
            Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(NavigationDrawerConstants.SITE_URL));
            startActivity(urlIntent);
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


    // Gets our connection status for the database back from firebase
    ;

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
                Long value = dataSnapshot.getValue(Long.class);
                int currentLoves = Math.toIntExact(value);
                int totalNewLoveCount = currentLoves + newLoves;
                addNewLoves(totalNewLoveCount);
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

