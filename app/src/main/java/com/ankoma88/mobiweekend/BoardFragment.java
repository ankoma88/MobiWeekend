package com.ankoma88.mobiweekend;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;


public class BoardFragment extends ParentBoardFragment {
    private static final String TAG = "BoardFragment";

    public static Fragment newInstance(int category, String place_id) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_BOARD_CATEGORY, category);
        args.putString(EXTRA_BOARD_PLACE_ID, place_id);
        BoardFragment fragment = new BoardFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void setupMcategory() {
        int tab = getArguments().getInt(EXTRA_BOARD_CATEGORY);
        switch (tab) {
            case 0:
                mCategory = BoardPagerActivity.TAB_NATURE.toLowerCase(); break;
            case 1:
                mCategory = BoardPagerActivity.TAB_CITY.toLowerCase(); break;
            case 2:
                mCategory = BoardPagerActivity.TAB_CUSTOM.toLowerCase(); break;
            case 3:
                mCategory = BoardPagerActivity.TAB_MY.toLowerCase(); break; //TODO setup make and download own photo
            default:
                mCategory = BoardPagerActivity.TAB_NATURE.toLowerCase(); break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_board, container, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (NavUtils.getParentActivityName(getActivity()) != null && getActivity().getActionBar()!=null) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
        mGridView = (GridView) v.findViewById(R.id.gridView);

        setupAdapter();

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
            String flickrId = sharedPrefs.getString(FlickrFetchr.PREF_FLICKR_ID, BoardPagerActivity.DEFAULT_FLICKR_ID);

            if (mCategory.equals(BoardPagerActivity.TAB_CUSTOM.toLowerCase()) ) {
                mCategory = queryTag.toLowerCase();
            } else if (mCategory.equals(BoardPagerActivity.TAB_MY.toLowerCase())) {
                return new FlickrFetchr().searchMyPhotos(flickrId);
            }

            return new FlickrFetchr().search(placeId, mCategory);
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
        inflater.inflate(R.menu.fragment_board, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                    return true;
                }
            case R.id.menu_item_custom:
                Intent i = new Intent(getActivity(), CustomSearchActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


}