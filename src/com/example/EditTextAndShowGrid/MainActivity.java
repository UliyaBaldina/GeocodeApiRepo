package com.example.EditTextAndShowGrid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;

import com.google.gson.Gson;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    String url = "http://maps.googleapis.com/maps/api/geocode/json?address=";

    private ProgressDialog pDialog;
    String myresults="kdjo";
    private GridviewAdapter mAdapter;
    private GridView gridView;
    private ArrayList<String> listCountry;
    private ArrayList<Integer> listFlag;
    Handler handler=new Handler();
    Thread t;
    final String LOG_TAG = "myLogs";
    private final long DELAY = 1000; // in ms
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                mAdapter = new GridviewAdapter(MainActivity.this, listCountry, listFlag);
                // Set custom adapter to gridview
                gridView = (GridView) findViewById(R.id.gridView1);
                gridView.setAdapter(mAdapter);
            };
        };
       //new GetContacts().execute();
        /*new GetContacts().execute(new String[] {
                url + "moc"
        });*/
        /*TextView myOutputBox = (TextView) findViewById(R.id.myOutputBox);
        myOutputBox.setText(myresults);*/

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
                            try { Thread.sleep(2000);
                                getJson(url+myTextBox.getText().toString());
                                handler.sendEmptyMessage(i);
                            }                   // Wait 1000 milliseconds
                            catch (InterruptedException e) {
                            }

                            // пишем лог
                           // Log.d(LOG_TAG, "i = " + i);
                        }
                    }
                });
                t.start();
               
               /* new GetContacts().execute(new String[] {
                        url + s*/
                }
                //TO DO
                //функция, которая делает прасинг и помещает данные в грид
                //в функцию передавать данные, которые вводит польз-ль т.е. переменную S
                //1, ф-я скачивает нужный json
                //2, ф-я парсит
                //3 помещает данные в грид

        });
      /*  mAdapter = new GridviewAdapter(this,listCountry, listFlag);
        // Set custom adapter to gridview
        gridView = (GridView) findViewById(R.id.gridView1);
        gridView.setAdapter(mAdapter);*/

    }
    public void prepareList()
    {
        /*listCountry = new ArrayList<String>();

        listCountry.add("india");
        listCountry.add("Brazil");
        listCountry.add("Canada");
        listCountry.add("China");
        listCountry.add("France");
        listCountry.add("Germany");
        listCountry.add("Iran");
        listCountry.add("Italy");
        listCountry.add("Japan");
        listCountry.add("Korea");
        listCountry.add("Mexico");
        listCountry.add("Netherlands");
        listCountry.add("Portugal");
        listCountry.add("Russia");
        listCountry.add("Saudi Arabia");
        listCountry.add("Spain");
        listCountry.add("Turkey");
        listCountry.add("United Kingdom");
        listCountry.add("United States");*/

        listFlag = new ArrayList<Integer>();
        listFlag.add(R.drawable.ic_launcher);
        listFlag.add(R.drawable.ic_launcher);
        listFlag.add(R.drawable.ic_launcher);
        listFlag.add(R.drawable.ic_launcher);
        listFlag.add(R.drawable.ic_launcher);
        listFlag.add(R.drawable.ic_launcher);
        listFlag.add(R.drawable.ic_launcher);
        listFlag.add(R.drawable.ic_launcher);
        listFlag.add(R.drawable.ic_launcher);
        listFlag.add(R.drawable.ic_launcher);
        listFlag.add(R.drawable.ic_launcher);
        listFlag.add(R.drawable.ic_launcher);
        listFlag.add(R.drawable.ic_launcher);
        listFlag.add(R.drawable.ic_launcher);
        listFlag.add(R.drawable.ic_launcher);
        listFlag.add(R.drawable.ic_launcher);
        listFlag.add(R.drawable.ic_launcher);
        listFlag.add(R.drawable.ic_launcher);
    }
    /**
     * Async task class to get json by making HTTP call
     */
    public Void getJson (String url) {
        ServiceHandler sh = new ServiceHandler();
        //КОСТЫЛЬ??
        //String url = params[0];
        //
//            // Making a request to url and getting response
        String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
//
        Log.d("Response: ", "> " + jsonStr);
        if (jsonStr != null) {
            try {
                prepareList();
                myresults = jsonStr;
                listCountry = getFullAddressFromJSON(jsonStr);
                    /*mAdapter = new GridviewAdapter(this, getFullAddressFromJSON(myresults), listFlag);
                    gridView = (GridView) findViewById(R.id.gridView1);
                    gridView.setAdapter(mAdapter);*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("ServiceHandler", "Couldn't get any data from the url");
        }
        return null;
    }
    private class GetContacts extends AsyncTask < String, Void, Void > {
       @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
           pDialog.show();        }

        @Override
       protected Void doInBackground(String... params) {
           // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            //КОСТЫЛЬ??
            String url = params[0];
//
//            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
//
            Log.d("Response: ", "> " + jsonStr);
           if (jsonStr != null) {
               try {
                    prepareList();
                    myresults = jsonStr;
                    listCountry=getFullAddressFromJSON(jsonStr);
                    /*mAdapter = new GridviewAdapter(this, getFullAddressFromJSON(myresults), listFlag);
                    gridView = (GridView) findViewById(R.id.gridView1);
                    gridView.setAdapter(mAdapter);*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing()) pDialog.dismiss();
        }
    }

    public ArrayList getFullAddressFromJSON( String jsonString) {
        ArrayList  arrayFormattedAddress = new ArrayList ();
        Gson gson = new Gson();
        GeocodeResponse response = gson.fromJson(jsonString, GeocodeResponse.class);
        List < Results > results = response.results;
        for (Results result: results) {
            arrayFormattedAddress.add(result.formattedAddress);
            Log.i("d,", result.formattedAddress);
        }
        return arrayFormattedAddress;
    }
    public ArrayList getLatLngFromJSON( String jsonString) {
        ArrayList  arrayLatLng = new ArrayList ();
        String imageLink = "http://maps.googleapis.com/maps/api/staticmap?center=";
        String imageZoomParameters ="zoom=13";
        String imageSizeParameters="size=200x200";
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
            Log.i("d,", latLng);        }
        return arrayLatLng;
    }

    public void btnshow_onClick(View v) {
       /* EditText myTextBox = (EditText) findViewById(R.id.et_place);
        String res = myTextBox.getText().toString();
        new GetContacts().execute(new String[] {url+res});*/
        mAdapter = new GridviewAdapter(this,listCountry, listFlag);
        // Set custom adapter to gridview
        gridView = (GridView) findViewById(R.id.gridView1);
        gridView.setAdapter(mAdapter);
       /* EditText myTextBox = (EditText) findViewById(R.id.et_place);
        String res = myTextBox.getText().toString();
        Log.i("HA-HA", res);
        new GetContacts().execute(new String[] {url+res});

       // Log.i("HA-HA",myresults);
       /* getFullAddressFromJSON(myresults);
        getLatLngFromJSON(myresults);
        prepareList();
        // Log.i("HA-HA",myresults);*/
        // prepared arraylist and passed it to the Adapter class

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