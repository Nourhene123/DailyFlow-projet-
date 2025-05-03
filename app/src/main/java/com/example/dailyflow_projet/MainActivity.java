package com.example.dailyflow_projet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Spinner spinner;
    private FirebaseFirestore db;
    private List<Task> taskList;
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("MainActivity: onCreate started");
        setContentView(R.layout.activity_main);
        System.out.println("MainActivity: Layout set");

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        System.out.println("MainActivity: Firestore initialized");

        // Initialize UI components
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        spinner = findViewById(R.id.filterSpinner);
        System.out.println("MainActivity: UI components initialized");

        // Initialize task list and adapter
        taskList = new ArrayList<>();
        System.out.println("MainActivity: taskList initialized");
        taskAdapter = new TaskAdapter(taskList);
        System.out.println("MainActivity: taskAdapter initialized");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(taskAdapter);
        System.out.println("MainActivity: RecyclerView set up");

        //  Spinner for filtering
        String[] filterOptions = {"All", "Completed", "High Priority"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filterOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        System.out.println("MainActivity: Spinner set up");

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String filter = parent.getItemAtPosition(position).toString();
                applyFilter(filter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        swipeRefreshLayout.setOnRefreshListener(() -> {
            applyFilter(spinner.getSelectedItem().toString());
            swipeRefreshLayout.setRefreshing(false);
        });
        System.out.println("MainActivity: SwipeRefreshLayout set up");


        applyFilter("All");
        System.out.println("MainActivity: Initial filter applied");
    }

    private void applyFilter(String filter) {
        System.out.println("applyFilter: Starting with filter = " + filter);
        if (!isInternetAvailable()) {
            System.out.println("applyFilter: No internet connection");
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = "user123";
        System.out.println("applyFilter: userId = " + userId);
        Query query;
        if ("Completed".equals(filter)) {
            query = db.collection("Task").whereEqualTo("userId", userId).whereEqualTo("completed", true);
        } else if ("High Priority".equals(filter)) {
            query = db.collection("Task").whereEqualTo("userId", userId).whereEqualTo("priority", "High");
        } else {
            query = db.collection("Task").whereEqualTo("userId", userId);
        }
        System.out.println("applyFilter: Query created");

        query.get()
                .addOnSuccessListener(result -> {
                    System.out.println("Firestore: Found " + result.size() + " tasks");
                    taskList.clear();
                    System.out.println("applyFilter: taskList cleared");
                    for (QueryDocumentSnapshot document : result) {
                        Task task = document.toObject(Task.class);
                        taskList.add(task);
                        System.out.println("applyFilter: Added task - " + task.getTitle());
                    }
                    if (taskList.isEmpty()) {
                        Toast.makeText(this, "No tasks found", Toast.LENGTH_SHORT).show();
                    }
                    taskAdapter.notifyDataSetChanged();
                    System.out.println("applyFilter: Adapter notified");
                })
                .addOnFailureListener(e -> {
                    System.out.println("Firestore Error: " + e.getMessage());
                    Toast.makeText(this, "Failed to load tasks: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }
}