package no.knowit.trafikantenkiller.route;

import no.knowit.trafikantenkiller.domain.Station;
import no.knowit.trafikantenkiller.domain.Traveltype;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class HopOptimizedRouteplanner implements Routeplanner {


	@Override
	public Route planRoute(Station from, Station to) {
		Route res = null;
		Transaction transaction = this.database.beginTx();
		try{
			Node startNode = from.getUnderlyingNode();
			final Node endNode = to.getUnderlyingNode();

			Traverser traverser = startNode.traverse(
					Traverser.Order.DEPTH_FIRST, 
					new StopEvaluator(){
						@Override
						public boolean isStopNode(TraversalPosition currentPos) {
							long currentNodeId = currentPos.currentNode().getId();
							long destinationNodeId = endNode.getId();

							return currentNodeId == destinationNodeId;
						}
					},
					ReturnableEvaluator.ALL,
					Traveltype.SUB,
					Direction.OUTGOING,
					Traveltype.TRAM,
					Direction.OUTGOING);

			Route.Builder routeBuilder = null;
			for (Node node : traverser) {
				TraversalPosition currentPosition = traverser.currentPosition();
				boolean isStartNode = currentPosition.isStartNode();
				if(isStartNode){
					routeBuilder = new Route.Builder(from.getName());
					continue;
				}
				String name = (String) node.getProperty("name");

				Relationship lastRelationshipTraversed = currentPosition.lastRelationshipTraversed();
				Integer duration = isStartNode ? 0 : (Integer) lastRelationshipTraversed.getProperty("duration");
				Traveltype type = isStartNode ? null :Traveltype.valueOf(lastRelationshipTraversed.getType().name());
				RouteElement routeElement = new RouteElement.Builder().setName(name).setDuration(duration).setTravelType(type).build();
				routeBuilder.addRouteelement(routeElement);

				if(node.getId() == endNode.getId()){
					Route temp = routeBuilder.build();
					if(res == null || res.getHops() > temp.getHops()){
						res = temp;
					}
				}
			}
			transaction.success();
		}catch (Exception e) {
			Logger.getLogger(HopOptimizedRouteplanner.class).error("Programmet klarer ikke å finne rute", e);
			transaction.failure();
			res = null;
		}finally{	
			transaction.finish();
		}
		return res;
	}

}
