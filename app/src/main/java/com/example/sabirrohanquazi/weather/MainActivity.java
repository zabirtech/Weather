package com.example.sabirrohanquazi.weather;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sabirrohanquazi.weather.Helper.Helper;
import com.example.sabirrohanquazi.weather.Model.OpenWeatherMap;
import com.example.sabirrohanquazi.weather.common.Common;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;

public class MainActivity extends AppCompatActivity {

    TextView txtCity, txtLastUpdate, txtDescription, txtHumidity, txtTime, txtCelsius;
    ImageView imageView;
    EditText citytxt;
    PlaceAutocompleteFragment autocompleteFragment;
    String City;
    LocationManager locationManager;
    //String provider;
    static double lat, lng;
    OpenWeatherMap openWeatherMap = new OpenWeatherMap();

    int MY_PERMISSION = 0;
    Criteria critera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Control
        txtCity = (TextView) findViewById(R.id.txtCity);
        txtLastUpdate = (TextView) findViewById(R.id.txtLastUpdate);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        txtHumidity = (TextView) findViewById(R.id.txtHumidity);
        txtTime = (TextView) findViewById(R.id.txtTime);
        txtCelsius = (TextView) findViewById(R.id.txtCelsius);
        imageView = (ImageView) findViewById(R.id.imageView);
        //citytxt = (EditText)findViewById(R.id.citytext);



        //Get Coordinates



        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
       // provider = locationManager.getBestProvider(new Criteria(), false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                   // Manifest.permission.WRITE_EXTERNAL_STORAGE


            }, MY_PERMISSION);
        }

        critera = new Criteria();
        critera.setAccuracy(Criteria.POWER_LOW);



        locationManager.requestSingleUpdate(critera, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lng = location.getLongitude();


                new  GetWeather().execute(Common.apiRequest(String.valueOf(lat), String.valueOf(lng)));

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        },null);

        getLocation();


         autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.d("TAG", "Place: " + place.getName());
                City = place.getAddress().toString();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.d("TAG", "An error occurred: " + status);
            }
        });








    }












    public void citybutton(View view) {



        new  GetWeather().execute(Common.apiRequest2(City));
        //new  GetWeather().execute(Common.apiRequest2(citytxt.getText().toString()));



    }

    public void getLocation(View view) {
        getLocation();
    }

    public class GetWeather extends AsyncTask<String,Void,String>{

        ProgressDialog pd = new ProgressDialog(MainActivity.this);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setTitle("Please Wait....");
            pd.show();

        }



        @Override
        protected String doInBackground(String... params) {
            String stream = null;
            String urlString = params[0];

            Helper http = new Helper();
            stream = http.getHTTPData(urlString);
            return stream;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.contains("Error: No City found")){
                pd.dismiss();
                return;
            }

            Gson gson = new Gson();
            Type mType = new TypeToken<OpenWeatherMap>(){}.getType();
            openWeatherMap = gson.fromJson(s,mType);
            pd.dismiss();

            txtCity.setText(String.format("%s,%s",openWeatherMap.getName(),openWeatherMap.getSys().getCountry()));
            txtLastUpdate.setText(String.format("Last Updated: %s", Common.getDateNow()));
            txtDescription.setText(String.format("%s",openWeatherMap.getWeather().get(0).getDescription()));
            txtHumidity.setText(String.format("%d%%",openWeatherMap.getMain().getHumidity()));
            txtTime.setText("Sunrise/Sunset "+String.format("%s/%s",Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunrise()),Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunset())));
            txtCelsius.setText(String.format("%.2f °C", openWeatherMap.getMain().getTemp()));
            Picasso.with(MainActivity.this)
                    .load(Common.getImage(openWeatherMap.getWeather().get(0).getIcon()))
                    .into(imageView);

        }
    }

    private void getLocation(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    // Manifest.permission.WRITE_EXTERNAL_STORAGE


            }, MY_PERMISSION);
        }


        locationManager.requestSingleUpdate(critera, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lng = location.getLongitude();


                new  GetWeather().execute(Common.apiRequest(String.valueOf(lat), String.valueOf(lng)));

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        },null);
    }


}