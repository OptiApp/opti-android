package io.github.imruahmed.opti;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class ConfirmActivity extends AppCompatActivity {

    Context mContext;

    ArrayList<String> places;
    ArrayList<PlaceItem> placeItems;
    ListView listView;
    PlaceAdapter placeAdapter;

    Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        mContext = this;
        places = getIntent().getStringArrayListExtra("placesExtra");

        listView = (ListView) findViewById(R.id.places_list_view);
        placeAdapter = new PlaceAdapter(this, R.layout.place_cardview, places);
        listView.setAdapter(placeAdapter);

        confirmButton = (Button) findViewById(R.id.button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeItems = (ArrayList<PlaceItem>) placeAdapter.getPlaces();
                for (int i = 0; i < placeItems.size(); i++) {
                    if (placeItems.get(i).duration == -1) {
                        placeItems.get(i).duration = 20;
                    }
                }
                Log.v("IMRAN AAA", placeItems.toString());
                PlaceItem p = null;
                for (int i = 0; i < placeItems.size(); i++) {
                    if (placeItems.get(i).start) {
                        p = placeItems.get(i);
                        break;
                    }
                }
                if (p != null) {
                    placeItems.remove(p);
                    placeItems.add(0, p);
                }
                for (int i = 0; i < placeItems.size(); i++) {
                    if (placeItems.get(i).end) {
                        p = placeItems.get(i);
                        break;
                    }
                }
                if (p != null) {
                    placeItems.remove(p);
                    placeItems.add(p);
                }
                Log.v("IMRAN AAA", placeItems.toString());

                new RunGATask().execute();
            }
        });

    }

    class RunGATask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog;
        Call<TempObj> call;
        ArrayList<Integer> order;
        String stuff;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Optimizing your route!");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            StringBuilder origins = new StringBuilder();
            StringBuilder durations = new StringBuilder();
            StringBuilder ids = new StringBuilder();

            for (int i = 0; i < placeItems.size(); i++) {
                origins.append(placeItems.get(i).latLng.latitude);
                origins.append(",");
                origins.append(placeItems.get(i).latLng.longitude);

                durations.append(placeItems.get(i).duration);

                ids.append(placeItems.get(i).id);

                if (i != placeItems.size() - 1) {
                    origins.append("|");
                    durations.append(",");
                    ids.append(",");
                }
            }
            stuff = "origins="+origins.toString()+"destinations="+origins.toString()+"durations="+durations.toString()+"placeIds="+ids.toString();
            //Log.v("IMRAN", "origins="+origins.toString()+"destinations="+origins.toString()+"durations="+durations.toString()+"placeIds="+ids.toString());
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://hackthenorth16-1758.appspot.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            TSPService service = retrofit.create(TSPService.class);
            call = service.runTSPService(origins.toString(), origins.toString(),
                    durations.toString(), ids.toString());
            try {
                order = (ArrayList<Integer>) call.execute().body().optimalRoute;
                Log.v("IMRAN YES", order.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            Log.v("IMRAN", stuff);
            if (order != null) {
                Intent intent = new Intent(mContext, DirectionsActivity.class);
                ArrayList<String> extra = new ArrayList<>();
                for (int i = 0; i < order.size(); i++) {
                    extra.add(placeItems.get(order.get(i)).toString());
                }
                intent.putStringArrayListExtra("ROUTE", extra);
                startActivity(intent);
            }
            super.onPostExecute(aVoid);
        }
    }

    public interface TSPService {
        @GET("api/locations")
        Call<TempObj> runTSPService(@Query("origins") String origins,
                                    @Query("destinations") String destinations,
                                    @Query("durations") String durations,
                                    @Query("placeIds") String placeIds);
    }

    class TempObj {
        List<Integer> optimalRoute;
    }
}
