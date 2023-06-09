package uk.ac.tees.c2372619.mynotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText emailEditText,passwordEditText;
    Button loginButton;
    ProgressBar progressBar;
    TextView createAccountButtonTextView;
    TextView forgotPasswordTextView;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.progress_bar);
        createAccountButtonTextView = findViewById(R.id.create_account_text_view_button);
        forgotPasswordTextView = findViewById(R.id.forgot_password);

        loginButton.setOnClickListener(view -> loginUser());
        createAccountButtonTextView.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this,UserAccountActivity.class)));
        forgotPasswordTextView.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this,ResetPassword.class)));
        sharedPreferences  = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        emailEditText.setText(sharedPreferences.getString("emailAddress",""));
    }

    void loginUser(){
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        boolean isValidated =validateData(email,password);
        if (!isValidated){
            return;
        }

        loginAccountInFirebase(email,password);

    }

    void loginAccountInFirebase(String email,String password){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        emailEditText.clearFocus();
        passwordEditText.clearFocus();
        changeInProgress(true);
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if (task.isSuccessful()){

                    if (firebaseAuth.getCurrentUser().isEmailVerified()){

                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        myEdit.putString("emailAddress", email);
                        myEdit.apply();


                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();

                    }else{
                        Utility.showToast(LoginActivity.this, "Email not verified, Please verify your email");
                    }


                }else{
                    Utility.showToast(LoginActivity.this, task.getException().getLocalizedMessage());
                }

            }
        });


    }

    void changeInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email,String password){

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email is invalid");
            return false;
        }
        if (password.length()<6){
            passwordEditText.setError("Password length invalid");
            return false;
        }
        return true;
    }
}