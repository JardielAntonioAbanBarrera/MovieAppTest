package com.example.moviedb.ui.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.moviedb.R;
import com.example.moviedb.ui.Fragments.AboutFragment;
import com.example.moviedb.ui.Fragments.MovieFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // BottonNavigateView
        BottomNavigationView mBottomNav = findViewById(R.id.navigation);
        mBottomNav.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.menu_home:
                    selectedFragment = MovieFragment.newInstance();
                    break;
                case R.id.menu_about:
                    selectedFragment = AboutFragment.newInstance();
                    break;
            }

            fragmentView(selectedFragment);
            return true;
        });
        fragmentView(MovieFragment.newInstance());
    }

    private void fragmentView(Fragment Fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, Fragment);
        transaction.commitNow();
    }
}