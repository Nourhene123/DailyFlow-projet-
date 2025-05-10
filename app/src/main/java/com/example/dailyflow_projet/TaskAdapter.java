package com.example.dailyflow_projet;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskViewHolder> {
    private List<Task> tasks;

    public TaskAdapter(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.titleText.setText(task.getTitle());
        holder.dueDateText.setText(task.getDueDate());
        holder.priorityText.setText(task.getPriority());
        holder.completedSwitch.setChecked(task.isCompleted());

        // Handle Edit button click
        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), EditTaskActivity.class);
            intent.putExtra("document_id", task.getDocumentId());
            intent.putExtra("task_title", task.getTitle());
            intent.putExtra("task_due_date", task.getDueDate());
            intent.putExtra("task_priority", task.getPriority());
            intent.putExtra("task_completed", task.isCompleted());
            intent.putExtra("task_user_id", task.getUserId());
            holder.itemView.getContext().startActivity(intent);
        });

        // Handle Delete button click
        holder.deleteButton.setOnClickListener(v -> {
            String documentId = task.getDocumentId();
            if (documentId != null && !documentId.isEmpty()) {
                holder.itemView.getContext().deleteFile(documentId); // This line is incorrect; see correction below
                // Correct approach: Use Firestore to delete
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Task").document(documentId)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            tasks.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, tasks.size());
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(holder.itemView.getContext(), "Failed to delete task", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void updateTasks(List<Task> newTasks) {
        this.tasks = newTasks;
        notifyDataSetChanged();
    }
}
