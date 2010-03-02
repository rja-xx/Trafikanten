
package no.knowit.trafikantenkiller;

import java.util.List;

import junit.framework.Assert;
import no.knowit.trafikantenkiller.domain.Station;
import no.knowit.trafikantenkiller.route.Route;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * Unit test for trafikantenkiller.
 */
public class TrafikantenKillerTest 
{
	
	private static Logger logger = Logger.getLogger(TrafikantenKillerTest.class);
	
	private static TrafikantenKiller app = new TrafikantenKiller();

	@Test
	public void testOppsettAvDatabase(){
		try{
			app.initDatabase();
		}catch(Exception e){
			logger.info(e.getMessage());
		}
	}
	
	@Test
	public void testHentingAvHopOptimertRute(){
		Station majorstuen = getStationByName("Majorstuen");
		Station jernbanetorget = getStationByName("Jernbanetorget");
		
		Route route = app.planHopOptimizedRoute(jernbanetorget, majorstuen);
				
		Assert.assertTrue("Stoppoptimert rute mellom jernbanetorget og majorstuen skal bestå av maks to stopp.", route.getHops() <= 2);
	}
	
	@Test
	public void testHentingAvTidsOptimertRute(){
		Station majorstuen = getStationByName("Majorstuen");
		Station jernbanetorget = getStationByName("Jernbanetorget");
		
		Route route = app.planTimeOptimizedRoute(jernbanetorget, majorstuen);
				
		Assert.assertTrue("Tidsoptimert rute mellom jernbanetorget og majorstuen skal ta maks 10 min.", route.getTotalDuration() <= 10);
	}
	
	@Test
	public void testSearch(){
		List<Station> list = app.searchForStation(".*o.*");
		Assert.assertEquals("Ved søk på stasjoner skal rett resultat returneres.", 3, list.size());
	}
	
	
	private Station getStationByName(String stationName) {
		List<Station> stations = app.searchForStation(stationName);
		Assert.assertTrue("Søk på gyldig stasjonsnavn, "+stationName+", ska gi ett treff.", stations.size()==1);
		return stations.get(0);
	}
}
