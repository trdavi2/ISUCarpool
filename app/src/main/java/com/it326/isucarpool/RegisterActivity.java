package com.it326.isucarpool;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
    private Button reg;

    private FirebaseAuth fb;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fb = FirebaseAuth.getInstance();

        firstName = (AutoCompleteTextView) findViewById(R.id.firstName);
        lastName = (AutoCompleteTextView) findViewById(R.id.lastName);
        email = (AutoCompleteTextView) findViewById(R.id.registerEmail);
        password = (AutoCompleteTextView) findViewById(R.id.registerPassword);
        confirmPassword = (AutoCompleteTextView) findViewById(R.id.registerConfirmPassword);

        reg = (Button) findViewById(R.id.registerUserButton);
        reg.setOnClickListener(new View.OnClickListener() {
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

        password.setError(null);
        confirmPassword.setError(null);
        email.setError(null);

        String pwd = password.getText().toString();
        String cpwd = confirmPassword.getText().toString();
        String em = email.getText().toString();
        boolean cancel = false;
        View focusView = null;


        // Check to make sure the password and confirmPassword are the same
        if(!pwd.equals(cpwd))
        {
            password.setError("The two passwords must match.");
            confirmPassword.setError("The two passwords must match.");
            cancel = true;
            focusView = password;
        }
        if(!em.contains("@ilstu.edu"))
        {
            email.setError("The email must be an ilstu.edu email.");
            cancel = true;
            focusView = email;
        }

        if(cancel)
        {
            focusView.requestFocus();
        }
        else
        {
            User user = new User(firstName.getText().toString(), lastName.getText().toString(), email.getText().toString());


            fb.createUserWithEmailAndPassword(em, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    //showProgress(false);

                    if(!task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this, "Could not register", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        User user = new User(firstName.getText().toString(), lastName.getText().toString(), email.getText().toString());
                        String uid = fb.getCurrentUser().getUid();
                        fb.getCurrentUser().sendEmailVerification();
                        FirebaseDatabase.getInstance().getReference("users").child(uid).child("profile").setValue(user);
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }

        /*
        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);

            fb.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    showProgress(false);

                    if(!task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this, "Could not register", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
        */
    }




}
