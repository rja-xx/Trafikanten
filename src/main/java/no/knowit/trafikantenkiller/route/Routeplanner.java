package no.knowit.trafikantenkiller.route;

import no.knowit.trafikantenkiller.model.nodes.Station;

public interface Routeplanner {

	public Route planRoute(Station from, Station to);

}
