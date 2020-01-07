package com.josephpaulmckenzie.iloveteeceememories.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.josephpaulmckenzie.iloveteeceememories.DatabaseUpdater;
import com.josephpaulmckenzie.iloveteeceememories.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static android.app.Activity.RESULT_OK;

public class UploadFileFragment extends Fragment {

    private static final int SIGN_IN_REQUEST_CODE = 5647457;
    private static final String TAG = "Upload File";
    final int REQUEST_IMAGE = 1;
    private FirebaseUser mFirebaseUser;
    private String mPhotoUrl;
    private String mUsername;
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
    private DatabaseReference mFirebaseDatabaseReference;
    public static String MESSAGES_CHILD;
    public static final String ANONYMOUS = "anonymous";
    public static String fileName = null;
    public static  String fileType;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Chat Room");
        mUsername = ANONYMOUS;

    }

    private void displayChatMessages() {

    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_uploadfile, container, false);


//        FirebaseUser username = FirebaseAuth.getInstance().getCurrentUser();
        //        String displayName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
//        List < AuthUI.IdpConfig > providers = Arrays.asList(
//                new AuthUI.IdpConfig.EmailBuilder().build(),
//                new AuthUI.IdpConfig.PhoneBuilder().build(),
//                new AuthUI.IdpConfig.GoogleBuilder().build());
        //                new AuthUI.IdpConfig.FacebookBuilder().build(),
        //                new AuthUI.IdpConfig.TwitterBuilder().build());


//        if (username == null) {
//            // Start sign in/sign up activity
//            startActivityForResult(
//                    AuthUI.getInstance()
//                            .createSignInIntentBuilder()
//                            .setAvailableProviders(providers)
//                            .build(),
//                    554
//            );
//        } else {
            Toast.makeText(getActivity(),
                    "Let's upload some bits of love, " + FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getDisplayName(),
                    Toast.LENGTH_LONG)
                    .show();

            Button select_image = root.findViewById(R.id.select_image);
        select_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    startActivityForResult(intent, REQUEST_IMAGE);
                }
            });
            return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mUsername = mFirebaseUser.getDisplayName();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final Uri uri = data.getData();

                    try {
                        String decodedUrl = URLDecoder.decode(uri.getPath(), "UTF-8");
                        Log.i("!!!!!!!!",decodedUrl);

                       fileType = decodedUrl.substring(decodedUrl.lastIndexOf("/") + 1,decodedUrl.lastIndexOf(":"));
                       Log.i("!!@@@@!@!!@!@!@",fileType);
                        MESSAGES_CHILD = fileType;
                        fileName = decodedUrl.substring(decodedUrl.lastIndexOf("/") + 1);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                    DatabaseUpdater tempMessage = new DatabaseUpdater(null, mUsername, mPhotoUrl,
                            LOADING_IMAGE_URL);
                    mFirebaseDatabaseReference.child(MESSAGES_CHILD).push()
                            .setValue(tempMessage, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError,
                                                       DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        final String key = databaseReference.getKey();
                                        final StorageReference storageReference =
                                                FirebaseStorage.getInstance()
                                                        .getReference("newfieMedia/")
                                                        //.getReference(mFirebaseUser.getUid()) // Can be used if we want to store to a folder for user who uploads
                                                        .child(fileType)
                                                        .child(fileName);

                                        try {

                                            final ImageView imageView2 = getActivity().findViewById(R.id.imageView2);
                                            final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(uri.toString()));
                                            imageView2.setImageBitmap(bitmap);

                                            Button upload_button = getActivity().findViewById(R.id.upload_button);
                                            upload_button.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    putImageInStorage(storageReference, uri, key);
                                                    Toast.makeText(getActivity(),
                                                            "Uploading " + fileName +  " to Firebase, Please wait",
                                                            Toast.LENGTH_LONG)
                                                            .show();
                                                }
                                            });
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Log.w(TAG, "Unable to write message to database.",
                                                databaseError.toException());
                                    }
                                }
                            });
                }
            }
        }
    }

    private void putImageInStorage(StorageReference storageReference, Uri uri, final String key) {
        storageReference.putFile(uri).addOnCompleteListener(getActivity(),
                new OnCompleteListener < UploadTask.TaskSnapshot > () {
                    @Override
                    public void onComplete(@NonNull Task < UploadTask.TaskSnapshot > task) {
                        if (task.isSuccessful()) {
                            task.getResult().getMetadata().getReference().getDownloadUrl()
                                    .addOnCompleteListener(getActivity(),
                                            new OnCompleteListener < Uri > () {
                                                @Override
                                                public void onComplete(@NonNull Task < Uri > task) {
                                                    if (task.isSuccessful()) {
                                                        DatabaseUpdater friendlyMessage =
                                                                new DatabaseUpdater(null, mUsername, mPhotoUrl,
                                                                        task.getResult().toString());
                                                        mFirebaseDatabaseReference.child(MESSAGES_CHILD).child(key)
                                                                .setValue(friendlyMessage);
                                                    }
                                                    Toast.makeText(getActivity(),
                                                            "Uploaded " + fileName +  " to Firebase",
                                                            Toast.LENGTH_LONG)
                                                            .show();
                                                    final ImageView imageView2 = getActivity().findViewById(R.id.imageView2);
                                                    imageView2.setImageResource(R.drawable.ic_newfoundland_head);



                                                }
                                            });
                        } else {
                            Log.w(TAG, "Image upload task was not successful.",
                                    task.getException());
                        }
                    }
                });
    }
}