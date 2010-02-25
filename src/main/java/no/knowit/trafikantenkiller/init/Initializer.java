package no.knowit.trafikantenkiller.init;

import no.knowit.trafikantenkiller.exceptions.AlreadyInitiatedException;
import no.knowit.trafikantenkiller.model.nodes.Station;
import no.knowit.trafikantenkiller.model.relationships.Traveltype;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class Initializer {

	private static Logger logger = Logger.getLogger(Initializer.class);
	private final EmbeddedGraphDatabase database;;

	public Initializer(EmbeddedGraphDatabase database) {
		this.database = database;
	}

	public void initDatabase() throws AlreadyInitiatedException{
		Transaction transaction = database.beginTx();
		try{
			for(Node node : database.getAllNodes()){
				if(node.getProperty("name", "n/a").equals("Utgangsnode")){
					throw new AlreadyInitiatedException("Databasen er allerede satt opp!");
				}
			}
			Node baseNode = database.createNode();
			baseNode.setProperty("name", "Utgangsnode");

			Node node = database.createNode();
			Station majorstuen = new Station(node);
			majorstuen.setName("Majorstuen");

			node = database.createNode();
			Station bislett = new Station(node);
			bislett.setName("Bislett");

			node = database.createNode();
			Station tullinlokka = new Station(node);
			tullinlokka.setName("Tullinløkka");

			node = database.createNode();
			Station nasjonalteateret = new Station(node);
			nasjonalteateret.setName("Nasjonalteateret");

			node = database.createNode();
			Station jernbanetorget= new Station(node);
			jernbanetorget.setName("Jernbanetorget");

			baseNode.createRelationshipTo(majorstuen.getUnderlyingNode(), Table.STATIONS);
			baseNode.createRelationshipTo(bislett.getUnderlyingNode(), Table.STATIONS);
			baseNode.createRelationshipTo(tullinlokka.getUnderlyingNode(), Table.STATIONS);
			baseNode.createRelationshipTo(nasjonalteateret.getUnderlyingNode(), Table.STATIONS);
			baseNode.createRelationshipTo(jernbanetorget.getUnderlyingNode(), Table.STATIONS);

			makeConnection(majorstuen, nasjonalteateret, 5, Traveltype.SUB);
			makeConnection(majorstuen, nasjonalteateret, 12, Traveltype.TRAM);
			makeConnection(jernbanetorget, nasjonalteateret, 5, Traveltype.SUB);
			makeConnection(jernbanetorget, nasjonalteateret, 8, Traveltype.TRAM);
			makeConnection(jernbanetorget, tullinlokka, 8, Traveltype.TRAM);
			makeConnection(bislett, tullinlokka, 8, Traveltype.TRAM);

			transaction.success();
			String message = "Du er nå klar for workshopen!";
			System.out.println(message);
			logger.info(message);
		}catch(AlreadyInitiatedException e){
			throw e;
		}catch(Exception e){
			logger.fatal("Kunne ikke skape demonstrasjonsdatabase.", e);
			transaction.failure();
		}finally{
			transaction.finish();
		}
	}

	private static void makeConnection(Station from, Station to, int duration, Traveltype traveltype) {
		Relationship rute = from.getUnderlyingNode().createRelationshipTo(to.getUnderlyingNode(), traveltype);
		rute.setProperty("duration", duration);

		rute = to.getUnderlyingNode().createRelationshipTo(from.getUnderlyingNode(), traveltype);
		rute.setProperty("duration", duration);
	}

}