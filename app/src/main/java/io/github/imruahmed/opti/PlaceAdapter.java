package io.github.imruahmed.opti;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class PlaceAdapter extends ArrayAdapter<String> {

    private static Context mContext;
    private List<PlaceItem> places;
    private List<String> durations;

    public PlaceAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        mContext = context;
        places = new ArrayList<>();
        for (int i = 0; i < objects.size(); i++) {
            String[] array = objects.get(i).split(",");
            PlaceItem placeItem = new PlaceItem();
            placeItem.name = array[0];
            Double d = Double.parseDouble(array[1]);
            Double d1 = Double.parseDouble(array[2]);
            placeItem.latLng = new LatLng(d.doubleValue(), d1.doubleValue());
            placeItem.address = array[3];
            places.add(placeItem);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final PlaceViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.place_cardview, parent, false);

            viewHolder = new PlaceViewHolder();
            viewHolder.cardView = (CardView) convertView.findViewById(R.id.cardview);
            viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.name_textview);
            viewHolder.addressTextView = (TextView) convertView.findViewById(R.id.address_textview);
            viewHolder.durationTextView = (TextView) convertView.findViewById(R.id.duration_textview);
            viewHolder.durationEditText = (EditText) convertView.findViewById(R.id.duration_edittext);

            viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                    alert.setTitle("Set Duration");
                    final EditText input = new EditText(mContext);
                    int i = mContext.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
                    input.setPadding(i, 0, i, 0);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    input.setRawInputType(Configuration.KEYBOARD_12KEY);
                    alert.setView(input);
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            places.get(position).duration = 20;
                            viewHolder.durationTextView.setText("Duration: "+places.get(position).duration);
                        }
                    });
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //Put actions for CANCEL button here, or leave in blank
                        }
                    });
                    alert.show();
                }
            });

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (PlaceViewHolder) convertView.getTag();
        }

        viewHolder.nameTextView.setText(places.get(position).name);
        viewHolder.addressTextView.setText(places.get(position).address);

        return convertView;
    }

    public List<PlaceItem> getPlaces() {
        return places;
    }

    static class PlaceViewHolder {
        CardView cardView;
        TextView nameTextView;
        TextView addressTextView;
        TextView durationTextView;
        EditText durationEditText;
    }
}
