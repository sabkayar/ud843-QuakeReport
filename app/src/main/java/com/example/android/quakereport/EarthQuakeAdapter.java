package com.example.android.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.DecimalFormat;
import java.util.List;

public class EarthQuakeAdapter extends ArrayAdapter<EarthQuake> {
    public EarthQuakeAdapter(Context context, List<EarthQuake> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }


        EarthQuake singleEarthQuake = getItem(position);

        double magnitude = singleEarthQuake.getMagnitudeOfEarthQuake();
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        String formattedMagnitude = decimalFormat.format(magnitude);

        TextView magnitudeView = ((TextView) itemView.findViewById(R.id.magnitude_text_view));

        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable magnitudeCircle = (GradientDrawable) magnitudeView.getBackground();

        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(magnitude);

        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);

        //Set the formatted text on the text view
        magnitudeView.setText(formattedMagnitude);


        String place = singleEarthQuake.getPlaceOfEarthQuake();
        String placePartOne = place.substring(0, place.indexOf("of") + 2);
        String placePartTwo = place.substring(place.indexOf("of") + 2).trim();

        ((TextView) itemView.findViewById(R.id.place_part_1)).setText(placePartOne);
        ((TextView) itemView.findViewById(R.id.place_part_2)).setText(placePartTwo);


        long timeInMilliSeconds = singleEarthQuake.getDateOfEarthQuake();
        ((TextView) itemView.findViewById(R.id.date_text_view)).setText(QueryUtils.formattedDateTime("MMM DD, yyyy", timeInMilliSeconds));
        ((TextView) itemView.findViewById(R.id.time_text_view)).setText(QueryUtils.formattedDateTime("h:mm a", timeInMilliSeconds));
        return itemView;
    }

    private int getMagnitudeColor(double magnitude) {
        switch ((int) magnitude) {
            case 0:
            case 1:
                return ContextCompat.getColor(getContext(), R.color.magnitude1);
            case 2:
                return ContextCompat.getColor(getContext(), R.color.magnitude2);
            case 3:
                return ContextCompat.getColor(getContext(), R.color.magnitude3);
            case 4:
                return ContextCompat.getColor(getContext(), R.color.magnitude4);
            case 5:
                return ContextCompat.getColor(getContext(), R.color.magnitude5);
            case 6:
                return ContextCompat.getColor(getContext(), R.color.magnitude6);
            case 7:
                return ContextCompat.getColor(getContext(), R.color.magnitude7);
            case 8:
                return ContextCompat.getColor(getContext(), R.color.magnitude8);
            case 9:
                return ContextCompat.getColor(getContext(), R.color.magnitude9);
            case 10:
                return ContextCompat.getColor(getContext(), R.color.magnitude10plus);
            default:
                return ContextCompat.getColor(getContext(), R.color.magnitude10plus);

        }
    }
}
