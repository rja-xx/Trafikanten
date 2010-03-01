package no.knowit.trafikantenkiller;

import java.util.List;

import no.knowit.trafikantenkiller.domain.DomainServices;
import no.knowit.trafikantenkiller.domain.Station;
import no.knowit.trafikantenkiller.init.Initializer;
import no.knowit.trafikantenkiller.route.Route;
import no.knowit.trafikantenkiller.route.RouteplannerFactory;

public class TrafikantenKiller {

	public Route planHopOptimizedRoute(Station from, Station to) {
		return DomainServices.getInstance().planRoute(from, to, RouteplannerFactory.getHopOptimizedRouteplanner());
	}

	public Route planTimeOptimizedRoute(Station from, Station to) {
		return DomainServices.getInstance().planRoute(from, to,RouteplannerFactory.getTimeOptimizedRouteplanner());
	}

	public List<Station> getAvailableStations() {
		return DomainServices.getInstance().searchStation(".*");
	}

	public void initDatabase() {
		new Initializer().initDatabase();
	}

	public List<Station> searchForStation(String regexp) {
		return DomainServices.getInstance().searchStation(regexp);
	}

}
