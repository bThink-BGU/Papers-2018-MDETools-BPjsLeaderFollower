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
    public Double RovX;

    public Double RovY;

    public Double LeadX;

    public Double LeadY;

    public Double Compass;

    public Double Dist;

    public Telemetry(Double RovX, Double RovY, Double LeadX, Double LeadY, Double Compass, Double Dist) {
        super("Telemetry(" + RovX + "," + RovY + "," + LeadX + "," + LeadY + "," + Compass + "," + Dist + ")");
        this.RovX = RovX;
        this.RovY = RovY;
        this.LeadX = LeadX;
        this.LeadY = LeadY;
        this.Compass = Compass;
        this.Dist = Dist;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + Objects.hashCode(this.RovX);
        hash = 31 * hash + Objects.hashCode(this.LeadX);
        hash = 31 * hash + Objects.hashCode(this.Compass);
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
        if (!Objects.equals(this.RovX, other.RovX)) {
            return false;
        }
        if (!Objects.equals(this.RovY, other.RovY)) {
            return false;
        }
        if (!Objects.equals(this.LeadX, other.LeadX)) {
            return false;
        }
        if (!Objects.equals(this.LeadY, other.LeadY)) {
            return false;
        }
        if (!Objects.equals(this.Compass, other.Compass)) {
            return false;
        }
        if (!Objects.equals(this.Dist, other.Dist)) {
            return false;
        }
        return true;
    }
    
    
}
