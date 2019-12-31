package com.josephpaulmckenzie.iloveteeceememories.fragments;

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

import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment {

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

            FirebaseStorage storage = FirebaseStorage.getInstance("gs://joes-react-native-app.appspot.com");
            StorageReference listRef = storage.getReference().child("photos");
            listRef.listAll()
                    .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                        @Override
                        public void onSuccess(ListResult listResult) {
                            List<StorageReference> teeceelinks = listResult.getItems();
                            Random rand = new Random();
                            String firstpart = "https://firebasestorage.googleapis.com/v0/b/joes-react-native-app.appspot.com/o/photos%2F";
                            String second = String.valueOf(teeceelinks.get(rand.nextInt(teeceelinks.size()))).split("/")[4];
                            String teeceeimage = firstpart + second + "?alt=media";
                            Log.i("RRRRRRR", teeceeimage);
                            Glide.with(root)
                                    .load(teeceeimage)
                                    .apply(new RequestOptions()
                                            .fitCenter()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    )
                                    .into(navBackground);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Uh-oh, an error occurred!
                            Log.i("Error", String.valueOf(e));
                        }
                    });
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
