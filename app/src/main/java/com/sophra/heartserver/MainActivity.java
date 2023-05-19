package com.sophra.heartserver;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.widget.TextView;

import com.sophra.heartserver.databinding.ActivityMainBinding;

public class MainActivity extends Activity {

    private TextView mTextView;
    private ActivityMainBinding binding;

    SensorManager sensorManager;
    Sensor hearRate;

    TextView text_bpm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        text_bpm = findViewById(R.id.text_bpm);

        text_bpm.setText("89");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        hearRate = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        //sensorManager.registerListener((SensorEventListener) this,hearRate,SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(msensorEventListner,hearRate,SensorManager.SENSOR_DELAY_FASTEST);



    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(msensorEventListner,hearRate,SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(msensorEventListner);
    }

    private SensorEventListener msensorEventListner = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if(sensorEvent.sensor.getType() == Sensor.TYPE_HEART_RATE)
            {
                String msg = "" + sensorEvent.values[0];
                text_bpm.setText(msg);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

}