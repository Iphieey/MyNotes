package uk.ac.tees.c2372619.mynotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NotesActivity extends AppCompatActivity {

    EditText titleEditText,bodyEditText;
    ImageButton saveNoteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        titleEditText =findViewById(R.id.notes_title_text_view);
        bodyEditText = findViewById(R.id.notes_body_text_view);
        saveNoteButton = findViewById(R.id.save_note_button);

        saveNoteButton.setOnClickListener(view -> saveNote());
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

        saveNoteToFirebase(note);

    }

    void saveNoteToFirebase(Note note){
        DocumentReference documentReference;
        documentReference = Utility.getCollectionForNotes().document();

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
}
