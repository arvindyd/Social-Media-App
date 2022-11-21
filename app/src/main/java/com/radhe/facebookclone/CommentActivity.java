package com.radhe.facebookclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.radhe.facebookclone.Adapters.CommentAdapter;
import com.radhe.facebookclone.Modelss.Comment;
import com.radhe.facebookclone.Modelss.Post;
import com.radhe.facebookclone.Modelss.Users;
import com.radhe.facebookclone.databinding.ActivityCommentBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class CommentActivity extends AppCompatActivity {

    ActivityCommentBinding binding;
    String postId;
    String postedBy;
    Intent intent;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ArrayList<Comment>list =new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

//        setSupportActionBar(binding.toolbar);
//        CommentActivity.this.setTitle("Comments");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database =FirebaseDatabase.getInstance();
        auth= FirebaseAuth.getInstance();


        intent= getIntent();
        postId = intent.getStringExtra("postId");
        postedBy = intent.getStringExtra("postedBy");

        database.getReference().child("posts")
                .child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Post post= snapshot.getValue(Post.class);

                Picasso.get()
                        .load(post.getPostImage())
                        .placeholder(R.drawable.cover)
                        .into(binding.post);
                binding.descrip.setText(post.getPostDescription());
                binding.like.setText(post.getPostLike()+"");
                binding.comment.setText(post.getCommentCount()+"");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.getReference().child("Users")
                .child(postedBy).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Users users= snapshot.getValue(Users.class);
                Picasso.get()
                        .load(users.getProfile())
                        .placeholder(R.drawable.userprofile)
                        .into(binding.profile);
                binding.userName.setText(users.getUserName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Comment comment= new Comment();
                comment.setCommentBody(binding.etComment.getText().toString());
                comment.setCommentedAt(new Date().getTime());
                comment.setCommentBy(FirebaseAuth.getInstance().getUid());

                database.getReference().child("posts")
                        .child(postId)
                        .child("comments")
                        .push()
                        .setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        database.getReference().child("posts")
                                .child(postId)
                                .child("commentCount").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                int comment=0;
                                if (snapshot.exists()){

                                    comment= snapshot.getValue(Integer.class);
                                }

                                database.getReference().child("posts")
                                        .child(postId)
                                        .child("commentCount")
                                        .setValue(comment +1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        binding.etComment.setText("");
                                        Toast.makeText(CommentActivity.this, "Commented", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

            }
        });


        CommentAdapter adapter = new CommentAdapter(list,this);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        binding.rvComment.setLayoutManager(layoutManager);
        binding.rvComment.setAdapter(adapter);

        database.getReference().child("posts")
                .child(postId)
                .child("comments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){

                    Comment comment= dataSnapshot.getValue(Comment.class);
                    list.add(comment);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}