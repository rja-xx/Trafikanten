
package no.knowit.trafikanten;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.Assert;
import no.knowit.trafikanten.domain.Station;
import no.knowit.trafikanten.route.Route;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

public class TrafikantenTest 
{
	
	private static Logger logger = Logger.getLogger(TrafikantenTest.class);
	
	private static Trafikanten app;

	/**
	 * Denne initrutinen skaper en testdatabase i OS:ets temp-folder. Databasen slettes etter att testene er kjørt.
	 *  
	 * @throws IOException
	 */
	@BeforeClass
	public static void init() throws IOException{
		File temp = File.createTempFile("trafikanten", "tdb");
		temp.delete();
		temp.mkdir();
		temp.deleteOnExit();
		String dbLocation = temp.getAbsolutePath();
		logger.info("Skaper database i "+dbLocation);
		app = new Trafikanten(dbLocation);
		
	}
	
	@Test
	public void testCreateDatabase(){
		try{
			app.createDatabase();
		}catch(Exception e){
			logger.info(e.getMessage());
			Assert.fail("Databasen kunne ikke skapes!");
		}
	}
	
	@Test
	public void testSearch(){
		List<Station> list = app.searchForStation(".*o.*");
		Assert.assertEquals("Ved søk på stasjoner skal rett resultat returneres.", 6, list.size());
	}
	
	@Test
	public void testSjekkAtForbindelseFinnes(){
		Station bislett = getStationByName("Bislett");
		Station kronstad = getStationByName("Kronstad");
		boolean connectionExists = app.connectionExists(bislett, kronstad);
		Assert.assertTrue("Forbindelse mellom bislett og kronstad eksisterer", connectionExists);
	}
	
	@Test
	public void testHentingRute(){
		Station majorstuen = getStationByName("Majorstuen");
		Station jernbanetorget = getStationByName("Jernbanetorget");
		
		Route route = app.planRoute(jernbanetorget, majorstuen);
				
		Assert.assertTrue("Rute mellom jernbanetorget og majorstuen skal bestå av maks to stopp.", route.getHops() <= 2);
	}
	
	private Station getStationByName(String stationName) {
		List<Station> stations = app.searchForStation(stationName);
		Assert.assertTrue("Søk på gyldig stasjonsnavn, "+stationName+", ska gi ett treff.", stations.size()==1);
		return stations.get(0);
	}
}
