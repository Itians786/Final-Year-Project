package com.example.jalopyfine_tune.historyRecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.jalopyfine_tune.History;
import com.example.jalopyfine_tune.HistorySingleObject;
import com.example.jalopyfine_tune.R;


public class HistoryViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView workId;
    public TextView time;
    public HistoryViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        workId = (TextView) itemView.findViewById(R.id.workId);
        time = (TextView) itemView.findViewById(R.id.time);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), HistorySingleObject.class);
        Bundle bundle = new Bundle();
        bundle.putString("workId", workId.getText().toString());
        intent.putExtras(bundle);
        v.getContext().startActivity(intent);
    }
}
