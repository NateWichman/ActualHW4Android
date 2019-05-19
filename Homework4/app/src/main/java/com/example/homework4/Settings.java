package com.example.homework4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

/**********************************************************************************
 * This class handles the logic for the settings page (Activity). It allows the
 * user to change the units on the distance (km or miles) and the units on
 * the bearing (degrees or mils). That data is then passed back to the user
 *
 * @author Nathan Wichman, Joseph Stahle
 * @version Summer 2019
 *********************************************************************************/
public class Settings extends AppCompatActivity {

    /** Both the Spinners caught from the view **/
    private Spinner spinnerDistance;
    private Spinner spinnerBearing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Private helper methods that add the km/miles and mis/degrees to the spinners
        addItemsOnDistanceSpinner();
        addItemsOnBearingSpinner();

        //"Apply" button
        final Button btnFinish = (Button) findViewById(R.id.btnFinish);


        /*
        This method(s) run when the user clicks the "Apply" button.
            The selected distance and bearing units are concatenated
            together on a string called 'message' which is returned
            back to the main activity through the setResults method.
         */
        btnFinish.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String message = spinnerDistance.getSelectedItem().toString();
                message += " ";
                message += spinnerBearing.getSelectedItem().toString();

                Intent intent = new Intent();
                intent.putExtra("message", message);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    /*
    Adds km,miles to the distance spinner on the view
     */
    public void addItemsOnDistanceSpinner(){
        spinnerDistance = (Spinner) findViewById(R.id.spinnerDistance);
        List<String> list = new ArrayList<String>();
        list.add("Kilometers");
        list.add("Miles");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDistance.setAdapter(dataAdapter);
    }

    //Adds the mils,degrees to the spinner on the view
    public void addItemsOnBearingSpinner(){
        spinnerBearing = (Spinner) findViewById(R.id.spinnerBearing);
        List<String> list = new ArrayList<String>();
        list.add("Degrees");
        list.add("Mils");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBearing.setAdapter(dataAdapter);
    }
}
