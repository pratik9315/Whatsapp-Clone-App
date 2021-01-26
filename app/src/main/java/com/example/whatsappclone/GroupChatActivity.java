package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.whatsappclone.Adapters.ChatAdapter;
import com.example.whatsappclone.Models.MessageModel;
import com.example.whatsappclone.databinding.ActivityChatDetailsBinding;
import com.example.whatsappclone.databinding.ActivityGroupChatBinding;
import com.example.whatsappclone.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class GroupChatActivity extends AppCompatActivity {

    ActivityGroupChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());//inflates the view?
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        binding.backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupChatActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final ArrayList<MessageModel> messageModels = new ArrayList<>();

        final String senderID = FirebaseAuth.getInstance().getUid();
        binding.userName.setText("FRIENDS CHAT");

        final ChatAdapter chatAdapter = new ChatAdapter(messageModels, this);
        binding.chatRecyclerView.setAdapter(chatAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(linearLayoutManager);

        firebaseDatabase.getReference().child("Group Chats")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageModels.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            MessageModel model = dataSnapshot.getValue(MessageModel.class);
                            messageModels.add(model);
                        }
                        chatAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(binding.chatText.getText().toString().isEmpty()){
                    binding.chatText.setError("Empty Message?");
                    return;
                }

                final String message = binding.chatText.getText().toString();
                final MessageModel model = new MessageModel(senderID, message);
                model.setTextTime(new Date().getTime());
                binding.chatText.setText("");

                firebaseDatabase.getReference().child("Group Chats").push().setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });

            }
        });
    }
}