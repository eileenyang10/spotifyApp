package com.example.spotifyapp;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

public class GamesTabFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_games_tab, container, false);

        ViewPager viewPager = view.findViewById(R.id.viewPager);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);

        GamesPagerAdapter gamesPagerAdapter = new GamesPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(gamesPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    private class GamesPagerAdapter extends FragmentPagerAdapter {

        public GamesPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
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
                    return "Game 1";
                case 1:
                    return "Game 2";
                default:
                    return null;
            }
        }
    }
}