package android.example.fantasyfootball.draft;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.example.fantasyfootball.R;
import android.example.fantasyfootball.util.adapter.MessageAdapter;
import android.example.fantasyfootball.util.network.RestApiCalls;
import android.example.fantasyfootball.util.websocket.SpringBootWebSocketClient;
import android.example.fantasyfootball.util.websocket.StompMessage;
import android.example.fantasyfootball.util.websocket.StompMessageListener;
import android.example.fantasyfootball.util.TokenAccess;
import android.example.fantasyfootball.util.websocket.TopicHandler;
import android.example.fantasyfootball.util.network.VolleyCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class BeforeDraft extends AppCompatActivity {

    private static final String LOG_TAG = BeforeDraft.class.getSimpleName();
    private SpringBootWebSocketClient client;
    private String draftId;
    private MessageAdapter messageAdapter;
    private ArrayList<String> messages;
    private ListView messagesView;
    private boolean superUser;
    private EditText messageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_draft);
        // Check if the the user  is still logged in
        init();
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

    private void init() {
        if (TokenAccess.hasTokenExpired(getApplicationContext())) {
            finish();
        }

        messageText = (EditText)findViewById(R.id.message_text);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        final Intent i = getIntent();
        String draftDets = i.getStringExtra("sampleObject");
        // TODO: need to make api call to get if the user is in the draft
        try {
            JSONObject jsonObject = new JSONObject(draftDets);
            draftId = String.valueOf((int)jsonObject.get("id"));
            JSONObject user = (JSONObject)jsonObject.getJSONObject("userCreated");
            String theuserCreated = (String)user.getString("userName");
            if (theuserCreated.compareTo(TokenAccess.getUserName(getApplicationContext())) == 0) {
                superUser = true;
            } else {
                superUser = false;
            }
        }catch (JSONException err){
            Log.d("Error", err.toString());
        }

        getButtons();
        TextView t = findViewById(R.id.draft_header);
        t.setText("Draft " + draftId);


        messages = new ArrayList<String>();
        messagesView = findViewById(R.id.messages);
        messageAdapter = new MessageAdapter(BeforeDraft.this, messages);
        messagesView.setAdapter(messageAdapter);

        connectToSocket();
    }

    private void setText(final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!messages.contains(value)) {
                    messages.add(value);
                    messageAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void end() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Draft has started!", Toast.LENGTH_SHORT);
                Bundle conData = new Bundle();
                conData.putString("param_result", "Start_draft");
                Intent intent = new Intent();
                intent.putExtras(conData);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void connectToSocket() {
        String charRoom = "/draft/chatRoom/" + draftId;
        String draftStarted = "/draft/draftStarted/" + draftId;
        client = new SpringBootWebSocketClient();
        client.setId("sub-001");
        TopicHandler handler = client.subscribe(charRoom);
        TopicHandler handler2 = client.subscribe(draftStarted);
        handler.addListener(new StompMessageListener() {
            @Override
            public void onMessage(StompMessage message) {
                try {
                    JSONObject jsonObject = new JSONObject(message.getContent());
                    String user = (String)jsonObject.get("from");
                    String content = (String)jsonObject.get("text");
                    setText(user + ": " + content);
                }catch (JSONException err){
                    Log.d("Error", err.toString());
                }
            }
        });
        handler2.addListener(new StompMessageListener() {
            @Override
            public void onMessage(StompMessage message) {
                end();
            }
        });
        client.connect("ws://10.0.2.2:8000/draft-socket");
    }

    private void getButtons() {
        //start_draft
        if (superUser) {
            View b = findViewById(R.id.start_draft);
            b.setVisibility(View.VISIBLE);
            b.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    RestApiCalls.startDraft(getApplicationContext(), draftId, new VolleyCallback() {
                        @Override
                        public void onSuccess(JSONObject response){
                            View b = findViewById(R.id.accept_draft);
                            b.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Joined the draft!", Toast.LENGTH_SHORT);
                        }
                    });
                }
            });

        } else {
            RestApiCalls.getResponse(getApplicationContext(), "api/getUsersInADraft/" + draftId,new VolleyCallback() {
                @Override
                public void onSuccess(JSONObject response){
                    try {
                        JSONArray invitedUser = (JSONArray)response.getJSONArray("usersInvited");
                        for (int j = 0; j < invitedUser.length(); j++) {
                            String theUser = (String)invitedUser.get(j);
                            if (theUser.compareTo(TokenAccess.getUserName(getApplicationContext())) == 0) {

                                View b = findViewById(R.id.accept_draft);
                                b.setVisibility(View.VISIBLE);
                                b.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View view) {
                                        RestApiCalls.joinDraft(getApplicationContext(), draftId, new VolleyCallback() {
                                            @Override
                                            public void onSuccess(JSONObject response){
                                                View b = findViewById(R.id.accept_draft);
                                                b.setVisibility(View.GONE);
                                                Toast.makeText(getApplicationContext(), "Joined the draft!", Toast.LENGTH_SHORT);
                                            }
                                        });
                                    }
                                });
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    public void sendMessage(View view) {
        String text = messageText.getText().toString();
        //    public static void sendMsg(final Context context, final String content, String draftId, String user) {
        if (text.length() > 0) {
            RestApiCalls.sendMsg(getApplicationContext(), text, draftId, TokenAccess.getUserName(getApplicationContext()));
            messageText.setText("", TextView.BufferType.EDITABLE);
        }
    }
}
