package com.ksoocho.farmdiary;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 *   Class Name  : WriteActivity
 *   Description : 꼼꼼일기 - 계획쓰기
 */

public class WriteActivity extends AppCompatActivity {

    TextView dateText;
    TextView dateInfoText;
    EditText planDescrText;

    int farmerUserId;
    int farmerPlantId;

    int vYear;
    int vMonth;
    int vDay;

    String vDate;
    String vDateInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        // 앞화면에서 넘어온 파라미터 받기
        Intent intent = this.getIntent();

        farmerUserId = intent.getIntExtra("USERID",0);
        farmerPlantId = intent.getIntExtra("PLANTID",0);

        vYear = intent.getIntExtra("YEAR",0);
        vMonth = intent.getIntExtra("MONTH",0);
        vDay = intent.getIntExtra("DAY",0);
        vDate = intent.getStringExtra("DATE");
        vDateInfo = intent.getStringExtra("DATE_INFO");

        // 선택한 날짜
        dateText = (TextView) findViewById(R.id.dateTextWrite);
        dateText.setText(vYear+"년 "+vMonth+"월 "+vDay+"일");

        // 선택한 날짜 정보
        dateInfoText = (TextView) findViewById(R.id.dateInfoTextWrite);
        dateInfoText.setText(vDateInfo);
    }

    // -----------------------------------------------------------------
    // Option Menu 보이기 - onCreateOptionsMenu
    // -----------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    // -----------------------------------------------------------------
    // Option Menu 선택한 경우 - onOptionsItemSelected
    // -----------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int curId = item.getItemId();

        switch (curId) {
            case R.id.menu_save:

                // -------------------------------------
                // 꼼꼼일지 - 계획쓰기
                // -------------------------------------
                planDescrText = (EditText) findViewById(R.id.diaryEditWrite);
                String vPlanDescr = planDescrText.getText().toString();

                new PostAsyncWrite().execute(new AsyncWriteParam(farmerUserId, farmerPlantId, vDate, vPlanDescr,"",""));

                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        Intent data = new Intent();
        setResult(RESULT_OK, data);

        super.finish();
    }

    // AsyncTask Parameter Mapping
    //   AsyncWriteParam  - doInBackground
    //   String -
    //   JSONArray -  onPostExecute
    private class PostAsyncWrite extends AsyncTask<AsyncWriteParam, String, JSONArray> {

        JSONParser jsonParser = new JSONParser();

        private ProgressDialog pDialog;

        private static final String WRITE_URL = "http://ksoocho.cafe24.com/farm_diary/ajax/ajaxDiaryInsertPlan.php";

        @Override
        protected void onPreExecute() {

            pDialog = new ProgressDialog(WriteActivity.this);
            pDialog.setMessage("Saving Diary...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        } // onPreExecute

        @Override
        protected JSONArray doInBackground(AsyncWriteParam... args) {

            try {

                HashMap<String, String> params = new HashMap<>();

                params.put("user_id", String.valueOf(args[0].user_id));
                params.put("plant_id", String.valueOf(args[0].plant_id));
                params.put("diary_date", args[0].diary_date);
                params.put("plan_descr", args[0].plan_descr);

                Log.d("Save Request", "starting");

                JSONArray json = jsonParser.makeHttpRequestArr(WRITE_URL, "POST", params);

                if (json != null) {
                    Log.d("JSON result", json.toString());
                    return json;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;

        } // doInBackground

        protected void onPostExecute(JSONArray json) {

            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            String returnCode = "S";
            String errorMessage = "OK";

            if (json != null) {

                Log.d("JSON parameter", json.toString());

                try {

                    for (int i=0; i<json.length(); i++) {

                        JSONObject obj = json.getJSONObject(i);

                        returnCode = obj.getString("return_code");
                        errorMessage = obj.getString("error_message");

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            // 호출한 Activity로 결과값 전달함.
            Intent intent = new Intent();
            intent.putExtra("MESSAGE",errorMessage);
            setResult(2,intent);

            // 저장처리하고 Activity 닫음.
            WriteActivity.this.finish();


        } // onPostExecute

    } // PostAsyncWrite
}
