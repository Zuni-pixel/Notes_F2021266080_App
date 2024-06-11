package com.example.notes_f202166080_v1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.internal.FallbackServiceBroker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class NotesAdapter extends FirebaseRecyclerAdapter<Note, NotesAdapter.NotesViewHolder> {

    Context parent ;
    public NotesAdapter(Context context,@NonNull FirebaseRecyclerOptions<Note> options) {
        super(options);
        parent = context;

    }


    @Override
    protected void onBindViewHolder(@NonNull NotesViewHolder notesViewHolder, @SuppressLint("RecyclerView") int i, @NonNull Note note) {

        notesViewHolder.tvTitle.setText(note.getTitle());
        notesViewHolder.tvContent.setText(note.getContent());
        notesViewHolder.tvTimeStamp.setText(note.getTimestamp());
        notesViewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                                AlertDialog.Builder confirmationdialog =
                                        new AlertDialog.Builder(parent)
                                                .setTitle("Confirmation")
                                                        .setMessage("Doyou really want to delete")
                                                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {

                                                                        getRef(i)
                                                                                .removeValue()
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {
                                                                                        Toast.makeText(parent, "Deleted", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                })
                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        Toast.makeText(parent, e.getMessage(), Toast.LENGTH_SHORT).show();

                                                                                    }
                                                                                });
                                                                    }
                                                                })

                                                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialog, int which) {

                                                                            }
                                                                        });
                                                                        confirmationdialog.create();
                                                                        confirmationdialog.show();
        }
    });


        notesViewHolder.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View vii = LayoutInflater.from(parent)
                        .inflate(R.layout.add_new_note_dialog_design,null);

                TextInputEditText etTitle = vii.findViewById(R.id.ettitle);
                TextInputEditText etContent = vii.findViewById(R.id.etcontent);
                TextView tvTimestamp = vii.findViewById(R.id.tvtimestamp);

                etTitle.setText(note.getTitle());
                etContent.setText(note.getContent());

                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/mm/yyy hh:mm:ss");
                        Date  date = new Date();
                tvTimestamp.setText(formatter.format(date));


                AlertDialog.Builder updateDialog = new AlertDialog.Builder(parent)
                        .setTitle("Update Note")
                        .setView(vii)
                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String title = etTitle.getText().toString().trim();
                                String content = etContent.getText().toString().trim();
                                String timestamp = tvTimestamp.getText().toString();

                                HashMap<String,Object> data = new HashMap<>();
                                data.put("title",title);
                                data.put("Content",content);
                                data.put("TimeStamp",timestamp);
                                FirebaseDatabase .getInstance()
                                        .getReference(String.valueOf(i))
                                        .updateChildren(data)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(parent, "Updated", Toast.LENGTH_SHORT).show();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                Toast.makeText(parent,e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });


                            }

                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                updateDialog.create();
                updateDialog.show();

            }
        });


    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vi = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_note_item_design,parent, false);


        return new NotesViewHolder(vi);
    }

    public class NotesViewHolder extends RecyclerView.ViewHolder{


        TextView tvTitle , tvContent,tvTimeStamp;
        ImageView ivEdit,ivDelete;
        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvcontent);
            tvTitle = itemView.findViewById(R.id.tvtitle);
            tvTimeStamp = itemView.findViewById(R.id.tvtimestamp);
            ivEdit= itemView.findViewById(R.id.ivedit);
            ivDelete= itemView.findViewById(R.id.ivdelete);
        }
    }
};
