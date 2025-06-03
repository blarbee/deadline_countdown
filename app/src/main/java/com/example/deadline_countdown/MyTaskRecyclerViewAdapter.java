package com.example.deadline_countdown;

import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.deadline_countdown.placeholder.PlaceholderContent.PlaceholderItem;
import com.example.deadline_countdown.databinding.FragmentItemBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.

 */
public class MyTaskRecyclerViewAdapter extends RecyclerView.Adapter<MyTaskRecyclerViewAdapter.ViewHolder> {
    private TaskDAO dao;
    Context context;

    private final List<Task> mValues;
    private final OnTaskClickListener listener;

    public MyTaskRecyclerViewAdapter(List<Task> items, OnTaskClickListener listener) {
        mValues = items;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // Annuler tout timer existant avant de lier de nouvelles donnees
        if (holder.countdownTimer != null) {
            holder.countdownTimer.cancel();
            holder.countdownTimer = null;
        }

        Task currentTask = mValues.get(position);
        holder.mTask = currentTask;
        holder.mTitleView.setText(currentTask.getTitle());
        holder.itemView.setOnClickListener(v -> listener.OnTaskClick(currentTask));

        String colorName = currentTask.getColor();
        int colorResId = holder.mRootView.getResources().getIdentifier(colorName, "color", holder.mRootView.getContext().getPackageName());
        int color = ContextCompat.getColor(holder.mRootView.getContext(), colorResId);
        ViewCompat.setBackgroundTintList(holder.mRootView, ColorStateList.valueOf(color));

        AppDatabase db = AppDatabase.getInstance(context);
        dao = db.taskDao();
        holder.mDeleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Executors.newSingleThreadExecutor().execute(() -> {
                    dao.delete(currentTask);
                });
            }
        });

        holder.countdownTimer = createCountdownTimer(
                currentTask.getDate_and_time(),
                currentTask.getFormat(),
                holder.mCountdownView
        );
        if (holder.countdownTimer != null) {
            holder.countdownTimer.start();
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mTitleView;
        public final TextView mCountdownView;
        public final ImageButton mDeleteView;
        public Task mTask;

        public CountDownTimer countdownTimer = null;

        public final View mRootView;

        public ViewHolder(FragmentItemBinding binding) {
            super(binding.getRoot());
            mTitleView = binding.itemTitle;
            mCountdownView = binding.itemCountdown;
            mDeleteView  = binding.itemDelButton;

            mRootView = binding.getRoot();
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mCountdownView.getText() + "'";
        }
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.countdownTimer != null) {
            holder.countdownTimer.cancel();
            holder.countdownTimer = null;
        }


    }

    private CountDownTimer createCountdownTimer(String targetDateStr, String formatStr, TextView countdownView) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Date targetDate = sdf.parse(targetDateStr);
            assert targetDate != null;
            long targetMillis = targetDate.getTime();
            long currentMillis = System.currentTimeMillis();
            long diff = targetMillis - currentMillis;

            if (diff > 0) {
                return new CountDownTimer(diff, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        long remaining = millisUntilFinished;
                        StringBuilder display = new StringBuilder();

                        if (formatStr.contains("week")) {
                            long weeks = remaining / (7 * 24 * 60 * 60 * 1000L);
                            display.append(weeks).append("w ");
                            remaining %= (7 * 24 * 60 * 60 * 1000L);
                        }

                        if (formatStr.contains("day")) {
                            long days = remaining / (24 * 60 * 60 * 1000L);
                            display.append(days).append("d ");
                            remaining %= (24 * 60 * 60 * 1000L);
                        }

                        if (formatStr.contains("hour")) {
                            long hours = remaining / (60 * 60 * 1000L);
                            display.append(hours).append("h ");
                            remaining %= (60 * 60 * 1000L);
                        }

                        if (formatStr.contains("min")) {
                            long minutes = remaining / (60 * 1000L);
                            display.append(minutes).append("m ");
                            remaining %= (60 * 1000L);
                        }

                        if (formatStr.contains("sec")) {
                            long seconds = remaining / 1000L;
                            display.append(seconds).append("s");
                        }
                        countdownView.setText(display.toString().trim());
                    }

                    @Override
                    public void onFinish() {
                        countdownView.setText("It's too late now");
                    }
                };
            } else {
                countdownView.setText("That deadline is already in the past");
            }

        } catch (Exception e) {
            countdownView.setText("Invalid date format");
            e.printStackTrace();
        }

        return null;
    }

    public void setData(List<Task> newTasks) {
        mValues.clear();
        mValues.addAll(newTasks);
        notifyDataSetChanged();
    }


}
