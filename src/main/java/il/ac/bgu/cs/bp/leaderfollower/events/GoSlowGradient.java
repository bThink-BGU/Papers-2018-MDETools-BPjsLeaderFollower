package il.ac.bgu.cs.bp.leaderfollower.events;

import il.ac.bgu.cs.bp.bpjs.model.BEvent;


@SuppressWarnings("serial")
public class GoSlowGradient extends BEvent implements java.io.Serializable {
        
        public int power;
	
	public GoSlowGradient(int power) {
		super("GoSlowGradient(" + power + ")");
		this.power = power;
	}

}
