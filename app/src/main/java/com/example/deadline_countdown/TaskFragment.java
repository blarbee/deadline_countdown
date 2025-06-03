package com.example.deadline_countdown;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class TaskFragment extends Fragment {
    private static String TAG = "debug";

    private Context context;
    private TaskDAO dao;

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TaskFragment() {
    }

    @SuppressWarnings("unused")
    public static TaskFragment newInstance(int columnCount) {
        TaskFragment fragment = new TaskFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        Context context = requireContext();
        AppDatabase db = AppDatabase.getInstance(context);
        dao = db.taskDao();

        // Set the adapter
        if (view instanceof RecyclerView) {
//            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            // Initialiser avec une liste vide
            MyTaskRecyclerViewAdapter adapter = new MyTaskRecyclerViewAdapter(new ArrayList<>(), task -> {
                Log.d(TAG, "TaskFragment clicked on a task : " + task.id + " Title : " + task.getTitle());

                Intent intent = new Intent(getActivity(), EditActivity.class);
                intent.putExtra("task_id", task.id);
                startActivity(intent);
            });
            recyclerView.setAdapter(adapter);

            dao.getAllTasks().observe(getViewLifecycleOwner(), tasks -> {
                if (tasks != null) {
                    adapter.setData(tasks);
                }
            });

        }

        return view;
    }

}