package com.josephpaulmckenzie.iloveteeceememories.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.josephpaulmckenzie.iloveteeceememories.MainActivity;
import com.josephpaulmckenzie.iloveteeceememories.R;
import com.josephpaulmckenzie.iloveteeceememories.constants.NavigationDrawerConstants;

import org.w3c.dom.Text;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;
import android.view.inputmethod.InputMethodManager;

public class SettingsFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getActivity().setTitle(NavigationDrawerConstants.TAG_SETTINGS);





        // Will retrieve our saved data from shared prefs
//        SharedPreferences sharedPref = getActivity().getSharedPreferences("loves", Context.MODE_PRIVATE);
//        int ifAdded = sharedPref.getInt("loves", 100);
//        Log.i("!!!!!!!", String.valueOf(ifAdded));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_settings, container, false);
        final SharedPreferences sharedPref = getActivity().getSharedPreferences("loves", Context.MODE_PRIVATE);
        final int currentLoveIntervals = sharedPref.getInt("loves", 100);

        final TextView textView = root.findViewById(R.id.loves);
        textView.setHint("Current Loves per clicks: " + currentLoveIntervals);
        final View mButton = root.findViewById(R.id.submit_github);
//================ Hide Virtual Key Board When  Clicking==================//


//======== Hide Virtual Keyboard =====================//
// Not going to use yet
//
//        textView.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start,
//                                          int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start,
//                                      int before, int count) {
//                Log.i("!!!!!!!!", String.valueOf(start));
//                Log.i("!!!!!!!!", String.valueOf(before));
//                Log.i("!!!!!!!!", String.valueOf(count));
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//Log.i("W@@@@@@@", String.valueOf(editable));
//            }
//        });
        mButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {

                        SharedPreferences pref = getActivity().getSharedPreferences("loves", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();

                        String  input = textView.getText().toString();
                        editor.putInt("loves", Integer.parseInt(input));
                        editor.apply();
                        editor.commit();
                        // Collapses the virtual keyboard after hitting submit button
                        // If you click done on the keyboard it will collapse then you can click submit
                        // However if you click the button without hitting done the keyboard stays up
                        try  {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                        } catch (Exception e) {
                            Log.i("Error","When closing keyboard ");
                        }
                        textView.setText("");
                        int currentLoveIntervals = sharedPref.getInt("loves", 100);
                        textView.setHint("Current Loves per clicks: " + currentLoveIntervals);
                    }
                });


        // Inflate the layout for this fragment
        return root;
    }



}
