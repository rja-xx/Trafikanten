package no.knowit.trafikantenkiller.model.nodes;

import org.neo4j.graphdb.Node;

public class Station {

	private final Node underlyingNode;

	public Station(Node node) {
		this.underlyingNode = node;
	}

	public Node getUnderlyingNode() {
		return underlyingNode;
	}

	public void setName(String name) {
		this.underlyingNode.setProperty("name", name);
	}
	
	public String getName() {
		Object property = this.underlyingNode.getProperty("name");
		String res = (String) property;
		return res;
	}

	@Override
	public String toString(){
		return this.getName();
	}
	
	
}
