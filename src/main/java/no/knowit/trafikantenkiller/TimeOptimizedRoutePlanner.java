package no.knowit.trafikantenkiller;

import no.knowit.trafikantenkiller.model.nodes.Station;

import org.neo4j.kernel.EmbeddedGraphDatabase;

public class TimeOptimizedRoutePlanner implements Routeplanner {

	@SuppressWarnings("unused")
	private final EmbeddedGraphDatabase database;

	public TimeOptimizedRoutePlanner(EmbeddedGraphDatabase database) {
		this.database = database;
	}

	@Override
	public Route planRoute(Station from, Station to) {
		throw new NotImplementedException("dette er oppgave 1");
	}

}
