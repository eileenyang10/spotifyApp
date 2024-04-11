package com.example.spotifyapp;// GamesTabFragment.java
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.*;

import com.example.spotifyapp.R;
import com.google.android.material.tabs.TabLayout;

public class GamesTabFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_games_tab, container, false);

        ViewPager viewPager = view.findViewById(R.id.viewPager);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);

        viewPager.setAdapter(new GamesPagerAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    private class GamesPagerAdapter extends FragmentPagerAdapter {

        public GamesPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new GameFragment();
                case 1:
                    return new GameFragment2();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2; // number of games
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Guess Playlist";
                case 1:
                    return "Guess Artist";
                default:
                    return null;
            }
        }
    }
}