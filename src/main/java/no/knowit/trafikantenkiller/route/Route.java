package no.knowit.trafikantenkiller.route;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Route implements Iterable<RouteElement>
{

	public static class Builder {
		
		private final List<RouteElement> routeElements = new LinkedList<RouteElement>();
		private final String from;
		
		public Builder(String from){
			this.from = from;
		}
		
		public Builder addRouteelement(RouteElement routeElement) {
			routeElements.add(routeElement);
			return this;
		}

		public Route build() {
			return new Route(from, routeElements);
		}

	}
	
	private final List<RouteElement> routeElements = new LinkedList<RouteElement>();
	private final String from;

	private Route(String from, List<RouteElement> routeElements) {
		this.from = from;
		this.routeElements.addAll(routeElements);
	}

	@Override
	public Iterator<RouteElement> iterator() {
		return this.routeElements.iterator();
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for (RouteElement routeElement : this) {
			sb.append(routeElement).append("\n");
		}
		return sb.toString();
	}

	public String getFrom() {
		return this.from;
	}

	public String getTo() {
		int index = this.routeElements.size()-1;
		return this.routeElements.get(index).getDestination();
	}

	public int getTotalDuration() {
		int res = 0;
		for (RouteElement r : routeElements) {
			res += r.getDuration();
		}
		return res;
	}

}
