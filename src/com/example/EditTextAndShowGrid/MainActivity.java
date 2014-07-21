package com.example.EditTextAndShowGrid;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;

import com.google.gson.Gson;

public class MainActivity extends Activity {

    String url = "http://maps.googleapis.com/maps/api/geocode/json?address=";
    String mapsUrl="http://maps.google.com/maps?q=";

    private GridviewAdapter mAdapter;
    private GridView gridView;
    private ArrayList<String> listFormattedAddress;
    private ArrayList<String> listImagesURL;
    private ArrayList<String> listLatLng;
    DBHelper dbHelper;

    Handler handler=new Handler();
    Thread t;
    final String LOG_TAG = "myLogs";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                mAdapter = new GridviewAdapter(MainActivity.this, listFormattedAddress, listImagesURL, listLatLng);
                // Set custom adapter to gridview
                gridView = (GridView) findViewById(R.id.gridView1);
                gridView.setAdapter(mAdapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {

                        // get the clicked folder name
                        String coordinates = ((TextView) view
                                .findViewById(R.id.textViewLatLng))
                                .getText()
                                .toString();
                        // just toast it
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl+coordinates));
                        startActivity(browserIntent);
                    }
                });

            };
        };

        final EditText myTextBox = (EditText) findViewById(R.id.et_place);
        myTextBox.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {}

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                t = new Thread(new Runnable()
                {
                    public void run() {
                    for (int i = 1; i <= 10; i++) {
                    // долгий процесс
                        try {
                            Thread.sleep(2000);
                            SQLiteDatabase db=dbHelper.getReadableDatabase();
                           Cursor cur=null;
                                  cur=db.rawQuery("SELECT geocodeDataID as _id, geocodeDataAddress FROM GeocodeData WHERE " +
                                    "geocodeDataAddress=?", new String []{myTextBox.getText().toString()});
                           // Log.i("Lopolp", cur.getCount()+"yes");
                            if (cur.getCount()==0)
                           {
                                Log.i("Lopolp", cur+"yes");
                                getAndParseJson(url + myTextBox.getText().toString());
                                for(int k=0; k<=listFormattedAddress.size(); k++) {
                                    ContentValues cv = new ContentValues();
                                    db = dbHelper.getWritableDatabase();
                                    cv.put(dbHelper.colAddress,myTextBox.getText().toString());
                                    cv.put(dbHelper.colFullAddress,listFormattedAddress.get(k));
                                    cv.put(dbHelper.colImageLink, listImagesURL.get(k));
                                    cv.put(dbHelper.colLatLng, listLatLng.get(k));
                                    db.insert(dbHelper.geocodeDataTable,null, cv);
                                }
                           }
                            else
                            {
                                Log.i("Lopolp", cur+"no");
                            }
                            //String item_content;//="rew-rew-rew";
                           // item_content = cur.getString(cur.getColumnIndex("geocodeDataAddress"));

                            /*if (item_content=="rew-rew-rew")
                            {
                                /*Toast toast = Toast.makeText(getApplicationContext(),"lya",
                                        Toast.LENGTH_SHORT);
                                toast.show();
                                dbHelper.getWritableDatabase();*/
                           /*     Log.i("Lopolp", item_content+"yes");
                            }
                            else
                            {
                                Log.i("Lopolp", item_content+"no");
                            }*/


                            //проверка если myTextBox.getText().toString() exist в БД, то берем оттуда, если нет, то парсим
                            /*SQLiteDatabase db = dbHelper.getWritableDatabase();
                            ContentValues cv = new ContentValues();
                            cv.put(colDeptID, 1);
                            cv.put(colDeptName, "Sales");
                            db.insert(deptTable, colDeptID, cv);

                            cv.put(colDeptID, 2);
                            cv.put(colDeptName, "IT");
                            db.insert(deptTable, colDeptID, cv);
                            db.close();*/

                            //добавляем данные в БД

                            handler.sendEmptyMessage(i);

                        }                   // Wait 1000 milliseconds
                        catch (Exception e) {e.printStackTrace();}//Log.i("error", e.getMessage());}
                    }
                    }
                });
                t.start();
            }
        });

    }
    /**
     * Async task class to get json by making HTTP call
     */
    public Void getAndParseJson (String url) {
        ServiceHandler sh = new ServiceHandler();
        String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
        if (jsonStr != null) {
            try {
                listFormattedAddress = getFullAddressFromJSON(jsonStr);
                listImagesURL = getImageLinkFromJSON(jsonStr);
                listLatLng = getLatLngFromJSON(jsonStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("ServiceHandler", "Couldn't get any data from the url");
        }
        return null;
    }

    public ArrayList getFullAddressFromJSON( String jsonString) {
        int maxLengthOfFormattedAddress = 15;

        ArrayList  arrayFormattedAddress = new ArrayList ();
        Gson gson = new Gson();
        GeocodeResponse response = gson.fromJson(jsonString, GeocodeResponse.class);
        List < Results > results = response.results;
        for (Results result: results) {
            if ((result.formattedAddress).length()>maxLengthOfFormattedAddress){
                StringBuffer buffer = new StringBuffer(result.formattedAddress);
                buffer.setLength(maxLengthOfFormattedAddress);
                arrayFormattedAddress.add(buffer.toString());
            }
            else
            arrayFormattedAddress.add((result.formattedAddress));
        }
        return arrayFormattedAddress;
    }
    public ArrayList getImageLinkFromJSON(String jsonString) {
        ArrayList  arrayImageLink = new ArrayList ();
        String imageLink = "http://maps.googleapis.com/maps/api/staticmap?center=";
        String imageZoomParameters ="zoom=13";
        String imageSizeParameters="size=240x140";
        String latLng = null;
        Gson gson = new Gson();
        GeocodeResponse response = gson.fromJson(jsonString, GeocodeResponse.class);
        List < Results > results = response.results;
        for (Results result: results) {
            latLng = imageLink +
                     result.geometry.location.lat + "," +
                     result.geometry.location.lng+"&"+
                     imageZoomParameters +"&"+
                     imageSizeParameters;
            arrayImageLink.add(latLng);
        }
        return arrayImageLink;
    }
    public ArrayList getLatLngFromJSON(String jsonString) {
        ArrayList  arrayLatLng = new ArrayList ();
        String latLng = null;
        Gson gson = new Gson();
        GeocodeResponse response = gson.fromJson(jsonString, GeocodeResponse.class);
        List < Results > results = response.results;
        for (Results result: results) {
            latLng = result.geometry.location.lat + "," +
                    result.geometry.location.lng;
            arrayLatLng.add(latLng);
        }
        return  arrayLatLng;
    }

    public void btnshow_onClick(View v) {
        final EditText myTextBox = (EditText) findViewById(R.id.et_place);
        t = new Thread(new Runnable()
        {
            public void run() {
        for (int i = 1; i <= 10; i++) {
        // долгий процесс
            try {
                Thread.sleep(2000);
                //проверка если myTextBox.getText().toString() exist в БД, то берем оттуда, если нет, то парсим
                getAndParseJson(url + myTextBox.getText().toString());
                //добавляем данные в БД
                handler.sendEmptyMessage(i);
            }                   // Wait 1000 milliseconds
            catch (InterruptedException e) {}
        }
            }
        });
        t.start();
    }

    class DBHelper extends SQLiteOpenHelper implements BaseColumns {
        //static final String dbName="DB";
        static final String geocodeDataTable="GeocodeData";
        static final String colID="geocodeDataID";
        static final String colAddress="geocodeDataAddress";
        static final String colFullAddress="fullAddress";
        static final String colImageLink="imageLink";
        static final String colLatLng="latLng";

        public DBHelper(Context context) {
            // конструктор суперкласса
            super(context, "myDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "--- onCreate database ---");
            // создаем таблицу с полями
            db.execSQL("CREATE TABLE "+geocodeDataTable+" ("+colID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "+colAddress+
                    " TEXT, "+colFullAddress+
                    " TEXT, "+colImageLink+" TEXT, "+colLatLng+" TEXT)");

        }
        public void dropTables(SQLiteDatabase db) {


            db.execSQL("DROP TABLE IF EXISTS resultTable");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS resultTable");
            Log.i("Yrra", "Yrrrer");
            /*db.execSQL("DELETE TABLE resultTable");
            db.execSQL("DELETE TABLE geocodeDataTable");
            Log.i("Yrra", "Yrrrer");
            db.execSQL("CREATE TABLE "+geocodeDataTable+" ("+colID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "+colAddress+
                    " TEXT, "+colFullAddress+
                    " TEXT, "+colImageLink+" TEXT, "+colLatLng+" TEXT)");
*/
        }
    }
}