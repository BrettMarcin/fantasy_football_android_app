package android.example.fantasyfootball.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

public class TokenAccess extends Application {

    private static TokenAccess sInstance;
    private static final String LOG_TAG = TokenAccess.class.getSimpleName();
    String accessToken;
    public static String userName;

    @Override
    public void onCreate(){
        super.onCreate();
        sInstance = this;
//        accessToken = retrieveTokenFromSharedPrefs();
    }

    public static TokenAccess getInstance() {
        return sInstance;
    }

    public static String getAccessToken(final Context context) {
        SharedPreferences prefs = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        return prefs.getString("token","");
    }

    public static String getUserName(final Context context) {
        SharedPreferences prefs = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        return prefs.getString("userName","");
    }

    private void setToken(String token) {
        accessToken = token;
        // save your access token in the SharedPrefs
    }

//    public String getAccessToken() {
//        return accessToken;
//    }

//    public boolean hasLoggedIn() {
//        return getAccessToken() != null;
//    }

    public static boolean hasTokenExpired(final Context context) {
        SharedPreferences prefs = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        if (prefs.contains("time") && prefs.contains("token")) {
            long timeOfToken = prefs.getLong("time", 0);
            int hoursOfToken   = (int) ((timeOfToken / (1000*60*60)) % 24);
            Date date = new Date(System.currentTimeMillis());
            int nowHours   = (int) ((date.getTime() / (1000*60*60)) % 24);
            if (nowHours - hoursOfToken == 0) {
                return false;
            } else {
                prefs.edit().remove("time").commit();
                prefs.edit().remove("userName").commit();
                prefs.edit().remove("token").commit();
                return true;
            }
        } else {
            return true;
        }
    }
}
