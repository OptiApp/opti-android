package io.github.imruahmed.opti;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class DirectionsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<PlaceItem> places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        ArrayList<String> placeExtra = getIntent().getStringArrayListExtra("ROUTE");
        places = new ArrayList<>();

        for (int i = 0; i < placeExtra.size(); i++) {
            PlaceItem p = new PlaceItem();
            String[] split = placeExtra.get(i).split("=");
            p.name = split[0];
            p.address = split[1];
            p.latLng = new LatLng(Double.valueOf(split[2]), Double.valueOf(split[3]));
            p.duration = Integer.valueOf(split[4]);
            p.id = split[5];
            places.add(p);
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        new DirectionsTask().execute();
    }

    class DirectionsTask extends AsyncTask<Void, Void, Void> {

        ArrayList<LatLng> coords;

        @Override
        protected Void doInBackground(Void... params) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://maps.googleapis.com/maps/api/directions/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            DirectionsApi directionsApi = retrofit.create(DirectionsApi.class);
            String origin = places.get(0).latLng.latitude+","+places.get(0).latLng.longitude;
            int end = places.size() - 1;
            String destination = places.get(end).latLng.latitude+","+places.get(end).latLng.longitude;
            String waypoints = "";
            for (int i = 1; i < places.size() - 1; i++) {
                String s = places.get(0).latLng.latitude+","+places.get(0).latLng.longitude;
                if (i != places.size() - 2) {
                    s += "|";
                }
                waypoints += s;
            }

            Call<Answer> call = directionsApi.getDirections(origin, destination, waypoints);


            try {
                Log.v("Executed", "call");
                Answer answer = call.execute().body();
                if (answer.status.equals("OK")) {
                    Log.v("IMRAN", answer.routes.length+"");
                    String polyline = answer.routes[0].overview_polyline.points;
                    coords = (ArrayList<LatLng>) PolyUtil.decode(polyline);
                    //Log.v("IMRAN", coords.toString());
                    Log.v("IMRAN", polyline);
                } else {
                    Log.v("IMRAN", answer.status);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (coords != null) {
                if (mMap != null) {
                    Log.v("IMRAN", coords.toString());
                    PolylineOptions p = new PolylineOptions();
                    for (int i = 0; i < coords.size(); i++) {
                        p.add(coords.get(i));
                    }
                    mMap.addPolyline(p);
                }
            }
            super.onPostExecute(aVoid);
        }
    }
    public interface DirectionsApi {
        @GET("json")
        Call<Answer> getDirections(@Query("origin") String origin,
                                   @Query("destination") String destination,
                                   @Query("waypoints") String waypoints);
    }

    class Answer {
        String status;
        Object[] geocodedWaypoints;
        RouteArray[] routes;
        Object[] availableTravelModes;

        class RouteArray {
            Object summary;
            Object[] legs;
            Object[] waypointOrder;
            Poly overview_polyline;
            Object bounds;
            Object copyrights;
            Object[] warnings;
            Object fare;

            class Poly {
                String points;
            }
        }
    }
}
