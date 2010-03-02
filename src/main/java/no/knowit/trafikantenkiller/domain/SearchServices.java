package no.knowit.trafikantenkiller.domain;

import java.util.LinkedList;
import java.util.List;

import no.knowit.trafikantenkiller.init.Table;
import no.knowit.trafikantenkiller.route.Route;
import no.knowit.trafikantenkiller.route.RouteElement;
import no.knowit.trafikantenkiller.route.Route.Builder;

import org.apache.log4j.Logger;
import org.neo4j.graphalgo.shortestpath.Dijkstra;
import org.neo4j.graphalgo.shortestpath.SingleSourceSingleSinkShortestPath;
import org.neo4j.graphalgo.shortestpath.std.IntegerAdder;
import org.neo4j.graphalgo.shortestpath.std.IntegerComparator;
import org.neo4j.graphalgo.shortestpath.std.IntegerEvaluator;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class SearchServices {

	private static final Object[] OUTGOING_TRAVELTYPES = new Object[Traveltype
			.values().length * 2];
	static {
		Traveltype[] traveltypes = Traveltype.values();
		int i = 0;
		for (Traveltype traveltype : traveltypes) {
			OUTGOING_TRAVELTYPES[i++] = traveltype;
			OUTGOING_TRAVELTYPES[i++] = Direction.OUTGOING;
		}
	}

	private static Logger logger = Logger.getLogger(SearchServices.class);

	private final EmbeddedGraphDatabase database;

	private final Node basenode;

	private final static IntegerEvaluator COST_EVALUATOR= new IntegerEvaluator(null){
		@Override
		public Integer getCost(Relationship relationship, boolean backwards) {
			return 1;
		}
	};;

	public SearchServices(EmbeddedGraphDatabase database, Node basenode) {
		this.database = database;
		this.basenode = basenode;
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
		Iterable<Relationship> relationships = basenode.getRelationships(
				Table.STATIONS, Direction.OUTGOING);
		for (Relationship relationship : relationships) {
			Station station = new Station(relationship.getEndNode());
			if (station.getName().matches(regexp)) {
				res.add(station);
			}
		}
	}

	public Route findHopOptimizedRoute(Station from, Station to) {
		Route res = null;
		Builder builder = new Route.Builder(from.getName());
		Transaction tx = database.beginTx();
		try {
			List<Relationship> path = getShortestPath(from, to, COST_EVALUATOR);
			res = buildRoute(builder, path);
			tx.success();
		} catch (Exception e) {
			logger.error("Klarte ikke å finne hop-optimert rute mellom " + from
					+ " og " + to, e);
			tx.failure();
		} finally {
			tx.finish();
		}
		return res;
	}

	
	public Route findTimeoptimizedRoute(Station from, Station to) {
		Route res = null;
		Builder builder = new Route.Builder(from.getName());
		Transaction tx = database.beginTx();
		try {
			List<Relationship> path = getShortestPath(from, to, new IntegerEvaluator("duration"));
			res = buildRoute(builder, path);
			tx.success();
		} catch (Exception e) {
			logger.error("Klarte ikke å finne tids-optimert rute mellom " + from
					+ " og " + to, e);
			tx.failure();
		} finally {
			tx.finish();
		}
		return res;
	}

	private Route buildRoute(Builder builder, List<Relationship> path) {
		Route res;
		for (Relationship relationship : path) {
			RouteElement.Builder b = new RouteElement.Builder();
			b.setDuration((Integer) relationship.getProperty("duration"));
			b.setTravelType(Traveltype.valueOf(relationship.getType()
					.name()));
			b.setName((String) relationship.getEndNode()
					.getProperty("name"));
			builder.addRouteelement(b.build());
		}
		res = builder.build();
		return res;
	}

	private List<Relationship> getShortestPath(Station from, Station to,
			IntegerEvaluator costEvaluator) {
		List<Relationship> path;
		SingleSourceSingleSinkShortestPath<Integer> shortestPathCalculator = new Dijkstra<Integer>(
				0, from.getUnderlyingNode(), to.getUnderlyingNode(),
				costEvaluator, new IntegerAdder(),
				new IntegerComparator(), Direction.OUTGOING, Traveltype
						.values());
		path = shortestPathCalculator
				.getPathAsRelationships();
		return path;
	}

}
