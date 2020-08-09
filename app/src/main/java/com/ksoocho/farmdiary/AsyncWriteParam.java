package com.ksoocho.farmdiary;


public class AsyncWriteParam {

    int user_id;
    int plant_id;
    String diary_date;
    String plan_descr;
    String act_descr;
    String memo_descr;

    public AsyncWriteParam(int userId, int plantId, String diaryDate, String planDescr, String actDescr, String memoDescr) {
        this.user_id = userId;
        this.plant_id = plantId;
        this.diary_date = diaryDate;
        this.plan_descr = planDescr;
        this.act_descr = actDescr;
        this.memo_descr = memoDescr;
    }


}
