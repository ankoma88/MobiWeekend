package com.ankoma88.mobiweekend;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;


public class StartFragment extends Fragment {
    public static final String TAG = "StartFragment";

    public static final String INCORRECT_LOCATION = "Incorrect location";

    private static final String DIALOG_PLACE = "place";
    private static final String DIALOG_ID = "flickr_id";
    private static final int REQUEST_PLACE = 0;
    private static final int REQUEST_ID = 1;

    private String mLocation;
    private String mPlaceId;
    private String mFlickrId;

    private Button mLocationBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_start, container, false);

        ImageButton imageButton = (ImageButton) v.findViewById(R.id.fragment_start_imageButton);
        mLocationBtn = (Button) v.findViewById(R.id.fragment_start_choseLocationBtn);
        Button flickrIdBtn = (Button) v.findViewById(R.id.fragment_start_flickIdBtn);
        Button mStartBtn = (Button) v.findViewById(R.id.fragment_start_startBtn);

        imageButton.setEnabled(false);

        mLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                PlacePickerFragment dialog = new PlacePickerFragment();
                dialog.setTargetFragment(StartFragment.this, REQUEST_PLACE);
                dialog.show(fm, DIALOG_PLACE);

            }
        });

        flickrIdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                IdChooserFragment dialog = new IdChooserFragment();
                dialog.setTargetFragment(StartFragment.this, REQUEST_ID);
                dialog.show(fm, DIALOG_ID);
            }
        });

        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .edit()
                        .putString(FlickrFetchr.PREF_PLACE_ID, mPlaceId)
                        .putString(FlickrFetchr.PREF_FLICKR_ID,mFlickrId)
                        .commit();

                Intent i = new Intent(getActivity(), BoardPagerActivity.class);
                startActivity(i);
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_PLACE) {
            mLocation = (String) data.getSerializableExtra(PlacePickerFragment.EXTRA_PLACE_INPUT);
            mLocationBtn.setText(mLocation.toUpperCase());
            new FetchItemsTask().execute();
        }
        if (requestCode == REQUEST_ID) {
            mFlickrId = data.getStringExtra(IdChooserFragment.EXTRA_ID_INPUT);
            Log.i(TAG, "Flickr_id in StartFragment: " + mFlickrId);
        }
    }


    private class FetchItemsTask extends AsyncTask<Void, Void, Place> {
        @Override
        protected Place doInBackground(Void... params) {

            Activity activity = getActivity();
            if (activity == null) return new Place();

            return new FlickrFetchr().searchLocation(mLocation);
        }

        @Override
        protected void onPostExecute(Place place) {
            mPlaceId = place.getPlaceId();
            Log.i(TAG, "Place_id in StartFragment: " + mPlaceId);
            if (mPlaceId.equals(INCORRECT_LOCATION)) {
                mLocationBtn.setText(INCORRECT_LOCATION);
            } else {
                String[] result = place.getPlaceFullName().split(",");
                String shownLocation = result[0] +", "+ result[result.length-1];
                mLocationBtn.setText(shownLocation);

            }
        }
    }

}
