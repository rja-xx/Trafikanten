package no.knowit.trafikantenkiller.domain;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public class Connection {

	private static final String DURATION = "duration";
	private final Relationship underlyingRelationship;

	Connection(Relationship relationship) {
		this.underlyingRelationship = relationship;
	}

	Relationship getUnderlyingRelationship() {
		return underlyingRelationship;
	}

	public Station getDestination() {
		Node node = this.underlyingRelationship.getEndNode();
		return new Station(node);
	}

	public Traveltype getTravelType() {
		String name = (String) this.underlyingRelationship.getType().name();
		return Traveltype.valueOf(name);
	}

	public void setDuration(int duration) {
		this.underlyingRelationship.setProperty(DURATION, duration);
	}
	
	public int getDuration(){
		return (Integer)this.underlyingRelationship.getProperty(DURATION);
	}

}
