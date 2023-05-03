package uk.ac.tees.c2372619.mynotes;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class NotesActivity extends AppCompatActivity {

    EditText titleEditText, bodyEditText;
    ImageButton saveNoteButton;
    TextView pageTitleTextView;
    String title, body, docId;
    boolean isEditMode = false;
    TextView deleteNoteTextViewButton;
    TextView addressTitleTextView;

    private FusedLocationProviderClient fusedLocationClient;
    Geocoder geocoder;
    List<Address> addresses;
    String addressToSave;
    String savedAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        titleEditText = findViewById(R.id.notes_title_text_view);
        bodyEditText = findViewById(R.id.notes_body_text_view);
        saveNoteButton = findViewById(R.id.save_note_button);
        pageTitleTextView = findViewById(R.id.page_title);
        addressTitleTextView = findViewById(R.id.saved_location_text_view);
        deleteNoteTextViewButton = findViewById(R.id.delete_note_text_view_button);


        title = getIntent().getStringExtra("title");
        body = getIntent().getStringExtra("body");
        docId = getIntent().getStringExtra("docId");
        savedAddress= getIntent().getStringExtra("location");



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        geocoder = new Geocoder(this, Locale.getDefault());

        if (docId != null && !docId.isEmpty()) {
            isEditMode = true;
        }

        titleEditText.setText(title);
        bodyEditText.setText(body);
        if (isEditMode) {
            pageTitleTextView.setText("You can edit note");
            deleteNoteTextViewButton.setVisibility(View.VISIBLE);
        }

        if(savedAddress!=null){
            addressTitleTextView.setText(savedAddress);
        }
        else{
            addressTitleTextView.setText("We could not get the device location");
        }

        saveNoteButton.setOnClickListener(view -> saveNote());

        deleteNoteTextViewButton.setOnClickListener(view -> deleteNoteFromFirebase());

        checkForLocationPermissionAccess();
    }

    boolean checkForLocationPermissionAccess() {

        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {

                }
                );

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //Request for permission

            locationPermissionRequest.launch(new String[] {
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
            });


            return false;
        }
        else {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                    addressToSave = addresses.get(0).getAddressLine(0);

                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }



                                // Logic to handle location object
                            }
                        }
                    });

            return true;
        }

    }
    void saveNote(){
        String noteTitle = titleEditText.getText().toString();
        String noteBody = bodyEditText.getText().toString();
        if (noteTitle==null || noteTitle.isEmpty() ){
            titleEditText.setError("Give a title");
            return;
        }
        Note note = new Note();
        note.setTitle(noteTitle);
        note.setBody(noteBody);
        note.setTimestamp(Timestamp.now());
        note.setLocation(addressToSave);
        saveNoteToFirebase(note);


    }

    void saveNoteToFirebase(Note note){
        DocumentReference documentReference;
        if (isEditMode){
            documentReference = Utility.getCollectionForNotes().document(docId);
        }else{
            documentReference = Utility.getCollectionForNotes().document();
        }


        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Utility.showToast(NotesActivity.this,"Note successfully added");
                    finish();
                }else{
                   Utility.showToast(NotesActivity.this,"Add note unsuccessful");

                }
            }
        });

    }

    void deleteNoteFromFirebase(){
        DocumentReference documentReference;

            documentReference = Utility.getCollectionForNotes().document(docId);

            documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Utility.showToast(NotesActivity.this,"Note successfully deleted");
                    finish();
                }else{
                    Utility.showToast(NotesActivity.this,"Delete note unsuccessful");

                }
            }
        });

    }
}
