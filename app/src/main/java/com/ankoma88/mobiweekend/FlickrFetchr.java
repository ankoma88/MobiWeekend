package com.ankoma88.mobiweekend;


import android.net.Uri;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FlickrFetchr {
    public static final String TAG = "FlickrFetchr";

    public static final String PREF_SEARCH_QUERY = "searchQueryTag";
    public static final String PREF_PLACE_ID = "placeId";
    public static final String PREF_FLICKR_ID = "flickrId";

    private static final String ENDPOINT = "https://api.flickr.com/services/rest/";
    private static final String API_KEY = "8b790982caf9fac294930d39af66435e";
    private static final String METHOD_SEARCH = "flickr.photos.search";
    private static final String METHOD_SEARCH_MY_PHOTOS = "flickr.people.getPublicPhotos";
    private static final String ACCURACY = "1";
    private static final String PER_PAGE = "300";
    private static final String HAS_GEO = "1";
    private static final String PARAM_EXTRAS = "extras";
    private static final String EXTRA_SMALL_URL = "url_s";
    private static final String EXTRA_GEO = "geo";
    private static final String XML_PHOTO = "photo";
    private static final String XML_PLACE = "place";
    private static final String SELECTED_PARAM_EXTRAS = EXTRA_SMALL_URL+","+EXTRA_GEO;
    private static final String METHOD_FIND_PLACES = "flickr.places.find";



    byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrl(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public ArrayList<BoardItem> search(String place_id, String tags) {
        String url = Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("method", METHOD_SEARCH)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("place_id", place_id)
                .appendQueryParameter("per_page", PER_PAGE)
                .appendQueryParameter("tags", tags)
                .appendQueryParameter("accuracy", ACCURACY)
                .appendQueryParameter("has_geo",HAS_GEO)
                .appendQueryParameter(PARAM_EXTRAS, SELECTED_PARAM_EXTRAS)
                .build().toString();

        Log.i(TAG, "Built URL: " + url);
        return downloadBoardItems(url);
    }

    public ArrayList<BoardItem> searchMyPhotos(String flickrId) {
        String url = Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("method", METHOD_SEARCH_MY_PHOTOS)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("user_id", flickrId)
                .appendQueryParameter("per_page", PER_PAGE)
                .appendQueryParameter("has_geo", HAS_GEO)
                .appendQueryParameter(PARAM_EXTRAS, SELECTED_PARAM_EXTRAS)
                .build().toString();

        Log.i(TAG, "Built URL: " + url);
        return downloadBoardItems(url);
    }

    public Place searchLocation(String place) {
        String url = Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("method", METHOD_FIND_PLACES)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("query", place)
                .build().toString();
        Log.i(TAG, "Built URL: " + url);

        return downloadLocationInfo(url);
    }

    private ArrayList<BoardItem> downloadBoardItems(String url) {
        ArrayList<BoardItem> items = new ArrayList<BoardItem>();
        try {
            String xmlString = getUrl(url);
            Log.i(TAG, "Received xml: " + xmlString);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xmlString));
            parseItems(items, parser);

        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        } catch (XmlPullParserException xppe) {
            Log.e(TAG, "Failed to parse items", xppe);
        }
        return items;
    }

    private Place downloadLocationInfo(String url) {
        String xmlString="";
        Place foundPlace = new Place();

        try {
            xmlString = getUrl(url);
            Log.i(TAG, "Received xml: " + xmlString);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xmlString));
            foundPlace = parseLocationInfo(parser);
            if (foundPlace.getPlaceId() == null) {
                foundPlace.setPlaceId("Incorrect location");
                foundPlace.setPlaceFullName("Incorrect location");
                Log.i(TAG, "foundPlace in download" + foundPlace);
                return foundPlace;
            } else {
                Log.i(TAG, "Fetched place_id: " + foundPlace.getPlaceId());
                return foundPlace;
            }

        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
            return new Place();
        } catch (XmlPullParserException e) {
            Log.e(TAG, "Failed to fetch items", e);
            return new Place();
        }

    }

    private Place parseLocationInfo(XmlPullParser parser) throws IOException, XmlPullParserException {
        List<String> placeIdList = new LinkedList<String>();
        List<String> placeFullNameList = new LinkedList<String>();

        int eventType = parser.next();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && XML_PLACE.equals(parser.getName())) {
                String place_id = parser.getAttributeValue(null,"place_id");
                placeIdList.add(place_id);
            }
            if (eventType == XmlPullParser.TEXT) {
                String full_place_name = parser.getText();
                Log.i(TAG, "Full place name: " + full_place_name);
                placeFullNameList.add(full_place_name);
            }
            eventType = parser.next();
        }
        Log.i(TAG, "fullnameslist: " + placeFullNameList.size());
        Place foundPlace = new Place();
        if(placeIdList.size()>0&&placeFullNameList.size()>0) {
            foundPlace.setPlaceId(placeIdList.get(0));
            foundPlace.setPlaceFullName(placeFullNameList.get(2));
        }
        Log.i(TAG, "foundPlace in parse" + foundPlace);
        return foundPlace;
    }



    private void parseItems(ArrayList<BoardItem> items, XmlPullParser parser) throws IOException, XmlPullParserException {
        int eventType = parser.next();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && XML_PHOTO.equals(parser.getName())) {
                String id = parser.getAttributeValue(null, "id");
                String caption = parser.getAttributeValue(null, "title");
                String smallUrl = parser.getAttributeValue(null, EXTRA_SMALL_URL);
                String owner = parser.getAttributeValue(null, "owner");
                String latitude = parser.getAttributeValue(null, "latitude");
                String longitude = parser.getAttributeValue(null, "longitude");

                String secret = parser.getAttributeValue(null, "secret");
                String server = parser.getAttributeValue(null, "server");
                String farm = parser.getAttributeValue(null, "farm");

                BoardItem item = new BoardItem();
                item.setId(id);
                item.setCaption(caption);
                item.setUrl(smallUrl);
                item.setOwner(owner);
                item.setLatitude(latitude);
                item.setLongitude(longitude);

                item.setFarm(farm);
                item.setSecret(secret);
                item.setServer(server);

                items.add(item);
            }
            eventType = parser.next();
        }
    }
}
































