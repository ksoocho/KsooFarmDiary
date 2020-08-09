package com.ksoocho.farmdiary;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class DiaryActivity extends AppCompatActivity {

    TextView dateText;
    TextView dateInfoText;

    TextView planTextDiary;
    TextView activityTextDiary;
    TextView memoTextDiary;

    int vUserId;
    int vPlantId;

    int vYear;
    int vMonth;
    int vDay;

    String vDate;
    String vDateInfo;

    Intent intentUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        // 앞화면에서 넘어온 파라미터 받기
        Intent intent = this.getIntent();

        vUserId = intent.getIntExtra("USERID",0);
        vPlantId = intent.getIntExtra("PLANTID",0);

        vYear = intent.getIntExtra("YEAR",0);
        vMonth = intent.getIntExtra("MONTH",0);
        vDay = intent.getIntExtra("DAY",0);

        vDate = intent.getStringExtra("DATE");
        vDateInfo = intent.getStringExtra("DATE_INFO");

        // 선택한 날짜
        dateText = (TextView) findViewById(R.id.dateTextDiary);
        dateText.setText(vYear+"년 "+vMonth+"월 "+vDay+"일");

        // 선택한 날짜 정보
        dateInfoText = (TextView) findViewById(R.id.dateInfoTextDiary);
        dateInfoText.setText(vDateInfo);

        // -------------------------------------
        // 농부일지정보 가져오기
        // -------------------------------------
        planTextDiary = (TextView) findViewById(R.id.planTextDiary);
        activityTextDiary = (TextView) findViewById(R.id.activityTextDiary);
        memoTextDiary = (TextView) findViewById(R.id.memoTextDiary);

        new DiaryActivity.PostAsyncDiary().execute(new AsyncTaskParam(vUserId,vPlantId,vDate));

        // 다른 화면 호출
        intentUpdate = new Intent( this, UpdateActivity.class);

        intentUpdate.putExtra("USERID", vUserId );
        intentUpdate.putExtra("PLANTID", vPlantId);

        intentUpdate.putExtra("YEAR", vYear);
        intentUpdate.putExtra("MONTH", vMonth);
        intentUpdate.putExtra("DAY", vDay);

        intentUpdate.putExtra("DATE", vDate);
        intentUpdate.putExtra("DATE_INFO", vDateInfo);


        planTextDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intentUpdate.putExtra("DESCR_TYPE", "PLAN");
                intentUpdate.putExtra("DESCR_CONTENT", planTextDiary.getText());

                // Activity is started with requestCode 2
                startActivityForResult(intentUpdate, 11);

            }
        });

        activityTextDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intentUpdate.putExtra("DESCR_TYPE", "ACT");
                intentUpdate.putExtra("DESCR_CONTENT", activityTextDiary.getText());

                // Activity is started with requestCode 2
                startActivityForResult(intentUpdate, 11);

            }
        });

        memoTextDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intentUpdate.putExtra("DESCR_TYPE", "MEMO");
                intentUpdate.putExtra("DESCR_CONTENT", memoTextDiary.getText());

                // Activity is started with requestCode 2
                startActivityForResult(intentUpdate, 11);

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

        switch (requestCode) {

            case RESULT_OK:  // Back Button
                break;

            case 11: // UpdateActivity
                setResult(resultCode);
                finish();
                break;

            default:
                break;

        }

    }

    @Override
    public void finish() {
        Intent data = new Intent();
        setResult(RESULT_OK, data);

        super.finish();
    }


    // AsyncTask Parameter Mapping
    //   AsyncTaskParam  - doInBackground
    //   String -
    //   JSONArray -  onPostExecute
    private class PostAsyncDiary extends AsyncTask<AsyncTaskParam, String, JSONArray> {

        JSONParser jsonParser = new JSONParser();

        private ProgressDialog pDialog;

        private static final String DIARY_URL = "http://ksoocho.cafe24.com/farm_diary/ajax/ajaxDiarySelectPlan.php";

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(DiaryActivity.this);
            pDialog.setMessage("Diary Loading...");
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
                params.put("diary_date", args[0].diary_date);

                JSONArray json = jsonParser.makeHttpRequestArr(DIARY_URL, "POST", params);

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

            String plan_descr = "";
            String activity_descr = "";
            String memo_descr = "";

            if (json != null) {

                Log.d("JSON parameter", json.toString());

                try {

                    for (int i=0; i<json.length(); i++) {

                        JSONObject obj = json.getJSONObject(i);

                        plan_descr = obj.getString("plan_descr");
                        activity_descr = obj.getString("activity_descr");
                        memo_descr= obj.getString("memo_descr");

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // 계획, 일지, 메모 표시
                planTextDiary.setText(plan_descr);
                activityTextDiary.setText(activity_descr);
                memoTextDiary.setText(memo_descr);

            }

        }

    }
}
