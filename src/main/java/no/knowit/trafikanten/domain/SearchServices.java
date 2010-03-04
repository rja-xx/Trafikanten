package no.knowit.trafikanten.domain;

import java.util.LinkedList;
import java.util.List;

import no.knowit.trafikanten.route.Route;
import no.knowit.trafikanten.route.RouteElement;
import no.knowit.trafikanten.route.Route.Builder;

import org.apache.log4j.Logger;
import org.neo4j.graphalgo.shortestpath.Dijkstra;
import org.neo4j.graphalgo.shortestpath.SingleSourceSingleSinkShortestPath;
import org.neo4j.graphalgo.shortestpath.std.IntegerAdder;
import org.neo4j.graphalgo.shortestpath.std.IntegerComparator;
import org.neo4j.graphalgo.shortestpath.std.IntegerEvaluator;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;
import org.neo4j.kernel.EmbeddedGraphDatabase;

/**
 * Denne klassen inneholder alle tjenestene som er koblet til søke innehold fra databasen.
 */
public class SearchServices {

	/** 
	 * Denne konstanten definerer en array som inneholder alle reisetypene, samt utgående retning. Denne kan brukes ved traversering av grafen dersom
	 * alle reisemiddel er tillatt. 
	 */
	private static final Object[] OUTGOING_TRAVELTYPES = new Object[Connectiontype.values().length * 2];
	static {
		Connectiontype[] traveltypes = Connectiontype.values();
		int i = 0;
		for (Connectiontype traveltype : traveltypes) {
			OUTGOING_TRAVELTYPES[i++] = traveltype;
			OUTGOING_TRAVELTYPES[i++] = Direction.OUTGOING;
		}
	}

	private static Logger logger = Logger.getLogger(SearchServices.class);

	private final EmbeddedGraphDatabase database;

	public SearchServices(EmbeddedGraphDatabase database) {
		this.database = database;
	}

	public List<Station> searchStation(String regexp) {
		List<Station> res = new LinkedList<Station>();
		Transaction tx = database.beginTx();
		try {
			findMatchingStations(regexp, res);
			tx.success();
		} catch (Exception e) {
			logger.error("Klarer ikke å finne matchende stasjoner", e);
			tx.failure();
		} finally {
			tx.finish();
		}
		return res;
	}

	private void findMatchingStations(String regexp, List<Station> res) {
		Iterable<Relationship> relationships = getBasenode().getRelationships(Station.TABLE, Direction.OUTGOING);
		for (Relationship relationship : relationships) {
			Node endNode = relationship.getEndNode();
			Station station = new Station(endNode);
			boolean matches = station.getName().matches(regexp);
			if (matches) {
				res.add(station);
			}
		}
	}


	public Route findRoute(Station from, Station to) {
		Route res = null;
		Builder builder = new Route.Builder(from.getName());
		Transaction tx = database.beginTx();
		try {
			List<Relationship> path = null;
			SingleSourceSingleSinkShortestPath<Integer> shortestPathCalculator = new Dijkstra<Integer>(0, from.getUnderlyingNode(), to.getUnderlyingNode(), new IntegerEvaluator("duration"),
					new IntegerAdder(), new IntegerComparator(), Direction.OUTGOING, Connectiontype.values());
			path = shortestPathCalculator.getPathAsRelationships();
			res = buildRoute(builder, path);
			tx.success();
		} catch (Exception e) {
			logger.error("Klarte ikke å finne tids-optimert rute mellom " + from + " og " + to, e);
			tx.failure();
		} finally {
			tx.finish();
		}
		return res;
	}

	private Route buildRoute(Builder builder, List<Relationship> path) {
		Route res;
		for (Relationship relationship : path) {
			String destination = (String) relationship.getEndNode().getProperty("name");
			Integer duration = (Integer) relationship.getProperty("duration");
			Connectiontype traveltype = Connectiontype.valueOf(relationship.getType().name());
			RouteElement re = new RouteElement(destination, duration, traveltype);
			builder.addRouteelement(re);
		}
		res = builder.build();
		return res;
	}

	public boolean connectionExists(Station from, Station to) {
		Transaction tx = database.beginTx();
		try {
			Node fromNode = from.getUnderlyingNode();
			Traverser traverser = fromNode.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, ReturnableEvaluator.ALL, OUTGOING_TRAVELTYPES);
			for (Node node : traverser) {
				Station station = new Station(node);
				if (station.equals(to)) {
					return true;
				}
			}
			tx.success();
		} catch (Exception e) {
			logger.error("Kunne ikke finne kobling mellom " + from + " og " + to + " på grunn av en feil!", e);
			tx.failure();
		} finally {
			tx.finish();
		}
		return false;
	}

	private Node getBasenode() {
		return this.database.getNodeById(0);
	}
}
