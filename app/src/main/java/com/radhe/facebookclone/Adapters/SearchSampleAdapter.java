package com.radhe.facebookclone.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.radhe.facebookclone.Modelss.Followers;
import com.radhe.facebookclone.Modelss.Users;
import com.radhe.facebookclone.R;
import com.radhe.facebookclone.databinding.SearchSampleBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class SearchSampleAdapter extends RecyclerView.Adapter<SearchSampleAdapter.viewHolder> {

    ArrayList<Users>list;
    Context context;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;

    public SearchSampleAdapter(ArrayList<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.search_sample,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        Users users= list.get(position);
        storage= FirebaseStorage.getInstance();
        database= FirebaseDatabase.getInstance();
        auth= FirebaseAuth.getInstance();


        Picasso.get()
                .load(users.getProfile())
                .placeholder(R.drawable.userprofile)
                .into(holder.binding.followProfile);
        holder.binding.flowName.setText(users.getUserName());




        database.getReference().child("Users")
                .child(users.getUserId())
                .child("followers")
                .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    holder.binding.follow.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.followign_btn));
                    holder.binding.follow.setText("Following");
                    holder.binding.follow.setTextColor(context.getResources().getColor(R.color.gray));
                    holder.binding.follow.setEnabled(false);

                }else {



                    holder.binding.follow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Followers followers= new Followers();
                            followers. setFollowedBy(FirebaseAuth.getInstance().getUid());
                            followers.setFollowedAt(new Date().getTime());

                            database.getReference().child("Users")
                                    .child(users.getUserId())
                                    .child("followers")
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .setValue(followers).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    database.getReference().child("Users")
                                            .child(users.getUserId())
                                            .child("followerCount")
                                            .setValue(users.getFollowerCount() + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            holder.binding.follow.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.followign_btn));
                                            holder.binding.follow.setText("Following");
                                            holder.binding.follow.setTextColor(context.getResources().getColor(R.color.gray));
                                            holder.binding.follow.setEnabled(false);


                                            Toast.makeText(context, "you follower"+users.getUserName(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            });
                        }
                    });
                }
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
        SearchSampleBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            binding=SearchSampleBinding.bind(itemView);
        }
    }
}
