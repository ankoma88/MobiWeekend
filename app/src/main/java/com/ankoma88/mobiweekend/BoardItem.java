package com.ankoma88.mobiweekend;

import java.io.Serializable;

public class BoardItem implements Serializable{

    private String mCaption;
    private String mId;
    private String mUrl;
    private String mOwner;
    private String mLatitude;
    private String mLongitude;

    private String mFarm;
    private String mServer;
    private String mSecret;


    @Override
    public String toString() {
        return mCaption;
    }

    public String getLatitude() {
        return mLatitude;
    }

    public void setLatitude(String latitude) {
        mLatitude = latitude;
    }

    public String getLongitude() {
        return mLongitude;
    }

    public void setLongitude(String longitude) {
        mLongitude = longitude;
    }

    public String getCaption() {
        return mCaption;
    }

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String owner) {
        mOwner = owner;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getFarm() {
        return mFarm;
    }

    public void setFarm(String farm) {
        mFarm = farm;
    }

    public String getServer() {
        return mServer;
    }

    public void setServer(String server) {
        mServer = server;
    }

    public String getSecret() {
        return mSecret;
    }

    public void setSecret(String secret) {
        mSecret = secret;
    }

    public String getPhotoPageUrl() {
        return "http://www.flickr.com/photos/" + mOwner + "/" + mId;

    }

    public String getPhotoPageFullSize() {
        return "http://farm"+mFarm+".static.flickr.com/" + mServer +"/"+mId + "_" + mSecret +  "_b" + ".jpg";
    }

    public String defineFileExtension(String ursS) {
        String[] result = ursS.split("\\.");
        return "."+result[result.length-1];
    }

}
