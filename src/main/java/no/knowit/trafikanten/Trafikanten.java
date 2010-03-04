package no.knowit.trafikanten;

import java.util.List;

import no.knowit.trafikanten.domain.DomainServices;
import no.knowit.trafikanten.domain.SearchServices;
import no.knowit.trafikanten.domain.Station;
import no.knowit.trafikanten.init.Initializer;
import no.knowit.trafikanten.route.Route;

import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class Trafikanten {

	private DomainServices domainServices;
	private SearchServices searchServices;

	public Trafikanten(String databaseLocation){
		final EmbeddedGraphDatabase database = initDatabase(databaseLocation);
		initFields(database);
	}


	private EmbeddedGraphDatabase initDatabase(String databaseLocation) {
		final EmbeddedGraphDatabase database = new EmbeddedGraphDatabase(databaseLocation);
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				database.shutdown();
			}
		});
		return database;
	}


	private void initFields(final EmbeddedGraphDatabase database) {
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
	
	public Route planRoute(Station from, Station to) {
		return searchServices.findRoute(from, to);
	}

	public List<Station> getAvailableStations() {
		return searchServices.searchStation(".*");
	}

	public void createDatabase() {
		boolean graphExist = !searchServices.searchStation(".*").isEmpty();
		if (graphExist) {
			throw new RuntimeException("Databasen er allerede satt opp!");
		}
		new Initializer().initDatabase(domainServices);
	}

	public List<Station> searchForStation(String regexp) {
		return searchServices.searchStation(regexp);
	}


	public boolean connectionExists(Station from, Station to) {
		return searchServices.connectionExists(from, to);
	}

}
