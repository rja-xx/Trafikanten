package no.knowit.trafikanten.domain;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;

/**
 * Denne klassen inneholder alle tjenestene som er koblet til skape innehold i databasen.
 */
public class DomainServices {

	private static Logger logger = Logger.getLogger(DomainServices.class);
	private final EmbeddedGraphDatabase database;

	public DomainServices(EmbeddedGraphDatabase database) {
		this.database = database;
	}

	public Station createStation(String name) {
		Station res = null;
		Transaction tx = database.beginTx();
		try {
			Node node = database.createNode();
			getReferencenode().createRelationshipTo(node, Station.TABLE);
			res = new Station(node);
			res.setName(name);
			logger.info("Created new station: " + name);
			tx.success();
		} catch (Exception e) {
			logger.error("Klarte ikke å skape stasjonen", e);
			tx.failure();
		} finally {
			tx.finish();
		}
		return res;
	}

	private Node getReferencenode() {
		return database.getNodeById(0);
	}

	public void createBidirectionalConnection(Station from, Station to, int duration, Connectiontype type) {
		Transaction tx = database.beginTx();
		try {
			from.addConnection(to, duration, type);
			to.addConnection(from, duration, type);
			logger.info("Opprettet forbindese mellom "+from+" og "+to);
			tx.success();
		} catch (Exception e) {
			logger.error("Klarer ikke å opprette forbindelsen mellom " + from + " og " + to, e);
			tx.failure();
		} finally {
			tx.finish();
		}
	}

}
