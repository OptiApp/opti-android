package io.github.imruahmed.opti;

import android.app.ProgressDialog;
import android.content.Context;
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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
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
                new RunGATask().execute();
            }
        });

    }

    class RunGATask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog;
        Call<ResponseBody> call;

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
            for (int i = 0; i < placeItems.size(); i++) {
                origins.append(placeItems.get(i).latLng.latitude);
                origins.append(",");
                origins.append(placeItems.get(i).latLng.longitude);
                if (i != placeItems.size() - 1) {
                    origins.append("|");
                }
            }
            Log.v("IMRAN", origins.toString());
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://hackthenorth16-1758.appspot.com/")
                    .build();

            TSPService service = retrofit.create(TSPService.class);
            call = service.runTSPService(origins.toString(),
                    origins.toString());

            try {
                String body = call.execute().body().string();
                Log.v("IMRAN YES", body);
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
            super.onPostExecute(aVoid);
        }
    }

    public interface TSPService {
        @GET("api/locations")
        Call<ResponseBody> runTSPService(@Query("origins") String origins,
                                         @Query("destinations") String destinations);
    }
}
