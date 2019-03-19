package com.example.jalopyfine_tune.historyRecyclerView;

public class HistoryObject {
    private String workId;
    private String time;

    public HistoryObject(String workId, String time){
        this.workId = workId;
        this.time = time;
    }

    public String getWorkId(){
        return workId;
    }
    public void setWorkId(String workId){
        this.workId = workId;
    }

    public String getTime(){
        return time;
    }
    public void setTime(String time){
        this.time= time;
    }



}