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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
                new RunGATask().execute();
            }
        });

    }

    class RunGATask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog = new ProgressDialog(mContext);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Optimizing your route!");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                continue;
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
}
