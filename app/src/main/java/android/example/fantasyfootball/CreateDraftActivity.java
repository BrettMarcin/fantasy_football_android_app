package android.example.fantasyfootball;

import androidx.appcompat.app.AppCompatActivity;

import android.example.fantasyfootball.util.Player;
import android.example.fantasyfootball.util.TokenAccess;
import android.example.fantasyfootball.util.adapter.FantasyAdapter;
import android.example.fantasyfootball.util.adapter.TeamAdapter;
import android.example.fantasyfootball.util.network.RestApiCalls;
import android.example.fantasyfootball.util.network.VolleyCallback;
import android.example.fantasyfootball.util.network.VolleyCallbackWithArray;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.codecrafters.tableview.TableView;

public class CreateDraftActivity extends AppCompatActivity {

    private TeamAdapter teamAdapter;
    private ArrayAdapter<String> usersToInviteAdapter;
    private ArrayList<String> invitedUsers;
    private ArrayList<String> usersToInvite;
    private ListView list_of_invited_users;
    private Spinner userToInviteSpinner;
    private Button createDraftButton;
    private Switch isPublicSwitch;
    private boolean isFirstSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_draft);
        init();
    }
    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        if (TokenAccess.hasTokenExpired(getApplicationContext())) {
            finish();
        }


        list_of_invited_users = findViewById(R.id.current_teams);
        userToInviteSpinner = findViewById(R.id.menu_teams);
        createDraftButton = findViewById(R.id.create_draft_button);
        isPublicSwitch = findViewById(R.id.public_switch);
        invitedUsers = new ArrayList<String>();
        usersToInvite = new ArrayList<String>();
        usersToInvite.add("");
        createDraftButton.setEnabled(false);

        usersToInviteAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, usersToInvite);
        userToInviteSpinner.setAdapter(usersToInviteAdapter);
        teamAdapter = new TeamAdapter(getApplicationContext(), invitedUsers, usersToInvite, usersToInviteAdapter, createDraftButton);
        list_of_invited_users.setAdapter(teamAdapter);

        userToInviteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                if(position != 0) {
                    invitedUsers.add(usersToInvite.get(position));
                    teamAdapter.notifyDataSetChanged();
                    usersToInvite.remove(position);
                    adapterView.getAdapter();
                    userToInviteSpinner.setSelection(0);
                }
                if (invitedUsers.size() > 0) {
                    createDraftButton.setEnabled(true);
                }
//                usersToInviteAdapter.notifyDataSetChanged();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub
            }});

        RestApiCalls.getResponse(getApplicationContext(), "api/getUserNames" ,new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray teams = response.getJSONArray("users");
//                    for (String team : JSONArray) {
//                        if (usersToInvite.contains(team)) {
//                            usersToInvite.add(team);
//                        }
//                    }
                    for (int i = 0; i < teams.length(); i++) {
                        String team = teams.getString(i);
                        if (!usersToInvite.contains(team)) {
                            usersToInvite.add(team);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                usersToInviteAdapter.notifyDataSetChanged();
            }
        });

        createDraftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject json = new JSONObject();
                JSONArray jArrIds=new JSONArray();
                try {
                    for (String s : invitedUsers) {
                        jArrIds.put(s);
                    }
                    json.put("usersInvited", jArrIds);
                    json.put("isPublic", isPublicSwitch.isChecked());
                    RestApiCalls.createDraft(getApplicationContext(), json,new VolleyCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            finish();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

}
