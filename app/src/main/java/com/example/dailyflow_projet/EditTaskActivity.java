package com.example.dailyflow_projet;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditTaskActivity extends AppCompatActivity {
    private EditText titleEditText, dueDateEditText, priorityEditText;
    private CheckBox completedCheckBox;
    private Button saveButton;
    private String documentId, userId;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        titleEditText = findViewById(R.id.titleEditText);
        dueDateEditText = findViewById(R.id.dueDateEditText);
        priorityEditText = findViewById(R.id.priorityEditText);
        completedCheckBox = findViewById(R.id.completedCheckBox);
        saveButton = findViewById(R.id.saveButton);

        // Get task data from Intent
        documentId = getIntent().getStringExtra("document_id");
        String title = getIntent().getStringExtra("task_title");
        String dueDate = getIntent().getStringExtra("task_due_date");
        String priority = getIntent().getStringExtra("task_priority");
        boolean completed = getIntent().getBooleanExtra("task_completed", false);
        userId = getIntent().getStringExtra("task_user_id");

        // Populate form with task data
        titleEditText.setText(title);
        dueDateEditText.setText(dueDate);
        priorityEditText.setText(priority);
        completedCheckBox.setChecked(completed);

        // Handle Save button click
        saveButton.setOnClickListener(v -> saveTask());
    }

    private void saveTask() {
        String title = titleEditText.getText().toString().trim();
        String dueDate = dueDateEditText.getText().toString().trim();
        String priority = priorityEditText.getText().toString().trim();
        boolean completed = completedCheckBox.isChecked();
        Log.d("EditTaskActivity", "Saving - Title: " + title + ", DueDate: " + dueDate + ", Priority: " + priority + ", Completed: " + completed);
        // Basic validation
        if (title.isEmpty() || dueDate.isEmpty() || priority.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (documentId == null || documentId.isEmpty()) {
            Toast.makeText(this, "Invalid task ID", Toast.LENGTH_SHORT).show();
            Log.e("EditTaskActivity", "Document ID is null or empty");
            return;
        }

        // Create updated task object
        Task updatedTask = new Task(title, dueDate, priority, completed, userId);
        updatedTask.setDocumentId(documentId);

        // Update task in Firestore
        db.collection("Task").document(documentId)
                .set(updatedTask)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error updating task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}