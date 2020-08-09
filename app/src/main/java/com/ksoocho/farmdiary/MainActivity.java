package com.ksoocho.farmdiary;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.ksoocho.farmdiary.SimpleGestureFilter.SimpleGestureListener;

/**
 * 꼼꼼농부달력 Main Activity
 */
public class MainActivity extends AppCompatActivity implements SimpleGestureListener {

    static Context mContext;

    private SimpleGestureFilter detector;

    MonthItem[] items;

    TextView monthText;
    GridView monthView;
    MonthAdapter adapter;

    int start_inx;
    int farmerUserId;
    int farmerPlantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        // 농부 ID Setting
        farmerUserId = new PrefManager(this).getUserId();
        farmerPlantId = 0; // 일반, 농작물별 일지 추가예정

        // 화면 Title 변경하기
        String vTitle = new PrefManager(this).getUserName()+" 일기";
        setTitle(vTitle);

        setContentView(R.layout.activity_main);

        // Detect touched area
        detector = new SimpleGestureFilter(MainActivity.this, this);

        start_inx = 0;

        monthText = (TextView) findViewById(R.id.monthText);
        monthView = (GridView) findViewById(R.id.monthView);

        adapter = new MonthAdapter();
        monthView.setAdapter(adapter);

        monthText.setText(adapter.getCurrentYear()+"년 "+adapter.getCurrentMonth()+"월");

        // ------------------------------------------------------------------
        // 달력 날짜를 Click한 경우
        // ------------------------------------------------------------------
        monthView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int vDay = items[position].day;

