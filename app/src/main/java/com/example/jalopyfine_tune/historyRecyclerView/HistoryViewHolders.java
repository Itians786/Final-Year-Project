package com.example.jalopyfine_tune.historyRecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.jalopyfine_tune.R;


public class HistoryViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView workId;
    public HistoryViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        workId = (TextView) itemView.findViewById(R.id.workId);
    }


    @Override
    public void onClick(View v) {

    }
}
