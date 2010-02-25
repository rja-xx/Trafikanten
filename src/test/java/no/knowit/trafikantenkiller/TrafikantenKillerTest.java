
package no.knowit.trafikantenkiller;

import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;
import no.knowit.trafikantenkiller.exceptions.AlreadyInitiatedException;
import no.knowit.trafikantenkiller.model.nodes.Station;
import no.knowit.trafikantenkiller.route.Route;
import no.knowit.trafikantenkiller.route.RouteElement;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class TrafikantenKillerTest 
{
	
	private static Logger logger = Logger.getLogger(TrafikantenKillerTest.class);
	
	private static TrafikantenKiller app = TrafikantenKiller.getInstance();

	@Test
	public void testOppsettAvDatabase(){
		try{
			app.initDatabase();
		}catch(AlreadyInitiatedException e){
			logger.info("Databasen er allerede initialisert!");
		}
	}
	
	@Test
	public void testHentingAvHopOptimertRute(){
		Station majorstuen = null;
		Station jernbanetorget = null;
		
		List<Station> stations = app.getAvailableStations();
		for (Station s : stations) {
			if(s.getName().equals("Majorstuen")){
				majorstuen = s;
			}
			if(s.getName().equals("Jernbanetorget")){
				jernbanetorget = s;
			}
		}
		
		Route route = app.planHopOptimizedRoute(jernbanetorget, majorstuen);
				
		Iterator<RouteElement> iterator = route.iterator();
		Assert.assertNotNull(iterator);
		
		Assert.assertEquals("Jernbanetorget", iterator.next().getDestination());
		Assert.assertEquals("Nasjonalteateret", iterator.next().getDestination());
		Assert.assertEquals("Majorstuen", iterator.next().getDestination());
	}
	
	@Test
	public void testHentingAvTidsOptimertRute(){
		Station majorstuen = null;
		Station jernbanetorget = null;
		
		List<Station> stations = app.getAvailableStations();
		for (Station s : stations) {
			if(s.getName().equals("Majorstuen")){
				majorstuen = s;
			}
			if(s.getName().equals("Jernbanetorget")){
				jernbanetorget = s;
			}
		}
		
		//Route route = app.planTimeOptimizedRoute(jernbanetorget, majorstuen);
		
//		Iterator<RouteElement> iterator = route.iterator();
//		Assert.assertNotNull(iterator);
//		
//		Assert.assertEquals("Jernbanetorget", iterator.next().getName());
//		Assert.assertEquals("Nasjonalteateret", iterator.next().getName());
//		Assert.assertEquals("Majorstuen", iterator.next().getName());
	}
	
	
	@Test
	public void testPrintingRoute(){
		Station majorstuen = null;
		Station jernbanetorget = null;
		List<Station> stations = app.getAvailableStations();
		for (Station s : stations) {
			if(s.getName().equals("Majorstuen")){
				majorstuen = s;
			}
			if(s.getName().equals("Jernbanetorget")){
				jernbanetorget = s;
			}
		}
		Route route = app.planHopOptimizedRoute(jernbanetorget, majorstuen);
		
		String routeString = route.toString();
		logger.info(routeString);
	}
	
}
