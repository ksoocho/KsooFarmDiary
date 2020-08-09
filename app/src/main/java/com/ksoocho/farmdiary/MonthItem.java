package com.ksoocho.farmdiary;

public class MonthItem {

    int day;

    String lunarDate;
    String ganJi;
    String calMemo;
    String checkDiary;
    String diaryDescr;
    int dayColor;

    public MonthItem(int day) {
        this.day = day;
    }

    public int getDay() {
        return day;
    }
    public void setDay(int day) {
        this.day = day;
    }

    public String getLunarDate() {
        return lunarDate;
    }
    public void setLunarDate(String lunarDate) {
        this.lunarDate = lunarDate;
    }

    public String getGanJi() {
        return ganJi;
    }
    public void setGanJi(String ganJi) {
        this.ganJi = ganJi;
    }

    public String getCalMemo() {
        return calMemo;
    }
    public void setCalMemo(String calMemo) {
        this.calMemo = calMemo;
    }

    public String getCheckDiary() {
        return checkDiary;
    }
    public void setCheckDiary(String checkDiary) {
        this.checkDiary = checkDiary;
    }

    public String getDiaryDescr() {
        return diaryDescr;
    }
    public void setDiaryDescr(String diaryDescr) {
        this.diaryDescr = diaryDescr;
    }

    public int getDayColor() {
        return dayColor;
    }
    public void setDayColor(int color) {
        this.dayColor = color;
    }
}
