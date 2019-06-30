package il.ac.bgu.cs.bp.leaderfollower.events;

import java.util.Objects;
import il.ac.bgu.cs.bp.bpjs.model.BEvent;

@SuppressWarnings("serial")
public class ParameterizedMove extends BEvent implements java.io.Serializable {

    public final Integer powerForward;
    public final Integer powerLeft;
    public final Integer spin;

    public ParameterizedMove(Integer powerForward, Integer powerLeft, Integer spin) {
        super(String.format("ParameterizedMove(%d,%d,%d)", powerForward, powerLeft, spin));
        this.powerForward = powerForward;
        this.powerLeft = powerLeft;
        this.spin = spin;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        if(powerForward != null) hash = 89 * hash + this.powerForward;
        if(powerLeft != null) hash = 89 * hash + this.powerLeft;
        if(spin != null) hash = 89 * hash + this.spin;
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
        final ParameterizedMove other = (ParameterizedMove) obj;
        return Objects.equals(other.powerForward, this.powerForward) &&
            Objects.equals(other.powerLeft, this.powerLeft);
    }
}
