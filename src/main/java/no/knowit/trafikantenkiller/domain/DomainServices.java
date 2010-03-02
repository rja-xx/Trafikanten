package no.knowit.trafikantenkiller.domain;

import no.knowit.trafikantenkiller.init.Table;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class DomainServices {

	private static Logger logger = Logger.getLogger(DomainServices.class);
	private final EmbeddedGraphDatabase database;
	private final Node basenode;

	public DomainServices(EmbeddedGraphDatabase database, Node basenode) {
		this.database = database;
		this.basenode = basenode;
	}

	public Station createStation(String name) {
		Node node = database.createNode();
		basenode.createRelationshipTo(node, Table.STATIONS);
		Station res = new Station(node);
		res.setName(name);
		logger.info("Created new station!");
		return res; 
	}


}
