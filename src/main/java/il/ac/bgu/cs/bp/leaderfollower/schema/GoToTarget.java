package il.ac.bgu.cs.bp.leaderfollower.schema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries(value = {
        @NamedQuery(name = "GoToTarget", query = "SELECT gt FROM GoToTarget gt WHERE gt.target!=''"),
        @NamedQuery(name = "GoToBall", query = "SELECT gt FROM GoToTarget gt WHERE gt.target='ball'"),
        @NamedQuery(name = "SetTarget", query = "Update GoToTarget gt set gt.target=:target"),})
public class GoToTarget extends BasicEntity {
    @Column
    public final String target;

    public GoToTarget() {
        super("GoToTarget");
        target = "";
    }

    @Override
    public String toString() {
        return "GoToTarget_" + target;
    }
    /*
     * public GoToTarget(String target) { super("GoToTarget_" + target); this.target = target; }
     */
}
