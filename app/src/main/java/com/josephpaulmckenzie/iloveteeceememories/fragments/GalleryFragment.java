package com.josephpaulmckenzie.iloveteeceememories.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.ListResult;
//import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Random;

public class GalleryFragment extends Fragment {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(NavigationDrawerConstants.TAG_GALLERY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        final ImageView navBackground = root.findViewById(R.id.image_gallery);

        Boolean internetStatus = ((MainActivity) getActivity()).connectionStatus();

        if (internetStatus) {

            // We have Internet so we can serve
            // Inflate the layout for this fragment
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
                            Glide.with(root).load(teeceeimage)
                                    .thumbnail(0.5f)
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
            Glide.with(root)
                    .load(R.drawable.teeceee)
                    .apply(new RequestOptions().fitCenter())
                    .into(navBackground);

        }

        // Inflate the layout for this fragment
        return root;
    }

}
