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
		
		Station gardermoen = domainServices.createStation("Gardermoen");
		Station altaLufthavn = domainServices.createStation("Alta Lufthavn");
		Station sentrum = domainServices.createStation("Alta Sentrum");
		Station city = domainServices.createStation("Alta City");
		Station kronstad = domainServices.createStation("Kronstad");
		Station bossekopp = domainServices.createStation("Bossekopp");
		Station elvebakken = domainServices.createStation("Elvebakken");

		domainServices.createBidirectionalConnection(majorstuen, nasjonalteateret, 5, Traveltype.SUB);
		domainServices.createBidirectionalConnection(majorstuen, nasjonalteateret, 12, Traveltype.TRAM);
		domainServices.createBidirectionalConnection(jernbanetorget, nasjonalteateret, 5, Traveltype.SUB);
		domainServices.createBidirectionalConnection(jernbanetorget, nasjonalteateret, 8, Traveltype.TRAM);
		domainServices.createBidirectionalConnection(jernbanetorget, tullinlokka, 8, Traveltype.TRAM);
		domainServices.createBidirectionalConnection(bislett, tullinlokka, 8, Traveltype.TRAM);
		domainServices.createBidirectionalConnection(bislett, majorstuen, 10, Traveltype.WALK);
		domainServices.createBidirectionalConnection(nasjonalteateret, tullinlokka, 5, Traveltype.WALK);
		
		domainServices.createBidirectionalConnection(jernbanetorget, gardermoen, 19, Traveltype.AIRPORT_EXPRESS);
		domainServices.createBidirectionalConnection(gardermoen, altaLufthavn, 150, Traveltype.AIRPLANE);
		domainServices.createBidirectionalConnection(altaLufthavn, elvebakken, 8, Traveltype.WALK);
		domainServices.createBidirectionalConnection(altaLufthavn, city, 15, Traveltype.BUS);
		
		domainServices.createBidirectionalConnection(bossekopp, city, 15, Traveltype.BUS);
		domainServices.createBidirectionalConnection(city, sentrum, 5, Traveltype.BUS);
		domainServices.createBidirectionalConnection(sentrum, kronstad, 10, Traveltype.BUS);

		logger.info("Du er nå klar for workshopen!");
	}



}