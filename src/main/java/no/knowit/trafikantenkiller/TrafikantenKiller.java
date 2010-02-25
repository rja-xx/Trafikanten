package no.knowit.trafikantenkiller;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import no.knowit.trafikantenkiller.init.Table;
import no.knowit.trafikantenkiller.model.nodes.Station;
import no.knowit.trafikantenkiller.propertyutils.ApplickationProperties;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class TrafikantenKiller {
	private static final int BASE_NODE = 1;
	private static Comparator<Station> stringComparator = new Comparator<Station>() {
		@Override
		public int compare(Station o1, Station o2) {
			return o1.getName().compareTo(o2.getName());
		}
	};
	private static TrafikantenKiller instance = new TrafikantenKiller();

	public static TrafikantenKiller getInstance() {
		return instance ;
	}

	private EmbeddedGraphDatabase database;
	private static Logger logger = Logger.getLogger(TrafikantenKiller.class);;

	private TrafikantenKiller(){
		super();
		ApplickationProperties properties = ApplickationProperties.getInstance();
		String databaseLocation = properties.getDatabaseLocation();
		database = new EmbeddedGraphDatabase(databaseLocation);
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				database.shutdown();
			}
		});
	}

	public Route planHopOptimizedRoute(Station from, Station to) {
		Routeplanner routeplanner = RouteplannerFactory.getHopOptimizedRouteplanner(database);
		return routeplanner.planRoute(from, to);
	}

	public Route planTimeOptimizedRoute(Station jernbanetorget, Station majorstuen) {
		Routeplanner timeOptimizedRoutePlanner = RouteplannerFactory.getTimeOptimizedRoutePlanner(database);
		return timeOptimizedRoutePlanner.planRoute(jernbanetorget, majorstuen);
	}

	public List<Station> getAvailableStations() {
		List<Station> res = new LinkedList<Station>();
		Transaction tx = database.beginTx();
		try{
			Node baseNode = database.getNodeById(BASE_NODE);
			Traverser traverse = baseNode.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, Table.STATIONS, Direction.OUTGOING);
			for (Node node : traverse) {
				res.add(new Station(node));
			}
			Collections.sort(res, stringComparator);
			tx.success();
		}catch (Exception e) {
			logger.error("Klarer ikke å finne valgbare stasjoner", e);
			tx.failure();
		}finally{
			tx.finish();
		}
		return res;
	}


}
