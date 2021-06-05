package com.example.android.quakereport;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import org.w3c.dom.Text;

import android.graphics.drawable.GradientDrawable;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EarthQuakeAdapter extends ArrayAdapter<EarthQuake> {

    //class for formatting the date taken by  the json response
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    //class for formatting the time taken by the json response
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

    private String formatMagnitude(double magnitude) {
        DecimalFormat formatter = new DecimalFormat("0.0");
        return formatter.format(magnitude);
    }

    private int getMagnitudeColor(double magnitude) {
        int magnitudeFloor = (int) Math.floor(magnitude);
        int magnitudeColorResourceId = 0;

        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
            break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            case 10:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }




    public EarthQuakeAdapter(Context context, ArrayList<EarthQuake> earthquakes) {
        super(context, 0, earthquakes);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        final EarthQuake earthquake = getItem(position);


        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(earthquake.getUrl()));
                getContext().startActivity(intent);
            }
        });

        TextView magnitudeTextView = (TextView) listItemView.findViewById(R.id.magnitude);

        //set the proper background on the magnitude circle
        //fetch the background from the textView which is a gradient drawable
        GradientDrawable magnitudeCircle = (GradientDrawable) magnitudeTextView.getBackground();

        //get the appropriate background color based on the current magnitude
        int magnitudeColor = getMagnitudeColor(earthquake.getMagnitude());

        //set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);

        String formattedMag = formatMagnitude(earthquake.getMagnitude());
        magnitudeTextView.setText(formattedMag);


        TextView locationOffsetTextView = (TextView) listItemView.findViewById(R.id.locationOffset);

        TextView primaryLocationTextView = (TextView) listItemView.findViewById(R.id.primaryLocation);

        String completeLocation = earthquake.getLocation();


        if (completeLocation.contains("of")) {
            String temp[] = completeLocation.split("(?<=of)");
            String locationOffset = temp[0];
            String primaryLocation = temp[1];
            locationOffsetTextView.setText(locationOffset);
            primaryLocationTextView.setText(primaryLocation);
        } else {
            String locationOffset = "Near of";
            locationOffsetTextView.setText(locationOffset);
            primaryLocationTextView.setText(completeLocation);


        }


//            create a new date object by using time
        Date dateObject = new Date(earthquake.getTime());

        TextView dateTextView = (TextView) listItemView.findViewById(R.id.date);

        //storing the formatted date in a string variable
        String formattedDate = formatDate(dateObject);

        //setting the date on the layout
        dateTextView.setText(formattedDate);

        TextView timeTextView = (TextView) listItemView.findViewById(R.id.time);

        //storing the formatted time in a string variable
        String formattedTime = formatTime(dateObject);

        //setting the time on the layout
        timeTextView.setText(formattedTime);






        return listItemView;
    }
}

