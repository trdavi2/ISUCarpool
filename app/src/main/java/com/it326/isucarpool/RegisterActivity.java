package com.it326.isucarpool;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.it326.isucarpool.model.User;

public class RegisterActivity extends AppCompatActivity
{
    private AutoCompleteTextView firstName;
    private AutoCompleteTextView lastName;
    private AutoCompleteTextView email;
    private AutoCompleteTextView password;
    private AutoCompleteTextView confirmPassword;
    private RadioButton male;
    private RadioButton female;
    private AutoCompleteTextView address;
    private AutoCompleteTextView city;
    private AutoCompleteTextView state;
    private Button register;

    private FirebaseAuth fb;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fb = FirebaseAuth.getInstance();

        firstName = (AutoCompleteTextView) findViewById(R.id.firstName);
        lastName = (AutoCompleteTextView) findViewById(R.id.lastName);
        email = (AutoCompleteTextView) findViewById(R.id.registerEmail);
        password = (AutoCompleteTextView) findViewById(R.id.registerPassword);
        confirmPassword = (AutoCompleteTextView) findViewById(R.id.registerConfirmPassword);
        male = (RadioButton) findViewById(R.id.male);
        female = (RadioButton) findViewById(R.id.female);
        address = (AutoCompleteTextView) findViewById(R.id.address);
        city = (AutoCompleteTextView) findViewById(R.id.city);
        state = (AutoCompleteTextView) findViewById(R.id.state);
        register = (Button) findViewById(R.id.registerUserButton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private void registerUser()
    {
        if(fb == null)
        {
            Toast.makeText(RegisterActivity.this, "Null", Toast.LENGTH_SHORT).show();
            return;
        }
        TextView gender = (TextView) findViewById(R.id.gender);
        boolean cancel = false;
        View focusView = null;
        //Set Errors to null
        firstName.setError(null);
        lastName.setError(null);
        password.setError(null);
        confirmPassword.setError(null);
        email.setError(null);
        gender.setError(null);
        address.setError(null);
        city.setError(null);
        state.setError(null);

        final String firstNameText = firstName.getText().toString();
        final String lastNameText = lastName.getText().toString();
        String passwordText = password.getText().toString();
        String confirmPasswordText = confirmPassword.getText().toString();
        final String emailText = email.getText().toString();
        final String addressText = address.getText().toString();
        final String cityText = city.getText().toString();
        final String stateText = state.getText().toString();
        String genderText = "";

        //Check to make sure a gender is selected
        if(male.isChecked()) genderText = "Male";
        else if(female.isChecked()) genderText = "Female";
        else{ gender.setError("Please select a gender."); cancel = true; focusView = gender; }

        if(firstNameText.isEmpty())
        {
            firstName.setError("All fields must filled out.");
            focusView = firstName;
            cancel = true;
        }
        if(lastNameText.isEmpty())
        {
            lastName.setError("All fields must filled out.");
            focusView = lastName;
            cancel = true;
        }
        if(addressText.isEmpty())
        {
            address.setError("All fields must filled out.");
            focusView = address;
            cancel = true;
        }
        if(cityText.isEmpty())
        {
            city.setError("All fields must filled out.");
            focusView = city;
            cancel = true;
        }
        if(stateText.isEmpty())
        {
            state.setError("All fields must filled out.");
            focusView = state;
            cancel = true;
        }
        if(emailText.isEmpty())
        {
            email.setError("All fields must filled out.");
            focusView = email;
            cancel = true;
        }
        if(passwordText.isEmpty())
        {
            password.setError("All fields must filled out.");
            focusView = password;
            cancel = true;
        }
        if(confirmPasswordText.isEmpty())
        {
            confirmPassword.setError("All fields must filled out.");
            focusView = confirmPassword;
            cancel = true;
        }

        if(!cancel) {
            // Check to make sure the password and confirmPassword are the same
            if (!passwordText.equals(confirmPasswordText)) {
                password.setError("The two passwords must match.");
                confirmPassword.setError("The two passwords must match.");
                cancel = true;
                focusView = password;
            }
            //Check to make sure email is an ISU email
            if (!emailText.contains("@ilstu.edu")) {
                email.setError("The email must be an ilstu.edu email.");
                cancel = true;
                focusView = email;
            }
        }

        if(cancel)
        {
            focusView.requestFocus();
        }
        else
        {
            final String fgenderText = genderText;
            fb.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this, "Could not register", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        User user = new User(firstNameText, lastNameText, emailText, fgenderText, addressText, cityText, stateText);
                        String uid = fb.getCurrentUser().getUid();
                        fb.getCurrentUser().sendEmailVerification();
                        FirebaseDatabase.getInstance().getReference("users").child(uid).child("profile").setValue(user);
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }




}
