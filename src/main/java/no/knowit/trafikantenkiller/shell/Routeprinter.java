package no.knowit.trafikantenkiller.shell;

import no.knowit.trafikantenkiller.route.Route;
import no.knowit.trafikantenkiller.route.RouteElement;

public class Routeprinter {


		private final Route route;

		public Routeprinter(Route route) {
			this.route = route;
		}

		@Override
		public String toString(){
			StringBuilder sb = new StringBuilder();
			sb.append("Rute mellom ").append(route.getFrom()).append(" og ").append(route.getTo()).append(":\n");
			for (RouteElement r : route) {
				String travelBy = r.getTravelType().getName();
				String dest = r.getDestination();
				Integer dur = r.getDuration();
				sb.append("Ta "+travelBy+" til "+dest+" det tar "+dur+" minutter.");
				sb.append("\n");
			}
			sb.append("Turen tar totalt "+route.getTotalDuration()+" minutter");
			return sb.toString();
		}	
	
}
