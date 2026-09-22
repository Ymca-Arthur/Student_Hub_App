package com.example.studenthubapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final List<Task> tasks;

    public TaskAdapter(List<Task> tasks) {
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.titleText.setText(task.getTitle());
        holder.dueDateText.setText(task.getDueDate());
        holder.doneCheckbox.setOnCheckedChangeListener(null);
        holder.doneCheckbox.setChecked(task.isDone());
        holder.doneCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> task.setDone(isChecked));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        final TextView titleText;
        final TextView dueDateText;
        final CheckBox doneCheckbox;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.taskTitleText);
            dueDateText = itemView.findViewById(R.id.taskDueDateText);
            doneCheckbox = itemView.findViewById(R.id.taskDoneCheckbox);
        }
    }
}
