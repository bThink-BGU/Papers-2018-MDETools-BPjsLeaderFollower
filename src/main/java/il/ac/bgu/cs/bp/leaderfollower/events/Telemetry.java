package il.ac.bgu.cs.bp.leaderfollower.events;

import il.ac.bgu.cs.bp.bpjs.model.BEvent;
import il.ac.bgu.cs.bp.bpjs.model.eventsets.EventSet;
import il.ac.bgu.cs.bp.leaderfollower.PlayerCommands.GpsData;
import java.util.Objects;

/**
 * An event that holds all the current telemetry
 */
@SuppressWarnings("serial")
public class Telemetry extends BEvent implements java.io.Serializable {
    public static final AnyTelemetryEventSet ANY = new AnyTelemetryEventSet();
    public final GpsData BallGps;
    public final GpsData PlayerGps;
    public final Double PlayerCompass;
    public final Double DistancePlayerToBall;

    public Telemetry(GpsData BallGps, GpsData PlayerGps, Double PlayerCompass,
            Double DistancePlayerToBall) {
        super("Telemetry(" + BallGps + "," + PlayerGps + "," + PlayerCompass + ","
                + DistancePlayerToBall + ")");
        this.BallGps = BallGps;
        this.PlayerGps = PlayerGps;
        this.PlayerCompass = PlayerCompass;
        this.DistancePlayerToBall = DistancePlayerToBall;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + Objects.hashCode(this.PlayerGps);
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
        if (!Objects.equals(this.PlayerCompass, other.PlayerCompass)) {
            return false;
        }
        return true;
    }

    private static class AnyTelemetryEventSet implements EventSet {
        @Override
        public boolean contains(BEvent event) {
            return event instanceof Telemetry;
        }

    }
}
