package il.ac.bgu.cs.bp.leaderfollower.events;

import il.ac.bgu.cs.bp.bpjs.model.BEvent;


@SuppressWarnings("serial")
public class LeaderGPS extends BEvent {

	/**
	 * The Leader GPS Data
	 */
	public Double LeadGpsX;
        
        public Double LeadGpsY;

	
	public LeaderGPS(Double LeadGpsX, Double LeadGpsY) {
		super("LeaderGPS(" + LeadGpsX + "," + LeadGpsY + ")");
		this.LeadGpsX = LeadGpsX;
                this.LeadGpsX = LeadGpsX;
	}

}
