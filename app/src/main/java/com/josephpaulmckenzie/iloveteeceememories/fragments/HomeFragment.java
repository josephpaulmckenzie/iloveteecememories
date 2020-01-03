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
        Boolean internetStatus = ((MainActivity) getActivity()).connectionStatus();

        if (internetStatus) {
            // We have an active connection to the big ol cloud so we can serve images from somewhere out there.
            // Currently we are using Firebase because I already know AWS S3 storage and authentication (cognito/iam)

            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference photosRef = rootRef.child("photos");
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        String bucket = ds.child("bucket").getValue(String.class);
                        String contentType = ds.child("contentType").getValue(String.class);
                        String id = ds.child("id").getValue(String.class);
                        String md5Hash = ds.child("md5Hash").getValue(String.class);
                        String mediaLink = ds.child("mediaLink").getValue(String.class);
                        photoList.add(mediaLink);
                        String name = ds.child("name").getValue(String.class);
                        String size = ds.child("size").getValue(String.class);
                        String timeCreated = ds.child("timeCreated").getValue(String.class);
                        String timeUpdated = ds.child("timeUpdated").getValue(String.class);
                    }
                    Random randomGenerator = new Random();
                    int index = randomGenerator.nextInt(photoList.size());
                    String item = photoList.get(index);

                    Glide.with(root)
                        .load(item)
                        .apply(new RequestOptions()
                                .fitCenter()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                        )
                        .into(navBackground);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            photosRef.addListenerForSingleValueEvent(eventListener);
        } else {

            // We don't have access to that big ol cloud so we will show so images that we have locally in the app.
            // Option to considering adding. In the case of no internet we can display a message
            Glide.with(root)
                    .load(R.drawable.teeceee)
                    .apply(new RequestOptions()
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                    )

                    .into(navBackground);
        }
        // Inflate the layout for this fragment
        return root;
    }
}
