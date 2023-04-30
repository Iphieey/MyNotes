package uk.ac.tees.c2372619.mynotes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

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

    }

    void changeInProgress(boolean inProgress){

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