package com.example.acer.diplo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v7.widget.PopupMenu;




import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class new_note extends AppCompatActivity
        implements PopupMenu.OnMenuItemClickListener {
    private Button btnCreate;
    private EditText etTitle, etContent;

    private FirebaseAuth fAuth;
    private DatabaseReference fNotesDatabase;

    private String noteID ="no";
    private Menu mainMenu;

    private boolean isExist;



    public void backhome(View view) {
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        this.finish();
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);

        popup.setOnMenuItemClickListener(this);
            popup.inflate(R.menu.notes_menu);
        popup.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note);

        try {
            noteID = getIntent().getStringExtra("noteId");

            //Toast.makeText(this, noteID, Toast.LENGTH_SHORT).show();

            if (!noteID.trim().equals("")) {
                isExist = true;
            } else {
                isExist = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        btnCreate = (Button) findViewById(R.id.done);
        etTitle = (EditText) findViewById(R.id.add_title);
        etContent = (EditText) findViewById(R.id.content);

      //  getSupportActionBar().setDisplayShowHomeEnabled(true);
      //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fAuth = FirebaseAuth.getInstance();
        fNotesDatabase = FirebaseDatabase.getInstance().getReference().child("Notes").child(fAuth.getCurrentUser().getUid());

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = etTitle.getText().toString().trim();
                String content = etContent.getText().toString().trim();

                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content))
                    CreateNote(title, content);
                else {
                    Snackbar.make(view, "Fill empty fields", Snackbar.LENGTH_SHORT).show();
                }

            }
        });

        putData();
    }



    private void putData() {

        if (isExist) {
            fNotesDatabase.child(noteID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("title") && dataSnapshot.hasChild("content")) {
                        String title = dataSnapshot.child("title").getValue().toString();
                        String content = dataSnapshot.child("content").getValue().toString();

                        etTitle.setText(title);
                        etContent.setText(content);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void CreateNote(String title, String content) {
        if (fAuth.getCurrentUser() != null) {

            if (isExist) {
                // UPDATE A NOTE
                Map updateMap = new HashMap();
                updateMap.put("title", etTitle.getText().toString().trim());
                updateMap.put("content", etContent.getText().toString().trim());
                updateMap.put("timestamp", ServerValue.TIMESTAMP);

                fNotesDatabase.child(noteID).updateChildren(updateMap);

                Toast.makeText(this, "Note updated0", Toast.LENGTH_SHORT).show();
            } else {
                // CREATE A NEW NOTE
                final DatabaseReference newNoteRef = fNotesDatabase.push();

                final Map noteMap = new HashMap();
                noteMap.put("title", title);
                noteMap.put("content", content);
                noteMap.put("timestamp", ServerValue.TIMESTAMP);

                Thread mainThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        newNoteRef.setValue(noteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(new_note.this, "Note added to database", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(new_note.this, "ERROR: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }
                });
                mainThread.start();
            }



        } else {
            Toast.makeText(this, "USERS IS NOT SIGNED IN", Toast.LENGTH_SHORT).show();
        }
    }
    @Override

    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.notes_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

//        switch (item.getItemId()) {
//            case R.id.category:
//                Toast.makeText(this, "Clicked Category", Toast.LENGTH_SHORT).show();
//
//                finish();
//                break;
//            case R.id.delete:
//                if (isExist) {
////                    deleteNote();
//                    Toast.makeText(this, "Nothing deleted", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(this, "Nothing to delete", Toast.LENGTH_SHORT).show();
//                }
//                break;
//        }

        int id = item.getItemId();

        if(id == R.id.category){
            Toast.makeText(this, "Clicked Category", Toast.LENGTH_SHORT).show();
            finish();
        }else if (id == R.id.delete){
            if(isExist){
                Toast.makeText(this, "Nothing deleted", Toast.LENGTH_SHORT).show();
//
            } else {
                Toast.makeText(this, "Nothing to delete", Toast.LENGTH_SHORT).show();
            }
//                }
        }
        super.onOptionsItemSelected(item);
        return true;
    }

    private void deleteNote() {

        fNotesDatabase.child(noteID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(new_note.this, "Note Deleted", Toast.LENGTH_SHORT).show();
                    noteID = "no";
                    finish();
                } else {
                    Log.e("NewNoteActivity", task.getException().toString());
                    Toast.makeText(new_note.this, "ERROR: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.category:
                Toast.makeText(this, "Clicked Category", Toast.LENGTH_SHORT).show();

                finish();
                break;
            case R.id.delete:
                if (isExist) {
                    deleteNote();
                    Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Nothing to delete", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return true;
    }
}