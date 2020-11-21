package com.example.mynotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity3 extends AppCompatActivity {
    private EditText textTitle;
    private EditText textDescription;
    private TextView textPriority;
    private NumberPicker prioritypicker;
    private TextView textDate;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    CollectionReference collectionReference= MainActivity2.db.collection(uid);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        textTitle= findViewById(R.id.textViewTitle);
        textDescription=findViewById(R.id.textDescription);
        textDate=findViewById(R.id.textViewdate);

        prioritypicker=findViewById(R.id.priorityPicker);
        prioritypicker.setMinValue(0);
        prioritypicker.setMaxValue(10);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String date=  sdf.format(new Date());
        textDate.setText(date);

    }

    public void addNote(View v) {
        String title = textTitle.getText().toString();
        String date = textDate.getText().toString();
        String description = textDescription.getText().toString();
        int priority = prioritypicker.getValue();

        Notes note = new Notes(title, description, priority, date);
        collectionReference.add(note)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        Toast.makeText(MainActivity3.this, "Note Added", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity3.this, MainActivity2.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity3.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d("Mainactivity3", e.toString());
                    }
                });
    }

}