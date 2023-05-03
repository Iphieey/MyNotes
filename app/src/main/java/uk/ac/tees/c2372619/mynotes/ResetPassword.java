package uk.ac.tees.c2372619.mynotes;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    EditText emailEditText;
    Button reset_password_btn;
    ProgressBar progressBar;
    TextView loginButtonTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        emailEditText = findViewById(R.id.email_edit_text);

        reset_password_btn = findViewById(R.id.reset_password_btn);
        progressBar = findViewById(R.id.progress_bar);
        loginButtonTextView = findViewById(R.id.login_text_view_button);

        reset_password_btn.setOnClickListener(view -> resetPassword());
        loginButtonTextView.setOnClickListener(view -> finish());

    }

    void resetPassword(){
        String email = emailEditText.getText().toString();

        boolean isValidated =validateData(email);
        if (!isValidated){
            return;
        }

        resetInFirebase(email);
    }

    void resetInFirebase(String email){
        emailEditText.clearFocus();
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Utility.showToast(ResetPassword.this, "Successfully,Check email to verify");
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            finish();
                         }
                        else{
                            changeInProgress(false);
                            Utility.showToast(ResetPassword.this,task.getException().getLocalizedMessage());
                            Toast.makeText(ResetPassword.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();

                        }
                    }
                });


    }

    void changeInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            reset_password_btn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            reset_password_btn.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email){

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email is invalid");
            return false;
        }

        return true;
    }
}