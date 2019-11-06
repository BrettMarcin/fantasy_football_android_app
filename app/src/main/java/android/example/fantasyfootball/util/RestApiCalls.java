package android.example.fantasyfootball.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.app.PendingIntent.getActivity;

public class RestApiCalls {

    private static final String LOG_TAG = RestApiCalls.class.getSimpleName();

    public static void loginService(final Context context, String account, String pass) {
        String url="http://10.0.2.2:8000/api/auth/signin";
        final JSONObject obj=new JSONObject();
        try{
            obj.put("usernameOrEmail",account);
            obj.put("password",pass);

        }catch (JSONException e)
        {
            e.printStackTrace();
        }

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.i(LOG_TAG, "Got response:" + response.getString("token"));
                        SharedPreferences prefs;
                        SharedPreferences.Editor edit;
                        prefs=context.getSharedPreferences("myPrefs",Context.MODE_PRIVATE);
                        edit=prefs.edit();
                        try {
                            String saveToken=response.getString("access_token");
                            edit.putString("token",saveToken);

                            Date date = new Date(System.currentTimeMillis());
                            edit.putLong("time", date.getTime());
                            Log.i("Login",saveToken);
                            edit.commit();
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String log = error.getMessage();
                        Log.d(LOG_TAG, log);
                    }

                });
        MySingleton.getInstance(context).addToRequestQue(req);
    }

    public static void getResponse(final Context context, String lastUrl,final VolleyCallback callback) {
        String url="http://10.0.2.2:8000/" + lastUrl;
        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.GET,
                        url,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                callback.onSuccess(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(LOG_TAG, error.toString());
                            }
                        }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Bearer " + TokenAccess.getAccessToken(context));
                        return headers;
                    }
                };

        MySingleton.getInstance(context).addToRequestQue(jsonObjectRequest);
    }
}
