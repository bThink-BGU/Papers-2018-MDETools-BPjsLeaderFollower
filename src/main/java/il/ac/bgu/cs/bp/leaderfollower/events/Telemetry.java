package il.ac.bgu.cs.bp.leaderfollower.events;

import il.ac.bgu.cs.bp.bpjs.model.BEvent;
import il.ac.bgu.cs.bp.leaderfollower.PlayerCommands.GpsData;
import java.util.Objects;

/**
 * An event that holds all the current telemetry
 */
@SuppressWarnings("serial")
public class Telemetry extends BEvent implements java.io.Serializable {
    public final GpsData BallGps;
    public final GpsData PlayerGps;
    public final GpsData OpponentGps;
    public final Double PlayerCompass;

    public Telemetry(GpsData BallGps, GpsData PlayerGps, GpsData OpponentGps,
            Double PlayerCompass) {
        super("Telemetry(" + BallGps + "," + PlayerGps + "," + OpponentGps + "," + PlayerCompass
                + ")");
        this.BallGps = BallGps;
        this.PlayerGps = PlayerGps;
        this.OpponentGps = OpponentGps;
        this.PlayerCompass = PlayerCompass;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + Objects.hashCode(this.PlayerGps);
        hash = 31 * hash + Objects.hashCode(this.OpponentGps);
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
        if (!Objects.equals(this.PlayerGps, other.PlayerGps)) {
            return false;
        }
        if (!Objects.equals(this.OpponentGps, other.OpponentGps)) {
            return false;
        }
        if (!Objects.equals(this.PlayerCompass, other.PlayerCompass)) {
            return false;
        }
        return true;
    }


}
