package uk.ac.tees.c2372619.mynotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class UserAccountActivity extends AppCompatActivity {

    EditText emailEditText,passwordEditText,confirmPasswordEditText;
    Button createAccountButton;
    ProgressBar progressBar;
    TextView loginButtonTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
        createAccountButton = findViewById(R.id.create_account_button);
        progressBar = findViewById(R.id.progress_bar);
        loginButtonTextView = findViewById(R.id.login_text_view_button);

        createAccountButton.setOnClickListener(view -> createAccount());
        loginButtonTextView.setOnClickListener(view -> finish());

    }

    void createAccount(){
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        boolean isValidated =validateData(email,password,confirmPassword);
        if (!isValidated){
            return;
        }

        createAccountInFirebase(email,password);
    }

    void createAccountInFirebase(String email,String password){
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(UserAccountActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if (task.isSuccessful()){
                    Utility.showToast(UserAccountActivity.this, "Successfully created account,Check email to verify");
                    firebaseAuth.getCurrentUser().sendEmailVerification();
                    firebaseAuth.signOut();
                    finish();
                }else{

                    Utility.showToast(UserAccountActivity.this,task.getException().getLocalizedMessage());
                    Toast.makeText(UserAccountActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    void changeInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            createAccountButton.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            createAccountButton.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email,String password, String confirmPassword){

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email is invalid");
            return false;
        }
        if (password.length()<6){
            passwordEditText.setError("Password length invalid");
            return false;
        }
        if (!password.equals(confirmPassword)){
            confirmPasswordEditText.setError("Password not matched");
            return false;

        }
        return true;
    }
}