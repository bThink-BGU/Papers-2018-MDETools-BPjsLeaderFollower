package il.ac.bgu.cs.bp.leaderfollower.events;

import il.ac.bgu.cs.bp.bpjs.model.BEvent;

@SuppressWarnings("serial")
public class ParameterizedMove extends BEvent implements java.io.Serializable {

    public final int powerX;
    public final int powerZ;
    public final int spin;

    public ParameterizedMove(int powerX, int powerZ, int spin) {
        super(String.format("ParameterizedMove(%d,%d,%d)", powerX, powerZ, spin));
        this.powerX = powerX;
        this.powerZ = powerZ;
        this.spin = spin;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.powerX;
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
        return this.powerX == other.powerX && this.powerZ == other.powerZ
                && this.spin == other.spin;
    }


}
