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
    public final GpsData ballGps;
    public final GpsData playerGps;
    public final GpsData gateGps;
    public final Double playerCompass;
    public final SourceToTargetData playerToBall;
    public final SourceToTargetData playerToGate;


    public Telemetry(GpsData PlayerGps, GpsData BallGps, GpsData GateGps, Double PlayerCompass) {
        super("Telemetry(" + BallGps + "," + PlayerGps + "," + PlayerCompass + ")");
        this.ballGps = BallGps;
        this.playerGps = PlayerGps;
        this.gateGps = GateGps;
        this.playerCompass = PlayerCompass;
        this.playerToBall = SourceToTargetData.sourceToGoal(playerGps, ballGps, playerCompass);
        this.playerToGate = SourceToTargetData.sourceToGoal(playerGps, GateGps, playerCompass);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + Objects.hashCode(this.playerGps);
        hash = 31 * hash + Objects.hashCode(this.ballGps);
        hash = 31 * hash + Objects.hashCode(this.playerCompass);
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
        if (!Objects.equals(this.playerGps, other.playerGps)) {
            return false;
        }
        if (!Objects.equals(this.playerCompass, other.playerCompass)) {
            return false;
        }
        return true;
    }

    public static class SourceToTargetData {
        public final Double distance;
        public final Double degree;
        public final Double dx;
        public final Double dz;

        private SourceToTargetData(Double distance, Double degree, Double dx, Double dz) {
            this.distance = distance;
            this.degree = degree;
            this.dx = dx;
            this.dz = dz;
        }

        public static SourceToTargetData sourceToGoal(GpsData source, GpsData goal, Double sourceCompass) {
            Double distance = getDistance(source, goal);
            Double degree = compDeg2Target(source, goal, sourceCompass);
            Double[] xz = calcXZ( distance, degree);
            return new SourceToTargetData(distance, degree, xz[0], xz[1]);
        }

        private static Double getDistance(GpsData source, GpsData goal) {
            double dx = Math.abs(source.x - goal.x);
            double dz = Math.abs(source.z - goal.z);
            return Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));
        }

        private static Double compDeg2Target(GpsData source, GpsData goal, Double sourceCompass) {
            Double LRDeg, DDeg;
            LRDeg = Math.atan2((goal.x - source.x), -(goal.z - source.z));
            LRDeg = (LRDeg / Math.PI) * 180;
            DDeg = (90 - sourceCompass) - LRDeg;
            if (Math.abs(DDeg) >= 360) {
                if (DDeg > 0) {
                    DDeg = DDeg - 360;
                } else {
                    DDeg = DDeg + 360;
                }
            }
            if (Math.abs(DDeg) > 180) {
                if (DDeg > 180) {
                    DDeg = DDeg - 360;
                }
                if (DDeg < (-180)) {
                    DDeg = DDeg + 360;
                }
            }
            return DDeg;
        }

        private static Double[] calcXZ(Double distance, Double degreeToTarget) {
            int signX = 1;
            int signZ = 1;
            Double degree = -degreeToTarget;
            if (degree > 90 && degree <= 180) {
                degree = 180 - degree;
                signX = -1;
            } else if (degree < 0 && degree >= -90) {
                degree = -degree;
                signZ = -1;
            } else if (degree < -90 && degree >= -180) {
                degree = 180 + degree;
                signZ = -1;
                signX = -1;
            }
            Double degreeRadians = degree * Math.PI / 180;
            return new Double[] {
                signX * distance * Math.cos(degreeRadians),
                signZ * distance * Math.sin(degreeRadians)
            };
        }
    }

    private static class AnyTelemetryEventSet implements EventSet {
        @Override
        public boolean contains(BEvent event) {
            return event instanceof Telemetry;
        }

    }
}
