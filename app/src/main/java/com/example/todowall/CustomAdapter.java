package com.example.todowall;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private ArrayList todo_id, todo_title, todo_status;

    CustomAdapter(Context context, ArrayList id, ArrayList title, ArrayList status) {
        this.context = context;
        todo_id = id;
        todo_title = title;
        todo_status = status;
    }
    @NonNull

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.todo_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.MyViewHolder holder, int position) {
        String title = String.valueOf(todo_title.get(position));

        if(String.valueOf(todo_status.get(position)).equals("checked")) {
            holder.todo_title_txt.setPaintFlags(holder.todo_title_txt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.checkBox.setChecked(true);
        } else {
            holder.todo_title_txt.setPaintFlags(holder.todo_title_txt.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }

        holder.todo_title_txt.setText(title);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    ToDoFragment.updateStatus(position, "checked");
                } else {
                    ToDoFragment.updateStatus(position, "unchecked");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return todo_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView todo_title_txt;
        CheckBox checkBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            todo_title_txt = itemView.findViewById(R.id.txtTodo);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
}
