package uk.ac.tees.c2372619.mynotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NotesActivity extends AppCompatActivity {

    EditText titleEditText,bodyEditText;
    ImageButton saveNoteButton;
    TextView pageTitleTextView;
    String title,body,docId;
    boolean isEditMode = false;
    TextView deleteNoteTextViewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        titleEditText =findViewById(R.id.notes_title_text_view);
        bodyEditText = findViewById(R.id.notes_body_text_view);
        saveNoteButton = findViewById(R.id.save_note_button);
        pageTitleTextView = findViewById(R.id.page_title);
        deleteNoteTextViewButton =findViewById(R.id.delete_note_text_view_button);


        title = getIntent().getStringExtra("title");
        body = getIntent().getStringExtra("body");
        docId = getIntent().getStringExtra("docId");

        if (docId!=null && !docId.isEmpty()){
            isEditMode = true;
        }

        titleEditText.setText(title);
        bodyEditText.setText(body);
        if(isEditMode){
            pageTitleTextView.setText("You can edit note");
            deleteNoteTextViewButton.setVisibility(View.VISIBLE);
        }


        saveNoteButton.setOnClickListener(view -> saveNote());

        deleteNoteTextViewButton.setOnClickListener(view -> deleteNoteFromFirebase());
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
