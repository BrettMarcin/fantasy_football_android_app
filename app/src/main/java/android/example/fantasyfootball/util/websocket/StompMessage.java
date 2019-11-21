package android.example.fantasyfootball.util.websocket;

import java.util.HashMap;
import java.util.Map;

public class StompMessage {
    private Map<String, String> headers;
    private String body="";
    private String command;

    public StompMessage(String command) {
        this.command = command;
        headers = new HashMap<>();
    }

    public StompMessage() {

        headers = new HashMap<>();
    }

    public String getHeader(String name){
        return headers.get(name);
    }

    public void put(String name, String value){
        headers.put(name, value);
    }
    public void setContent(String body){
        this.body = body;
    }
    public String getContent() {
        return body;
    }
    public Map<String, String> getHeaders() {
        return headers;
    }
    public String getCommand() {
        return command;
    }
}