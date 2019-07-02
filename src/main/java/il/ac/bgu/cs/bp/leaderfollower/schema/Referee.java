package il.ac.bgu.cs.bp.leaderfollower.schema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries(value = {
        @NamedQuery(name = "BallIsFree", query = "SELECT r FROM Referee r WHERE r.ballPosession=''"),
        @NamedQuery(name = "IPossesTheBall", query = "SELECT r FROM Referee r WHERE r.ballPosession = r.playerName"),
        @NamedQuery(name = "OpponnentPossesTheBall", query = "SELECT r FROM Referee r WHERE r.ballPosession != '' AND r.ballPosession != r.playerName"),
        @NamedQuery(name = "Playing", query = "SELECT r FROM Referee r WHERE r.gameOver = false"),
        @NamedQuery(name = "GameOver", query = "SELECT r FROM Referee r WHERE r.gameOver = true"),
        @NamedQuery(name = "MyScoreIs", query = "SELECT r FROM Referee r WHERE r.myScore=:score"),
        @NamedQuery(name = "OpponentScoreIs", query = "SELECT r FROM Referee r WHERE r.opponentScore=:score"),
        @NamedQuery(name = "NearlyTimeout", query = "SELECT r FROM Referee r WHERE r.ballPosession = r.playerName AND r.timeout IS NOT NULL AND r.timeout<=3"),
        @NamedQuery(name = "TimeoutInASecond", query = "SELECT r FROM Referee r WHERE r.ballPosession = r.playerName AND r.timeout IS NOT NULL AND r.timeout=1"),
        @NamedQuery(name = "UpdateTimeout", query = "Update Referee r set r.timeout=:timeout"),
        @NamedQuery(name = "UpdatePosession", query = "Update Referee r set r.ballPosession=:posession"),
        @NamedQuery(name = "UpdateMyScore", query = "Update Referee r set r.myScore=:score"),
        @NamedQuery(name = "UpdateOpponentScore", query = "Update Referee r set r.opponentScore=:score"),
        @NamedQuery(name = "MarkGameAsOver", query = "Update Referee r set r.gameOver = true"),})
public class Referee extends BasicEntity {
    @Column
    private final String playerName;

    @Column
    private final String ballPosession;

    @Column
    private final int myScore;

    @Column
    private final int opponentScore;

    @Column
    private final Integer timeout;

    @Column
    private final boolean gameOver;

    private Referee() {
        this("");
    }

    public Referee(String playerName) {
        super("referee");
        this.playerName = playerName;
        this.ballPosession = "";
        this.myScore = 0;
        this.opponentScore = 0;
        this.timeout = null;
        this.gameOver = false;
    }

}
