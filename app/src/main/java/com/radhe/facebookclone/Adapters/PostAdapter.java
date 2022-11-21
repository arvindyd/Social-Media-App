package com.radhe.facebookclone.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.radhe.facebookclone.CommentActivity;
import com.radhe.facebookclone.Modelss.Post;
import com.radhe.facebookclone.Modelss.Users;
import com.radhe.facebookclone.R;
import com.radhe.facebookclone.databinding.PostSampleBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.viewHolder>{

    ArrayList<Post>list;
    ArrayList<Users>usersArrayList;
    Context context;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;

    public PostAdapter(ArrayList<Post> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.post_sample,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        Post post= list.get(position);

        storage= FirebaseStorage.getInstance();
        database= FirebaseDatabase.getInstance();
        auth= FirebaseAuth.getInstance();
//        holder.imageView.setImageResource(retrive.getProfile());
//        holder.textView.setText(retrive.getName());


        Picasso.get()
                .load(post.getPostImage())
                .placeholder(R.drawable.cover)
                .into(holder.binding.postImage);
        holder.binding.bio.setText(post.getPostDescription());
        holder.binding.like.setText(post.getPostLike()+"");
        holder.binding.comment.setText(post.getCommentCount()+"");


            database.getReference().child("Users")
                    .child(post.getPostedBy()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Users users= snapshot.getValue(Users.class);
                    Picasso.get()
                            .load(users.getProfile())
                            .placeholder(R.drawable.profile)
                            .into(holder.binding.profileImage);
                    holder.binding.userName.setText(users.getUserName());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        database.getReference().child("posts")
                .child(post.getPostId())
                .child("likes")
                .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_red, 0, 0, 0);

                }else {


                    holder.binding.like.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            database.getReference().child("posts")
                                    .child(post.getPostId())
                                    .child("likes")
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    database.getReference().child("posts")
                                            .child(post.getPostId())
                                            .child("postLike")
                                            .setValue(post.getPostLike() + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_red, 0, 0, 0);
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


        holder.binding.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent= new Intent(context, CommentActivity.class);
                intent.putExtra("postId",post.getPostId());
                intent.putExtra("postedBy",post.getPostedBy());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
       PostSampleBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding= PostSampleBinding.bind(itemView);
        }
    }
}
