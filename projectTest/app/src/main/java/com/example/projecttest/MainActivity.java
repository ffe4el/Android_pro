package com.example.projecttest;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private TextView resultTextView;
    String key="7462685365636f6439327457626243";
    String data;

    //파싱한 좌표 데이터를 저장하는 ArrayList 선언
    private ArrayList<LatLng> coordinates = new ArrayList<>();

    //각 좌표에 대한 정보를 저장하는 HashMap
    private HashMap<LatLng, MarkerInfo> markerInfoMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultTextView = findViewById(R.id.resultTextView);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    data = data();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resultTextView.setText(data);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    String data() throws IOException{
        String result;

        StringBuilder urlBuilder = new StringBuilder("http://openapi.seoul.go.kr:8088");
        urlBuilder.append("/" + URLEncoder.encode(key, "UTF-8"));
        urlBuilder.append("/" + URLEncoder.encode("xml", "UTF-8"));
        urlBuilder.append("/" + URLEncoder.encode("TbGtnHwcwP", "UTF-8"));
        urlBuilder.append("/" + URLEncoder.encode("1", "UTF-8"));
        urlBuilder.append("/" + URLEncoder.encode("200", "UTF-8"));
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
//            Log.v("태그", "결과 값"  + sb.toString());
            result = sb.toString();
        } else {
            // 에러 처리
            return "Error: " + conn.getResponseCode();
        }
        //XML 파싱 부분을 data() 함수의 끝에 추가
        try {
            return parseXML(result);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return "XML Parsing Error: " + e.getMessage();
        }
    }

    // XML 문자열을 파싱하여 LO와 LA 값을 추출하는 메서드
    String parseXML(String xml) throws XmlPullParserException, IOException {
        double lo = 0.0, la=0.0; // 위도 경도 임시저장 변수
        String r_add = null;
        double area=0.0;
        int user=0, fan=0, air=0;

        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(new StringReader(xml));

        StringBuilder resultBuilder = new StringBuilder();
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) { //xml의 end_document가 나올때까지
            if (eventType == XmlPullParser.START_TAG && parser.getName().equals("row")) {
                // row 태그 내부를 처리
                while (!(eventType == XmlPullParser.END_TAG && parser.getName().equals("row"))) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (parser.getName().equals("R_DETL_ADD")) {
                            parser.next(); // R_DETL_ADD 값으로 이동
                            resultBuilder.append("도로명 주소: ").append(parser.getText()).append("\n");
                            r_add = parser.getText();
                        } else if (parser.getName().equals("R_AREA_SQR")) {
                            parser.next(); // R_AREA_SQR 값으로 이동
                            resultBuilder.append("면적: ").append(parser.getText()).append("\n");
                            area= Double.parseDouble(parser.getText());
                        } else if (parser.getName().equals("USE_PRNB")) {
                            parser.next(); // USE_PRNB 값으로 이동
                            resultBuilder.append("이용 가능 인원: ").append(parser.getText()).append("\n");
                            user = Integer.parseInt(parser.getText());
                        } else if (parser.getName().equals("CLER1_CNT")) {
                            parser.next(); // CLER1_CNT 값으로 이동
                            resultBuilder.append("선풍기 보유 대수: ").append(parser.getText()).append("\n");
                            fan= Integer.parseInt(parser.getText());
                        } else if (parser.getName().equals("CLER2_CNT")) {
                            parser.next(); // CLER2_CNT 값으로 이동
                            resultBuilder.append("에어컨 보유 대수: ").append(parser.getText()).append("\n");
                            air= Integer.parseInt(parser.getText());
                        } else if (parser.getName().equals("LO")) {
                            parser.next(); // LO 값으로 이동
//                            resultBuilder.append("LO: ").append(parser.getText()).append("\n");
                            lo = Double.parseDouble(parser.getText());
                        } else if (parser.getName().equals("LA")) {
                            parser.next(); // LA 값으로 이동
//                            resultBuilder.append("LA: ").append(parser.getText()).append("\n");
                            la = Double.parseDouble(parser.getText());
                        }
                    }
                    eventType = parser.next();
                }
                //MarkerInfo 인스턴스를 생성하여 정보 저장
                MarkerInfo markerInfo = new MarkerInfo(r_add, area, user, fan, air);
                markerInfoMap.put(new LatLng(la, lo), markerInfo);

                coordinates.add(new LatLng(la, lo));
            }
            eventType = parser.next();

        }

        //모든 좌표가 파싱되고 리스트에 추가되면 MapsActivity가 시작됨
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        intent.putParcelableArrayListExtra("coordinates", coordinates);
        //정보 맵을 전달
        intent.putExtra("markerInfoMap", markerInfoMap);
        startActivity(intent);

//        return resultBuilder.toString();
        return "success";
    }





//    private class RetrieveDataAsyncTask extends AsyncTask<Void, Void, String> {
//        @Override
//        protected String doInBackground(Void... voids) {
//            try {
//                StringBuilder urlBuilder = new StringBuilder("https://openapi.seoul.go.kr:8088");
//                urlBuilder.append("/" + URLEncoder.encode(key, "UTF-8"));
//                urlBuilder.append("/" + URLEncoder.encode("xml", "UTF-8"));
//                urlBuilder.append("/" + URLEncoder.encode("TbGtnHwcwP", "UTF-8"));
//                urlBuilder.append("/" + URLEncoder.encode("1", "UTF-8"));
//                urlBuilder.append("/" + URLEncoder.encode("200", "UTF-8"));
//                URL url = new URL(urlBuilder.toString());
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setRequestMethod("GET");
//                conn.setRequestProperty("Content-type", "application/xml");
//
//                if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
//                    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                    StringBuilder sb = new StringBuilder();
//                    String line;
//                    while ((line = rd.readLine()) != null) {
//                        sb.append(line);
//                    }
//                    rd.close();
//                    conn.disconnect();
//                    return sb.toString();
//                } else {
//                    // 에러 처리
//                    return "Error: " + conn.getResponseCode();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                return "Error: " + e.getMessage();
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            // 결과를 TextView에 표시
//            resultTextView.setText(result);
//        }
//    }
}
