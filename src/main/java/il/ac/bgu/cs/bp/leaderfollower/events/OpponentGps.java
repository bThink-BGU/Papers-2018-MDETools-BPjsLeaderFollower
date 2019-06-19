package il.ac.bgu.cs.bp.leaderfollower.events;

import il.ac.bgu.cs.bp.bpjs.model.BEvent;


@SuppressWarnings("serial")
public class OpponentGps extends BEvent {

	/**
	 * The Leader GPS Data
	 */
	public Double OpponentGpsX;

	public Double OpponentGpsY;


	public OpponentGps(Double OpponentGpsX, Double OpponentGpsY) {
		super("LeaderGPS(" + OpponentGpsX + "," + OpponentGpsY + ")");
		this.OpponentGpsX = OpponentGpsX;
		this.OpponentGpsX = OpponentGpsX;
	}

}
