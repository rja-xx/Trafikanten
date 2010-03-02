package no.knowit.trafikantenkiller.domain;

import java.util.LinkedList;
import java.util.List;

import no.knowit.trafikantenkiller.init.Table;
import no.knowit.trafikantenkiller.route.Route;
import no.knowit.trafikantenkiller.route.RouteElement;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class SearchServices {

	private static Logger logger = Logger.getLogger(SearchServices.class);

	private final EmbeddedGraphDatabase database;

	private final Node basenode;

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

		Transaction tx = database.beginTx();
		try {
			Node startNode = from.getUnderlyingNode();
			final Node endNode = to.getUnderlyingNode();

			Traverser traverser = startNode.traverse(
					Traverser.Order.DEPTH_FIRST,
					new StopEvaluator() {
						@Override
						public boolean isStopNode(TraversalPosition currentPos) {
							long currentNodeId = currentPos.currentNode()
									.getId();
							long destinationNodeId = endNode.getId();

							return currentNodeId == destinationNodeId;
						}
					}, ReturnableEvaluator.ALL, Traveltype.SUB,
					Direction.OUTGOING, Traveltype.TRAM, Direction.OUTGOING);

			Route.Builder routeBuilder = null;
			for (Node node : traverser) {
				TraversalPosition currentPosition = traverser.currentPosition();
				boolean isStartNode = currentPosition.isStartNode();
				if (isStartNode) {
					routeBuilder = new Route.Builder(from.getName());
					continue;
				}
				String name = (String) node.getProperty("name");

				Relationship lastRelationshipTraversed = currentPosition
						.lastRelationshipTraversed();
				Integer duration = isStartNode ? 0
						: (Integer) lastRelationshipTraversed
								.getProperty("duration");
				Traveltype type = isStartNode ? null : Traveltype
						.valueOf(lastRelationshipTraversed.getType().name());
				RouteElement routeElement = new RouteElement.Builder().setName(
						name).setDuration(duration).setTravelType(type).build();
				routeBuilder.addRouteelement(routeElement);

				if (node.getId() == endNode.getId()) {
					Route temp = routeBuilder.build();
					if (res == null || res.getHops() > temp.getHops()) {
						res = temp;
					}
				}
			}
			tx.success();
		} catch (Exception e) {
			logger.error(
					"Systemet klarte ikke å kartlegge en stopp-optimert rute.",
					e);
			tx.failure();
		} finally {
			tx.finish();
		}
		return res;
	}

	public Route findTimeoptimizedRoute(Station from, Station to) {
		Route res = null;
		Transaction tx = database.beginTx();
		try {
			Node startnode = from.getUnderlyingNode();
			final Node toNode = to.getUnderlyingNode();
			Traverser traverser = startnode.traverse(
					Order.DEPTH_FIRST,
					new StopEvaluator() {
						@Override
						public boolean isStopNode(TraversalPosition currentPos) {
							return currentPos.currentNode().getId() == toNode
									.getId();
						}
					}, ReturnableEvaluator.ALL, Traveltype.SUB,
					Direction.OUTGOING, Traveltype.TRAM, Direction.OUTGOING);

			Route.Builder builder = null;
			for (Node node : traverser) {
				TraversalPosition currentPosition = traverser.currentPosition();
				Relationship lastRelationshipTraversed = currentPosition
						.lastRelationshipTraversed();
				if (currentPosition.isStartNode()) {
					builder = new Route.Builder(from.getName());
					continue;
				}
				Station station = new Station(node);
				Iterable<Relationship> relationships = node
						.getRelationships(Direction.INCOMING);

				Relationship fastestRelation = lastRelationshipTraversed;
				for (Relationship relationship : relationships) {
					Node endNode = relationship.getStartNode();
					boolean isValidRelation = endNode.getId() == lastRelationshipTraversed
							.getStartNode().getId();
					if (!isValidRelation) {
						continue;
					}
					Integer thisDuration = (Integer) relationship
							.getProperty("duration");
					Integer fastestDuration = (Integer) fastestRelation
							.getProperty("duration");
					if (thisDuration < fastestDuration) {
						fastestRelation = relationship;
					}
				}
				RouteElement.Builder reb = new RouteElement.Builder();
				Integer duration = (Integer) fastestRelation
						.getProperty("duration");
				reb.setDuration(duration);
				reb.setName(station.getName());
				reb.setTravelType(Traveltype.valueOf(fastestRelation.getType()
						.name()));
				builder.addRouteelement(reb.build());
				if (station.equals(to)) {
					Route temp = builder.build();
					if (res == null
							|| temp.getTotalDuration() < res.getTotalDuration()) {
						res = temp;
					}
				}
			}
			tx.success();
		} catch (Exception e) {
			logger.error(
					"Systemet klarte ikke å kartlegge en stopp-optimert rute.",
					e);
			tx.failure();
		} finally {
			tx.finish();
		}
		return res;
	}

}
