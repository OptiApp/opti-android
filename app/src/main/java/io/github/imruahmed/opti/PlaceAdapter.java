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
import android.widget.RadioButton;
import android.widget.Spinner;
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
            String[] array = objects.get(i).split("=");
            PlaceItem placeItem = new PlaceItem();
            placeItem.name = array[0];
            Double d = Double.parseDouble(array[1]);
            Double d1 = Double.parseDouble(array[2]);
            placeItem.latLng = new LatLng(d.doubleValue(), d1.doubleValue());
            placeItem.address = array[3];
            placeItem.id = array[4];
            places.add(placeItem);
        }
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final PlaceViewHolder viewHolder;

        if (convertView == null) {
            final LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.place_cardview, parent, false);

            viewHolder = new PlaceViewHolder();
            viewHolder.cardView = (CardView) convertView.findViewById(R.id.cardview);
            viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.name_textview);
            viewHolder.addressTextView = (TextView) convertView.findViewById(R.id.address_textview);
            viewHolder.durationTextView = (TextView) convertView.findViewById(R.id.duration_textview);

            viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                    alert.setTitle("Set Duration");
                    final View alertDialogView = inflater.inflate(R.layout.alert_dialog_view, null);
                    alert.setView(alertDialogView);
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Spinner spinner = (Spinner) alertDialogView.findViewById(R.id.duration_spinner);
                            RadioButton abutton = (RadioButton) alertDialogView.findViewById(R.id.a_button);
                            RadioButton bbutton = (RadioButton) alertDialogView.findViewById(R.id.b_button);

                            if (abutton.isChecked()) {
                                places.get(position).start = true;
                                places.get(position).end = false;
                            } else if (bbutton.isChecked()) {
                                places.get(position).start = false;
                                places.get(position).end = true;
                            } else {
                                places.get(position).start = false;
                                places.get(position).end = false;
                            }
                            int[] nums = new int[]{5, 10, 15, 30, 45, 60};
                            places.get(position).duration = nums[spinner.getSelectedItemPosition()];
                            viewHolder.durationTextView.setText("Duration: "+places.get(position).duration+" min");
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
    }
}
