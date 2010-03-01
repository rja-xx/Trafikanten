package no.knowit.trafikantenkiller.route;

import no.knowit.trafikantenkiller.domain.Station;

public interface Routeplanner {

	public Route planRoute(Station from, Station to);

}
