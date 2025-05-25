package com.example.deadline_countdown;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executors;

//TODO: add dialogfragment listeners for date and time buttons
public class TaskActivity extends AppCompatActivity {

    TaskDAO dao ;

    private static final String TAG = "debug";
    Calendar calendar = Calendar.getInstance();

    private EditText task_title;
    private Button save_button_v, task_date, task_time;
    CheckBox format_week, format_day, format_hour, format_min, format_sec;
    EditText task_description;

    private String task_title_txt = "";
    private String task_selected_color = "";
    private int task_day, task_month, task_year, task_hour, task_min;
    private boolean selected_week, selected_day, selected_hour, selected_min, selected_sec = false;
    private String optional_description = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task);
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        dao = db.taskDao();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listenForTaskColor();
        listenForDate();
        listenForTime();
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
                String format = getSelectedFormatString();
                String date = String.format(Locale.getDefault(), "%02d/%02d/%04d %02d:%02d", task_day, task_month, task_year, task_hour, task_min);
                if(allFieldsAreCompleted()){
//                    TODO:create task object once all fields are completed
//                    TODO:send it to mainactivity to handle / save the data and let recyclerview handle it
//                    TODO: go back to main activity once clicked and saved


                    Log.d(TAG, "saveTaskOnClick() Task's title:  " + task_title_txt);

                    Log.d(TAG, "saveTaskOnClick() Task's color:  " + task_selected_color);

                    Log.d(TAG, "saveTaskOnClick() Task's date and time:  " + date);

                    Log.d(TAG, "saveTaskOnClick() Task's countdown format: " + format);

                    Task newTask = new Task(task_title_txt, task_selected_color, format, date);

                    if(!optional_description.isEmpty()){
                        Log.d(TAG, "saveTaskOnClick() Task's optional description: " + optional_description);
                        newTask.setDescription(optional_description);

                    }
                    Executors.newSingleThreadExecutor().execute(() -> {
                        dao.insert(newTask);

                        runOnUiThread(() -> {
                            Toast.makeText(TaskActivity.this, "Task saved", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    });

                }else{
                    Log.w(TAG, "saveTaskOnClick() fields are missing");
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
                    Log.i(TAG, "id of the view is "+ getResources().getResourceEntryName(id));
                    task_selected_color = getResources().getResourceEntryName(id);
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
        int current_day = calendar.get(Calendar.DAY_OF_MONTH);
        int current_month = calendar.get(Calendar.MONTH);
        int current_year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                task_year = year;
                task_month = month+1;
                task_day = day;
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String formattedDate = dateFormat.format(calendar.getTime());

                task_date.setText(formattedDate);
            }
        }, current_year, current_month, current_day);
        datePickerDialog.show();
    }

    private void listenForTime(){
        task_time = findViewById(R.id.time_button);
        task_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {openTimeDialog();}
        });
    }

    private void openTimeDialog(){
        int current_hour = calendar.get(Calendar.HOUR);
        int current_minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, min);
                task_hour = hour;
                task_min = min;

                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String formattedTime = timeFormat.format(calendar.getTime());

                task_time.setText(formattedTime);
            }
        }, current_hour, current_minute, true);
        timePickerDialog.show();
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

                if(view.getId() == R.id.format_week && !format_week.isChecked()){
                    selected_week = false;
                } else if (view.getId() == R.id.format_day && !format_day.isChecked()){
                    selected_day = false;
                } else if (view.getId() == R.id.format_hour && !format_hour.isChecked()) {
                    selected_hour = false;
                } else if (view.getId() == R.id.format_min && !format_min.isChecked()) {
                    selected_min = false;
                } else if (view.getId() == R.id.format_sec && !format_sec.isChecked()){
                    selected_sec = false;
                }
            }
        };
    }

    private String getSelectedFormatString() {
        StringBuilder format = new StringBuilder();

        if (selected_week) format.append("week,");
        if (selected_day) format.append("day,");
        if (selected_hour) format.append("hour,");
        if (selected_min) format.append("min,");
        if (selected_sec) format.append("sec,");

        if (format.length() > 0)
            format.setLength(format.length() - 1); // Supprime la dernière virgule

        return format.toString();
    }


    private boolean allFieldsAreCompleted(){
        if(!task_title_txt.isEmpty() || task_selected_color != ""){
            if(task_date.getText() != "Date" || task_time.getText() != "Time"){
                return selected_week || selected_day || selected_hour || selected_min || selected_sec;
            }
        }
        return false;
    }
}