package com.radhe.facebookclone.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.radhe.facebookclone.Modelss.Followers;
import com.radhe.facebookclone.Modelss.Users;
import com.radhe.facebookclone.R;
import com.radhe.facebookclone.databinding.FollowerSampleBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.viewHolder>{

    ArrayList<Followers>list;
    Context context;

    public FollowersAdapter(ArrayList<Followers> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.follower_sample,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        Followers followers= list.get(position);

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(followers.getFollowedBy()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Users users= snapshot.getValue(Users.class);
                Picasso.get()
                        .load(users.getProfile())
                        .placeholder(R.drawable.profile)
                        .into(holder.binding.profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

       FollowerSampleBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding= FollowerSampleBinding.bind(itemView);
        }
    }
}
