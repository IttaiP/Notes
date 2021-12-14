package com.moveo.notes;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.moveo.notes.databinding.ActivityMainBinding;
import com.moveo.notes.databinding.ListFragmentBinding;

import java.util.ArrayList;
import java.util.List;

public class ListFrag extends Fragment {
    List<Note> notesList;
    ListFragmentBinding bi;
    RecyclerAnimationAdapter adapter;
    NotesApp app;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        bi = DataBindingUtil.inflate(
                inflater, R.layout.list_fragment, container, false);
        View view = bi.getRoot();

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        app = (NotesApp) getActivity().getApplication();
        notesList = app.info.noteList;
//        bi = DataBindingUtil.setContentView(getActivity(), R.layout.list_fragment);
        initRecyclerView();

    }


    public void initRecyclerView(){
        adapter = new RecyclerAnimationAdapter(getActivity(), notesList,
                item -> {
                    Intent intent = new Intent(getActivity(), NewNote.class);
                    intent.putExtra("id", item.id);
                    intent.putExtra("index",app.info.noteList.indexOf(item));
                    startActivity(intent);
                    getActivity().finish(); // todo: check if works

                });
        bi.myList.setLayoutManager(new LinearLayoutManager(getActivity()));
        bi.myList.setAdapter(adapter);
    }

}