package il.ac.bgu.cs.bp.leaderfollower.events;

import il.ac.bgu.cs.bp.bpjs.model.BEvent;
import java.util.Objects;

/**
 * An event that holds all the current telemetry
 */
@SuppressWarnings("serial")
public class Telemetry extends BEvent implements java.io.Serializable {

    /**
     * The Telemetry
     */
    public Double PlayerX;

    public Double PlayerY;

    public Double OpponentX;

    public Double OpponentY;

    public Double PlayerCompass;
    
    public Double PlayerDistanceToBall;

    public Telemetry(Double PlayerX, Double PlayerY, Double OpponentX, Double OpponentY, Double PlayerCompass, Double PlayerDistanceToBall) {
        super("Telemetry(" + PlayerX + "," + PlayerY + "," + OpponentX + "," + OpponentY + "," + PlayerCompass + "," + PlayerDistanceToBall + ")");
        this.PlayerX = PlayerX;
        this.PlayerY = PlayerY;
        this.OpponentX = OpponentX;
        this.OpponentY = OpponentY;
        this.PlayerCompass = PlayerCompass;
        this.PlayerDistanceToBall = PlayerDistanceToBall;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + Objects.hashCode(this.PlayerX);
        hash = 31 * hash + Objects.hashCode(this.OpponentX);
        hash = 31 * hash + Objects.hashCode(this.PlayerCompass);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Telemetry other = (Telemetry) obj;
        if (!Objects.equals(this.PlayerX, other.PlayerX)) {
            return false;
        }
        if (!Objects.equals(this.PlayerY, other.PlayerY)) {
            return false;
        }
        if (!Objects.equals(this.OpponentX, other.OpponentX)) {
            return false;
        }
        if (!Objects.equals(this.OpponentY, other.OpponentY)) {
            return false;
        }
        if (!Objects.equals(this.PlayerCompass, other.PlayerCompass)) {
            return false;
        }
        if (!Objects.equals(this.PlayerDistanceToBall, other.PlayerDistanceToBall)) {
            return false;
        }
        return true;
    }
    
    
}
