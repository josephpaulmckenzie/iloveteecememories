package com.josephpaulmckenzie.iloveteeceememories;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.UUID;


@IgnoreExtraProperties
public class ContactForm {
    public String your_name;
    public String your_email;
    public String the_subject;
    public String the_message;

    public ContactForm() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public ContactForm(String your_name, String your_email, String the_subject, String the_message) {
        this.your_name = your_name;
        this.your_email = your_email;
        this.the_subject = the_subject;
        this.the_message = the_message;
    }


    public static void writeNewUser(String your_name, String your_email,
                                    String the_subject, String the_message, FirebaseDatabase database) {
    }
}
