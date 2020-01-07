package com.josephpaulmckenzie.iloveteeceememories.fragments;

import android.content.ClipData;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
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

public class HomeFragment extends Fragment {
    final ArrayList<String> photoList = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(NavigationDrawerConstants.TAG_HOME);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        final ImageView navBackground = root.findViewById(R.id.rotating_home_image);
        TextView textView = root.findViewById(R.id.teecee_main_heading);
        textView.setText("I Love Teecee Memories");
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference photosRef = rootRef.child("photos");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
//                    String bucket = ds.child("bucket").getValue(String.class);
//                    String contentType = ds.child("contentType").getValue(String.class);
//                    String id = ds.child("id").getValue(String.class);
//                    String md5Hash = ds.child("md5Hash").getValue(String.class);
                    String mediaLink = ds.child("imageUrl").getValue(String.class);
                    if (!mediaLink.equals("https://www.google.com/images/spin-32.gif")){
                        photoList.add(mediaLink);
                    }

//                    String name = ds.child("name").getValue(String.class);
//                    String size = ds.child("size").getValue(String.class);
//                    String timeCreated = ds.child("timeCreated").getValue(String.class);
//                    String timeUpdated = ds.child("timeUpdated").getValue(String.class);
                }

                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
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

                                });
                            }
                        }
                    }, 0, 10000); // End of your timer code.
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            photosRef.addListenerForSingleValueEvent(eventListener);
        return root;
    }
}
