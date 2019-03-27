package com.example.jalopyfine_tune;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;

import com.example.jalopyfine_tune.historyRecyclerView.HistoryAdapter;
import com.example.jalopyfine_tune.historyRecyclerView.HistoryObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class History extends AppCompatActivity {
    private String historyRequest, userId;

    private RecyclerView mHistoryRecyclerView;
    private RecyclerView.Adapter mHistoryAdapter;
    private RecyclerView.LayoutManager mHistoryLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mHistoryRecyclerView = (RecyclerView) findViewById(R.id.historyRecyclerView);
        mHistoryRecyclerView.setNestedScrollingEnabled(false);
        mHistoryRecyclerView.setHasFixedSize(true);
        mHistoryLayoutManager = new LinearLayoutManager(History.this);
        mHistoryRecyclerView.setLayoutManager(mHistoryLayoutManager);
        mHistoryAdapter = new HistoryAdapter(getDataSetHistory(), History.this);
        mHistoryRecyclerView.setAdapter(mHistoryAdapter);

        historyRequest = getIntent().getExtras().getString("historyRequest");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        getUserHistoryIds();

    }

    private void getUserHistoryIds() {
        DatabaseReference userHistoryDatabase = FirebaseDatabase.getInstance().getReference().child(historyRequest).child(userId).child("history");
        userHistoryDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot history : dataSnapshot.getChildren()){
                        FetchWorkInformation(history.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void FetchWorkInformation(String workKey) {
        DatabaseReference historyDatabase = FirebaseDatabase.getInstance().getReference().child("history").child(workKey);
        historyDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String workId = dataSnapshot.getKey();
                    Long timeStamp = 0L;
                    for (DataSnapshot child : dataSnapshot.getChildren()){
                        if (child.getKey().equals("5timestamp")){
                            timeStamp = Long.valueOf(child.getValue().toString());
                        }
                    }
                    HistoryObject obj = new HistoryObject(workId, getDate(timeStamp));
                    resultsHistory.add(obj);
                    mHistoryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getDate(Long timeStamp) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(timeStamp * 1000);
        String date = DateFormat.format("dd-MM-yyyy hh:mm", cal).toString();

        return date;
    }

    private List resultsHistory = new ArrayList<HistoryObject>();
    private List<HistoryObject> getDataSetHistory() {
        return resultsHistory;
    }

}
