package com.example.EditTextAndShowGrid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

    private ProgressDialog pDialog;
    String myresults=null;
    private GridviewAdapter mAdapter;
    private GridView gridView;
    private ArrayList<String> listFormattedAddress;
    private ArrayList<String> listImagesURL;
    Handler handler=new Handler();
    Thread t;
    final String LOG_TAG = "myLogs";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                mAdapter = new GridviewAdapter(MainActivity.this,listFormattedAddress, listImagesURL);
                // Set custom adapter to gridview
                gridView = (GridView) findViewById(R.id.gridView1);
                gridView.setAdapter(mAdapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    /*@Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id)  {
                        Toast.makeText(MainActivity.this, "la-la"+position+"la-la"+id, Toast.LENGTH_SHORT).show();
                    }*/
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,
                                            long id) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=37.771008,+-122.41175&iwloc=A&hl=ru "));
                        startActivity(browserIntent);

                        /*TextView textView1 = (TextView)findViewById(R.id.textView1);
                        Toast.makeText(getBaseContext(), textView1.getText().toString(), Toast.LENGTH_LONG).show();*/
                }});
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
                        //проверка если myTextBox.getText().toString() exist в БД, то берем оттуда, если нет, то парсим
                        getJson(url+myTextBox.getText().toString());
                        //добавляем данные в БД
                        handler.sendEmptyMessage(i);
                    }                   // Wait 1000 milliseconds
                    catch (InterruptedException e) {}
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
    public Void getJson (String url) {
        ServiceHandler sh = new ServiceHandler();
        String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
        if (jsonStr != null) {
            try {
                myresults = jsonStr;
                listFormattedAddress = getFullAddressFromJSON(jsonStr);
                listImagesURL = getLatLngFromJSON(jsonStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("ServiceHandler", "Couldn't get any data from the url");
        }
        return null;
    }

    public ArrayList getFullAddressFromJSON( String jsonString) {
        ArrayList  arrayFormattedAddress = new ArrayList ();
        Gson gson = new Gson();
        GeocodeResponse response = gson.fromJson(jsonString, GeocodeResponse.class);
        List < Results > results = response.results;
        for (Results result: results) {
            arrayFormattedAddress.add(result.formattedAddress);
        }
        return arrayFormattedAddress;
    }
    public ArrayList getLatLngFromJSON( String jsonString) {
        ArrayList  arrayLatLng = new ArrayList ();
        String imageLink = "http://maps.googleapis.com/maps/api/staticmap?center=";
        String imageZoomParameters ="zoom=13";
        String imageSizeParameters="size=120x120";
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
            arrayLatLng.add(latLng);
        }
        return arrayLatLng;
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
                getJson(url+myTextBox.getText().toString());
                //добавляем данные в БД
                handler.sendEmptyMessage(i);
            }                   // Wait 1000 milliseconds
            catch (InterruptedException e) {}
        }
            }
        });
        t.start();
    }

    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            // конструктор суперкласса
            super(context, "myDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "--- onCreate database ---");
            // создаем таблицу с полями
            db.execSQL("create table geocodeApiTable ("
                    + "id integer primary key autoincrement,"
                    + "address text,"
                    + "formatted_address text,"
                    + "imageLink text"+");");
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}