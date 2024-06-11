package com.example.notes_f202166080_v1;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton btnAddNewNote;


    RecyclerView rvNotes;
    ItemTouchHelper itemTouchHelper;

    NotesAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        init();

        btnAddNewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View vi= LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.add_new_note_dialog_design , null);

                TextView tvtimestamp = vi.findViewById(R.id.tvtimestamp);
                TextInputEditText etTitle = vi.findViewById(R.id.ettitle);
                TextInputEditText etcontent = vi.findViewById(R.id.etcontent);

                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

                Date date = new Date();

                tvtimestamp.setText(formatter.format(date));




                AlertDialog.Builder  addNoteDialog =
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Creating New Note")
                                .setView(vi)
                                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String title =  Objects.requireNonNull(etTitle.getText()).toString().trim();
                                        String content = Objects.requireNonNull(etcontent.getText()).toString().trim();

                                        HashMap<String, Object> data = new HashMap<>();
                                        data .put("title",title);
                                        data.put("Content",content);
                                        data.put("timestamp",tvtimestamp.getText().toString());
                                        FirebaseDatabase.getInstance()
                                                .getReference()
                                                .child("Notes")
                                                .push()
                                                .setValue(data)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(MainActivity.this , "Notes Saved successfully ",Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(MainActivity.this , e.getMessage() , Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                addNoteDialog.create();
                addNoteDialog.show();
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void init()
    {
        btnAddNewNote = findViewById(R.id.btnAddNewNote);

        rvNotes = findViewById(R.id.rvNotes);
        itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rvNotes);

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Notes");


        FirebaseRecyclerOptions<Note> options =
                new FirebaseRecyclerOptions.Builder<Note>()
                        .setQuery(query, Note.class)
                        .build();

        adapter = new NotesAdapter(this
                ,options);
        rvNotes.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,itemTouchHelper.RIGHT | itemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAbsoluteAdapterPosition();
            Note removedUtem = adapter.getItem(position);


            //adapter.removeItem(position);
            DatabaseReference itemRef = FirebaseDatabase.getInstance()
                    .getReference()
                    .child(removedUtem.getId());
                     itemRef.removeValue();
        }
    };
}
