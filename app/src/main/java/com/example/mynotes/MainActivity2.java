package com.example.mynotes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity  {
    private RecyclerView recyclerView;
    private static final String TAG = "MainActivity2";
    String field="priority";
    Query.Direction direction;
    Adapter adapter;
    List<Notes> notesList;


    public static FirebaseFirestore db= FirebaseFirestore.getInstance();

    private ListenerRegistration noteListener;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    CollectionReference collectionReference= db.collection(uid);

    ItemTouchHelper itemTouchHelper;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater= getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.add:
                Log.i("Item selected","Add new note");
                Intent intent = new Intent(MainActivity2.this,MainActivity3.class);
                startActivity(intent);


//            case R.id.settings:
//                Log.i("Item selected","Settings");
//                return true;

            case R.id.sort:
                Log.i("Item selected","Sort");
                return true;

            case R.id.priority:
                Log.i("Item selected","Sort-Priority");
                field="priority";
                itemTouchHelper.attachToRecyclerView(null);
                recyclerView.setAdapter(null);
                setRecyclerView();
                return true;

            case R.id.date:
                Log.i("Item selected","Sort-Date");
                field = "date";
                itemTouchHelper.attachToRecyclerView(null);
                recyclerView.setAdapter(null);
                setRecyclerView();
                return true;

            case R.id.title:
                Log.i("Item selected","Sort-Title");
                field="title";
                itemTouchHelper.attachToRecyclerView(null);
                recyclerView.setAdapter(null);
                setRecyclerView();
                return true;

            case R.id.help:
                Log.i("Item selected","Help");
                Intent emailIntent= new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                //mail to protocol that lets you mail using client installed on your device
                String[] to={"neetibisht919@gmail.com"};
                emailIntent.putExtra(Intent.EXTRA_EMAIL,to);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT,"My Notes App");
                emailIntent.setType("message/rfc822");
                //specification for email
                Intent chooser=Intent.createChooser(emailIntent,"Send Email");
                startActivity(chooser);
                return true;

            case R.id.logOut:
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Confirm Log out")
                        .setMessage("Are you sure you want to log out ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity2.this," Successfully Logged Out",Toast.LENGTH_SHORT).show();
                                MainActivity.mAuth.signOut();
                                Intent intent= new Intent(MainActivity2.this,MainActivity.class);
                                Log.i("Item selected","Log out");
                                startActivity(intent);

                            }
                        })
                        .setNegativeButton("No",null)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteListener=collectionReference.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!= null){
                    Toast.makeText(MainActivity2.this,"Error!",Toast.LENGTH_SHORT).show();
                    Log.d("TAG",error.toString());
                    return;
                }

                setRecyclerView();
            }
        });
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        recyclerView=findViewById(R.id.recyclerView);

    }


    private void setRecyclerView() {
        if(field.equals("priority")){
            direction=Query.Direction.DESCENDING;
        }else {
            direction=Query.Direction.ASCENDING;
        }
        notesList=new ArrayList<>();

        collectionReference
                .orderBy(field,direction)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            Notes note= documentSnapshot.toObject(Notes.class);
                            note.setDocumentID(documentSnapshot.getId());
                            notesList.add(note);
                        }
                        adapter= new Adapter(notesList);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        itemTouchHelper=new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {

                            @Override
                            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                                return false;
                            }

                            @Override
                            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                                new AlertDialog.Builder(MainActivity2.this)
                                        .setIcon(android.R.drawable.ic_menu_delete)
                                        .setTitle("Confirm delete")
                                        .setCancelable(false)
                                        .setMessage("Are you sure you want to delete this note ?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
//                                                adapter.deleteNote(viewHolder.getAdapterPosition());

                                                Log.i("neetiii",String.valueOf(viewHolder.getAdapterPosition()));

                                                Notes noteDel=notesList.get(viewHolder.getAdapterPosition());
                                                collectionReference.document(noteDel.getDocumentID()).delete();
                                                notesList.remove(viewHolder.getAdapterPosition());
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                                                itemTouchHelper.attachToRecyclerView(null);
                                                recyclerView.setAdapter(null);
                                                setRecyclerView();
                                            }
                                        }).show();

                                adapter.notifyDataSetChanged();
                            }
                        });
                        itemTouchHelper.attachToRecyclerView(recyclerView);

                        adapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
                            @Override
                            public void onItemClick( int position) {
                                Notes oldNotes=notesList.get(position);
                                String selectedDocID=oldNotes.getDocumentID();

                                Intent intent = new Intent(MainActivity2.this,MainActivity4.class);
                                intent.putExtra("selectedDocID",selectedDocID);
                                startActivity(intent);
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity2.this,"Error!",Toast.LENGTH_SHORT).show();
                        Log.d(TAG,e.toString());
                    }
                });

    }


    private boolean doubleBackToExitPressedOnce;
    private Handler mHandler = new Handler();

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) { mHandler.removeCallbacks(mRunnable); }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            this.finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please press back again to exit", Toast.LENGTH_SHORT).show();

        mHandler.postDelayed(mRunnable, 2000);
    }

}

//    @Override
//    protected void onResume() {
//        super.onResume();
//        recyclerView.getAdapter().notifyDataSetChanged();
//    }

//    @Override
//    public void onBackPressed() {
//        int counter=0;
//
//        this.finishAffinity();
//    }

//    boolean doubleBackToExitPressedOnce = false;
//
//    @Override
//    public void onBackPressed() {
//        if (doubleBackToExitPressedOnce) {
//            this.finishAffinity();
//            return;
//        }
//
//        this.doubleBackToExitPressedOnce = true;
//        Toast.makeText(this, "Please press back again to exit", Toast.LENGTH_SHORT).show();
//
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                doubleBackToExitPressedOnce=false;
//            }
//        }, 2000);



