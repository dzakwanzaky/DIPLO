package com.example.acer.diplo;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class NoteViewHolder extends RecyclerView.ViewHolder {

    private TextView textTitle, textTime;
    CardView noteCard;

    public NoteViewHolder(View itemView) {
        super(itemView);

        textTitle = itemView.findViewById(R.id.note_title);
        textTime = itemView.findViewById(R.id.note_time);
        noteCard = itemView.findViewById(R.id.note_card);

    }

    public void setNoteTitle(String title) {
        textTitle.setText(title);
    }

    public void setNoteTime(String time) {
        textTime.setText(time);
    }

}
