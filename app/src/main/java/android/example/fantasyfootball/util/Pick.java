package android.example.fantasyfootball.util;

public class Pick {

    private Player player;

    private String username;

    private int round;

    private int pickNumber;

    public Pick(Player player, String username, int round, int pickNumber) {
        this.player = player;
        this.username = username;
        this.round = round;
        this.pickNumber = pickNumber;
    }

    public Pick() {

    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getPickNumber() {
        return pickNumber;
    }

    public void setPickNumber(int pickNumber) {
        this.pickNumber = pickNumber;
    }
}
