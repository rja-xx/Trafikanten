package no.knowit.trafikantenkiller.domain;

import org.neo4j.graphdb.RelationshipType;


public enum Traveltype implements RelationshipType{
	
	TRAM("Trikk"), SUB("T-bane"), WALK("Spasertur");
	
	private final String name;

	private Traveltype(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
}