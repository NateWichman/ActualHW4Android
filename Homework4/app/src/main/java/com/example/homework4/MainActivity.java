package com.example.homework4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;

/************************************************************************
 * This page represents the main page of this application. It allows for
 * the finding of both a bearing and a distance between two geocoordinates
 *
 * @author Nathan Wichman, Joseph Stahle
 * @version Summer 2019
 ***********************************************************************/
public class MainActivity extends AppCompatActivity {

    /** Holds the current metric for distance, can be changed on the settings page **/
    String distanceMetric = "Kilometers";

    /** Holds the current metric for bearing, can be changed on the settings page **/
    String bearingMetric = "Degrees";

    private Button calculateButton;
    private TextView tvDistance;
    private TextView tvBearing;

    private EditText etLat1;
    private EditText etLon1;
    private EditText etLat2;
    private EditText etLon2;

    private Button settingsButton;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Getting references to all buttons, textview, and edittext objects we are using
        calculateButton = (Button) findViewById(R.id.btnCalculate);
        tvDistance = (TextView) findViewById(R.id.textDistance);
        tvBearing = (TextView) findViewById(R.id.textBearing);

        etLat1 = findViewById(R.id.etLat1);
        etLon1 = findViewById(R.id.etLon1);
        etLat2 = findViewById(R.id.etLat2);
        etLon2 = findViewById(R.id.etLon2);

        final Button settingsButton = (Button) findViewById(R.id.btnSettings);

        /*This method(s) will calculate the bearing and the distance from
            the text fields on the view that the user inputs into
         */
        calculateButton.setOnClickListener((new View.OnClickListener() {
            public void onClick(View v) {
               calculate();
            }
        }));

        /* This method(s) handle when the settings button is clicked.
            When it is clicked, the settings view is initiated.
         */
        settingsButton.setOnClickListener((new View.OnClickListener() {
            public void onClick(View v) {
                //Launching settings page to be the active Activity
                launchSettings();
            }
        }));

    }

    private void calculate(){
        try {
            //Getting latitudes and longitudes for the two coordinates
            Double lat1 = Double.parseDouble(etLat1.getText().toString());
            Double lon1 = Double.parseDouble(etLon1.getText().toString());
            Double lat2 = Double.parseDouble(etLat2.getText().toString());
            Double lon2 = Double.parseDouble(etLon2.getText().toString());

            //calculating distance between the two points with a private helper method
            Double distance = calcDistance(lat1, lat2, lon1, lon2, 0.0, 0.0);

            //Converting to km
            distance /= 1000;

            //Rounding to 2 decimal places
            BigDecimal bd = new BigDecimal(distance.toString());
            bd = bd.setScale(2, RoundingMode.HALF_UP);

            //Setting the label on the view to display the distance and its units
            tvDistance.setText(bd.toString() + " " + distanceMetric);

            //calculating bearing
            double bearing = angleFromCoordinate(lat1,lon1,lat2,lon2);

            //Rounding to 2 decimal places
            BigDecimal bd2 = new BigDecimal(Double.toString(bearing));
            bd2 = bd2.setScale(2, RoundingMode.HALF_UP);


            //Setting the label on the view to display the bearing and its units
            tvBearing.setText(bd2.toString() + " " + bearingMetric);
        }catch(Exception e){
            //DO nothing
        }
    }

    /*
    This method runs whenever an activity result occurs. This happens when the user
        switches back from the settings page to this view. If the user selected different
        units for either distance or bearing on the settings page, then those units
        are passed back to this main Activity in this method, and the units are updated
        to the view.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent dataIntent){
        super.onActivityResult(requestCode,resultCode,dataIntent);

        if(requestCode == 1 && resultCode == RESULT_OK){

            /* Getting the units passed back from the settings screen
                they are in the format "distanceUnits bearingUnits" and must
                parsed out of the incoming string */
            String message = dataIntent.getStringExtra("message");

            //Parsing out the user chosen distance and bearing metrics
            String[] metrics = message.split(" ");

            //Setting distance and bearing units to the user's choice
            this.distanceMetric =  metrics[0];
            this.bearingMetric = metrics[1];

            calculate();
        }
    }

    /*
    This method launches the setting screen, it also sets it up to be able
    to return data
     */
    private void launchSettings(){
        Intent intent = new Intent(this, Settings.class);

        //Starting activity, marking 1 as the code for receiving data back
        startActivityForResult(intent, 1);
    }

   //Source: https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude
    //Changed method name from origion, from distance() to calcDistance, also made it not static
    public double calcDistance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {
        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        //also changed this
        distance = Math.sqrt(distance);

        switch(distanceMetric){
            case "Kilometers":
                return distance;
            case "Miles":
                return distance * 0.621371;
            default:
                return -1.0;

        }
    }

    //Source: https://stackoverflow.com/questions/3932502/calculate-angle-between-two-latitude-longitude-points
    //ADDED the switch statement at the bottom for returning different units
    private double angleFromCoordinate(double lat1, double long1, double lat2,
                                       double long2) {

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        brng = 360 - brng; // count degrees counter-clockwise - remove to make clockwise


        //This was added by us, its not in the sourced version
        switch(this.bearingMetric){
            case "Degrees":
                return brng;
            case "Mils":
                return brng * 17.777778;
            default:
                return -1.0;
        }
    }
}
