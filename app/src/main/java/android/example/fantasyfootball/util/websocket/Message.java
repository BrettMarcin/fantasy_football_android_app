package android.example.fantasyfootball.util.websocket;


public class Message{

    private String from;
    private String text;

    public Message(String f, String t) {
        from = f;
        text = t;
    }
}
