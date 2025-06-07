package com.example.deadline_countdown;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.util.concurrent.Executors;

public class EditActivity extends AppCompatActivity {

    Task current_task;
    TaskDAO dao ;

    private static final String TAG = "debug";
    Calendar calendar = Calendar.getInstance();

    private EditText task_title;
    private Button save_button_v, task_date, task_time;
    CheckBox format_week, format_day, format_hour, format_min, format_sec;
    EditText task_description;

    private int task_day, task_month, task_year, task_hour, task_min;
    private boolean selected_week, selected_day, selected_hour, selected_min, selected_sec;
    private String optional_description = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit);
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        dao = db.taskDao();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if(getIntent().getExtras() != null) {
            current_task = (Task) getIntent().getSerializableExtra("task");
            Log.d("debug", "EditActivity intent task_id :" + current_task.getId());
            setData();


        }else{
            Log.e("debug", "EditActivity intent task is null");

        }
        ImageButton delete_button = findViewById(R.id.item_del_button);
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Executors.newSingleThreadExecutor().execute(() -> {
                    dao.delete(current_task);
                    runOnUiThread(() -> {
                        Toast.makeText(EditActivity.this, "Task " + current_task.getTitle() + " was deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                });

            }
        });
        listenForTaskColor();
        listenForDate();
        listenForTime();
        listenForTaskFormat();
        saveTaskOnClick();


    }

    public void setData(){
        EditText titleView = findViewById(R.id.settings_color_option);
        titleView.setText(current_task.getTitle());

        RadioGroup colorsView = findViewById(R.id.color_layout);
        switch(current_task.getColor()){
            case("og_cream"):
                colorsView.check(R.id.og_cream);
                break;
            case("og_green"):
                colorsView.check(R.id.og_green);
                break;
            case("og_white"):
                colorsView.check(R.id.og_white);
                break;
            case("og_orange"):
                colorsView.check(R.id.og_orange);
                break;
        }

        Button dateButton = findViewById(R.id.date_button);
        Button timeButton = findViewById(R.id.time_button);
        String[] parts = current_task.getDate_and_time().split(" ");

        String datePart = parts[0];
        String timePart = parts[1];
        dateButton.setText(datePart);
        timeButton.setText(timePart);

        format_week = findViewById(R.id.format_week);
        format_day = findViewById(R.id.format_day);
        format_hour = findViewById(R.id.format_hour);
        format_min = findViewById(R.id.format_min);
        format_sec = findViewById(R.id.format_sec);
        task_date = findViewById(R.id.date_button);
        task_time = findViewById(R.id.time_button);

        if (current_task.getFormat().contains("week")){
            format_week.setChecked(true);
            selected_week = true;
        }
        if (current_task.getFormat().contains("day")){
            format_day.setChecked(true);
            selected_day = true;
        }
        if (current_task.getFormat().contains("hour")){
            format_hour.setChecked(true);
            selected_hour = true;
        }
        if (current_task.getFormat().contains("min")){
            format_min.setChecked(true);
            selected_min = true;
        }
        if (current_task.getFormat().contains("sec")){
            format_sec.setChecked(true);
            selected_sec = true;
        }

        task_description = findViewById(R.id.task_description);
        if(current_task.getDescription() != ""){
            task_description.setText(current_task.getDescription());
        }

    }

    public void saveTaskOnClick(){
        save_button_v = findViewById(R.id.save_button);
        save_button_v.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                task_title = findViewById(R.id.settings_color_option);

                current_task.setTitle(task_title.getText().toString());

                optional_description = String.valueOf(task_description.getText());
                String format = getSelectedFormatString();
                String date = String.format(Locale.getDefault(), "%02d/%02d/%04d %02d:%02d", task_day, task_month, task_year, task_hour, task_min);
                if(allFieldsAreCompleted()){

                    current_task.setFormat(format);
                    current_task.setDate_and_time(task_date.getText() + " " + task_time.getText());

                    Log.d(TAG, "saveTaskOnClick() Task's title:  " + current_task.getTitle());

                    Log.d(TAG, "saveTaskOnClick() Task's color:  " + current_task.getColor());

                    Log.d(TAG, "saveTaskOnClick() Task's date and time:  " + current_task.getDate_and_time());

                    Log.d(TAG, "saveTaskOnClick() Task's countdown format: " + current_task.getFormat());


                    if(!task_description.getText().isEmpty()){
                        Log.d(TAG, "saveTaskOnClick() Task's optional description: " + optional_description);
                        current_task.setDescription(optional_description);
                    }else{
                        current_task.setDescription("");
                    }
                    Executors.newSingleThreadExecutor().execute(() -> {
                        dao.update(current_task);

                        runOnUiThread(() -> {
                            Toast.makeText(EditActivity.this, "Task saved", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    });

                }else{
                    Log.w(TAG, "saveTaskOnClick() fields are missing");
                    Toast.makeText(EditActivity.this, "Missing required fields", Toast.LENGTH_SHORT).show();
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
                    current_task.setColor(getResources().getResourceEntryName(id));
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
        String task_title_txt = String.valueOf(task_title.getText());
        String task_selected_color = "";
        if(!task_title_txt.isEmpty() || task_selected_color != ""){
            if(task_date.getText() != "Date" || task_time.getText() != "Time"){
                return selected_week || selected_day || selected_hour || selected_min || selected_sec;
            }
        }
        return false;
    }
}

