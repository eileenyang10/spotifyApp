package com.example.spotifyapp;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.spotifyapp.ScreenSlidePageFragment;

import java.util.Arrays;
import java.util.List;

/**
 * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
 * sequence.
 */
public class ScreenSlidePagerAdapter extends FragmentStateAdapter {
    private Wrapped displayWrapped = new Wrapped();
    private static final int NUM_PAGES = 3;
    public ScreenSlidePagerAdapter(@NonNull FragmentManager fm, @NonNull Lifecycle lifecycle) {
        super(fm, lifecycle);
    }

    public ScreenSlidePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public Fragment createFragment(int position) {

        Bundle args = new Bundle();
        args.putSerializable("loadWrap", displayWrapped);
        Fragment fragment;

        switch (position) {
            default:
            case 0:
                fragment = new TopArtistsFragment();
                fragment.setArguments(args);
                return fragment;
            case 1:
                fragment = new TopGenresFragment();
                fragment.setArguments(args);
                return fragment;
            case 2:
                fragment = new TopSongsFragment();
                fragment.setArguments(args);
                return fragment;
        }
    }
    public static class PageTransformer implements ViewPager2.PageTransformer {

        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;
        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0f);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well.
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1).
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0f);
            }
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }

    public void setDisplayWrapped(Wrapped displayWrapped) {
        this.displayWrapped = displayWrapped;
    }
}