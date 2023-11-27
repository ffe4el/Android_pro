package com.example.arrayadaptertest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView lv_students;
    ArrayList<String> students;
    ArrayAdapter arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv_students = findViewById(R.id.lv_students);
        students = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, students);
        lv_students.setAdapter(arrayAdapter);

        students.add("Faker");
        students.add("Gumayusi");
        students.add("Keria");
        students.add("Oner");
        students.add("Zeus");

        students.add("MadLife");
        students.add("Insec");
        students.add("CloudTempler");
        students.add("Ambition");
        students.add("Dopa");

        students.add("Bengi");
        students.add("Bang");
        students.add("TheShy");
        students.add("Smeb");
        students.add("Duke");

        lv_students.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, students.get(position)
                        +", id:" +id, Toast.LENGTH_SHORT).show();
            }
        });
    }
}