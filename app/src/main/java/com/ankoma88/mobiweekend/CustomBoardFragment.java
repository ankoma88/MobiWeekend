package com.ankoma88.mobiweekend;


import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.SearchView;

import java.util.ArrayList;

public class CustomBoardFragment extends ParentBoardFragment {
    private static final String TAG = "CustomBoardFragment";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_board, container, false);
        mGridView = (GridView) v.findViewById(R.id.gridView);
        setupAdapter();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (NavUtils.getParentActivityName(getActivity()) != null && getActivity().getActionBar()!=null) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        setupOnPhotoClick();

        return v;
    }



    @Override
    public void updateItems() {
        new FetchItemsTask().execute();
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, ArrayList<BoardItem>> {
        @Override
        protected ArrayList<BoardItem> doInBackground(Void... params) {

            Activity activity = getActivity();
            if(activity==null)
                return new ArrayList<BoardItem>();

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity);
            String queryTag = sharedPrefs.getString(FlickrFetchr.PREF_SEARCH_QUERY, "");
            String placeId = sharedPrefs.getString(FlickrFetchr.PREF_PLACE_ID, BoardPagerActivity.DEFAULT_PLACE_ID);

            return new FlickrFetchr().search(placeId, queryTag.toLowerCase());
        }

        @Override
        protected void onPostExecute(ArrayList<BoardItem> items) {
            mItems = items;
            setupAdapter();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_board_custom, menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            MenuItem searchItem = menu.findItem(R.id.menu_item_search);
            SearchView searchView = (SearchView) searchItem.getActionView();
            SearchManager searchManager = (SearchManager)getActivity()
                    .getSystemService(Context.SEARCH_SERVICE);
            ComponentName name = getActivity().getComponentName();
            SearchableInfo searchInfo = searchManager.getSearchableInfo(name);
            searchView.setSearchableInfo(searchInfo);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
            case R.id.menu_item_search:
                getActivity().onSearchRequested();
                return true;
            case R.id.menu_item_clear:
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .edit()
                        .putString(FlickrFetchr.PREF_SEARCH_QUERY, "")
                        .commit();
                updateItems();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}