package com.ankoma88.mobiweekend;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

public class PhotoPageFullSizeActivity extends FragmentActivity {
    private static final String TAG = "FullSizeActivity";

    private ArrayList<BoardItem> mItems;
    ViewPager mViewPager;
    private int pos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);

        mItems = (ArrayList<BoardItem>) getIntent().getSerializableExtra(ParentBoardFragment.EXTRA_BOARD_ITEMS);
        pos = getIntent().getIntExtra(ParentBoardFragment.EXTRA_BOARD_POSITION,0);

        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                BoardItem boardItem = mItems.get(position);


                String url = boardItem.getPhotoPageFullSize();
                return PhotoPageFullSizeFragment.newInstance(url, boardItem);
            }

            @Override
            public int getCount() {
                return mItems.size();
            }
        });

        mViewPager.setCurrentItem(pos);


    }




}
