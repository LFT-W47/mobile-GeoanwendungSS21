package com.example.arcindoor;

import static android.content.ContentValues.TAG;

import com.navigine.naviginesdk.*;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initializing Navigation library (USER_HASH is your personal security key)
        if (!NavigineSDK.initialize(getApplicationContext(), "387A-70EC-D8AE-CB03", null))
            Toast.makeText(this, "Unable to initialize Navigation library!",
                    Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "succes",
                    Toast.LENGTH_LONG).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public class MyActivity extends Activity {
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_main);

            final Button button = findViewById(R.id.button_id);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //hier sollte Navigation starten
                    new LoadTask().execute();
                    updateNavigationResults();
                    NavigineSDK.finish();
                }
            });
        }
    }

    class LoadTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override protected Boolean doInBackground(Void... params)
        {
            return NavigineSDK.loadLocation("ARC-Greenlab", 30) ?
                    Boolean.TRUE : Boolean.FALSE;
        }

        @Override protected void onPostExecute(Boolean result)
        {
            if (result.booleanValue())
            {
                // Location is successully loaded
                // Do whatever you want here, e.g. you can start navigation
                NavigationThread navigation = NavigineSDK.getNavigation();
                if (navigation != null)
                    navigation.setMode(NavigationThread.MODE_NORMAL);
            }
            else
            {
                // Error downloading location
                // Try again later or contact technical support
                Log.d(TAG, "Error downloading location!");
            }
        }
    }


    void updateNavigationResults()
    {
        NavigationThread navigation = NavigineSDK.getNavigation();
        if (navigation == null)  // Check if navigation is available
            return;

        DeviceInfo info = navigation.getDeviceInfo();  // Get device position
        if (!info.isValid())
        {
            Toast.makeText(this, "fak up",
                    Toast.LENGTH_LONG).show();
            // Navigation results are not available
            // Try to find out the problem using navigation error code
            switch (info.errorCode)
            {
                case 4:
                    Log.d(TAG, "You are out of navigation zone! " +
                            "Check that your bluetooth is enabled!");
                    break;

                case 7:
                    Log.d(TAG, "Not enough reference points on the location!");
                    break;

                case 8:
                case 30:
                    Log.d(TAG, "Not enough beacons on the location!");
                    break;

                default:
                    Log.d(TAG, "Something is wrong with the location! " +
                            "Error code ");
                    break;
            }
        }
        else
            Toast.makeText(this, "jah jah",
                    Toast.LENGTH_LONG).show();
            Log.d(TAG, String.format(Locale.ENGLISH, "Device %s: [%d/%d, %.2f, %.2f]",
                    info.id, info.location, info.subLocation, info.x, info.y));
    }

}