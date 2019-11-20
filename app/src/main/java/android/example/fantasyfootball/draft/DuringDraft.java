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
import android.example.fantasyfootball.util.TokenAccess;
import android.example.fantasyfootball.util.VolleyCallback;
import android.example.fantasyfootball.util.VolleyCallbackWithArray;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
    private ArrayList<Player> playersInDraft;
    private PicksAdapter adapter;
    private PlayerTableDataAdapter playerTableDataAdapter;
    private String draftId;
    private Button draftButton;
    private Spinner menu;
    private static final String[] array = { "This", "is", "a", "test" };
    private static final String[] TABLE_HEADERS = { "rank", "first", "last", "pos" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_during_draft);
        draftButton = findViewById(R.id.draft_button);
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
        ArrayAdapter<String> items = new ArrayAdapter<String>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, array);
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


    }

    private void createTable() {
        TableView<Player> tableView = (TableView<Player>) findViewById(R.id.draft_table);
        playersInDraft = new ArrayList<>();
        playerTableDataAdapter = new PlayerTableDataAdapter(this, playersInDraft);
        tableView.setDataAdapter(playerTableDataAdapter);
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(this, TABLE_HEADERS));
        tableView.addDataClickListener(new PlayerClickListener());
        RestApiCalls.getResponseArray(getApplicationContext(), "api/getPicks/" + draftId,new VolleyCallbackWithArray() {
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
                        teamNames.add(team);
                        pickNums.add(pick);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });


    }

    private class PlayerClickListener implements TableDataClickListener<Player> {
        @Override
        public void onDataClicked(int rowIndex, Player clickedCar) {
            System.out.println("I see clicked");
//            String clickedCarString = clickedCar.getProducer().getName() + " " + clickedCar.getName();
//            Toast.makeText(getContext(), clickedCarString, Toast.LENGTH_SHORT).show();
        }
    }
}
