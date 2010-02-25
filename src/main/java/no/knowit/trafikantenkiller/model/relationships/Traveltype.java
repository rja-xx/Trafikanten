package no.knowit.trafikantenkiller.model.relationships;

import org.neo4j.graphdb.RelationshipType;


public enum Traveltype implements RelationshipType{
	TRAM("Trikk"), SUB("T-bane");
	
	private final String name;

	private Traveltype(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}
}