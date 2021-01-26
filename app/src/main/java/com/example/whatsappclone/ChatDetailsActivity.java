package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.whatsappclone.Adapters.ChatAdapter;
import com.example.whatsappclone.Fragments.ChatsFragment;
import com.example.whatsappclone.Models.MessageModel;
import com.example.whatsappclone.databinding.ActivityChatDetailsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatDetailsActivity extends AppCompatActivity {

    ActivityChatDetailsBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();


        final String senderID = auth.getUid(); //gets the firebase id from auth section
        String recieverID = getIntent().getStringExtra("UserID");//recieved from useradapter(intent)
        String recieveUserName = getIntent().getStringExtra("Username");
        String recieveUserProfile = getIntent().getStringExtra("UserProfile");

        binding.userName.setText(recieveUserName);//sets the recieved userName to the username textview
        Picasso.get().load(recieveUserProfile).placeholder(R.drawable.profile).into(binding.profileImage);

        binding.backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatDetailsActivity.this, ChatsFragment.class);
                startActivity(intent);
            }
        });

        final ArrayList<MessageModel> messageModels = new ArrayList<>();
        final ChatAdapter chatAdapter = new ChatAdapter(messageModels, this, recieverID);
        binding.chatRecyclerView.setAdapter(chatAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(linearLayoutManager);

        final String senderRoom = senderID + recieverID;
        final String recieverRoom = recieverID + senderID;

        database.getReference().child("chats").child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageModels.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()){
                            MessageModel model = snapshot1.getValue(MessageModel.class);

                            model.setMessageId(snapshot1.getKey());

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

                String message = binding.chatText.getText().toString();
                final MessageModel model = new MessageModel(senderID, message);
                model.setTextTime(new Date().getTime());
                binding.chatText.setText("");

                database.getReference().child("chats").child(senderRoom).push()
                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        database.getReference().child("chats").child(recieverRoom).push()
                                .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });
                    }
                });
            }
        });
    }
}