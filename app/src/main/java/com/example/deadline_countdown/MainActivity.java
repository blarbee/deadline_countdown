package com.example.deadline_countdown;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "debug";
    TaskFragment taskFragment = new TaskFragment();
    SettingsFragment settingsFragment = new SettingsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        replaceFragment();
    }

    public void replaceFragment(){
        TextView header_text = findViewById(R.id.header_tv);
        getSupportFragmentManager().beginTransaction().replace(R.id.task_fragment_container, taskFragment).commit();
        header_text.setText("Get it done in time!");

        BottomNavigationView nav = findViewById(R.id.nav_bar);
        nav.setOnItemSelectedListener(item ->{

            if(item.getItemId() == R.id.home_nav){
                getSupportFragmentManager().beginTransaction().replace(R.id.task_fragment_container, taskFragment).commit();
                header_text.setText("Get it done in time!");
                return true;
            } else if (item.getItemId() == R.id.settings_nav) {
                getSupportFragmentManager().beginTransaction().replace(R.id.task_fragment_container, settingsFragment).commit();
                header_text.setText("Settings");
                return true;
            } else if (item.getItemId() == R.id.add_task_button) {
                Intent myIntent = new Intent(this, TaskActivity.class);
//                myIntent.putExtra("key", value); //Optional parameters
                this.startActivity(myIntent);
                return true;
            }
            return false;
        });
    }
}

