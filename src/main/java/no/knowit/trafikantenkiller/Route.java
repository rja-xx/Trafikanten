package no.knowit.trafikantenkiller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Route implements Iterable<RouteElement>
{

	public static class Builder {
		
		private final List<RouteElement> routeElements = new LinkedList<RouteElement>();
		
		public Builder addRouteelement(RouteElement routeElement) {
			routeElements.add(routeElement);
			return this;
		}

		public Route build() {
			return new Route(routeElements);
		}

	}
	
	private final List<RouteElement> routeElements = new LinkedList<RouteElement>();

	private Route(List<RouteElement> routeElements) {
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

}
