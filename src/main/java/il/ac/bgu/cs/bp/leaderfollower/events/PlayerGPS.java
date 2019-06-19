package il.ac.bgu.cs.bp.leaderfollower.events;

import il.ac.bgu.cs.bp.bpjs.model.BEvent;
import il.ac.bgu.cs.bp.leaderfollower.PlayerCommands.GpsData;


@SuppressWarnings("serial")
public class PlayerGPS extends BEvent {
	public final GpsData gpsData;

	public PlayerGPS(GpsData gpsData) {
		super("PlayerGPS(" + gpsData + ")");
		this.gpsData = gpsData;
	}
}
