package no.knowit.trafikantenkiller.route;

import org.neo4j.kernel.EmbeddedGraphDatabase;

public class RouteplannerFactory {

	public static Routeplanner getHopOptimizedRouteplanner(EmbeddedGraphDatabase database) {
		return new HopOptimizedRouteplanner(database);
	}

	public static Routeplanner getTimeOptimizedRoutePlanner(EmbeddedGraphDatabase database) {
		return new TimeOptimizedRoutePlanner(database);
	}

	
	
}
