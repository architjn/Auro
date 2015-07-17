package com.architjn.acjmusicplayer.elements.adapters;

//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentStatePagerAdapter;
//
//import com.architjn.acjmusicplayer.ui.layouts.fragments.AlbumsFragment;
//import com.architjn.acjmusicplayer.ui.layouts.fragments.ArtistsFragment;
//import com.architjn.acjmusicplayer.ui.layouts.fragments.GenresFragment;
//import com.architjn.acjmusicplayer.ui.layouts.fragments.SongsFragment;
//
//public class ViewPagerAdapter extends FragmentStatePagerAdapter {
//
//    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
//    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created
//
//
//    // Build a Constructor and assign the passed Values to appropriate values in the class
//    public ViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
//        super(fm);
//
//        this.Titles = mTitles;
//        this.NumbOfTabs = mNumbOfTabsumb;
//
//    }
//
//    //This method return the fragment for the every position in the View Pager
//    @Override
//    public Fragment getItem(int position) {
//
//        if (position == 0) { // if the position is 0 we are returning the First tab
//            return new SongsFragment();
//        } else if (position == 1) {
//            return new AlbumsFragment();
//        } else if (position == 2) {
//            return new ArtistsFragment();
//        } else {
//            return new GenresFragment();
//        }
//
//    }
//
//    // This method return the titles for the Tabs in the Tab Strip
//
//    @Override
//    public CharSequence getPageTitle(int position) {
//        return Titles[position];
//    }
//
//    // This method return the Number of tabs for the tabs Strip
//
//    @Override
//    public int getCount() {
//        return NumbOfTabs;
//    }
//}

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFrag(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}