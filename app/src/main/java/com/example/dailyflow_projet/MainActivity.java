package com.example.dailyflow_projet;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private EditText titleInput, dueDateInput, priorityInput;
    private Switch completedSwitch;
    private Button addButton;

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Make sure you have this layout with the correct views

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // UI components
        titleInput = findViewById(R.id.titleInput);
        dueDateInput = findViewById(R.id.dueDateInput);
        priorityInput = findViewById(R.id.priorityInput);
        completedSwitch = findViewById(R.id.completedSwitch);
        addButton = findViewById(R.id.addButton);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList);
        recyclerView.setAdapter(taskAdapter);

        // Add Task Button Click
        addButton.setOnClickListener(v -> addTask());

        // Load existing tasks
        loadTasks();
    }

    private void addTask() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = titleInput.getText().toString().trim();
        String dueDate = dueDateInput.getText().toString().trim();
        String priority = priorityInput.getText().toString().trim();
        boolean completed = completedSwitch.isChecked();
        String userId = currentUser.getUid();

        if (title.isEmpty() || dueDate.isEmpty() || priority.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Task task = new Task(title, dueDate, priority, completed, userId);
        db.collection("Task")
                .add(task)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show();
                    loadTasks(); // Refresh list
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to add task", Toast.LENGTH_SHORT).show());
    }

    private void loadTasks() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        db.collection("Task")
                .whereEqualTo("userId", currentUser.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    taskList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Task task = document.toObject(Task.class);
                        taskList.add(task);
                    }
                    taskAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load tasks", Toast.LENGTH_SHORT).show());
    }
}
