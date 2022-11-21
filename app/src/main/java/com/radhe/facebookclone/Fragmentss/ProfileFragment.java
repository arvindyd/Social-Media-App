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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.radhe.facebookclone.Adapters.FollowersAdapter;
import com.radhe.facebookclone.Modelss.Followers;
import com.radhe.facebookclone.Modelss.Users;
import com.radhe.facebookclone.R;
import com.radhe.facebookclone.databinding.FragmentProfileBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ProfileFragment extends Fragment {

  FragmentProfileBinding binding;
  Uri uri;
  FirebaseAuth auth;
  FirebaseDatabase database;
  FirebaseStorage storage;
  ArrayList<Followers>list= new ArrayList<>();

    public ProfileFragment() {
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
        binding=FragmentProfileBinding .inflate(inflater, container, false);


        FollowersAdapter adapter= new FollowersAdapter(list,getContext());
        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        binding.friendsRV.setAdapter(adapter);
        binding.friendsRV.setLayoutManager(layoutManager);

        database.getReference().child("Users")
                .child(auth.getUid())
                .child("followers")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        list.clear();
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){

                            Followers followers= dataSnapshot.getValue(Followers.class);
                            list.add(followers);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        database.getReference().child("Users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    Users users= snapshot.getValue(Users.class);
                    Picasso.get()
                            .load(users.getProfile())
                            .placeholder(R.drawable.userprofile)
                            .into(binding.profileImage);
                    binding.usersName.setText(users.getUserName());
                    binding.followers.setText(users.getFollowerCount()+"");


                    Picasso.get()
                            .load(users.getCover())
                            .placeholder(R.drawable.cover)
                            .into(binding.cover);

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.setCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent= new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,7);

            }
        });


        binding.setProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent= new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,8);

            }
        });

        return binding.getRoot();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==7){
            if (data!=null){

                uri=data.getData();
                binding.cover.setImageURI(uri);

                final StorageReference reference= storage.getReference().child("cover").child(FirebaseAuth.getInstance().getUid());

                          reference.putFile(uri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Toast.makeText(getContext(), "Storage saved", Toast.LENGTH_SHORT).show();
                                reference.getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {

                                                database.getReference().child("Users").child(auth.getUid()).child("cover").setValue(uri.toString());
                                                Toast.makeText(getContext(), "Realtime data saved", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                        });
            }
        }
        else {


            uri=data.getData();
            binding.profileImage.setImageURI(uri);


            final  StorageReference storageReference= storage.getReference().child("profile").child(FirebaseAuth.getInstance().getUid());
               storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                       Toast.makeText(getContext(), "Profile is saved in Storage", Toast.LENGTH_SHORT).show();

                       storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                           @Override
                           public void onSuccess(Uri uri) {

                               database.getReference().child("Users").child(auth.getUid()).child("profile").setValue(uri.toString());
                               Toast.makeText(getContext(), "Profile is saved in Realtime Database", Toast.LENGTH_SHORT).show();
                           }
                       });
                   }
               });
        }
    }
}