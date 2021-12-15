package com.moveo.notes;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class ListFrag extends Fragment {
    List<Note> notesList;
    View view;
    RecyclerAnimationAdapter adapter;
    NotesApp app;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.list_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        app = (NotesApp) getActivity().getApplication();
        notesList = app.info.noteList;
        initRecyclerView();
    }

    public void initRecyclerView(){
        adapter = new RecyclerAnimationAdapter(getActivity(), notesList,
                item -> {
                    Intent intent = new Intent(getActivity(), NewNote.class);
                    intent.putExtra("id", item.id);
                    intent.putExtra("index",app.info.noteList.indexOf(item));
                    startActivity(intent);
                });
        androidx.recyclerview.widget.RecyclerView recyclerView = view.findViewById(R.id.myList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

}