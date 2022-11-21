package com.radhe.facebookclone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.iammert.library.readablebottombar.ReadableBottomBar;
import com.radhe.facebookclone.Fragmentss.HomeFragments;
import com.radhe.facebookclone.Fragmentss.PostFragment;
import com.radhe.facebookclone.Fragmentss.ProfileFragment;
import com.radhe.facebookclone.Fragmentss.SearchFragment;
import com.radhe.facebookclone.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      //  binding.toolbar.setVisibility(View.GONE);
        transaction.replace(R.id.content, new HomeFragments());
        transaction.commit();

        binding.readableBottomBar.setOnItemSelectListener(new ReadableBottomBar.ItemSelectListener() {
            @Override
            public void onItemSelected(int i) {

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch (i) {
                    case 0:
                       // binding.toolbar.setVisibility(View.GONE);
                        transaction.replace(R.id.content, new HomeFragments());
                        break;

                    case 2:
                        // binding.toolbar.setVisibility(View.GONE);
                        transaction.replace(R.id.content, new PostFragment());
                        break;

                    case 3:
                        // binding.toolbar.setVisibility(View.GONE);
                        transaction.replace(R.id.content, new SearchFragment());
                        break;


                    case 4:
                        // binding.toolbar.setVisibility(View.GONE);
                        transaction.replace(R.id.content, new ProfileFragment());
                        break;

                }
                transaction.commit();
            }
        });

    }
}