package com.ankoma88.mobiweekend;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

public abstract class ParentBoardFragment extends Fragment {
    protected static final String TAG = "ParentBoardFragment";

    protected String mCategory;

    GridView mGridView;
    ArrayList<BoardItem> mItems;
    ThumbnailDownloader<ImageView> mThumbnailThread;

    public static final String EXTRA_BOARD_CATEGORY = "mobiweekend.BOARD_CATEGORY";
    public static final String EXTRA_BOARD_PLACE_ID = "mobiweeked.BOARD_PLACE_ID";
    public static final String EXTRA_BOARD_ITEMS = "mobiweekend.BOARD_ITEMS";
    public static final String EXTRA_BOARD_POSITION = "mobiweekend.BOARD_POSITION";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupMcategory();
        setupInOnCreate();
    }

    public void setupInOnCreate() {
        setHasOptionsMenu(true);
        setRetainInstance(true);
        updateItems();

        mThumbnailThread = new ThumbnailDownloader<ImageView>(new Handler());
        mThumbnailThread.setListener(new ThumbnailDownloader.Listener<ImageView>() {
            @Override
            public void onThumbnailDownloaded(ImageView imageView, Bitmap thumbnail) {
                if (isVisible()) {
                    imageView.setImageBitmap(thumbnail);

                }
            }
        });
        mThumbnailThread.start();
        mThumbnailThread.getLooper();
        Log.i(TAG, "Background thread started");

    }

    public void setupMcategory() {
    }


    public abstract void updateItems();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailThread.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailThread.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    void setupAdapter() {
        if (getActivity() == null || mGridView == null) return;

        if (mItems != null) {
            mGridView.setAdapter(new BordItemAdapter(mItems));
        } else {
            mGridView.setAdapter(null);
        }
    }

    private class BordItemAdapter extends ArrayAdapter<BoardItem> {
        public BordItemAdapter(ArrayList<BoardItem> items) {
            super(getActivity(),0,items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.board_item, parent, false);
            }
            ImageView imageView = (ImageView) convertView.findViewById(R.id.board_item_imageView);
            imageView.setImageResource(R.drawable.defaultimg);

            BoardItem item = getItem(position);
            mThumbnailThread.queueThumbnail(imageView, item.getUrl());

            return convertView;
        }
    }

    public void setupOnPhotoClick() {
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                BoardItem item = mItems.get(position);
//                Uri photoPageUri = Uri.parse(item.getPhotoPageFullSize());
                Intent i = new Intent(getActivity(), PhotoPageFullSizeActivity.class);
//                i.setData(photoPageUri);
//                i.putExtra(EXTRA_BOARD_URI, photoPageUri);
                i.putExtra(EXTRA_BOARD_ITEMS, mItems);
                i.putExtra(ParentBoardFragment.EXTRA_BOARD_POSITION, position);
                startActivity(i);
            }
        });
    }



}

























