package com.josephpaulmckenzie.iloveteeceememories.fragments;

import android.app.ActivityManager;
import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.josephpaulmckenzie.iloveteeceememories.MainActivity;
import com.josephpaulmckenzie.iloveteeceememories.R;
import com.josephpaulmckenzie.iloveteeceememories.constants.NavigationDrawerConstants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.MODE_PRIVATE;
import static com.firebase.ui.auth.ui.email.RegisterEmailFragment.TAG;

public class HomeFragment extends Fragment {
    private static final boolean VERBOSE = true;
    private FirebaseAnalytics mFirebaseAnalytics;
    final ArrayList<String> photoList = new ArrayList<>();




    @Override
    public void onStart() {
        super.onStart();

        // Store our shared preference
        SharedPreferences sp = getActivity().getSharedPreferences("OURINFO", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("active", true);
        ed.apply();
        ed.commit();
        Log.i("DDDDDD","start");

    }

    @Override
    public void onStop() {
        super.onStop();
        // Store our shared preference
        SharedPreferences sp = getActivity().getSharedPreferences("OURINFO", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("active", false);
        ed.apply();
        ed.commit();
        Log.i("DDDDDD","Stop");

    }


    @Override
    public void onResume() {
        super.onResume();
        // Store our shared preference
        SharedPreferences sp = getActivity().getSharedPreferences("OURINFO", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("active", true);
        ed.apply();
        ed.commit();
        Log.i("DDDDDD","Stop");


    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(NavigationDrawerConstants.TAG_HOME);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
//
//        mFirebaseAnalytics.setCurrentScreen(this, "HomeScreen", null /* class override */);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
                mFirebaseAnalytics.setCurrentScreen(getActivity(), "HomeScreen", null /* class override */);

        final View root = inflater.inflate(R.layout.fragment_home, container, false);

        final ImageView navBackground = root.findViewById(R.id.rotating_home_image);
        TextView textView = root.findViewById(R.id.teecee_main_heading);
        textView.setText("I Love Teecee Memories");
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference photosRef = rootRef.child("image");


        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    String bucket = ds.child("bucket").getValue(String.class);
//                    String contentType = ds.child("contentType").getValue(String.class);
//                    String id = ds.child("id").getValue(String.class);
//                    String md5Hash = ds.child("md5Hash").getValue(String.class);
                    String mediaLink = ds.child("mediaUrl").getValue(String.class);
                    if (!mediaLink.contains("spin-32.gif")) {
                        photoList.add(mediaLink);
                    }
                    refreshImage(root,navBackground);
                }

            }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            photosRef.addListenerForSingleValueEvent(eventListener);
        return root;
    }


    public void refreshImage(final View root, final ImageView navBackground) {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            boolean appInForeground = getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED);
                            if (appInForeground == true) {
                                Random randomGenerator = new Random();
                                int index = randomGenerator.nextInt(photoList.size());
                                String item = photoList.get(index);
                                Log.i("Loading image", item);
                                Glide.with(root)
                                        .load(item)
                                        .apply(new RequestOptions()
                                                .fitCenter()
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        )
                                        .into(navBackground);
                            }
                        }

                    });
                }
            }
        }, 0, 10000); // End of your timer code.
    }

}
