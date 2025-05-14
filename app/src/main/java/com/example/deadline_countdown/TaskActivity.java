package com.example.deadline_countdown;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

//TODO: add dialogfragment listeners for date and time buttons
public class TaskActivity extends AppCompatActivity {
    private static final String TAG = "debug";

    private int current_day, current_month, current_year;


    private EditText task_title;
    private Button save_button_v, task_date, task_time;
    CheckBox format_week, format_day, format_hour, format_min, format_sec;
    EditText task_description;

    private String task_title_txt = "";
    private int task_selected_color = 0;
    private boolean selected_week, selected_day, selected_hour, selected_min, selected_sec = false;
    private String optional_description = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });




        listenForTaskColor();
        listenForDate();
        listenForTaskFormat();
        saveTaskOnClick();

    }

    public void saveTaskOnClick(){
        save_button_v = findViewById(R.id.save_button);
        save_button_v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task_title = findViewById(R.id.settings_color_option);
                task_title_txt = TaskActivity.this.task_title.getText().toString();
                task_description = findViewById(R.id.task_description);
                optional_description = task_description.getText().toString();
                if(allFieldsAreCompleted()){
//                    TODO:create task object once all fields are completed
//                    TODO:send it to mainactivity to handle / save the data and let recyclerview handle it
//                    TODO: go back to main activity once clicked and saved

                    Log.d(TAG, "task_title onclicksavebutton: " + task_title_txt);

                    Log.d(TAG, "task_color onCheckedChanged: " + task_selected_color);

                    Log.d(TAG, "datetime format week = " + selected_week + " day = " + selected_day + " hour = " + selected_hour + " min = " + selected_min + " sec = " + selected_sec);

                    if(!optional_description.isEmpty()){
                        Log.d(TAG, "optional description: " + optional_description);
//                        TODO: Task.setDescription()
                    }

                }else{
                    Log.d(TAG, "fields are missing");
                    Toast.makeText(TaskActivity.this, "Missing required fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void listenForTaskColor(){
        RadioGroup task_color_group = findViewById(R.id.color_layout);
        task_color_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                if(findViewById(id) != null && findViewById(id).getBackgroundTintList() != null){
                    task_selected_color = findViewById(id).getBackgroundTintList().getDefaultColor();
                }
            }
        });
    }

    public void listenForDate(){
        task_date = findViewById(R.id.date_button);
        task_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDateDialog();
            }
        });
    }

    public void openDateDialog(){
        Calendar calendar = Calendar.getInstance();
        current_day = calendar.get(Calendar.DAY_OF_MONTH);
        current_month = calendar.get(Calendar.MONTH);
        current_year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String formattedDate = dateFormat.format(calendar.getTime());

                task_date.setText(formattedDate);
            }
        }, current_year, current_month, current_day);
        datePickerDialog.show();
    }

    public void listenForTaskFormat(){
        format_week = findViewById(R.id.format_week);
        format_day = findViewById(R.id.format_day);
        format_hour = findViewById(R.id.format_hour);
        format_min = findViewById(R.id.format_min);
        format_sec = findViewById(R.id.format_sec);
        task_date = findViewById(R.id.date_button);
        task_time = findViewById(R.id.time_button);

        format_week.setOnClickListener(this.onClickCheckboxFormat());
        format_day.setOnClickListener(this.onClickCheckboxFormat());
        format_hour.setOnClickListener(this.onClickCheckboxFormat());
        format_min.setOnClickListener(this.onClickCheckboxFormat());
        format_sec.setOnClickListener(this.onClickCheckboxFormat());
    }

    private View.OnClickListener onClickCheckboxFormat() {
        return new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                CheckBox checkbox = (CheckBox) view;

                if(view.getId() == R.id.format_week && format_week.isChecked()){
                    selected_week = true;
                } else if (view.getId() == R.id.format_day && format_day.isChecked()){
                    selected_day = true;
                } else if (view.getId() == R.id.format_hour && format_hour.isChecked()) {
                    selected_hour = true;
                } else if (view.getId() == R.id.format_min && format_min.isChecked()) {
                    selected_min = true;
                } else if (view.getId() == R.id.format_sec && format_sec.isChecked()){
                    selected_sec = true;
                }
            }
        };
    }

    private boolean allFieldsAreCompleted(){
        if(!task_title_txt.isEmpty() && task_selected_color != 0){
            return selected_week || selected_day || selected_hour || selected_min || selected_sec;
        }
        return false;
    }
}