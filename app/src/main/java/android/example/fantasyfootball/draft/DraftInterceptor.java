package android.example.fantasyfootball.draft;

import android.content.Intent;
import android.example.fantasyfootball.LoginActivity;
import android.example.fantasyfootball.MainActivity;
import android.example.fantasyfootball.util.RestApiCalls;
import android.example.fantasyfootball.util.TokenAccess;
import android.example.fantasyfootball.util.VolleyCallback;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DraftInterceptor extends AppCompatActivity {

    private static final String LOG_TAG = DraftInterceptor.class.getSimpleName();
    private String draftDetails;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init();
        Intent activityIntent = getIntent();
        String id = activityIntent.getStringExtra("id");
        RestApiCalls.getResponse(getApplicationContext(), "api/getDraftDetails/" + id,new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response){
                try {
                    Boolean started = (Boolean)response.get("draftStarted");
                    if (started) {
                        Intent i = new Intent(getBaseContext(), DuringDraft.class);
                        draftDetails = response.toString();
                        i.putExtra("sampleObject", response.toString());
                        startActivity(i);
                    } else {
                        Intent i = new Intent(getBaseContext(), BeforeDraft.class);
                        draftDetails = response.toString();
                        i.putExtra("sampleObject", response.toString());
//                        startActivity(i);
                        startActivityForResult(i, 1);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //init();
    }

    // response code return from draft start = 1 otherwise is 0
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            Intent i = new Intent(getBaseContext(), DuringDraft.class);
            i.putExtra("sampleObject", draftDetails);
            startActivity(i);
        }

    }
}
