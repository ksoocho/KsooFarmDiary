package com.ksoocho.farmdiary;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 *   Class Name  : WeatherActivity
 *   Description : 기상청 공공데이타를 이용한 기상정보 조회
 */
public class WeatherActivity extends AppCompatActivity {

    URL RSSurl = null;

    URL url = null;
    String xml;

    rcvJson RJ;
    JSONObject JStoken;
    String getJSON;

    TextView regionTextView;
    TextView detailTextView;

    String posX="0",posY="0";

    Spinner spinnerLargeWeather;
    Spinner spinnerMiddleWeather;
    Spinner spinnerSmallWeather;

    RegionSpinAdapter largeAdapter;
    RegionSpinAdapter middleAdapter;
    RegionSpinAdapter smallAdapter;

    Button btnSubmitWeather;

    String largeRegionId;
    String largeRegionName;

    String middleRegionId;
    String middleRegionName;

    String smallRegionId;
    String smallRegionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        spinnerLargeWeather = (Spinner) findViewById(R.id.spinnerLargeWeather); //광역시도
        spinnerMiddleWeather = (Spinner) findViewById(R.id.spinnerMiddleWeather ); //시군구
        spinnerSmallWeather = (Spinner) findViewById(R.id.spinnerSmallWeather); //동

        btnSubmitWeather = (Button) findViewById(R.id.btnSubmitWeather);

        // 지역대분류 표시
        addItemsOnSpinnerLargeWeather();

