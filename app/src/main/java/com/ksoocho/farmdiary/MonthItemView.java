package com.ksoocho.farmdiary;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 달력 날짜별 정보 Display
 */
public class MonthItemView extends RelativeLayout {

    TextView textSolar;
    TextView textLunar;
    TextView textSeason;
    TextView textGanji;
    TextView textCheckDiary;

    public MonthItemView(Context context) {
        super(context);
        init(context);
    }

    public MonthItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.month_item, this,true);

        textSolar = (TextView) findViewById(R.id.textSolar);
        textLunar = (TextView) findViewById(R.id.textLunar);
        textSeason = (TextView) findViewById(R.id.textSeason);
        textGanji = (TextView) findViewById(R.id.textGanji);
        textCheckDiary = (TextView) findViewById(R.id.textCheckDiary);

    }

    public void setDay(int day) {
        if ( day == 0 ) {
            textSolar.setText(" ");
        } else {
            textSolar.setText(String.valueOf(day));
        }
    }

    public void setLunar(int day, String lunarDay) {
        if ( day == 0 ) {
            textLunar.setText(" ");
        } else {
            textLunar.setText(lunarDay);
        }
    }

    public void setSeason(int day, String seasonDay) {
        if ( day == 0 ) {
            textSeason.setText(" ");
        } else {
            textSeason.setText(seasonDay);
        }
    }

    public void setGanji(int day, String ganji) {
        if ( day == 0 ) {
            textGanji.setText(" ");
        } else {
            textGanji.setText(ganji);
        }
   }

    public void setCheckDiary(int day, String checkDiary) {
        if ( day == 0 ) {
            textCheckDiary.setText(" ");
        } else {
            textCheckDiary.setText(checkDiary);
        }
    }

    public void setColor(int color) {
        textSolar.setTextColor(color);
    }

}
