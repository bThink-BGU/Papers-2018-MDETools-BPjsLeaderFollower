package il.ac.bgu.cs.bp.leaderfollower.events;

import il.ac.bgu.cs.bp.bpjs.model.BEvent;
import il.ac.bgu.cs.bp.leaderfollower.PlayerCommands.GpsData;


@SuppressWarnings("serial")
public class OpponentGps extends BEvent {
	public final GpsData gpsData;

	public OpponentGps(GpsData gpsData) {
		super("OpponentGPS(" + gpsData + ")");
		this.gpsData = gpsData;
	}
}