        // Button Listener
        addListenerOnButton();

    }

    // --------------------------------------------------------------------------------------
    // addItemsOnSpinnerLargeWeather
    // 지역 대분류
    // --------------------------------------------------------------------------------------
    public void addItemsOnSpinnerLargeWeather() {

        try {
            JSONArray JSA;
            String    regionId;
            String    regionName;

            String compareValue = new PrefManager(this).getLargeRegionName();
            int spinnerPosition = 0;

            ArrayList<String[]> list2 = new ArrayList<String[]>();

            RSSurl = new URL("http://www.kma.go.kr/DFSROOT/POINT/DATA/top.json.txt");
            RJ = new rcvJson();
            RJ.start();
            RJ.join();
            JSA = new JSONArray(getJSON);

            for(int i=0; i < JSA.length(); i++){
                JStoken = JSA.getJSONObject(i);
                regionId = (String)JStoken.get(JStoken.names().getString(0));
                regionName = (String)JStoken.get(JStoken.names().getString(1));

                if (compareValue.equals(regionName)) {
                    spinnerPosition = i;
                }

                list2.add(new String[]{regionId, regionName});
            }

            RegionWeather[] regions = new RegionWeather[list2.size()];

            for(int t=0; t<list2.size(); t++) {
                System.out.println(list2.get(t)[0]);
                regions[t] = new RegionWeather();
                regions[t].setRegionId(list2.get(t)[0]);
                regions[t].setRegionName(list2.get(t)[1]);
            }

            largeAdapter = new RegionSpinAdapter(WeatherActivity.this,
                    android.R.layout.simple_spinner_item, regions);

            largeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerLargeWeather.setAdapter(largeAdapter);

            if (spinnerPosition > 0 ) {
                spinnerLargeWeather.setSelection(spinnerPosition);
            }

            spinnerLargeWeather.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view,
                                           int position, long id) {

                    middleRegionId ="";
                    middleRegionName = "";

                    smallRegionId = "";
                    smallRegionName = "";

                    RegionWeather region = largeAdapter.getItem(position);

                    largeRegionId = region.getRegionId();
                    largeRegionName = region.getRegionName();

                    addItemsOnSpinnerMiddleWeather(largeRegionId);

                }
                @Override
                public void onNothingSelected(AdapterView<?> adapter) {  }
            });

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    // --------------------------------------------------------------------------------------
    // addItemsOnSpinnerMiddleWeather
    // 날씨지역 중분류
    // --------------------------------------------------------------------------------------
    public void addItemsOnSpinnerMiddleWeather(String p_region_id) {

        try {
            JSONArray JSA;
            String    regionId;
            String    regionName;

            String compareValue = new PrefManager(this).getMiddleRegionName();
            int spinnerPosition = 0;

            ArrayList<String[]> list2 = new ArrayList<String[]>();

            RSSurl = new URL("http://www.kma.go.kr/DFSROOT/POINT/DATA/mdl."+p_region_id+".json.txt");
            RJ = new rcvJson();
            RJ.start();
            RJ.join();
            JSA = new JSONArray(getJSON);

            for(int i=0; i < JSA.length(); i++){
                JStoken = JSA.getJSONObject(i);
                regionId = (String)JStoken.get(JStoken.names().getString(0));
                regionName = (String)JStoken.get(JStoken.names().getString(1));

                if (compareValue.equals(regionName)) {
                    spinnerPosition = i;
                }

                list2.add(new String[]{regionId, regionName});
            }

            RegionWeather[] regions = new RegionWeather[list2.size()];

            for(int t=0; t<list2.size(); t++) {
                System.out.println(list2.get(t)[0]);
                regions[t] = new RegionWeather();
                regions[t].setRegionId(list2.get(t)[0]);
                regions[t].setRegionName(list2.get(t)[1]);
            }

            middleAdapter = new RegionSpinAdapter(WeatherActivity.this,
                    android.R.layout.simple_spinner_item, regions);

            middleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerMiddleWeather.setAdapter(middleAdapter);

            if (spinnerPosition > 0 ) {
                spinnerMiddleWeather.setSelection(spinnerPosition);
            }

            spinnerMiddleWeather.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view,
                                           int position, long id) {

                    RegionWeather region = middleAdapter.getItem(position);

                    middleRegionId = region.getRegionId();
                    middleRegionName = region.getRegionName();

                    addItemsOnSpinnerSmalleather(middleRegionId);
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapter) {  }
            });

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    // --------------------------------------------------------------------------------------
    // addItemsOnSpinnerSmalleather
    // 날씨지역 소분류
    // --------------------------------------------------------------------------------------
    public void addItemsOnSpinnerSmalleather(String p_region_id) {

        try {
            JSONArray JSA;
            String    regionId;
            String    regionName;

            String compareValue = new PrefManager(this).getSmallRegionName();
            int spinnerPosition = 0;

            ArrayList<String[]> list2 = new ArrayList<String[]>();

            RSSurl = new URL("http://www.kma.go.kr/DFSROOT/POINT/DATA/leaf."+p_region_id+".json.txt");
            RJ = new rcvJson();
            RJ.start();
            RJ.join();
            JSA = new JSONArray(getJSON);

            for(int i=0; i < JSA.length(); i++){
                JStoken = JSA.getJSONObject(i);
                regionId = (String)JStoken.get(JStoken.names().getString(0));
                regionName = (String)JStoken.get(JStoken.names().getString(1));

                if (compareValue.equals(regionName)) {
                    spinnerPosition = i;
                }

                list2.add(new String[]{regionId, regionName});
            }

            RegionWeather[] regions = new RegionWeather[list2.size()];

            for(int t=0; t<list2.size(); t++) {
                System.out.println(list2.get(t)[0]);
                regions[t] = new RegionWeather();
                regions[t].setRegionId(list2.get(t)[0]);
                regions[t].setRegionName(list2.get(t)[1]);
            }

            smallAdapter = new RegionSpinAdapter(WeatherActivity.this,
                    android.R.layout.simple_spinner_item, regions);

            smallAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSmallWeather.setAdapter(smallAdapter);

            if (spinnerPosition > 0 ) {
                spinnerSmallWeather.setSelection(spinnerPosition);
            }

            spinnerSmallWeather.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view,
                                           int position, long id) {

                    RegionWeather region = smallAdapter.getItem(position);

                    smallRegionId = region.getRegionId();
                    smallRegionName = region.getRegionName();

                    displayWeatherForRegion();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapter) {  }
            });

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    // --------------------------------------------------------------------------------------
    // 날씨정보 가져오기 버튼 처리
    // --------------------------------------------------------------------------------------
    public void addListenerOnButton() {

        btnSubmitWeather.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                displayWeatherForRegion();
            }

        });
    }

    public void displayWeatherForRegion() {

        // 날씨정보 보이기
        displayWeather();

        // 날씨지역정보 저장
        saveWeatherRegion(largeRegionName, middleRegionName, smallRegionName);

    }

    // --------------------------------------------------------------------------------------
    // 날씨정보 보여주기
    // --------------------------------------------------------------------------------------
    public void displayWeather() {

        regionTextView = (TextView) findViewById(R.id.regionTextWeather);
        // ----------------------------------
        // 날씨지역코드 가지고 오기
        // ----------------------------------
        try {

            if (largeRegionName.equals("")||middleRegionName.equals("")||smallRegionName.equals("")) {
                return;
            }

            String Local1 = largeRegionName;
            String Local2 = middleRegionName;
            String Local3 = smallRegionName;

            String temp;
            JSONArray JSA;

            // 지역 대분류
            RSSurl = new URL("http://www.kma.go.kr/DFSROOT/POINT/DATA/top.json.txt");
            RJ = new rcvJson();
            RJ.start();
            RJ.join();
            JSA = new JSONArray(getJSON);

            temp="";

            for(int i=0; i < JSA.length(); i++){
                JStoken = JSA.getJSONObject(i);
                if(JStoken.get(JStoken.names().getString(1)).equals(Local1)){
                    temp += JStoken.get(JStoken.names().getString(0));
                    break;
                }
            }

            // 지역 중분류
            RSSurl = new URL("http://www.kma.go.kr/DFSROOT/POINT/DATA/mdl."+temp+".json.txt");
            RJ = new rcvJson();
            RJ.start();
            RJ.join();
            JSA = new JSONArray(getJSON);

            temp="";

            for(int i=0; i < JSA.length(); i++){
                JStoken = JSA.getJSONObject(i);
                if(JStoken.get(JStoken.names().getString(1)).equals(Local2)){
                    temp += JStoken.get(JStoken.names().getString(0));
                    break;
                }
            }

            RSSurl = new URL("http://www.kma.go.kr/DFSROOT/POINT/DATA/leaf."+temp+".json.txt");
            RJ = new rcvJson();
            RJ.start();
            RJ.join();
            JSA = new JSONArray(getJSON);

            temp="";

            for(int i=0; i < JSA.length(); i++){
                JStoken = JSA.getJSONObject(i);

                posX = JStoken.get(JStoken.names().getString(2)).toString();
                posY = JStoken.get(JStoken.names().getString(3)).toString();

                if(JStoken.get(JStoken.names().getString(1)).equals(Local3))
                    break;
            }

            regionTextView.setText(Local1+" "+Local2+" "+Local3+" ("+ posX + ","+ posY+")");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // ----------------------------------
        // 날씨정보 가지고 오기
        // ----------------------------------
        detailTextView = (TextView) findViewById(R.id.detailTextWeather);

        try {
            String html = loadKmaData();

            //DOM 파싱.
            ByteArrayInputStream bai = new ByteArrayInputStream(html.getBytes());
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            //dbf.setIgnoringElementContentWhitespace(true);//화이트스패이스 생략
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document parse = builder.parse(bai);//DOM 파서

            //태그 검색
            NodeList datas = parse.getElementsByTagName("data");

            //String result = "data태그 수 =" + datas.getLength()+"\n";
            String result = "";

            //17개의 data태그를 순차로 접근
            for (int idx = 0; idx < datas.getLength(); idx++) {

                //필요한 정보들을 담을 변수 생성
                String day = "";
                String hour = "";
                String sky = "";
                String temp = "";
                String wind = "";

                Node node = datas.item(idx);//data 태그 추출
                int childLength = node.getChildNodes().getLength();

                //자식태그 목록 수정
                NodeList childNodes = node.getChildNodes();

                for (int childIdx = 0; childIdx < childLength; childIdx++) {

                    Node childNode = childNodes.item(childIdx);

                    int count = 0;

                    if(childNode.getNodeType() == Node.ELEMENT_NODE){
                        count ++;
                        //태그인 경우만 처리
                        //오늘,내일,모레 구분(시간정보 포함)
                        if(childNode.getNodeName().equals("day")){

                            int su = Integer.parseInt(childNode.getFirstChild().getNodeValue());

                            Calendar calendar;

                            Date vDate = new Date();
                            calendar = Calendar.getInstance();
                            calendar.setTime(vDate);
                            calendar.add(Calendar.DATE, su);

                            Date tmpDate = calendar.getTime();

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                            day = sdf.format(tmpDate);

                            //switch(su){
                            //   case 0 : day = "오늘"; break;
                            //   case 1 : day = "내일"; break;
                            //  case 2 : day = "모레"; break;
                            //}

                        }else if(childNode.getNodeName().equals("hour")){

                            hour = childNode.getFirstChild().getNodeValue();

                            if (Integer.parseInt(hour) < 10) {
                                hour = "0"+hour;
                            }

                        }else if(childNode.getNodeName().equals("wfKor")){   // 날씨

                            sky = childNode.getFirstChild().getNodeValue();

                        }else if(childNode.getNodeName().equals("wdKor")){   // 풍향

                            wind = childNode.getFirstChild().getNodeValue();

                        }else if(childNode.getNodeName().equals("temp")){    // 온도

                            temp = childNode.getFirstChild().getNodeValue();
                        }
                    }
                }//end 안쪽 for문

                result += day+" "+hour+"시 ("+sky+" / "+temp+"도 / "+wind+"풍 )\n";

            }//end 바깥쪽 for문\

            detailTextView.setText(result);

        } catch (Exception e) {
            detailTextView.setText("오류"+e.getMessage());
            e.printStackTrace();
        }
    }


    // --------------------------------------------------------------------------------------
    // JSON Receive
    // --------------------------------------------------------------------------------------
    class rcvJson extends Thread {
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        RSSurl.openStream(), "UTF-8"));
                getJSON = in.readLine();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    // --------------------------------------------------------------------------------------
    // 기상청 날씨정보 Load
    // --------------------------------------------------------------------------------------
    private String loadKmaData() throws Exception {

        String returnValue = "";

        try {
            rcvXml RX = new rcvXml();
            RX.start();
            RX.join();
            returnValue = xml;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return returnValue;

    }

    // --------------------------------------------------------------------------------------
    // XML Receive
    // --------------------------------------------------------------------------------------
    class rcvXml extends Thread {
        public void run() {

            String page = "http://www.kma.go.kr/wid/queryDFS.jsp?gridx="+posX+"&gridy="+posY;

            try {
                url = new URL(page);
                BufferedReader in = new BufferedReader(new InputStreamReader( url.openStream(), "UTF-8"));
                xml = in.readLine();
                while(true){
                    String temp;
                    temp = in.readLine();
                    if(temp==null)
                        break;
                    xml += temp+"\n";
                }
            } catch (Exception e) {

            }
        }
    }

    // --------------------------------------------------------------------------------------
    //  날씨지역정보 Class
    // --------------------------------------------------------------------------------------
    public class RegionWeather {

        private String regionId;
        private String regionName;

        public RegionWeather() {
            this.regionId = "";
            this.regionName = "";
        }

        public void setRegionId(String id) {
            this.regionId = id;
        }

        public String getRegionId() {
            return this.regionId;
        }

        public void setRegionName(String name) {
            this.regionName = name;
        }

        public String getRegionName() {
            return this.regionName;
        }

    }

    // --------------------------------------------------------------------------------------
    //  날씨지역정보 SpinAdapter
    // --------------------------------------------------------------------------------------
    public class RegionSpinAdapter extends ArrayAdapter<RegionWeather> {

        private Context context;
        private RegionWeather[] values;

        public RegionSpinAdapter(Context context, int textViewResourceId,
                                 RegionWeather[] values) {
            super(context, textViewResourceId, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public int getCount(){
            return values.length;
        }

        @Override
        public RegionWeather getItem(int position){
            return values[position];
        }

        @Override
        public long getItemId(int position){
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView label = (TextView) super.getView(position, convertView, parent);
            label.setTextColor(Color.BLACK);
            label.setText(values[position].getRegionName());
            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView label = (TextView) super.getDropDownView(position, convertView, parent);
            label.setTextColor(Color.BLACK);
            label.setText(values[position].getRegionName());

            return label;
        }
    }

    // --------------------------------------------------------------------------------------
    //  날씨지역정보 SharedPreferences 저장
    // --------------------------------------------------------------------------------------
    private void saveWeatherRegion(String largeArea, String middleArea, String smallArea) {
        new PrefManager(this).saveWeatherRegion(largeArea, middleArea, smallArea);
    }

    // --------------------------------------------------------------------------------------
    //  SpinnerIndex 가져오기
    // --------------------------------------------------------------------------------------
    private int getSpinnerIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }

}


