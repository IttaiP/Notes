package com.moveo.notes;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewHolder extends RecyclerView.ViewHolder{

    CircleImageView imageView;
    TextView name;
    TextView description;
    View parentLayout;

    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.image);
        name = itemView.findViewById(R.id.name);
        description = itemView.findViewById(R.id.desc);
        parentLayout = itemView.findViewById(R.id.parentLayout);
    }

    public void bind(final Note item, final RecyclerAnimationAdapter.OnItemClickListener listener){
        itemView.setOnClickListener(v -> listener.onItemClick(item));
    }
}