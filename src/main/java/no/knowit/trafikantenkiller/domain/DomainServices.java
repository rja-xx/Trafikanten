package no.knowit.trafikantenkiller.domain;

import java.util.LinkedList;
import java.util.List;

import no.knowit.trafikantenkiller.init.Table;
import no.knowit.trafikantenkiller.propertyutils.ApplickationProperties;
import no.knowit.trafikantenkiller.route.Route;
import no.knowit.trafikantenkiller.route.Routeplanner;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class DomainServices {

	private static Logger logger = Logger.getLogger(DomainServices.class);
	private static final int BASE_NODE_ID = 1;
	private static final DomainServices instance = new DomainServices();
	private final Node basenode;
	private final EmbeddedGraphDatabase database;
	
	private DomainServices(){
		ApplickationProperties properties = ApplickationProperties.getInstance();
		String databaseLocation = properties.getDatabaseLocation();
		database = new EmbeddedGraphDatabase(databaseLocation);
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				database.shutdown();
			}
		});
		
		basenode = database.getNodeById(BASE_NODE_ID); 
	}

	public Station createStation() {
		Node node = database.createNode();
		basenode.createRelationshipTo(node, Table.STATIONS);
		return new Station(node); 
	}

	public Route planRoute(Station from, Station to, Routeplanner routeplanner) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Station> searchStation(String regexp) {
		List<Station> res = new LinkedList<Station>();
		Transaction tx = database.beginTx();
		try {
			findMatchingStations(regexp, res);
			tx.success();
		} catch (Exception e) {
			logger.error("Klarer ikke å finne valgbare stasjoner", e);
			tx.failure();
		} finally {
			tx.finish();
		}
		return res;
	}

	private void findMatchingStations(String regexp, List<Station> res) {
		Iterable<Relationship> relationships = basenode.getRelationships(Table.STATIONS, Direction.OUTGOING);
		for (Relationship relationship : relationships) {
			Station station = new Station(relationship.getEndNode());
			if(station.getName().matches(regexp)){
				res.add(station);
			}
		}
	}

	public static DomainServices getInstance() {
		return instance;
	}

}
