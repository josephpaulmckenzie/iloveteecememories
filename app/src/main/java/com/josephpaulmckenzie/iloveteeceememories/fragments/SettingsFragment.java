package com.josephpaulmckenzie.iloveteeceememories.fragments;

import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.josephpaulmckenzie.iloveteeceememories.ContactForm;
import com.josephpaulmckenzie.iloveteeceememories.MainActivity;
import com.josephpaulmckenzie.iloveteeceememories.R;
import com.josephpaulmckenzie.iloveteeceememories.constants.NavigationDrawerConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;
import static com.josephpaulmckenzie.iloveteeceememories.ContactForm.writeNewUser;

import android.view.inputmethod.InputMethodManager;

import java.util.UUID;

public class SettingsFragment extends Fragment {
    AlertDialog contactDialogModal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getActivity().setTitle(NavigationDrawerConstants.TAG_SETTINGS);
//        SharedPreferences sharedPref = getActivity().getSharedPreferences("loves", Context.MODE_PRIVATE);
//        int ifAdded = sharedPref.getInt("loves", 100);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("loves", Context.MODE_PRIVATE);
        int currentLoveIntervals = sharedPref.getInt("loves", 1);
        TextView textView = root.findViewById(R.id.loves);
        textView.setHint("Current Loves per clicks: " + currentLoveIntervals);

        // OnClickListener for requesting an access code
        contactForm(root);
        // OnClickListener for getting and setting the love amount
        updateLoves(root);
        // OnClickListener for setting any access codes
        updateAccessCode(root);
        return root;
    }

    private void updateAccessCode(View root) {
        View accessCodeSubmitButton = root.findViewById(R.id.submit_code);
        final TextView accessCode = root.findViewById(R.id.accessCode);
        final SharedPreferences sharedPref = getActivity().getSharedPreferences("loves", Context.MODE_PRIVATE);
        accessCodeSubmitButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {

                        SharedPreferences pref = getActivity().getSharedPreferences("accessCodes", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        String input = accessCode.getText().toString();
                        editor.putString("accessCode", input);
                        editor.apply();
                        editor.commit();

                        try {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                        } catch (Exception e) {
                            Log.i("Error", "When closing keyboard ");
                        }

                        Snackbar.make(view, "Your access code has been updated",
                                Snackbar.LENGTH_LONG).show();
                        accessCode.setText("");
                        accessCode.setHint("Enter access code ");
                    }
                });
    }


    public void contactForm(View root) {
        TextView requestCode = root.findViewById(R.id.requestCode);

        requestCode.setTextIsSelectable(false);
        requestCode.setFocusable(true);
        requestCode.setFocusableInTouchMode(true);

        requestCode.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        Log.i("Access Code", "Requested");
                        LayoutInflater li = LayoutInflater.from(getContext());
                        final View contactView = li.inflate(R.layout.activity_contact_form, null);

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext(), R.style.ThemeOverlay_AppCompat_Dialog_Alert);
                        alertDialogBuilder.setView(contactView);
                        contactDialogModal = alertDialogBuilder.create();
                        contactDialogModal.show();
                        contactDialogModal.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);//
                        View submitButton = contactView.findViewById(R.id.post_message);

                        submitButton.setOnClickListener(
                                new View.OnClickListener() {
                                    public void onClick(View view) {
                                        // Upon submit save all fields to a new document tree in firebase realtime database
                                        // Pop up snackbar message saying thank you message
                                        try {
                                            final TextView name = contactView.findViewById(R.id.your_name);
                                            TextView email = contactView.findViewById(R.id.your_email);
                                            TextView subject = contactView.findViewById(R.id.your_subject);
                                            TextView message = contactView.findViewById(R.id.your_message);
                                            String your_name = name.getText().toString();
                                            String your_email = email.getText().toString();
                                            String the_subject = subject.getText().toString();
                                            String the_message = message.getText().toString();

                                            saveContactForm(your_name, your_email, the_subject, the_message);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        Log.i("Submitted", "Contact");
                                    }
                                });
                    }
                });
    }

    public void updateLoves(View root) {
        View mButton = root.findViewById(R.id.submit_github);
        final SharedPreferences sharedPref = getActivity().getSharedPreferences("loves", Context.MODE_PRIVATE);
        final TextView textView = root.findViewById(R.id.loves);
        mButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {

                        SharedPreferences pref = getActivity().getSharedPreferences("loves", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();

                        String input = textView.getText().toString();
                        editor.putInt("loves", Integer.parseInt(input));
                        editor.apply();
                        editor.commit();

                        //================ Hide Virtual Key Board When  Clicking==================//
                        // Collapses the virtual keyboard after hitting submit button
                        // If you click done on the keyboard it will collapse then you can click submit
                        // However if you click the button without hitting done the keyboard stays up
                        try {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                        } catch (Exception e) {
                            Log.i("Error", "When closing keyboard ");
                        }
                        //======== Hide Virtual Keyboard =====================//

                        textView.setText("");
                        int currentLoveIntervals = sharedPref.getInt("loves", 100);
                        textView.setHint("Current Loves per clicks: " + currentLoveIntervals);
                    }
                });

    }

    public void saveContactForm(String your_name, String your_email, String the_subject, String the_message) throws JSONException {

        String uniqueId = UUID.randomUUID().toString();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Contact_Form" + "/" + uniqueId);
        JSONObject json = new JSONObject();
        json.put("your_name", your_name);
        json.put("your_email", your_email);
        json.put("the_subject", the_subject);
        json.put("the_message", the_message);
        contactDialogModal.hide();
        ContactForm contact_details = new ContactForm(your_name, your_email, the_subject, the_message);
        myRef.setValue(contact_details);

        Log.i("Contact Form", "Submitted");
        ViewGroup view = getActivity().findViewById(android.R.id.content);
        Snackbar.make(view, "Thank you, your message has been sent.",
                Snackbar.LENGTH_LONG).show();
    }
}
