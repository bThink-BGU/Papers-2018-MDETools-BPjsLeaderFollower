package il.ac.bgu.cs.bp.leaderfollower.events;

import il.ac.bgu.cs.bp.bpjs.model.BEvent;

/**
 * An event that holds all the current telemetry
 */
@SuppressWarnings("serial")
public class Telemetry extends BEvent implements java.io.Serializable {

	/**
	 * The Telemetry
	 */
	public Double RovX;
        
        public Double RovY;
        
        public Double LeadX;
        
        public Double LeadY;
        
        public Double Compass;
        
        public Double Dist;

	
	public Telemetry(Double RovX, Double RovY,Double LeadX, Double LeadY,Double Compass, Double Dist) {
		super("Telemetry(" + RovX + "," + RovY +"," + LeadX +"," + LeadY +"," + Compass +"," + Dist + ")");
		this.RovX = RovX;
                this.RovY = RovY;
                this.LeadX = LeadX;
                this.LeadY = LeadY;
                this.Compass = Compass;
                this.Dist = Dist;              
	}

}
