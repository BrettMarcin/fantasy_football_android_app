package android.example.fantasyfootball.draft;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.example.fantasyfootball.R;

import android.example.fantasyfootball.util.PicksAdapter;
import android.example.fantasyfootball.util.Player;
import android.example.fantasyfootball.util.PlayerTableDataAdapter;
import android.example.fantasyfootball.util.RestApiCalls;
import android.example.fantasyfootball.util.SpringBootWebSocketClient;
import android.example.fantasyfootball.util.StompMessage;
import android.example.fantasyfootball.util.StompMessageListener;
import android.example.fantasyfootball.util.TeamTableDataAdapter;
import android.example.fantasyfootball.util.TokenAccess;
import android.example.fantasyfootball.util.TopicHandler;
import android.example.fantasyfootball.util.VolleyCallback;
import android.example.fantasyfootball.util.VolleyCallbackWithArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class DuringDraft extends AppCompatActivity {

    private ArrayList<String> teamNames;
    private ArrayList<String> pickNums;
    private ArrayList<String> roundNum;
    private ArrayList<Player> playersInDraft;
    private List<String> teamListForSpinner;
    private ArrayAdapter<String> adapter_for_team_spinner;
    private SpringBootWebSocketClient client;
    private PicksAdapter adapter;

    // Attributes for when you want to view a certain team #2 display
    private String currentTeamViewing;
    private TeamTableDataAdapter teamTableDataAdapter;
    private List<Player> teamPlayers;
    private static final String[] TABLE_HEADERS_team_view = { "first", "last", "pos", "team" };



    private PlayerTableDataAdapter playerTableDataAdapter;
    private String draftId;
    private Button draftButton;
    private Spinner menu;
    private TextView selectedPlayer;
    private TextView timer;
    private Player playerSelected;
    private static final String[] differentPages = { "Draft", "Team", "Draft History"};
    private static final String[] TABLE_HEADERS = { "rank", "first", "last", "pos" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_during_draft);
        init();
    }

    private void init() {
        if (TokenAccess.hasTokenExpired(getApplicationContext())) {
            finish();
        }
        //team_title

        timer = findViewById(R.id.timer);
        draftButton = findViewById(R.id.draft_button);
        selectedPlayer = findViewById(R.id.player_selected);
        draftButton.setEnabled(false);
        Spinner spinner = (Spinner) findViewById(R.id.menu_for_draft);


        // Menu for different displays
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                Spinner menu_spinner = findViewById(R.id.menu_teams);
                TableView<Player> tableView = (TableView<Player>) findViewById(R.id.draft_table);//team_table
                TableView<Player> teamTable = (TableView<Player>) findViewById(R.id.team_table);
                // set table back to original
                Spinner team_spinner = (Spinner) findViewById(R.id.menu_teams);
                team_spinner.setSelection(0);
                currentTeamViewing = TokenAccess.getUserName(getApplicationContext());
                createTeamTable();

                if (position == 0) {
                    menu_spinner.setVisibility(View.GONE);
                    teamTable.setVisibility(View.GONE);
                    selectedPlayer.setVisibility(View.VISIBLE);
                    tableView.setVisibility(View.VISIBLE);

                    // Team selection
                } else if (position == 1) {
                    // Hide draft info
                    selectedPlayer.setVisibility(View.GONE);
                    tableView.setVisibility(View.GONE);
                    // Show team elements
                    menu_spinner.setVisibility(View.VISIBLE);
                    teamTable.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub
            }});
        ArrayAdapter<String> items = new ArrayAdapter<String>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, differentPages);
        items.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(items);
        items.notifyDataSetChanged();
        //spinner.
        final Intent i = getIntent();
        String draftDets = i.getStringExtra("sampleObject");
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(draftDets);
            draftId = String.valueOf((int)jsonObject.get("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initPickBar();
        createTable();
        connectToSocket();
        init_team_spinner();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        client.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        client.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void createTable() {
        TableView<Player> tableView = (TableView<Player>) findViewById(R.id.draft_table);
        playersInDraft = new ArrayList<>();
        playerTableDataAdapter = new PlayerTableDataAdapter(this, playersInDraft);
        tableView.setDataAdapter(playerTableDataAdapter);
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(this, TABLE_HEADERS));
        tableView.addDataClickListener(new PlayerClickListener());
        RestApiCalls.getResponseArray(getApplicationContext(), "api/getPlayersRemaining/" + draftId,new VolleyCallbackWithArray() {
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
                        playersInDraft.add(player);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                playerTableDataAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initPickBar() {
        teamNames = new ArrayList<>();
        pickNums = new ArrayList<>();
        roundNum = new ArrayList<>();

        RecyclerView myList = (RecyclerView) findViewById(R.id.pick_list);
        adapter = new PicksAdapter(getApplicationContext(), teamNames, pickNums);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(DuringDraft.this, LinearLayoutManager.HORIZONTAL, false);
        myList.setLayoutManager(horizontalLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(myList.getContext(),
                horizontalLayoutManager.getOrientation());
        myList.addItemDecoration(dividerItemDecoration);
        myList.setItemAnimator(new DefaultItemAnimator());
        myList.setAdapter(adapter);

        RestApiCalls.getResponseArray(getApplicationContext(), "api/getPicks/" + draftId,new VolleyCallbackWithArray() {
            @Override
            public void onSuccess(JSONArray response) {
                for (int i = 0; i< response.length(); i++) {
                    try {
                        String team = (response.getJSONObject(i)).getString("username");
                        if (i == 0 && team.compareTo(TokenAccess.getUserName(getApplicationContext())) == 0)
                            draftButton.setEnabled(true);

                        String pick = (response.getJSONObject(i)).getString("pickNumber");
                        String round = (response.getJSONObject(i)).getString("round");
                        teamNames.add(team);
                        pickNums.add(pick);
                        roundNum.add(round);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
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

    public void draft_player(View view) throws JSONException {
        if (playerSelected == null) {
            Toast.makeText(getApplicationContext(), "Please select a player", Toast.LENGTH_LONG).show();
        } else {
            JSONObject json = new JSONObject();
            //obj.put("id",id);
            json.put("round", Integer.valueOf(roundNum.get(0)));
            json.put("pickNumber", Integer.valueOf(pickNums.get(0)));
            json.put("thePlayer", playerSelected.getJson());
            json.put("draftId", Integer.valueOf(draftId));
            json.put("username", TokenAccess.getUserName(getApplicationContext()));

            RestApiCalls.draftPlayer(getApplicationContext(), json, draftId, new VolleyCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    Toast.makeText(getApplicationContext(), "Drafted!", Toast.LENGTH_LONG).show();
                    playerSelected = null;
                    selectedPlayer.setText("");
                }
            });
        }
    }

    private class PlayerClickListener implements TableDataClickListener<Player> {
        @Override
        public void onDataClicked(int rowIndex, Player clickSelected) {
            playerSelected = clickSelected;
            selectedPlayer.setText(clickSelected.getFirstName() + " " + clickSelected.getLastName());
        }
    }

    private void updateViews(final JSONObject jsonObject){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                JSONObject selectedPlayerJson = null;
                int pickNum = -1;
                int round = -1;
                String userName = "";
                int theId = -1;
                Player thePlayerSelected = new Player();
                try {
                    selectedPlayerJson = jsonObject.getJSONObject("thePlayer");
                    round = jsonObject.getInt("round");
                    pickNum = jsonObject.getInt("pickNumber");
                    userName = jsonObject.getString("username");
                    theId = selectedPlayerJson.getInt("id");
                    thePlayerSelected.setId(theId);
                    thePlayerSelected.setFirstName(selectedPlayerJson.getString("firstName"));
                    thePlayerSelected.setLastName(selectedPlayerJson.getString("lastName"));
                    thePlayerSelected.setTeam(selectedPlayerJson.getString("team"));
                    thePlayerSelected.setPostion(selectedPlayerJson.getString("postion"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (currentTeamViewing.compareTo(userName) == 0) {
                    int x = -2;
                    for (int j = 0; j < teamPlayers.size(); j++) {
                        Player existing = teamPlayers.get(j);
                        if (existing.getId() == thePlayerSelected.getId()) {
                            x = j;
                            break;
                        }
                    }
                    if (x == -2) {
                        teamPlayers.add(thePlayerSelected);
                        teamTableDataAdapter.notifyDataSetChanged();
                    }
                }

                // update list for draft table
                for (Player p : playersInDraft) {
                    if (p.getId() == theId) {
                        break;
                    }
                    i++;
                }

                // TODO: verify that it was picked
                if (teamNames.get(0).compareTo(userName) == 0 && Integer.valueOf(pickNums.get(0)) == pickNum && Integer.valueOf(roundNum.get(0)) == round) {
                    teamNames.remove(0);
                    pickNums.remove(0);
                    roundNum.remove(0);
                }
                if (theId != -1) {
                    playersInDraft.remove(i);
                }

                playerSelected = null;
                selectedPlayer.setText("");
                if (teamNames.size() > 0 && teamNames.get(0).compareTo(TokenAccess.getUserName(getApplicationContext())) == 0) {
                    draftButton.setEnabled(true);
                } else {
                    draftButton.setEnabled(false);
                }

                adapter.notifyDataSetChanged();
                playerTableDataAdapter.notifyDataSetChanged();

            }
        });
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

    private void connectToSocket() {
        String charRoom = "/draft/pickSelected/" + draftId;
        String timer = "/draft/timer/" + draftId;
        client = new SpringBootWebSocketClient();
        client.setId("sub-001");
        TopicHandler handler = client.subscribe(charRoom);
        TopicHandler handler2 = client.subscribe(timer);
        handler.addListener(new StompMessageListener() {
            @Override
            public void onMessage(StompMessage message) {
                try {
                    JSONObject jsonObject = new JSONObject(message.getContent());
                    updateViews(jsonObject);

                }catch (JSONException err){
                    Log.d("Error", err.toString());
                }
            }
        });
        handler2.addListener(new StompMessageListener() {
            @Override
            public void onMessage(StompMessage message) {
                try {
                    JSONObject jsonObject = new JSONObject(message.getContent());
                    updateTime(jsonObject);

                }catch (JSONException err){
                    Log.d("Error", err.toString());
                }
            }
        });
        client.connect("ws://10.0.2.2:8000/draft-socket");
    }

    private void updateTime(final JSONObject jsonObject) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    int time = jsonObject.getInt("theTime");
                    long millis = time % 1000;
                    long second = (time / 1000) % 60;
                    long minute = (time / (1000 * 60)) % 60;
                    long hour = (time / (1000 * 60 * 60)) % 24;

                    String minutes = (minute < 10) ? "0" + String.valueOf(minute) : String.valueOf(minute);
                    String sec = (second < 10) ? "0" + String.valueOf(second) : String.valueOf(second);

                    String timeString = "";
                    if ((60 - second) < 10) {
                        timeString = (String.valueOf(1 - minute)) + ":0" + String.valueOf(60 - second);
                    } else {
                        timeString = String.valueOf(1 - minute) + ":" + String.valueOf(60 - second);
                    }
                    timer.setText(timeString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
