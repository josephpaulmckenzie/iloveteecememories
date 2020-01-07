package com.josephpaulmckenzie.iloveteeceememories.fragments;

        import android.content.ClipData;
        import android.net.Uri;
        import android.os.Bundle;

        import androidx.annotation.NonNull;
        import androidx.fragment.app.Fragment;

        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.MediaController;
        import android.widget.TextView;
        import android.widget.VideoView;

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

public class VideosFragment extends Fragment {
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


        getActivity().setContentView(R.layout.fragment_videos);

//        final View root = inflater.inflate(R.layout.fragment_home, container, false);
//        final ImageView navBackground = root.findViewById(R.id.rotating_home_image);
//        TextView textView = root.findViewById(R.id.teecee_main_heading);
//        textView.setText("I Love Teecee Memories");
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference photosRef = rootRef.child("video");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
//                    String bucket = ds.child("bucket").getValue(String.class);
//                    String contentType = ds.child("contentType").getValue(String.class);
//                    String id = ds.child("id").getValue(String.class);
//                    String md5Hash = ds.child("md5Hash").getValue(String.class);
                    String mediaLink = ds.child("mediaUrl").getValue(String.class);
                    if (!mediaLink.contains("spin-32.gif")){
                        photoList.add(mediaLink);
                    }

//                    String name = ds.child("name").getValue(String.class);
//                    String size = ds.child("size").getValue(String.class);
//                    String timeCreated = ds.child("timeCreated").getValue(String.class);
//                    String timeUpdated = ds.child("timeUpdated").getValue(String.class);
                }

                Random randomGenerator = new Random();
                int index = randomGenerator.nextInt(photoList.size());
                String item = photoList.get(index);
                Log.i("Loading image", item);


                VideoView videoView = getActivity().findViewById(R.id.vdVw);
//                MediaController mc = new MediaController(getContext());
//                mc.setPrevNextListeners(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //Handle next click here
//                        Log.i("SKIP AHEAD","!!!!!!");
//                    }
//                }, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Log.i("SKIP AHEAD behind","!!!!!!");
//
//                        //Handle previous click here
//                    }
//                });
//                videoView.setMediaController(mc);
                //Set MediaController  to enable play, pause, forward, etc options.
                MediaController mediaController= new MediaController(getContext());
                mediaController.setAnchorView(videoView);
                //Location of Media File
                Log.i("@@@@@@###@",item);
                Uri uri = Uri.parse(item);
                //Starting VideView By Setting MediaController and URI
                mediaController.setPrevNextListeners(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Handle next click here
                        Log.i("SKIP AHEAD","!!!!!!");
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("SKIP AHEAD behind","!!!!!!");

                        //Handle previous click here
                    }
                });
                videoView.setMediaController(mediaController);
                videoView.setVideoURI(uri);
                videoView.requestFocus();
                videoView.start();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        photosRef.addListenerForSingleValueEvent(eventListener);
        return inflater.inflate(R.layout.fragment_videos, container, false);
    }
}

