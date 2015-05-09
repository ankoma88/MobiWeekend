package com.ankoma88.mobiweekend;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {
    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return BoardFragment.newInstance(position, BoardPagerActivity.DEFAULT_PLACE_ID);
    }

    @Override
    public int getCount() {
        return 4;
    }
}
