package com.example.easytau;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.ByteArrayInputStream;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by slot1 on 13/12/2016.
 */

public class ParkingLotsList extends AppCompatActivity {
    public ParkingLotListener listener;
    public static List<ParkingLot> parkingLotsList = new ArrayList<>();

   private static final String TEL_AVIV_URL_API = "https://gisn.tel-aviv.gov.il/wsgis/service.asmx/GetLayer?layerCode=970&layerWhere=&xmin=180837&ymin=667485&xmax=181937&ymax=669428&projection=";

    public ParkingLotsList(ParkingLotListener listener) throws IOException, XmlPullParserException, JSONException {
        this.listener = listener;
        new DownloadRawData().execute(TEL_AVIV_URL_API);

    }


    class DownloadRawData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            try {
                url = new URL(params[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            String xmlStr = null;
            try {
                xmlStr = UrlManager.getUrlResponse(url);
                xmlStr = xmlStr.replaceAll("<?xml version=\"1.0\" encoding=\"utf-8\"?>","");
                xmlStr = xmlStr.replaceAll("<string xmlns=\"http://tempuri.org/\">","");
                xmlStr =xmlStr.replaceAll("</string>","");
                if(xmlStr .contains("<?xml")){
                    xmlStr = xmlStr.substring(xmlStr.indexOf("?>")+2);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

          return xmlStr;
        }
       private  String parseParkingXMLtoJson(InputStream in)throws XmlPullParserException, IOException {
           String res = null;
           try {
               XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
               parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
               parser.setInput(in, null);
               parser.getName();
               res = parser.getText();
           } finally {
               in.close();
           }
           return res;
       }
        @Override
        protected void onPostExecute(String res) {
            try {
                parseJson(res);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    private  void parseJson(String buff)throws  JSONException {
        try {

            if (buff == null)
                return;

            JSONObject json = new JSONObject(buff);

            JSONArray ParkingLotsArr = json.getJSONArray("features");
            for (int i = 0; i < ParkingLotsArr.length(); i++) {
                JSONObject jsonAttributes = ParkingLotsArr.getJSONObject(i);

                JSONObject attribute = jsonAttributes.getJSONObject("attributes");
                if ((!(attribute.getString("shem_chenyon")).equals("גולפיטק - חברת גני יהושוע")) &&(!(attribute.getString("shem_chenyon")).equals("סלודור"))&&(!(attribute.getString("shem_chenyon")).equals("טאגור"))) {
                    ParkingLot parkingLot = new ParkingLot();
                    parkingLot.setName(attribute.getString("shem_chenyon"));
                    parkingLot.setAddress(attribute.getString("ktovet"));
                    parkingLot.setPrice_day(attribute.getString("taarif_yom"));
                    parkingLot.setPrice_night(attribute.getString(("taarif_layla")));
                    parkingLot.setPrice_allday(attribute.getString("taarif_yomi"));
                    parkingLot.setNote(attribute.getString("hearot_taarif"));
                    parkingLot.setStatus(attribute.getString("status_chenyon"));
                    parkingLot.setStatus_time(attribute.getString("tr_status_chenyon"));

                    parkingLotsList.add(parkingLot);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        listener.onParkingLotSuccess(parkingLotsList);
     }
    }
}



