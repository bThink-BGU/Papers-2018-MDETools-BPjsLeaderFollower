package il.ac.bgu.cs.bp.leaderfollower.events;

import il.ac.bgu.cs.bp.bpjs.model.BEvent;

@SuppressWarnings("serial")
public class GoSlowGradient extends BEvent implements java.io.Serializable {

    public int power;

    public GoSlowGradient(int power) {
        super("GoSlowGradient(" + power + ")");
        this.power = power;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.power;
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
        final GoSlowGradient other = (GoSlowGradient) obj;
        return this.power == other.power;
    }
    
    
}
