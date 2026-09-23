package com.example.studenthubapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        List<Project> projects = new ArrayList<>();
        projects.add(new Project("Math homework - page 42", "Due tomorrow", false));
        projects.add(new Project("History essay draft", "Due Thursday", false));
        projects.add(new Project("Study for physics exam", "Due next Monday", true));
        projects.add(new Project("English reading response", "Due Friday", false));
        // Extra dummy projects below (temporary) so the list is longer than one screen
        // and we can actually watch RecyclerView recycle rows while scrolling.
        projects.add(new Project("Biology lab report", "Due Sunday", false));
        projects.add(new Project("Chemistry worksheet", "Due Monday", false));
        projects.add(new Project("Read chapter 5 - Bible studies", "Due Tuesday", true));
        projects.add(new Project("Geography map exercise", "Due Wednesday", false));
        projects.add(new Project("Computer science project part 1", "Due next week", false));
        projects.add(new Project("Practice SAT vocabulary", "Due Friday", false));
        projects.add(new Project("Civics reading questions", "Due Thursday", true));
        projects.add(new Project("Art project sketch", "Due next Monday", false));
        projects.add(new Project("Physical education form", "Due tomorrow", false));
        projects.add(new Project("Literature essay outline", "Due Sunday", false));
        projects.add(new Project("Statistics problem set", "Due Tuesday", true));
        projects.add(new Project("Music theory quiz prep", "Due Wednesday", false));
        projects.add(new Project("Economics case study", "Due next week", false));
        projects.add(new Project("Spanish vocabulary review", "Due Friday", false));
        projects.add(new Project("Robotics club report", "Due Thursday", false));
        projects.add(new Project("Volunteer hours log", "Due next Monday", true));

        RecyclerView recyclerView = findViewById(R.id.projectRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ProjectAdapter(projects));
    }
}
