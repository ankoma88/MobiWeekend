package com.ankoma88.mobiweekend;


import android.app.SearchManager;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;

public class CustomSearchActivity extends SingleFragmentActivity {
    private static final String TAG = "CustomSearchActivity";

    @Override
    protected Fragment createFragment() {
        return new CustomBoardFragment();
    }

    @Override
    protected void onNewIntent(Intent intent                                                                                                                                                                                                                                                                            ) {
        CustomBoardFragment fragment = (CustomBoardFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

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
}
