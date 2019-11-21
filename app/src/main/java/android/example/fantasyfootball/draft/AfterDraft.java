package android.example.fantasyfootball.draft;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.example.fantasyfootball.R;
import android.example.fantasyfootball.util.Player;
import android.example.fantasyfootball.util.TokenAccess;
import android.example.fantasyfootball.util.adapter.TeamTableDataAdapter;
import android.example.fantasyfootball.util.network.RestApiCalls;
import android.example.fantasyfootball.util.network.VolleyCallbackWithArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class AfterDraft extends AppCompatActivity {

    private String draftId;
    private String currentTeamViewing;
    private TeamTableDataAdapter teamTableDataAdapter;
    private List<Player> teamPlayers;
    private static final String[] TABLE_HEADERS_team_view = { "first", "last", "pos", "team" };
    private List<String> teamListForSpinner;
    private ArrayAdapter<String> adapter_for_team_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_draft);
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

        final Intent i = getIntent();
        String draftDets = i.getStringExtra("sampleObject");

        try {
            JSONObject jsonObject = new JSONObject(draftDets);
            draftId = String.valueOf((int)jsonObject.get("id"));
        }catch (JSONException err){
            Log.d("Error", err.toString());
        }

        Spinner team_spinner = (Spinner) findViewById(R.id.menu_teams);
        team_spinner.setSelection(0);
        currentTeamViewing = TokenAccess.getUserName(getApplicationContext());
        init_team_spinner();
    }

    private void init_team_spinner() {
        Spinner team_spinner = (Spinner) findViewById(R.id.menu_teams);
        teamListForSpinner = new ArrayList<>();
        teamListForSpinner.add(TokenAccess.getUserName(getApplicationContext()));
        adapter_for_team_spinner = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, teamListForSpinner);
        team_spinner.setAdapter(adapter_for_team_spinner);

        team_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                currentTeamViewing = adapterView.getItemAtPosition(position).toString();
                createTeamTable();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub
            }});
        // init list with players in the draft
        RestApiCalls.getResponseArray(getApplicationContext(), "api/getPlayersDuringDraft/" + draftId,new VolleyCallbackWithArray() {
            @Override
            public void onSuccess(JSONArray response) {
                for (int i = 0; i< response.length(); i++) {
                    try {
                        String team = (String)response.get(i);
                        if (team.compareTo(TokenAccess.getUserName(getApplicationContext())) != 0) {
                            teamListForSpinner.add(team);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter_for_team_spinner.notifyDataSetChanged();
            }
        });
        currentTeamViewing = TokenAccess.getUserName(getApplicationContext());
        createTeamTable();
    }

    public void createTeamTable() {
        //currentTeamViewing
        TableView<Player> tableView = (TableView<Player>) findViewById(R.id.team_table);
        teamPlayers = new ArrayList<>();
        teamTableDataAdapter = new TeamTableDataAdapter(this, teamPlayers);
        tableView.setDataAdapter(teamTableDataAdapter);
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(this, TABLE_HEADERS_team_view));
        RestApiCalls.getResponseArray(getApplicationContext(), "api/getPlayersTeamDrafted/" + draftId + "?user=" + currentTeamViewing,new VolleyCallbackWithArray() {
            @Override
            public void onSuccess(JSONArray response) {
                for (int i = 0; i< response.length(); i++) {
                    try {
                        Player player = new Player();
                        JSONObject json = response.getJSONObject(i);
                        player.setRank_player(json.getInt("rank_player"));
                        player.setFirstName(json.getString("firstName"));
                        player.setLastName(json.getString("lastName"));
                        player.setPostion(json.getString("postion"));
                        player.setId(json.getInt("id"));
                        player.setTeam(json.getString("team"));
                        int x = -2;
                        for (int j = 0; j < teamPlayers.size(); j++) {
                            Player existing = teamPlayers.get(j);
                            if (existing.getId() == player.getId()) {
                                x = j;
                                break;
                            }
                        }
                        if (x == -2) {
                            teamPlayers.add(player);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                teamTableDataAdapter.notifyDataSetChanged();
            }
        });
    }
}
