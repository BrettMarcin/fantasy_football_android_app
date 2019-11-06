package android.example.fantasyfootball.draft;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;

import android.content.Intent;
import android.example.fantasyfootball.R;
import android.example.fantasyfootball.util.Message;
import android.example.fantasyfootball.util.SpringBootWebSocketClient;
import android.example.fantasyfootball.util.StompMessage;
import android.example.fantasyfootball.util.StompMessageListener;
import android.example.fantasyfootball.util.TopicHandler;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;


public class BeforeDraft extends AppCompatActivity {

    private static final String LOG_TAG = BeforeDraft.class.getSimpleName();
    private SpringBootWebSocketClient client;
    private String draftId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_draft);

        Intent i = getIntent();
        String draftDets = i.getStringExtra("sampleObject");
        try {
            JSONObject jsonObject = new JSONObject(draftDets);
            draftId = String.valueOf((int)jsonObject.get("id"));
        }catch (JSONException err){
            Log.d("Error", err.toString());
        }
//        connectToSocket();

//        WebSocketClient simpleWebSocketClient = new StandardWebSocketClient();
//        List<Transport> transports = new ArrayList<>(1);
//        transports.add(new WebSocketTransport(simpleWebSocketClient));
//        SockJsClient sockJsClient = new SockJsClient(transports);
//        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
//        String url = "ws://10.0.2.2:8000/draft-socket";
//        StompSessionHandler sessionHandler = new MyStompSessionHandler();
//        try {
//            StompSession session = stompClient.connect(url, sessionHandler).get();
//            Message msg = new Message("Bob", "hi");
//            ObjectMapper Obj = new ObjectMapper();
//            String jsonStr = Obj.writeValueAsString(msg);
//            session.send("/api/chat/" + draftId, jsonStr);
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (JsonGenerationException e) {
//            e.printStackTrace();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//        String charRoom = "/api/chat/" + draftId;
//        SpringBootWebSocketClient client = new SpringBootWebSocketClient();
//        client.setId("sub-001");
//        TopicHandler handler = client.subscribe(charRoom);
//        client.connect("ws://10.0.2.2:8000/draft-socket");
//        client.sendPublishMessage(client.webSocket, charRoom, "WE are HERE!");
//        mWebSocketClient.
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        client.disconnect();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        client.disconnect();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        connectToSocket();
//    }

    private void connectToSocket() {
        String charRoom = "/draft/chatRoom/" + draftId;
        client = new SpringBootWebSocketClient();
        client.setId("sub-001");
        TopicHandler handler = client.subscribe(charRoom);
//        handler.
        handler.addListener(new StompMessageListener() {
            @Override
            public void onMessage(StompMessage message) {
                System.out.println(message.getHeader("destination") + ": " + message.getContent());
            }
        });
        client.connect("ws://10.0.2.2:8000/draft-socket");
    }


}
