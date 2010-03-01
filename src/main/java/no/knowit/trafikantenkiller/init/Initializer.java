package no.knowit.trafikantenkiller.init;

import no.knowit.trafikantenkiller.domain.DomainServices;
import no.knowit.trafikantenkiller.domain.Station;
import no.knowit.trafikantenkiller.domain.Traveltype;

import org.apache.log4j.Logger;

public class Initializer {

	private static Logger logger = Logger.getLogger(Initializer.class);

	public void initDatabase() {
		DomainServices domainService = DomainServices.getInstance();
		boolean graphExist = !domainService.searchStation(".*").isEmpty();
		if (graphExist) {
			throw new RuntimeException("Databasen er allerede satt opp!");
		}

		Station majorstuen = domainService.createStation();
		majorstuen.setName("Majorstuen");

		Station bislett = domainService.createStation();
		bislett.setName("Bislett");

		Station tullinlokka = domainService.createStation();
		tullinlokka.setName("Tullinløkka");

		Station nasjonalteateret = domainService.createStation();
		nasjonalteateret.setName("Nasjonalteateret");

		Station jernbanetorget = domainService.createStation();
		jernbanetorget.setName("Jernbanetorget");

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