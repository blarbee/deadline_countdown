package com.example.deadline_countdown;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    TaskFragment taskFragment = new TaskFragment();
    SettingsFragment settingsFragment = new SettingsFragment();

    DatePickerDialog datePickerDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putInt("some_int", 0);

            getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).add(R.id.task_fragment_container, TaskFragment.class, bundle).commit();
        }
        replaceFragment();

    }

    public void replaceFragment(){
        TextView header_text = findViewById(R.id.header_tv);
        header_text.setText("Get it done in time!");
        getSupportFragmentManager().beginTransaction().replace(R.id.task_fragment_container, taskFragment).commit();
        BottomNavigationView nav = findViewById(R.id.nav_bar);
        nav.setOnItemSelectedListener(item ->{


            if(item.getItemId() == R.id.home_nav){
                getSupportFragmentManager().beginTransaction().replace(R.id.task_fragment_container, taskFragment).commit();
                header_text.setText("Get it done in time!");
                Toast.makeText(this, "Go to homepage", Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.settings_nav) {
                getSupportFragmentManager().beginTransaction().replace(R.id.task_fragment_container, settingsFragment).commit();
                header_text.setText("Settings");
                Toast.makeText(this, "Go to settings", Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.add_task_button) {
                Intent myIntent = new Intent(this, TaskActivity.class);
//                myIntent.putExtra("key", value); //Optional parameters
                this.startActivity(myIntent);
                Toast.makeText(this, "Add a task", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

}