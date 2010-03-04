package no.knowit.trafikanten.route;

import no.knowit.trafikanten.domain.Connectiontype;

public class RouteElement {
	
	private final String destination;
	private final Connectiontype travelType;
	private final Integer duration;

	public RouteElement(String destination, Integer duration, Connectiontype travelType) {
		super();
		this.destination = destination;
		this.duration = duration;
		this.travelType = travelType;
	}

	public String getDestination() {
		return destination;
	}

	public Connectiontype getTravelType() {
		return travelType;
	}

	public Integer getDuration() {
		return duration;
	}

	@Override
	public String toString(){
		return "Traveltype: "+travelType+" Destination: "+destination+" Duration "+duration;
	}
}
