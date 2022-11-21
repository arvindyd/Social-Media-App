package com.radhe.facebookclone.Fragmentss;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.radhe.facebookclone.Adapters.PostAdapter;
import com.radhe.facebookclone.Adapters.StoryAdapter;
import com.radhe.facebookclone.Modelss.Post;
import com.radhe.facebookclone.Modelss.Story;
import com.radhe.facebookclone.Modelss.UserStory;
import com.radhe.facebookclone.Modelss.Users;
import com.radhe.facebookclone.R;
import com.radhe.facebookclone.databinding.FragmentHomeFragmentsBinding;

import java.util.ArrayList;
import java.util.Date;


public class HomeFragments extends Fragment {

    FragmentHomeFragmentsBinding binding;
    ArrayList<Post>list=new ArrayList<>();
   ArrayList<Story>storyArrayList = new ArrayList<>();
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseStorage storage;

    public HomeFragments() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentHomeFragmentsBinding.inflate(inflater, container, false);

        binding.rvPost.showShimmerAdapter();
        database= FirebaseDatabase.getInstance();
        auth= FirebaseAuth.getInstance();
        storage= FirebaseStorage.getInstance();

        PostAdapter adapter= new PostAdapter(list,getContext());
        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext());

        binding.rvPost.setLayoutManager(layoutManager);

        database.getReference().child("posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    Post post= dataSnapshot.getValue(Post.class);
                    post.setPostId(dataSnapshot.getKey());
                    list.add(post);

                }
                binding.rvPost.setAdapter(adapter);
                binding.rvPost.hideShimmerAdapter();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Story Recycler View

        StoryAdapter storyAdapter= new StoryAdapter(storyArrayList,getContext());
        LinearLayoutManager layoutManager1= new LinearLayoutManager(getContext());
        binding.storyRV.setLayoutManager(layoutManager1);
        binding.storyRV.setNestedScrollingEnabled(false);
        binding.storyRV.setAdapter(storyAdapter);

        database.getReference().child("stories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    for (DataSnapshot storySnapshot :snapshot.getChildren()){

                        Story story= new Story();
                        story.setStoryBy(storySnapshot.getKey());
                       // story.setStoryAt(storySnapshot.child("postedBy").getValue(Long.class));

                        ArrayList<UserStory> stories =new ArrayList<>();
                        for (DataSnapshot snapshot1 :storySnapshot.child("userStories").getChildren()){

                            UserStory userStory= snapshot1.getValue(UserStory.class);
                            stories.add(userStory);
                        }
                        story.setUserStories(stories);
                        storyArrayList.add(story);
                    }
                    storyAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.addStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent= new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,7);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==7){
            if (data!=null){
                Uri uri= data.getData();
                binding.addStoryImg.setImageURI(uri);

                final StorageReference reference= storage.getReference().child("stories")
                        .child(FirebaseAuth.getInstance().getUid())
                        .child(new Date().getTime()+"");

                 reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                     @Override
                     public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                         reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                             @Override
                             public void onSuccess(Uri uri) {

                                 Story story= new Story();
                                 story.setStoryAt(new Date().getTime());

                                 database.getReference().child("stories")
                                         .child(FirebaseAuth.getInstance().getUid())
                                         .child("postedBy")
                                         .setValue(story).addOnSuccessListener(new OnSuccessListener<Void>() {
                                     @Override
                                     public void onSuccess(Void unused) {

                                         UserStory userStory= new UserStory(uri.toString(),story.getStoryAt());

                                         database.getReference().child("stories")
                                                 .child(FirebaseAuth.getInstance().getUid())
                                                 .child("userStories")
                                                 .push()
                                                 .setValue(userStory);
                                     }
                                 });

                             }
                         });

                     }
                 });


            }
        }
    }
}