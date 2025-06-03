package com.example.deadline_countdown;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EditActivity extends AppCompatActivity {
    static int current_id;
    static String current_title;
    static String current_color;
    static String current_format;
    static String current_date_and_time;
    static String current_description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        current_id = getIntent().getIntExtra("task_id", -1);
        current_title = getIntent().getStringExtra("task_title");
        current_color = getIntent().getStringExtra("task_color");
        current_format = getIntent().getStringExtra("task_format");
        current_date_and_time = getIntent().getStringExtra("task_date_and_time");
        current_description = getIntent().getStringExtra("task_description");

        Log.d("debug", "EditActivity intent task_id :" + current_id);
        setData();


    }

    public void setData(){

    }
}

