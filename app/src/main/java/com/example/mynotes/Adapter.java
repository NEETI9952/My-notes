package com.example.mynotes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import java.util.List;


public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private static List<Notes> notesList;
    private OnItemClickListener listener;


    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();

    CollectionReference collectionReference= MainActivity2.db.collection(uid);


    public Adapter(List<Notes> notesList) {
        Adapter.notesList =notesList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Notes notes = notesList.get(position);
        holder.textTitle.setText(notes.getTitle());
        holder.textPriority.setText("Priority:" + notes.getPriority());
        holder.textDescription.setText(notes.getDescription());
        holder.textDate.setText(notes.getDate());

    }

    @Override
    public int getItemCount() {
        return notesList.size() ;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView textTitle;
        TextView textDescription;
        TextView textDate;
        TextView textPriority;
        LinearLayout linearLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.noteTitle);
            textDescription = itemView.findViewById(R.id.noteDescription);
            textDate = itemView.findViewById(R.id.noteDate);
            textPriority = itemView.findViewById(R.id.priority);
            linearLayout = itemView.findViewById(R.id.linearlayout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position= getAdapterPosition();

                    if(position!=RecyclerView.NO_POSITION ) {
                        listener.onItemClick(position);
                    }
                }

            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener=listener;
    }

    public void deleteNote(int position){
        Notes noteDel=notesList.get(position);
        collectionReference.document(noteDel.getDocumentID()).delete();
    }

    public void swap( List<Notes> newNotesList)
    {
        notesList.clear();
        notesList.addAll(newNotesList);
        notifyDataSetChanged();
    }


}
