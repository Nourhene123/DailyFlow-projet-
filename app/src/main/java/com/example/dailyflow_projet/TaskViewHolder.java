package com.example.dailyflow_projet;

import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class TaskViewHolder extends RecyclerView.ViewHolder {
    public TextView titleText;
    public TextView dueDateText;
    public TextView priorityText;
    public Switch completedSwitch;
    public Button editButton,deleteButton;

    public TaskViewHolder(View itemView) {
        super(itemView);
        titleText = itemView.findViewById(R.id.titleText);
        dueDateText = itemView.findViewById(R.id.dueDateText);
        priorityText = itemView.findViewById(R.id.priorityText);
        completedSwitch = itemView.findViewById(R.id.completedSwitch);
        editButton = itemView.findViewById(R.id.editButton);
        deleteButton = itemView.findViewById(R.id.deleteButton);
    }
}