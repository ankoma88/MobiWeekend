package com.ankoma88.mobiweekend;


import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;



public class BoardPagerActivity extends FragmentActivity implements ActionBar.TabListener {
    private static final String TAG = "BoardPagerActivity";

    private ViewPager mViewPager;
    private ActionBar mActionBar;

    public static final String DEFAULT_PLACE_ID = "5I6w0q5YUL586B8";
    public static final String DEFAULT_FLICKR_ID = "52630577@N08";
    public static final String TAB_NATURE = " Nature";
    public static final String TAB_CITY = " City";
    public static final String TAB_CUSTOM = " Custom";
    public static final String TAB_MY = " My";
    private String[] tabs = {TAB_NATURE, TAB_CITY, TAB_CUSTOM, TAB_MY};


    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_pager);

        mViewPager = (ViewPager) findViewById(R.id.boardPager);
        mActionBar = getActionBar();

        FragmentManager fm = getSupportFragmentManager();
        FragmentPagerAdapter pagerAdapter = new PagerAdapter(fm);
        mViewPager.setAdapter(pagerAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        mActionBar.setHomeButtonEnabled(true);

        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        setupTabs();


        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mActionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void setupTabs() {
        Drawable natureIcon = getResources().getDrawable(R.drawable.nature);
        Drawable cityIcon = getResources().getDrawable(R.drawable.city);
        Drawable customIcon = getResources().getDrawable(R.drawable.custom);
        Drawable myIcon = getResources().getDrawable(R.drawable.my);

        mActionBar.addTab(mActionBar.newTab().setIcon(natureIcon).setText(TAB_NATURE).setTabListener(this));
        mActionBar.addTab(mActionBar.newTab().setIcon(cityIcon).setText(TAB_CITY).setTabListener(this));
        mActionBar.addTab(mActionBar.newTab().setIcon(customIcon).setText(TAB_CUSTOM).setTabListener(this));
        mActionBar.addTab(mActionBar.newTab().setIcon(myIcon).setText(TAB_MY).setTabListener(this));
    }


    @Override
    protected void onNewIntent(Intent intent) {
        BoardFragment fragment = (BoardFragment) getSupportFragmentManager().findFragmentById(R.id.boardPager);


        System.out.println(fragment.toString());

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String queryTag = intent.getStringExtra(SearchManager.QUERY);
            Log.i(TAG, "Received a new search query tag: " + queryTag);

            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putString(FlickrFetchr.PREF_SEARCH_QUERY, queryTag)
                    .commit();

        }

        fragment.updateItems();
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}
