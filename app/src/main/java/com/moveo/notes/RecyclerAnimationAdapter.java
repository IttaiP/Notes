package com.moveo.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerAnimationAdapter extends RecyclerView.Adapter<RecyclerAnimationAdapter.ViewHolder> {


    Context context;
    List<Note> notesList;
    private final OnItemClickListener listener;


    public RecyclerAnimationAdapter(Context context, List<Note> noteList, OnItemClickListener listener) {
        this.listener = listener;
        this.context = context;
        if(noteList.size()>1)
        noteList.sort((note, t1) -> note.date.compareTo(t1.date));
        this.notesList = noteList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list, null);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {

        holder.name.setText(notesList.get(i).title);
        holder.description.setText(notesList.get(i).body);
        holder.bind(notesList.get(i), listener);

//        Picasso.get().load(list.get(i).image).into(holder.imageView);

    }



    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Note item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView imageView;
        TextView name;
        TextView description;
        View parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.desc);

            parentLayout = itemView.findViewById(R.id.parentLayout);
        }

        public void bind(final Note item, final OnItemClickListener listener){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
