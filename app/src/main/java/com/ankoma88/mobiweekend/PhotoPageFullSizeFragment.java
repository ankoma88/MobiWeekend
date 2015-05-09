package com.ankoma88.mobiweekend;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.ByteArrayOutputStream;


public class PhotoPageFullSizeFragment extends Fragment {
    private static final String TAG = "FullSizeFragment";

    public static final String EXTRA_FS_FRAGMENT_URL = "mobiweekend.FS_URL";
    public static final String EXTRA_FS_FRAGMENT_BOARD_ITEM = "mobiweekend.FS_BOARD_ITEM";

    private String mUrl;
    private WebView mWebView;
    private BoardItem mBoardItem;

    public static Fragment newInstance(String url, BoardItem boardItem) {
        Bundle args = new Bundle();
        args.putString(EXTRA_FS_FRAGMENT_URL, url);
        args.putSerializable(EXTRA_FS_FRAGMENT_BOARD_ITEM, boardItem);
        PhotoPageFullSizeFragment fragment = new PhotoPageFullSizeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

            mUrl = getArguments().getString(EXTRA_FS_FRAGMENT_URL);
            mBoardItem = (BoardItem) getArguments().getSerializable(EXTRA_FS_FRAGMENT_BOARD_ITEM);

        Log.i(TAG, "mUrl " + mUrl);
        Log.i(TAG, "mBoardItem " + mBoardItem);
    }


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_page_full_size, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (NavUtils.getParentActivityName(getActivity()) != null && getActivity().getActionBar()!=null) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        mWebView = (WebView) v.findViewById(R.id.webViewFS);
        mWebView.setBackgroundColor(Color.parseColor("#74b7e0"));
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        String data="<html><head><style>img{max-width: 100%; width:auto; height: auto;}</style></head>"
                +"<body><center><src=\""+mUrl+"\" /></center></body></html>";
        mWebView.loadData(data, "text/html", null);
        mWebView.loadUrl(mUrl);
        Log.i(TAG, "url to photo: "+mUrl);
        return v;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_page_fs, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            case R.id.menu_item_flickr_page:
                if (mBoardItem!=null) {
                    Uri photoPageUri = Uri.parse(mBoardItem.getPhotoPageUrl());
                    Intent i = new Intent(getActivity(), PhotoPageActivity.class);
                    i.setData(photoPageUri);
                    startActivity(i);
                }
                return true;
            case R.id.menu_item_map:
                String latitude = mBoardItem.getLatitude();
                String longitude = mBoardItem.getLongitude();
                String title = mBoardItem.getCaption();
                Intent intent = new Intent(getActivity(), MapActivity.class);
                intent.putExtra(MapActivity.EXTRA_MAP_LATITUDE, latitude);
                intent.putExtra(MapActivity.EXTRA_MAP_LONGITUDE, longitude);
                intent.putExtra(MapActivity.EXTRA_MAP_TITLE, title);
                startActivity(intent);
                return true;
            case R.id.menu_item_share :
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, getImgUri());
                shareIntent.setType("image/jpeg");
                startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Uri getImgUri() {
        Picture picture = mWebView.capturePicture();
        if (picture == null) {
            Log.i(TAG, "Pic == null");
            return null;
        }
        PictureDrawable pd = new PictureDrawable(picture);
        Bitmap bitmap = Bitmap.createBitmap(pd.getIntrinsicWidth(), pd.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawPicture(pd.getPicture());

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "Image", null);
        return Uri.parse(path);
    }




}


