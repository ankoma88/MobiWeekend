package com.ankoma88.mobiweekend;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ThumbnailDownloader<Token> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;

    Handler mHandler;
    Map<Token, String> requestMap = Collections.synchronizedMap(new HashMap<Token, String>());
    Handler mResponseHandler;
    Listener<Token> mListener;

    private static final int CACHE_SIZE = 10 * 1024 * 1024; // 10MB

    private final LruCache<String, Bitmap> mCache;

    public interface Listener<Token> {
        void onThumbnailDownloaded(Token token, Bitmap thumbnail);
    }

    public void setListener(Listener<Token> listener) {
        mListener = listener;
    }

    public Listener<Token> getListener() {
        return mListener;
    }

    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
        mCache = new LruCache<String, Bitmap>(CACHE_SIZE);
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    @SuppressWarnings("unckecked")
                    Token token = (Token) msg.obj;
                    Log.i(TAG, "Got a request for URL: " + requestMap.get(token));
                    handleRequest(token);
                }
            }
        };

    }

    public void handleRequest(final Token token) {
        final Bitmap bitmap;
        try {
            final String url = requestMap.get(token);
            if (url == null) return;

            byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
            if (mCache.get(url) != null) {
                bitmap = mCache.get(url);
                Log.i(TAG, "Cache size: " + mCache.size());
            } else {
                bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                Log.i(TAG, "Bitmap created");
                mCache.put(url, bitmap);
            }
                mResponseHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (requestMap.get(token) != url)
                            return;
                        requestMap.remove(token);
                        mListener.onThumbnailDownloaded(token, bitmap);
                    }

                });

        } catch (IOException ioe) {
            Log.e(TAG, "Error downloading image", ioe);
        }

    }


    public void queueThumbnail(Token token, String url) {
        Log.i(TAG, "Got an URL: " + url);
        requestMap.put(token, url);
        mHandler.obtainMessage(MESSAGE_DOWNLOAD, token).sendToTarget();

    }

    public void clearQueue() {
        mHandler.removeMessages(MESSAGE_DOWNLOAD);
        requestMap.clear();
    }
}
