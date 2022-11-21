package com.radhe.facebookclone.Fragmentss;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.radhe.facebookclone.Adapters.SearchSampleAdapter;
import com.radhe.facebookclone.Modelss.Users;
import com.radhe.facebookclone.R;
import com.radhe.facebookclone.databinding.FragmentSearchBinding;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

   FragmentSearchBinding binding;
   ArrayList<Users>list =new ArrayList<>();
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage= FirebaseStorage.getInstance();
        database= FirebaseDatabase.getInstance();
        auth= FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentSearchBinding.inflate(inflater, container, false);

        SearchSampleAdapter adapter= new SearchSampleAdapter(list,getContext());
        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext());
        binding.rvSearch.setLayoutManager(layoutManager);
        binding.rvSearch.setAdapter(adapter);

        database.getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot:snapshot.getChildren()){

                    Users users= dataSnapshot.getValue(Users.class);
                    users.setUserId(dataSnapshot.getKey());

                    if (!dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getUid())){
                        list.add(users);
                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return  binding.getRoot();
    }
}