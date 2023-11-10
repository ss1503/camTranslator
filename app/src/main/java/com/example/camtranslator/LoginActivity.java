package com.example.camtranslator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    //Firebase vars
    private FirebaseAuth mAuth;

    //component vars
    private EditText email;
    private EditText password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //init firebase auth
        mAuth = FirebaseAuth.getInstance();

        //init components
        email = (EditText) findViewById(R.id.editText_email);
        password = (EditText) findViewById(R.id.editText_password);

    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null)
        {
            Intent sourceIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(sourceIntent);
        }
    }

    /**
     * on click for loging in into your account
     * @param view
     */
    public void login(View view)
    {
        String emailValue = email.getText().toString();
        String passwordValue = password.getText().toString();

        mAuth.signInWithEmailAndPassword(emailValue, passwordValue)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            Intent sourceIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(sourceIntent);

                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * on click for registering in into your account
     * @param view
     */
    public void Register(View view)
    {
        String emailValue = email.getText().toString();
        String passwordValue = password.getText().toString();

        mAuth.createUserWithEmailAndPassword(emailValue, passwordValue)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            Intent sourceIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(sourceIntent);

                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "Register failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}