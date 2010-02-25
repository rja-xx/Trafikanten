package no.knowit.trafikantenkiller;

import no.knowit.trafikantenkiller.model.relationships.Traveltype;

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

	
	private final String name;
	private final Traveltype travelType;
	private final Integer duration;

	private RouteElement(String name, Integer duration, Traveltype travelType) {
		super();
		this.name = name;
		this.duration = duration;
		this.travelType = travelType;
	}

	public String getName() {
		return name;
	}

	public Traveltype getTravelType() {
		return travelType;
	}

	public Integer getDuration() {
		return duration;
	}

	@Override
	public String toString(){
		return "Traveltype: "+travelType+" Name: "+name+" Varer i "+duration;
	}
}
