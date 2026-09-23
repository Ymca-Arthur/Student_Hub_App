package com.example.studenthubapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {

    // Temporary logging tag: lets us watch onCreateViewHolder vs onBindViewHolder
    // frequency in Logcat while scrolling. Safe to remove once the concept clicks.
    private static final String TAG = "RecyclerDemo";

    private final List<Project> projects;

    public ProjectAdapter(List<Project> projects) {
        this.projects = projects;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: building a brand-new row (expensive - inflate + findViewById)");
        View row = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false);
        return new ProjectViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        Project project = projects.get(position);
        Log.d(TAG, "onBindViewHolder: reusing a row to show position " + position + " (\"" + project.getTitle() + "\")");
        holder.titleText.setText(project.getTitle());
        holder.dueDateText.setText(project.getDueDate());
        holder.doneCheckbox.setOnCheckedChangeListener(null);
        holder.doneCheckbox.setChecked(project.isDone());
        holder.doneCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> project.setDone(isChecked));
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    static class ProjectViewHolder extends RecyclerView.ViewHolder {
        final TextView titleText;
        final TextView dueDateText;
        final CheckBox doneCheckbox;

        ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.projectTitleText);
            dueDateText = itemView.findViewById(R.id.projectDueDateText);
            doneCheckbox = itemView.findViewById(R.id.projectDoneCheckbox);
        }
    }
}
