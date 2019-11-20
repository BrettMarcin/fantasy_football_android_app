package android.example.fantasyfootball.util;

public class Player {

    private int id;

    private String firstName;

    private String lastName;

    private int rank_player;

    private String team;

    private float totalPts;

    private int gamesPlayed;

    private int rushYards;

    private int rushTds;

    private int passYards;

    private int passTds;

    private int fumbles;

    private int interceptions;

    private String postion;

    private int recYards;

    private int rec;

    public Player() {

    };

    public Player(int id, String firstName, String lastName, int rank_player, String team, float totalPts, int gamesPlayed, int rushYards, int rushTds, int passYards, int passTds, int fumbles, int interceptions, String postion, int recYards, int rec) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.rank_player = rank_player;
        this.team = team;
        this.totalPts = totalPts;
        this.gamesPlayed = gamesPlayed;
        this.rushYards = rushYards;
        this.rushTds = rushTds;
        this.passYards = passYards;
        this.passTds = passTds;
        this.fumbles = fumbles;
        this.interceptions = interceptions;
        this.postion = postion;
        this.recYards = recYards;
        this.rec = rec;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getRank_player() {
        return rank_player;
    }

    public void setRank_player(int rank_player) {
        this.rank_player = rank_player;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public float getTotalPts() {
        return totalPts;
    }

    public void setTotalPts(float totalPts) {
        this.totalPts = totalPts;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getRushYards() {
        return rushYards;
    }

    public void setRushYards(int rushYards) {
        this.rushYards = rushYards;
    }

    public int getRushTds() {
        return rushTds;
    }

    public void setRushTds(int rushTds) {
        this.rushTds = rushTds;
    }

    public int getPassYards() {
        return passYards;
    }

    public void setPassYards(int passYards) {
        this.passYards = passYards;
    }

    public int getPassTds() {
        return passTds;
    }

    public void setPassTds(int passTds) {
        this.passTds = passTds;
    }

    public int getFumbles() {
        return fumbles;
    }

    public void setFumbles(int fumbles) {
        this.fumbles = fumbles;
    }

    public int getInterceptions() {
        return interceptions;
    }

    public void setInterceptions(int interceptions) {
        this.interceptions = interceptions;
    }

    public String getPostion() {
        return postion;
    }

    public void setPostion(String postion) {
        this.postion = postion;
    }

    public int getRecYards() {
        return recYards;
    }

    public void setRecYards(int recYards) {
        this.recYards = recYards;
    }

    public int getRec() {
        return rec;
    }

    public void setRec(int rec) {
        this.rec = rec;
    }
}
