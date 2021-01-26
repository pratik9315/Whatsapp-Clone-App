package com.example.whatsappclone.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.whatsappclone.Adapters.UserAdapter;
import com.example.whatsappclone.Models.Users;
import com.example.whatsappclone.R;
import com.example.whatsappclone.databinding.FragmentChatsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * This java class stores the chat section of all the user chats
 */

public class ChatsFragment extends Fragment {

    FragmentChatsBinding binding;
    ArrayList<Users> list = new ArrayList<>(); //ArrayList referencing Users Data
    FirebaseDatabase database;
    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatsBinding.inflate(inflater, container, false);

        database = FirebaseDatabase.getInstance();

        UserAdapter adapter = new UserAdapter(list, getContext()); //UserAdapter constructor consisting of a List and Context
        binding.recyclerView.setAdapter(adapter); //Recycler view in the fragment_chats is set with a adapter(UserAdapter)

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(linearLayoutManager);//recyclerview exists in the fragement_chats xml

        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Users users = dataSnapshot.getValue(Users.class);//gets Users class information
                    //users.getUserId(dataSnapshot.getKey());
                    users.setUserId(dataSnapshot.getKey());//gets the snapshot of user id
                    if (!users.getUserId().equals(FirebaseAuth.getInstance().getUid())) {
                        list.add(users); //adds user chats
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