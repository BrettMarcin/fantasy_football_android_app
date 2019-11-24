package android.example.fantasyfootball;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.example.fantasyfootball.util.adapter.FantasyAdapter;
import android.example.fantasyfootball.util.network.RestApiCalls;
import android.example.fantasyfootball.util.TokenAccess;
import android.example.fantasyfootball.util.network.VolleyCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private FantasyAdapter yourDraftAdapter;
    private FantasyAdapter publicDraftAdapter;
    private FantasyAdapter invitedDraftAdapter;
    private ListView my_drafts_list;
    private ListView invited_drafts_list;
    private ListView public_drafts_list;
    private ArrayList<String> yourDraftList;
    private ArrayList<String> publicDraftList;
    private ArrayList<String> invitedDraftList;
    private Button createDraft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }

    public void init() {
        if (TokenAccess.hasTokenExpired(getApplicationContext())) {
            finish();
        }

        yourDraftList = new ArrayList<String>();
        publicDraftList = new ArrayList<String>();
        invitedDraftList = new ArrayList<String>();
        my_drafts_list = findViewById(R.id.scroll_your_drafts);
        invited_drafts_list = findViewById(R.id.scroll_view_invited_drafts);
        public_drafts_list = findViewById(R.id.scroll_view_public_drafts);
        createDraft = findViewById(R.id.create_draft_button);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.scroll_your_drafts, yourDraftList);
        yourDraftAdapter = new FantasyAdapter(MainActivity.this, yourDraftList);
        publicDraftAdapter = new FantasyAdapter(MainActivity.this, publicDraftList);
        invitedDraftAdapter = new FantasyAdapter(MainActivity.this, invitedDraftList);


        my_drafts_list.setAdapter(yourDraftAdapter);
        invited_drafts_list.setAdapter(invitedDraftAdapter);
        public_drafts_list.setAdapter(publicDraftAdapter);

        RestApiCalls.getResponse(getApplicationContext(), "api/getDrafts",new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response){
                try {
                    JSONArray publicDrafts = (JSONArray)response.get("publicDrafts");
                    JSONArray invitedDrafts = (JSONArray)response.get("invitedDrafts");
                    JSONArray yourDrafts = (JSONArray)response.get("yourDrafts");
                    for (int i = 0; i < publicDrafts.length(); i++) {
                        JSONObject json = (JSONObject)publicDrafts.get(i);
                        int id = (int)json.get("id");
                        publicDraftList.add(String.valueOf(id));
                    }
                    for (int i = 0; i < invitedDrafts.length(); i++) {
                        JSONObject json = (JSONObject)invitedDrafts.get(i);
                        int id = (int)json.get("id");
                        invitedDraftList.add(String.valueOf(id));
                    }
                    for (int i = 0; i < yourDrafts.length(); i++) {
                        JSONObject json = (JSONObject)yourDrafts.get(i);
                        int id = (int)json.get("id");
                        yourDraftList.add(String.valueOf(id));
                    }
                    publicDraftAdapter.notifyDataSetChanged();
                    invitedDraftAdapter.notifyDataSetChanged();
                    yourDraftAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        createDraft.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                        Intent activityIntent = new Intent(getApplicationContext(), CreateDraftActivity.class);
                        startActivity(activityIntent);
                    }
                });
    }

    // Maybe check onResume to see if drafts are still avaiable
    @Override
    protected void onResume()
    {
        super.onResume();
        init();
    }

    @Override
    public void onBackPressed() {
        Log.i("MainActivity", "Going back");
        MainActivity.super.onBackPressed();
    }
}
