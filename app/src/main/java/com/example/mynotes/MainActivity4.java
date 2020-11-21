package com.example.mynotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;


public class MainActivity4 extends AppCompatActivity {
    private EditText textTitle;
    private EditText textDescription;
    private NumberPicker priorityPicker;
    private TextView textDate;
    String selectedDocID;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    CollectionReference collectionReference= MainActivity2.db.collection(uid);

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.delete_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.delete:
                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_baseline_delete_24)
                        .setTitle("Confirm Delete")
                        .setMessage("Are you sure you want to delete this note?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                collectionReference.document(selectedDocID).delete();
                                Toast.makeText(MainActivity4.this," Note Deleted",Toast.LENGTH_SHORT).show();
                                Intent intent= new Intent(MainActivity4.this,MainActivity2.class);
                                Log.i("Item selected","Log out");
                                startActivity(intent);

                            }
                        })
                        .setNegativeButton("No",null)
                        .show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        textTitle= findViewById(R.id.textViewTitle);
        textDescription=findViewById(R.id.textDescription);
        textDate=findViewById(R.id.textViewdate);

        priorityPicker=findViewById(R.id.priorityPicker);
        priorityPicker.setMinValue(0);
        priorityPicker.setMaxValue(10);

        Intent intent=getIntent();
        selectedDocID = intent.getStringExtra("selectedDocID");
        Log.d("intent",selectedDocID);

        DocumentReference oldNoteRef = collectionReference.document(selectedDocID);

        oldNoteRef.get()
               .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                   @Override
                   public void onSuccess(DocumentSnapshot documentSnapshot) {
                       if(documentSnapshot.exists()){
                           Log.d("snapshot","it exists");

                           Notes oldNote= documentSnapshot.toObject(Notes.class);
                           oldNote.setDocumentID(documentSnapshot.getId());

                           textTitle.setText(oldNote.getTitle());
                           textDate.setText(oldNote.getDate());
                           textDescription.setText(oldNote.getDescription());
                           priorityPicker.setValue(oldNote.getPriority());

                       }else {
                           Toast.makeText(MainActivity4.this,"Document does not exist",Toast.LENGTH_SHORT).show();
                           Intent intent = new Intent(MainActivity4.this, MainActivity2.class);
                           startActivity(intent);
                       }

                   }
               })
               .addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Intent intent = new Intent(MainActivity4.this , MainActivity2.class);
                       startActivity(intent);
                   }
               });

        textDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                collectionReference.document(selectedDocID).update("description",String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }




    public void updateNote(View v) {
        String title = textTitle.getText().toString();
        String date = textDate.getText().toString();
        String description = textDescription.getText().toString();
        int priority = priorityPicker.getValue();

        Notes note = new Notes(title, description, priority, date);
        collectionReference.document(selectedDocID).update("description",description);
        collectionReference.document(selectedDocID).update("priority",priority);
        collectionReference.document(selectedDocID).update("title",title)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity4.this, "Note Updated", Toast.LENGTH_SHORT).show();

                    }
                });
    }

}