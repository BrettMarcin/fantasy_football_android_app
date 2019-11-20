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
    private SpringBootWebSocketClient client;
    private PicksAdapter adapter;
    private PlayerTableDataAdapter playerTableDataAdapter;
    private String draftId;
    private Button draftButton;
    private Spinner menu;
    private TextView selectedPlayer;
    private Player playerSelected;
    private static final String[] differentPages = { "Draft", "Team", "Draft History", "Message Board"};
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

        draftButton = findViewById(R.id.draft_button);
        selectedPlayer = findViewById(R.id.player_selected);
        draftButton.setEnabled(false);
        Spinner spinner = (Spinner) findViewById(R.id.menu_for_draft);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
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
                try {
                    selectedPlayerJson = jsonObject.getJSONObject("thePlayer");
                    round = jsonObject.getInt("round");
                    pickNum = jsonObject.getInt("pickNumber");
                    userName = jsonObject.getString("username");
                    theId = selectedPlayerJson.getInt("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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

    private void connectToSocket() {
        String charRoom = "/draft/pickSelected/" + draftId;
        client = new SpringBootWebSocketClient();
        client.setId("sub-001");
        TopicHandler handler = client.subscribe(charRoom);
        handler.addListener(new StompMessageListener() {
            @Override
            public void onMessage(StompMessage message) {
                try {
                    System.out.println("Let's see");
                    JSONObject jsonObject = new JSONObject(message.getContent());
                    updateViews(jsonObject);

                }catch (JSONException err){
                    Log.d("Error", err.toString());
                }
            }
        });
        client.connect("ws://10.0.2.2:8000/draft-socket");
    }
}
