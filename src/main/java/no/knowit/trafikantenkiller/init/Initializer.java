package no.knowit.trafikantenkiller.init;

import no.knowit.trafikantenkiller.domain.DomainServices;
import no.knowit.trafikantenkiller.domain.Station;
import no.knowit.trafikantenkiller.domain.Traveltype;

import org.apache.log4j.Logger;

public class Initializer {

	private static Logger logger = Logger.getLogger(Initializer.class);

	public void initDatabase(DomainServices domainServices) {

		Station majorstuen = domainServices.createStation("Majorstuen");
		Station bislett = domainServices.createStation("Bislett");
		Station tullinlokka = domainServices.createStation("Tullinløkka");
		Station nasjonalteateret = domainServices.createStation("Nasjonalteateret");
		Station jernbanetorget = domainServices.createStation("Jernbanetorget");

		createBidirectionalConnection(majorstuen, nasjonalteateret, 5, Traveltype.SUB);
		createBidirectionalConnection(majorstuen, nasjonalteateret, 12, Traveltype.TRAM);
		createBidirectionalConnection(jernbanetorget, nasjonalteateret, 5, Traveltype.SUB);
		createBidirectionalConnection(jernbanetorget, nasjonalteateret, 8, Traveltype.TRAM);
		createBidirectionalConnection(jernbanetorget, tullinlokka, 8, Traveltype.TRAM);
		createBidirectionalConnection(bislett, tullinlokka, 8, Traveltype.TRAM);

		logger.info("Du er nå klar for workshopen!");
	}

	private void createBidirectionalConnection(Station from, Station to, int duration, Traveltype type) {
		from.addConnection(to, duration, type);
		to.addConnection(from, duration, type);
	}

}