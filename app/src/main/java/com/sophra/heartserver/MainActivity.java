package com.sophra.heartserver;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.sophra.heartserver.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

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

        //sendDataToGoogleSheet(100);

    }

    private void sendDataToGoogleSheet(float bpms) {
        String scriptURL = "https://script.google.com/macros/s/AKfycbyQAwb5bsVVh6MfU8V6DBqTApkqmc1moHqd4kmIzIRhb_kKARu7L6XZNP3M7AirfEHpeA/exec";
        String name = "현재심박수";
        float bpm = bpms;

        new SendDataAsyncTask().execute(scriptURL, name, String.valueOf(bpm));


    }

    private static class SendDataAsyncTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {

            Log.d("heartserver", "실행");

            String scriptUrl = params[0];
            String name = params[1];
            String age = params[2];

            try {
                URL url = new URL(scriptUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                String postData = "name=" + URLEncoder.encode(name, "UTF-8") +
                        "&age=" + URLEncoder.encode(age, "UTF-8");

                // 데이터 전송
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(postData.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                // 응답 받기
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }
                bufferedReader.close();
                inputStream.close();


                Log.d("heartserver", "response : " + response);
                return response.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            // 응답 처리
            if (s != null) {
                // 성공적으로 데이터를 저장한 경우
                Log.d("heartserver", "onPostExecute: success");
                // TODO: 처리 코드 작성
            } else {
                // 데이터 저장 실패한 경우
                Log.d("heartserver", "onPostExecute: fail");
                // TODO: 에러 처리 코드 작성
            }
        }
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
            text_bpm.setText(String.valueOf(sensorEvent.values[0]));
            if(sensorEvent.sensor.getType() == Sensor.TYPE_HEART_RATE)
            {
                Log.d("heartserver", "센서값 변경");
                String msg = "" + sensorEvent.values[0];

                text_bpm.setText(String.valueOf(sensorEvent.values[0]));

                Date date = new Date(System.currentTimeMillis());
                sendDataToGoogleSheet(sensorEvent.values[0]);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

}