package com.ksoocho.farmdiary;


public class AsyncTaskParam {

    int user_id;
    int plant_id;
    String diary_date;

    public AsyncTaskParam(int userId, int plantId, String diaryDate) {
        this.user_id = userId;
        this.plant_id = plantId;
        this.diary_date = diaryDate;
    }


}
