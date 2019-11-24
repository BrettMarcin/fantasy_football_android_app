package android.example.fantasyfootball.draft;

import android.content.Intent;
import android.example.fantasyfootball.util.network.RestApiCalls;
import android.example.fantasyfootball.util.network.VolleyCallback;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
                    String started = response.getString("wasRunning");
                    if (started.compareTo("running") == 0) {
                        Intent i = new Intent(getBaseContext(), DuringDraft.class);
                        draftDetails = response.toString();
                        i.putExtra("sampleObject", response.toString());
                        startActivityForResult(i, 2);
                    } else if(started.compareTo("no") == 0){
                        Intent i = new Intent(getBaseContext(), BeforeDraft.class);
                        draftDetails = response.toString();
                        i.putExtra("sampleObject", response.toString());
                        startActivityForResult(i, 2);
                    } else {
                        Intent i = new Intent(getBaseContext(), AfterDraft.class);
                        draftDetails = response.toString();
                        i.putExtra("sampleObject", response.toString());
                        startActivityForResult(i, 2);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //finish();
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
//        super.onActivityResult();
        String results = "";
        if (data != null) {
            results = data.getStringExtra("param_result");
        }

        if (results.compareTo("Start_draft") == 0) {
            Intent i = new Intent(getBaseContext(), DuringDraft.class);
            i.putExtra("sampleObject", draftDetails);
            startActivityForResult(i, 2);
        } else if (results.compareTo("Draft_end") == 0) {
            Intent i = new Intent(getBaseContext(), AfterDraft.class);
            i.putExtra("sampleObject", draftDetails);
            startActivityForResult(i, 2);
        } else {
            finish();
        }
    }
}
