package com.example.whatsappclone.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.ChatDetailsActivity;
import com.example.whatsappclone.Models.Users;
import com.example.whatsappclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.net.ContentHandler;
import java.util.ArrayList;

/**This whole java class is for holding User information such as User profile pic, User name, and the last
 *message. This class is the backend for the sample_show_user xml.
 */

//extends Recycler View
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    ArrayList<Users> list; //List of User data
    Context context;

    //constructor for user adapter which can be referenced in other java classes
    public UserAdapter(ArrayList<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    //generated using android studio getters,setters and constructor option
    //Inflates a new viewholder in sample_show_user xml file
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.sample_show_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    //It is used to bind realtime data with the textviews/information
    //db related work
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users users = list.get(position); //Gets the items according to their positions
        Picasso.get().load(users.getProfilePic()).placeholder(R.drawable.profile).into(holder.image);//gets the profile pic from users
        holder.profileName.setText(users.getUserName());//gets and sets the username from the users.(chatfragment one)

        //Gets the messages from firebase children to show the last message
        FirebaseDatabase.getInstance().getReference().child("chats")
                .child(FirebaseAuth.getInstance().getUid() + users.getUserId()) //firebase id and users id(recievers?)
                .orderByChild("textTime")
                .limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                         if(snapshot.hasChildren()){
                             for(DataSnapshot snapshot1 : snapshot.getChildren()){
                                 holder.lastMessage.setText(snapshot1.child("userMessage").getValue().toString());
                             }
                         }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatDetailsActivity.class);
                intent.putExtra("UserID",users.getUserId()); //Data(ID) is sent to the chatDetailsActivity activity (not yet recieved)
                intent.putExtra("UserProfile",users.getProfilePic());
                intent.putExtra("Username",users.getUserName());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    //Main thing- binds each of the elements in sample_show_user xml according to their ID's.
    //Creates a recycler view class
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image; //Profile pic
        TextView profileName; //Name
        TextView lastMessage; //The last message

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.profile_image);
            profileName = itemView.findViewById(R.id.profname);
            lastMessage = itemView.findViewById(R.id.lastmes);//all of their ID's.
        }
    }
}
