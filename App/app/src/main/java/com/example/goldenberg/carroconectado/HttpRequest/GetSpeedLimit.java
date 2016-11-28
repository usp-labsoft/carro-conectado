package com.example.goldenberg.carroconectado.HttpRequest;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Goldenberg on 21/10/16.
 */
public class GetSpeedLimit extends AsyncTask <String, Void, String>{

    private static final String ns = null;
    String GPS,velOBD,date,user,arduino;

    public GetSpeedLimit(){}

    public String getLimit(String s, String w, String n, String e) {

        try {
            String API_URL = "http://www.overpass-api.de/api/xapi?*[maxspeed=*][bbox=" + s + "," + w + "," + n + "," + e + "]";
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            return getResponse(connection);

        } catch (Exception error) {
            Log.e("Carro Conectado", "Error " + error.toString());
            return null;
        }
    }

/****************** XML PARSER **********************/
    public String parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            System.out.println("parse");
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readOSM(parser);
        } finally {
            in.close();
        }
    }

    private String readOSM(XmlPullParser parser) throws XmlPullParserException, IOException {
        String maxspeed = null;
        parser.require(XmlPullParser.START_TAG, ns, "osm");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("way")) {
                maxspeed = readWay(parser);
            } else {
                skip(parser);
            }
        }
        return maxspeed;
    }

    private String readWay(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "way");
        String speed = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            String maxspeed = parser.getAttributeValue(null, "k");
            if (name.equals("tag") && maxspeed.equals("maxspeed")) {
                speed = parser.getAttributeValue(null, "v");
            } else {
                skip(parser);
            }
        }
        return speed;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
/*************************************************************/

    public String getResponse(HttpURLConnection connection){
        String response;
        try{
            response = parse(connection.getInputStream());
            return response;
        } catch (XmlPullParserException error){
            Log.getStackTraceString(error);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    protected String doInBackground(String... params) {
        String s,w,n,e;
        s = params[0];
        w = params[1];
        n = params[2];
        e = params[3];
        this.GPS = params[0] + "," + params[1];
        this.velOBD = params[4];
        this.date = params[5];
        this.user = "583a02f555e01f5b9a9bf972";
        this.arduino = "583a030c55e01f5b9a9bf973";
        System.out.println("Requesting API with: " + s + ", " + w + ", " + n + ", " + e);
//        s = "5.6283473";
//        w = "50.5348043";
//        n = "5.6285261";
//        e = "50.534884";
        return this.getLimit(s,w,n,e);
    }

    @Override
    protected void onPostExecute(String limit) {
        try{
            Log.v("Speed Limit", limit);
            new SendData().execute(velOBD,limit,GPS,date,user,arduino);
        } catch (Exception e){
            Log.getStackTraceString(e);
        }
    }


}
