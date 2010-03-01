package no.knowit.trafikantenkiller.route;

import no.knowit.trafikantenkiller.domain.Traveltype;

public class RouteElement {

	public static class Builder {

		private String name;
		private Integer duration;
		private Traveltype travelType;

		public Builder(){
			super();
		}
		
		public Builder setName(String name) {
			this.name = name;
			return this;
		}
		
		public Builder setDuration(Integer duration) {
			this.duration = duration;
			return this;
		}

		public RouteElement build(){
			return new RouteElement(name, duration, travelType);
		}

		public Builder setTravelType(Traveltype type) {
			this.travelType = type;
			return this;
		}

	}

	
	private final String destination;
	private final Traveltype travelType;
	private final Integer duration;

	private RouteElement(String destination, Integer duration, Traveltype travelType) {
		super();
		this.destination = destination;
		this.duration = duration;
		this.travelType = travelType;
	}

	public String getDestination() {
		return destination;
	}

	public Traveltype getTravelType() {
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
