package il.ac.bgu.cs.bp.leaderfollower.events;

import il.ac.bgu.cs.bp.bpjs.model.BEvent;


@SuppressWarnings("serial")
public class PlayerGPS extends BEvent {

	/**
	 * The Player GPS Data
	 */
	public Double RovGpsX;
        
        public Double RovGpsY;

	
	public PlayerGPS(Double RovGpsX, Double RovGpsY) {
		super("PlayerGPS(" + RovGpsX + "," + RovGpsY + ")");
		this.RovGpsX = RovGpsX;
                this.RovGpsY = RovGpsY;
	}

}
