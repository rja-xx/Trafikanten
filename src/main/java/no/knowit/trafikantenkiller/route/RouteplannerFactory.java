package no.knowit.trafikantenkiller.route;


public class RouteplannerFactory {

	public static Routeplanner getHopOptimizedRouteplanner() {
		return new HopOptimizedRouteplanner();
	}

	public static Routeplanner getTimeOptimizedRouteplanner() {
		return new TimeOptimizedRouteplanner();
	}
	
}
