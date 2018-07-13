package il.ac.bgu.cs.bp.leaderfollower.events;

import il.ac.bgu.cs.bp.bpjs.model.BEvent;


@SuppressWarnings("serial")
public class RoverGPS extends BEvent {

	/**
	 * The Rover GPS Data
	 */
	public Double RovGpsX;
        
        public Double RovGpsY;

	
	public RoverGPS(Double RovGpsX, Double RovGpsY) {
		super("RoverGPS(" + RovGpsX + "," + RovGpsY + ")");
		this.RovGpsX = RovGpsX;
                this.RovGpsY = RovGpsY;
	}

}
