package uk.ac.tees.c2372619.mynotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton addNoteButton;
    RecyclerView recyclerView;
    ImageButton menuButton;
    private NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addNoteButton = findViewById(R.id.add_note_button);
        recyclerView = findViewById(R.id.recycler_view);
        menuButton = findViewById(R.id.menu_button);

        addNoteButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this,NotesActivity.class)));
        menuButton.setOnClickListener(view -> showMenu());
        setupRecyclerView();
    }
    void showMenu(){

    }
    void setupRecyclerView() {
        Query query = Utility.getCollectionForNotes().orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>().setQuery(query, Note.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(options, this);
        recyclerView.setAdapter(noteAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        noteAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        noteAdapter.notifyDataSetChanged();
    }
}