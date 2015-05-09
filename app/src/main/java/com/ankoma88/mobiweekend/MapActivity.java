package com.ankoma88.mobiweekend;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapActivity extends Activity {
    private static final String TAG = "MapActivity";

    public static final String EXTRA_MAP_LATITUDE = "mobiweekend.MAP_LATITUDE";
    public static final String EXTRA_MAP_LONGITUDE = "mobiweekend.MAP_LONGITUDE";
    public static final String EXTRA_MAP_TITLE = "mobiweekend.MAP_TITLE";

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        String latitude = getIntent().getStringExtra(EXTRA_MAP_LATITUDE);
        String longitude = getIntent().getStringExtra(EXTRA_MAP_LONGITUDE);
        String title = getIntent().getStringExtra(EXTRA_MAP_TITLE);
        Double latitude1 = Double.parseDouble(latitude);
        Double longitude1 = Double.parseDouble(longitude);

        Log.i(TAG, "mLatitude: " + latitude1);
        Log.i(TAG, "mLongitude: " + longitude1);

        GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        LatLng latLng = new LatLng(latitude1, longitude1);
        if (map != null) {
            map.addMarker(new MarkerOptions().position(latLng).title(title));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
            map.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);
        }

    }
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    Intent intent = new Intent(this, BoardPagerActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    this.finish();
                    return true;


                default:
                    return super.onOptionsItemSelected(item);
            }
        }


}