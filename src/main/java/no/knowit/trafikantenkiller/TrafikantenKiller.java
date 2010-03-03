package no.knowit.trafikantenkiller;

import java.util.List;

import no.knowit.trafikantenkiller.domain.DomainServices;
import no.knowit.trafikantenkiller.domain.SearchServices;
import no.knowit.trafikantenkiller.domain.Station;
import no.knowit.trafikantenkiller.init.Initializer;
import no.knowit.trafikantenkiller.propertyutils.ApplickationProperties;
import no.knowit.trafikantenkiller.route.Route;

import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class TrafikantenKiller {

	private DomainServices domainServices;
	private SearchServices searchServices;

	public TrafikantenKiller(){
		ApplickationProperties properties = ApplickationProperties.getInstance();
		String databaseLocation = properties.getDatabaseLocation();
		final EmbeddedGraphDatabase database = new EmbeddedGraphDatabase(databaseLocation);
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				database.shutdown();
			}
		});
		Transaction tx = database.beginTx();
		try{
			domainServices = new DomainServices(database);
			searchServices = new SearchServices(database);
			tx.success();
		}catch(Exception e){
			tx.failure();
			System.exit(-1);
		}finally{
			tx.finish();
		}
	}

	
	public Route planHopOptimizedRoute(Station from, Station to) {
		return searchServices.findHopOptimizedRoute(from, to);
	}

	public Route planTimeOptimizedRoute(Station from, Station to) {
		return searchServices.findTimeoptimizedRoute(from, to);
	}

	public List<Station> getAvailableStations() {
		return searchServices.searchStation(".*");
	}

	public void initDatabase() {
		boolean graphExist = !searchServices.searchStation(".*").isEmpty();
		if (graphExist) {
			throw new RuntimeException("Databasen er allerede satt opp!");
		}
		new Initializer().initDatabase(domainServices);
	}

	public List<Station> searchForStation(String regexp) {
		return searchServices.searchStation(regexp);
	}

}
