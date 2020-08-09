package com.ksoocho.farmdiary;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 로그인 정보 유지
 * 로그인 관련 정보 가지고 오기
 * 로그아웃 확인
 */
public class PrefManager {

    Context context;

    PrefManager(Context context) {
        this.context = context;
    }

    // 로그인 정보 저장
    public void saveLoginDetails(String email, int userId, String userName) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetail", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("Email", email);
        editor.putInt("UserId", userId);
        editor.putString("UserName", userName);

        editor.commit();
    }

    // 로그아웃처리
    public void removeLoginDetails() {

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetail", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove("Email");
        editor.remove("UserId");
        editor.remove("UserName");

        editor.commit();
    }

    // 로그인 사용자 메일 ID
    public String getEmail() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetail", Context.MODE_PRIVATE);
        return sharedPreferences.getString("Email", "");
    }

    // 로그인 사용자 ID
    public int getUserId() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetail", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("UserId", 0);
    }

    // 로그인 사용자 이름
    public String getUserName() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetail", Context.MODE_PRIVATE);
        return sharedPreferences.getString("UserName", "");
    }

    // 로그아웃 여부 Check
    public boolean isUserLogedOut() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetail", Context.MODE_PRIVATE);

        boolean isEmailEmpty = sharedPreferences.getString("Email", "").isEmpty();
        boolean isUserIdEmpty = (sharedPreferences.getInt("UserId", 0) == 0);
        boolean isUserNameEmpty = sharedPreferences.getString("UserName", "").isEmpty();

        return isEmailEmpty||isUserIdEmpty||isUserNameEmpty;
    }

    // 날씨정보 지역 저장
    public void saveWeatherRegion(String largeArea, String middleArea, String smallArea) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("WeatherRegion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("LargeRegionName",  largeArea);
        editor.putString("MiddleRegionName", middleArea);
        editor.putString("SmallRegionName",  smallArea);

        editor.commit();
    }

    // 날씨정보 지역 Clear
    public void removeWeatherRegion() {

        SharedPreferences sharedPreferences = context.getSharedPreferences("WeatherRegion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove("LargeRegionName");
        editor.remove("MiddleRegionName");
        editor.remove("SmallRegionName");

        editor.commit();
    }

    // 날씨지역정보 - Large
    public String getLargeRegionName() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("WeatherRegion", Context.MODE_PRIVATE);
        return sharedPreferences.getString("LargeRegionName", "");
    }

    // 날씨지역정보 - Middle
    public String getMiddleRegionName() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("WeatherRegion", Context.MODE_PRIVATE);
        return sharedPreferences.getString("MiddleRegionName", "");
    }

    // 날씨지역정보 - Small
    public String getSmallRegionName() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("WeatherRegion", Context.MODE_PRIVATE);
        return sharedPreferences.getString("SmallRegionName", "");
    }

}
