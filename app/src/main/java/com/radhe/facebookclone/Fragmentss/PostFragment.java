package com.radhe.facebookclone.Fragmentss;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.radhe.facebookclone.Modelss.Post;
import com.radhe.facebookclone.Modelss.Users;
import com.radhe.facebookclone.R;
import com.radhe.facebookclone.databinding.FragmentPostBinding;
import com.squareup.picasso.Picasso;

import java.util.Date;


public class PostFragment extends Fragment {

    FragmentPostBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage firebaseStorage;
    Uri uri;
    ProgressDialog dialog;



    public PostFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth= FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();
        firebaseStorage= FirebaseStorage.getInstance();
        dialog =new ProgressDialog(getContext());
        dialog.setTitle("Uploding");
        dialog.setMessage("plese wait");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
     binding =FragmentPostBinding.inflate(inflater, container, false);



     binding.postDescription.addTextChangedListener(new TextWatcher() {
         @Override
         public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

         }

         @Override
         public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

             String desc = binding.postDescription.getText().toString();
             if (!desc.isEmpty()){

                 binding.post.setBackgroundDrawable(ContextCompat.getDrawable(getContext(),R.drawable.btn_follow));
                 binding.post.setTextColor(getContext().getResources().getColor(R.color.white));
                 binding.post.setEnabled(true);
             }
             else {
                 binding.post.setBackgroundDrawable(ContextCompat.getDrawable(getContext(),R.drawable.followign_btn));
                 binding.post.setTextColor(getContext().getResources().getColor(R.color.gray));
                 binding.post.setEnabled(false);
             }
         }

         @Override
         public void afterTextChanged(Editable editable) {

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
                            .into(binding.profile);
                   binding.name.setText(users.getUserName());



                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.glarryPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent= new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
             startActivityForResult(intent,5);
            }
        });

        binding.post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                      dialog.show();
                final StorageReference storageReference= firebaseStorage.getReference().child("posts")
                        .child(FirebaseAuth.getInstance().getUid())
                        .child(new Date().getTime()+"");

                storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                       storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                           @Override
                           public void onSuccess(Uri uri) {

                               Post post= new Post();
                              post.setPostImage(uri.toString());
                              post.setPostedBy(FirebaseAuth.getInstance().getUid());
                              post.setPostDescription(binding.postDescription.getText().toString());
                              post.setPostedAt(new Date().getDate());

                              database.getReference().child("posts")
                                      .push()
                                      .setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                  @Override
                                  public void onSuccess(Void unused) {
                                      dialog.dismiss();
                                      Toast.makeText(getContext(), "Data is Saved in Realtime Database", Toast.LENGTH_SHORT).show();

                                  }
                              }).addOnFailureListener(new OnFailureListener() {
                                  @Override
                                  public void onFailure(@NonNull Exception e) {

                                      Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                  }
                              });

                           }
                       });
                    }
                });

            }
        });



        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==5){
            if (data!=null){

                 uri=data.getData();
               binding.upload.setImageURI(uri);
               binding.upload.setVisibility(View.VISIBLE);
            }
        }
    }
}