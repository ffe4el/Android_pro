package com.example.projecttest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultTextView = findViewById(R.id.resultTextView);

        // 백그라운드 스레드에서 API 요청을 수행
        new RetrieveDataAsyncTask().execute();
    }

    private class RetrieveDataAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                StringBuilder urlBuilder = new StringBuilder("http://openapi.seoul.go.kr:8088");
                urlBuilder.append("/" + URLEncoder.encode("sample", "UTF-8"));
                urlBuilder.append("/" + URLEncoder.encode("xml", "UTF-8"));
                urlBuilder.append("/" + URLEncoder.encode("CardSubwayStatsNew", "UTF-8"));
                urlBuilder.append("/" + URLEncoder.encode("1", "UTF-8"));
                urlBuilder.append("/" + URLEncoder.encode("5", "UTF-8"));
                urlBuilder.append("/" + URLEncoder.encode("20220301", "UTF-8"));

                URL url = new URL(urlBuilder.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-type", "application/xml");

                if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = rd.readLine()) != null) {
                        sb.append(line);
                    }
                    rd.close();
                    conn.disconnect();
                    return sb.toString();
                } else {
                    // 에러 처리
                    return "Error: " + conn.getResponseCode();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // 결과를 TextView에 표시
            resultTextView.setText(result);
        }
    }
}