                if ( vDay > 0 ) {

                    int vYear = adapter.getCurrentYear();
                    int vMonth = adapter.getCurrentMonth();

                    String vLunarDate =  items[position].lunarDate;
                    String vGanJi=  items[position].ganJi;
                    String vCalMemo =  items[position].calMemo;
                    String vCheckDiary =  items[position].checkDiary;

                    Calendar vCal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    vCal.set(vYear, vMonth-1, vDay);
                    Date vDate = vCal.getTime();
                    String vSelectDate  = sdf.format(vDate);

                    int dayNum = vCal.get(Calendar.DAY_OF_WEEK) ;
                    String vWeekDay = "";

                    switch(dayNum){
                        case 1:
                            vWeekDay = "일요일";
                            break ;
                        case 2:
                            vWeekDay = "월요일";
                            break ;
                        case 3:
                            vWeekDay = "화요일";
                            break ;
                        case 4:
                            vWeekDay = "수요일";
                            break ;
                        case 5:
                            vWeekDay = "목요일";
                            break ;
                        case 6:
                            vWeekDay = "금요일";
                            break ;
                        case 7:
                            vWeekDay = "토요일";
                            break ;

                    }

                    // 날짜정보
                    String vDateInfo = vWeekDay+" "+vLunarDate + " "+ vGanJi +" " + vCalMemo;

                    // 다른 화면 호출
                    Intent intent;

                    if (vCheckDiary != null && vCheckDiary.equals("Y")) {
                        intent = new Intent( mContext, DiaryActivity.class); // 읽기화면
                    } else {
                        intent = new Intent( mContext, WriteActivity.class); // 쓰기화면
                    }

                    intent.putExtra("USERID", farmerUserId);
                    intent.putExtra("PLANTID", farmerPlantId);

                    intent.putExtra("YEAR", vYear);
                    intent.putExtra("MONTH", vMonth);
                    intent.putExtra("DAY", vDay);

                    intent.putExtra("DATE", vSelectDate);
                    intent.putExtra("DATE_INFO", vDateInfo);

                    // ----------------------------------------------
                    // Activity is started with requestCode 2
                    // ----------------------------------------------
                    //mContext.startActivity(intent);
                    startActivityForResult(intent, 2);
                }
            }
        });
    }

    // -----------------------------------------------------------------
    // Call Back method  to get the Message form other Activity
    // -----------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // check if the request code is same as what is passed  here it is 2

        switch (requestCode) {


            case RESULT_OK:  // Back Button

                break;

            case 1: // WeatherActivity

                break;

            case 2: // WriteActivity , DiaryActivity

                refreshMonth();
                String message=data.getStringExtra("MESSAGE");
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();

                break;

            default:

                break;

        }

    }

    // -----------------------------------------------------------------
    // Option Menu 보이기 - onCreateOptionsMenu
    // -----------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // -----------------------------------------------------------------
    // Option Menu 선택한 경우 - onOptionsItemSelected
    // -----------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int curId = item.getItemId();

        switch (curId) {
            case R.id.menu_refresh:
                setMonthCurrent();
                Toast.makeText(this,"Refresh", Toast.LENGTH_LONG).show();
                break;

            case R.id.menu_setting:

                Intent intentWeather;
                intentWeather = new Intent( mContext, WeatherActivity.class); // 날씨화면

                //mContext.startActivity(intentWeather);
                startActivityForResult(intentWeather, 1);

                Toast.makeText(this,"날씨정보", Toast.LENGTH_LONG).show();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // -----------------------------------------------------------------
    // Gesture 정의 - Swipe
    // -----------------------------------------------------------------
    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    @Override
    public void onSwipe(int direction) {

        //Detect the swipe gestures and display toast
        String showToastMessage = "";

        switch (direction) {

            case SimpleGestureFilter.SWIPE_RIGHT:
                setMonthPrevious();
                showToastMessage = "Swipe Right";
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                setMonthNext();
                showToastMessage = "Swipe Left";
                break;
            case SimpleGestureFilter.SWIPE_DOWN:
                setYearPrevious();
                showToastMessage = "Swipe Down";
                break;
            case SimpleGestureFilter.SWIPE_UP:
                setYearNext();
                showToastMessage = "Swipe Up";
                break;

        }
        Toast.makeText(this, showToastMessage, Toast.LENGTH_SHORT).show();
    }


    //Toast shown when double tapped on screen
    @Override
    public void onDoubleTap() {
        Toast.makeText(this, "Double Tapped.", Toast.LENGTH_SHORT).show();
    }

    // ---------------------------------
    // Set Current Month
    // ---------------------------------
    public void setMonthCurrent() {

        adapter.setCurrMonth();
        adapter.notifyDataSetChanged();
        monthText.setText(adapter.getCurrentYear()+"년 "+adapter.getCurrentMonth()+"월");

    }

    // ---------------------------------
    // Set Previous Month
    // ---------------------------------
    public void setMonthPrevious() {

        adapter.setPrevMonth();
        adapter.notifyDataSetChanged();
        monthText.setText(adapter.getCurrentYear()+"년 "+adapter.getCurrentMonth()+"월");
    }

    // ---------------------------------
    // Set Next Month
    // ---------------------------------
    public void setMonthNext() {

        adapter.setNextMonth();
        adapter.notifyDataSetChanged();
        monthText.setText(adapter.getCurrentYear()+"년 "+adapter.getCurrentMonth()+"월");
    }

    // ---------------------------------
    // Set Previous Year
    // ---------------------------------
    public void setYearPrevious() {
        adapter.setPrevYear();
        adapter.notifyDataSetChanged();
        monthText.setText(adapter.getCurrentYear()+"년 "+adapter.getCurrentMonth()+"월");
    }

    // ---------------------------------
    // Set Next Year
    // ---------------------------------
    public void setYearNext() {
        adapter.setNextYear();
        adapter.notifyDataSetChanged();
        monthText.setText(adapter.getCurrentYear()+"년 "+adapter.getCurrentMonth()+"월");
    }

    // ---------------------------------
    // Set Current Month
    // ---------------------------------
    public void refreshMonth() {
        adapter.refreshMonth();
        adapter.notifyDataSetChanged();
    }

    // ---------------------------------
    // Month Adapter
    // ---------------------------------
    class MonthAdapter extends BaseAdapter {

        Calendar calendar;

        int firstDay;
        int lastDay;
        int curYear;
        int curMonth;

        public MonthAdapter() {

            items = new MonthItem[7*6];

            Date date = new Date();
            calendar = Calendar.getInstance();
            calendar.setTime(date);

            recalculate();
            resetDayNumbers();
        }

        public void setCurrMonth() {
            Date date = new Date();
            calendar.setTime(date);
            recalculate();
            resetDayNumbers();
        }

        public void setPrevMonth() {
            calendar.add(Calendar.MONTH, -1);
            recalculate();
            resetDayNumbers();
        }

        public void setNextMonth() {
            calendar.add(Calendar.MONTH, 1);
            recalculate();
            resetDayNumbers();
        }

        public void setPrevYear() {
            calendar.add(Calendar.MONTH, -12);
            recalculate();
            resetDayNumbers();
        }

        public void setNextYear() {
            calendar.add(Calendar.MONTH, 12);
            recalculate();
            resetDayNumbers();
        }

        public void refreshMonth() {

            recalculate();
            resetDayNumbers();
        }

        public int getCurrentYear() {
            return curYear;
        }

        public int getCurrentMonth() {
            return curMonth+1;
        }

        public void recalculate() {

            calendar.set(Calendar.DAY_OF_MONTH,1);

            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

            firstDay = getFirstDay(dayOfWeek);

            curYear = calendar.get(Calendar.YEAR);
            curMonth = calendar.get(Calendar.MONTH);

            lastDay = getLastDay();

        }

        public void resetDayNumbers() {

            Calendar vCal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
            vCal.set(curYear, curMonth, 1);
            Date vDate = vCal.getTime();
            String vYyyymm = sdf.format(vDate);

            // -------------------------------------
            // 농부달력정보 가져오기
            // -------------------------------------
            new PostAsyncCalendar().execute(new AsyncTaskParam(farmerUserId,farmerPlantId,vYyyymm));

            // 양력
            for (int i = 0; i < 42; i++) {
                int dayNumber = (i+1) - firstDay;

                if (dayNumber < 1 || dayNumber > lastDay) {
                    dayNumber = 0;
                }

                // 양력일자
                items[i] = new MonthItem(dayNumber);

                if ( dayNumber == 1 ) {
                    start_inx = i;
                }

                if (i%7==0) {
                    items[i].setDayColor(Color.RED);
                } else if (i%7==6) {
                    items[i].setDayColor(Color.BLUE);
                } else {
                    items[i].setDayColor(Color.BLACK);
                }
            }
        }

        public int getFirstDay(int dayOfWeek) {

            int result = 0;

            if(dayOfWeek == Calendar.SUNDAY) {
                result = 0;
            } else if(dayOfWeek == Calendar.MONDAY) {
                result = 1;
            } else if(dayOfWeek == Calendar.TUESDAY) {
                result = 2;
            } else if(dayOfWeek == Calendar.WEDNESDAY) {
                result = 3;
            } else if(dayOfWeek == Calendar.THURSDAY) {
                result = 4;
            } else if(dayOfWeek == Calendar.FRIDAY) {
                result = 5;
            } else if(dayOfWeek == Calendar.SATURDAY) {
                result = 6;
            }

            return  result;
        }

        public int getLastDay() {
            switch (curMonth) {
                case 0:
                case 2:
                case 4:
                case 6:
                case 7:
                case 9:
                case 11:
                    return 31;
                case 3:
                case 5:
                case 8:
                case 10:
                    return 30;
                default:
                    if(((curYear%4==0)&&(curYear%100!=0)) ||(curYear%400==0)){
                        return 29;
                    } else {
                        return 28;
                    }
            }
        }

        @Override
        public int getCount() {
            return 42;
        }

        @Override
        public Object getItem(int position) {
            return items[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MonthItemView view = null;

            if (convertView == null) {
                view = new MonthItemView((getApplicationContext()));
            } else {
                view = (MonthItemView) convertView;
            }

            int itemDay = items[position].day;
            String lunarDate =  items[position].lunarDate;
            String ganJi=  items[position].ganJi;
            String calMemo =  items[position].calMemo;
            int dayColor = items[position].dayColor;
            String checkDiary =  items[position].checkDiary;
            String diaryDescr =  items[position].diaryDescr;

            view.setDay(itemDay);
            view.setLunar(itemDay, lunarDate);
            view.setGanji(itemDay, ganJi);
            view.setSeason(itemDay, calMemo);

            if (checkDiary != null && checkDiary.equals("Y")) {
                view.setCheckDiary(itemDay, diaryDescr);
            } else {
                view.setCheckDiary(itemDay, " ");
            }

            view.setColor(dayColor);

            return view;
        }

    }

    // AsyncTask Parameter Mapping
    //   AsyncTaskParam  - doInBackground
    //   String -
    //   JSONArray -  onPostExecute
    private class PostAsyncCalendar extends AsyncTask<AsyncTaskParam, String, JSONArray> {

        JSONParser jsonParser = new JSONParser();

        private ProgressDialog pDialog;

        private static final String LOGIN_URL = "http://ksoocho.cafe24.com/farm_diary/ajax/ajaxMonthDiaryDate.php";

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(AsyncTaskParam... args) {

            try {

                HashMap<String, String> params = new HashMap<>();

                params.put("user_id", String.valueOf(args[0].user_id));
                params.put("plant_id", String.valueOf(args[0].plant_id));
                params.put("solar_date", args[0].diary_date);

                Log.d("Request", "starting");

                JSONArray json = jsonParser.makeHttpRequestArr(LOGIN_URL, "POST", params);

                if (json != null) {
                    Log.d("JSON result", json.toString());
                    return json;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(JSONArray json) {

            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            int solar_day = 0;
            String lunar_date = "";
            String ganji = "";
            String cal_memo = "";
            String diary_descr = "";
            int diary_count = 0;

            if (json != null) {

                Log.d("JSON parameter", json.toString());

                try {

                    for (int i=0; i<json.length(); i++) {

                        JSONObject obj = json.getJSONObject(i);

                        solar_day = obj.getInt("solar_day");
                        lunar_date = obj.getString("lunar_date");
                        ganji = obj.getString("ganji");
                        cal_memo = obj.getString("cal_memo");
                        diary_count = obj.getInt("diary_count");
                        diary_descr = obj.getString("diary_descr");

                        items[start_inx+solar_day-1].setLunarDate(lunar_date);
                        items[start_inx+solar_day-1].setGanJi(ganji);
                        items[start_inx+solar_day-1].setCalMemo(cal_memo);

                        if ( diary_count == 0 ) {
                            items[start_inx+solar_day-1].setCheckDiary("N");
                        } else  if ( diary_count > 0 ){
                            items[start_inx+solar_day-1].setCheckDiary("Y");
                        }

                        // Description을 5자리까지 보여 주기 + 한 줄만 보 여주기
                        Reader inputString = new StringReader(diary_descr);
                        BufferedReader br;
                        String line_descr = "";

                        try {
                            br = new BufferedReader(inputString);

                            while ((line_descr = br.readLine()) != null) {
                                break;
                            }

                            br.close();

                        } catch (IOException e) {
                            // e.printStackTrace();
                        }

                        items[start_inx+solar_day-1].setDiaryDescr(line_descr);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adapter.notifyDataSetChanged();
            }

        }

    }

}
