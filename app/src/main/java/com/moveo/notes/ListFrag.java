package com.moveo.notes;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;

import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moveo.notes.databinding.ActivityMainBinding;
import com.moveo.notes.databinding.ListFragmentBinding;

import java.util.ArrayList;
import java.util.List;

public class ListFrag extends Fragment {
    List<Note> notesList;
    ListFragmentBinding bi;
    RecyclerAnimationAdapter adapter;
    NotesApp app;


    private ListViewModel mViewModel;

    public static ListFrag newInstance() {
        return new ListFrag();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.list_fragment, container, false);

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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ListViewModel.class);
        // TODO: Use the ViewModel
    }

    public void initRecyclerView(){
        adapter = new RecyclerAnimationAdapter(getActivity(), notesList);
        bi.myList.setLayoutManager(new LinearLayoutManager(getActivity()));
        bi.myList.setAdapter(adapter);
    }

//    List<Note> getData() {
//        List<Note> list = new ArrayList<>();
//        TypedArray imagesArray = getResources().obtainTypedArray(R.array.people_images);
//        String[] names = getResources().getStringArray(R.array.people_names);
//
//        for (int i = 0; i < imagesArray.length(); i++) {
//            Person person = new Person();
//            person.name = names[i];
//            person.image = imagesArray.getResourceId(i, -1);
//            list.add(person);
//        }
//        Collections.shuffle(list);
//
//
//        return list;
//
//    }

}