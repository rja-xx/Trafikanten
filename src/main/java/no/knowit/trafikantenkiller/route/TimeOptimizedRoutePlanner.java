package no.knowit.trafikantenkiller.route;

import no.knowit.trafikantenkiller.init.Table;
import no.knowit.trafikantenkiller.model.nodes.Station;
import no.knowit.trafikantenkiller.model.relationships.Traveltype;

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

public class TimeOptimizedRoutePlanner implements Routeplanner {

	private final EmbeddedGraphDatabase database;

	public TimeOptimizedRoutePlanner(EmbeddedGraphDatabase database) {
		this.database = database;
	}

	@Override
	public Route planRoute(Station from, final Station to) {
		Route res = null;
		Transaction transaction = database.beginTx();
		try {
			Node startnode = from.getUnderlyingNode();
			Traverser traverser = startnode.traverse(
					Order.DEPTH_FIRST,
					new StopEvaluator() {
						@Override
						public boolean isStopNode(TraversalPosition currentPos) {
							return currentPos.currentNode().getId() == to.getUnderlyingNode().getId();
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
				Iterable<Relationship> relationships = node.getRelationships(Direction.INCOMING);
			
				Relationship fastestRelation = lastRelationshipTraversed;
				for (Relationship relationship : relationships) {
					Node endNode = relationship.getStartNode();
					boolean isValidRelation = endNode.getId() == lastRelationshipTraversed.getStartNode().getId();
					if(!isValidRelation){
						continue;
					}
					Integer thisDuration = (Integer) relationship.getProperty("duration");
					Integer fastestDuration = (Integer) fastestRelation.getProperty("duration");
					if(thisDuration < fastestDuration){
						fastestRelation = relationship;
					}
				}
				RouteElement.Builder reb = new RouteElement.Builder();
				Integer duration = (Integer) fastestRelation.getProperty("duration");
				reb.setDuration(duration);
				reb.setName(station.getName());
				reb.setTravelType(Traveltype.valueOf(fastestRelation.getType().name()));
				builder.addRouteelement(reb.build());
				if (station.equals(to)) {
					Route temp = builder.build();
					if (res == null || temp.getTotalDuration() < res.getTotalDuration()) {
						res = temp;
					}
				}
			}
			transaction.success();
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(
					"Klarer ikke å traversere databasen!", e);
			transaction.failure();
		} finally {
			transaction.finish();
		}
		return res;
	}
}
