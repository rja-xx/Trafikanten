
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
				
		Assert.assertTrue("Hop-optimert rute mellom jernbanetorget og majorstuen skal bestå av maks to stopp.", route.getHops() <= 2);
	}
	
	@Test
	public void testHentingAvTidsOptimertRute(){
		Station majorstuen = getStationByName("Majorstuen");
		Station jernbanetorget = getStationByName("Jernbanetorget");
		
		Route route = app.planTimeOptimizedRoute(jernbanetorget, majorstuen);
				
		Assert.assertTrue("Hop-optimert rute mellom jernbanetorget og majorstuen skal bestå av maks to stopp.", route.getHops() <= 2);
	}
	
	@Test
	public void testSearch(){
		List<Station> list = app.searchForStation(".*o.*");
		Assert.assertEquals(3, list.size());
	}
	

	
	//HELPER METHODS
	private Station getStationByName(String stationName) {
		Station res = null;
		List<Station> stations = app.getAvailableStations();
		for (Station s : stations) {
			if(s.getName().equals(stationName)){
				res = s;
			}
		}
		return res;
	}
}
