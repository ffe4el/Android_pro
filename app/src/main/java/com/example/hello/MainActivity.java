package com.example.hello;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Toast는 버튼을 클릭했을때 메세지를 잠깐 화면에 띄우는 기능
    public void onButton1Clicked(View v) {
        Toast.makeText(this, "확인1 버튼 눌렀습니다.", Toast.LENGTH_LONG).show();
    }

    public void onButton2Clicked(View v){
        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.naver.com"));
        startActivity(myIntent);
    }

    public void onButton3Clicked(View v){
        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:010-2977-5662"));
        startActivity(myIntent);
    }
}