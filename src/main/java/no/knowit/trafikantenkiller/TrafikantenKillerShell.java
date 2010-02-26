package no.knowit.trafikantenkiller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import no.knowit.trafikantenkiller.exceptions.AlreadyInitiatedException;
import no.knowit.trafikantenkiller.exceptions.NotImplementedException;
import no.knowit.trafikantenkiller.model.nodes.Station;
import no.knowit.trafikantenkiller.route.Route;
import no.knowit.trafikantenkiller.route.RouteElement;

public class TrafikantenKillerShell {

	public static class Routeprinter {

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

	private static final Command EXIT_COMMAND = new Command("Avslutt") {
		@Override
		public void run() {
			System.out.println("Heidå!");
			System.exit(0);
		}
	};
	private static final Command MIN_TIME_COMMAND = new Command(
			"Planlegg rute som tar kortest tid") {
		@Override
		public void run() {
			getTimeOptimizedRoute();
		}
	};
	private static final Command MIN_HOP_COMMAND = new Command(
			"Planlegg rute med minimalt antall stopp") {
		@Override
		public void run() {
			getHopOptimizedRoute();
		}
	};

	private static final Command INIT_COMMAND = new Command(
			"Initiering av grafdatabase") {
		@Override
		public void run() {
			try {
				trafikantenKiller.initDatabase();
				System.out.println("Databasen ble initiert!");
			} catch (AlreadyInitiatedException e) {
				System.out.println(e.getMessage());
			}
		}
	};

	private static final Command HELP_COMMAND = new Command("Hjelp") {
		@Override
		public void run() {
		}
	};
	
	private static final Command FIND_STATION_COMMAND = new Command("Finn stasjon") {
		@Override
		public void run() {
			try {
				System.out.println("Skriv in søketermen: ");
				String searchTerm = ""; 
				char c;
				System.in.read();
				while((c = (char)System.in.read()) != '\n'){
					searchTerm += c;
				}
				searchTerm.trim();
				searchTerm.replaceAll("\\*", ".*");
				searchTerm = ".*"+searchTerm+".*";
				System.out.println("Søker etter stasjoner som matcher '"+searchTerm+"'");
				List<Station> result = trafikantenKiller.searchForStation(searchTerm);
				System.out.println("Fant "+result.size()+" stasjoner:");
				for (Station station : result) {
					System.out.println(station.getName());
				}
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	};

	public static abstract class Command {

		private String name;

		public Command(String name) {
			this.name = name;
		}

		public abstract void run();

		@Override
		public String toString() {
			return name;
		}
	}

	private static final Map<Character, Command> COMMANDS = new HashMap<Character, Command>();

	static {
		COMMANDS.put('i', INIT_COMMAND);
		COMMANDS.put('s', MIN_HOP_COMMAND);
		COMMANDS.put('f', FIND_STATION_COMMAND);
		COMMANDS.put('t', MIN_TIME_COMMAND);
		COMMANDS.put('a', EXIT_COMMAND);
		COMMANDS.put('h', HELP_COMMAND);
	}

	private static TrafikantenKiller trafikantenKiller;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		printHelpmessage();

		trafikantenKiller = TrafikantenKiller.getInstance();

		byte read;
		while (true) {
			try {
				read = (byte) System.in.read();
				Character command = Character.valueOf((char) read);
				Command cmd = COMMANDS.get(command);
				if (cmd != null) {
					System.out.println("Du valgte: " + cmd);
					cmd.run();
				} else if (read != '\n') {
					System.out.println("Ukjent oppgave!");
				} else {
					continue;
				}
			} catch (NotImplementedException e) {
				System.out
						.println("Denne funksjonaliteten er ikke implementert: "
								+ e.getMessage());
			}
			printHelpmessage();
		}
	}

	private static void getTimeOptimizedRoute() {
		try {
			Station to;
			Station from;
			List<Station> stations = trafikantenKiller.getAvailableStations();
			System.out.println("Velg startstasjon: ");
			from = pickStation(stations);
			System.out.println("Velg endestasjon: ");
			to = pickStation(stations);
			Route route = trafikantenKiller.planTimeOptimizedRoute(from, to);
			System.out.println(new Routeprinter(route));
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private static void getHopOptimizedRoute() {
		try {
			Station to;
			Station from;
			List<Station> stations = trafikantenKiller.getAvailableStations();
			System.out.println("Velg startstasjon: ");
			from = pickStation(stations);
			System.out.println("Velg endestasjon: ");
			to = pickStation(stations);
			Route route = trafikantenKiller.planHopOptimizedRoute(from, to);
			System.out.println(new Routeprinter(route).toString());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private static Station pickStation(List<Station> stations)
			throws IOException {
		int s;
		int currentIndex;
		char read;
		String valueOf;
		currentIndex = 0;
		for (Station station : stations) {
			System.out.println(++currentIndex + " " + station.getName());
		}
		System.in.read();
		read = (char) System.in.read();
		valueOf = String.valueOf(read);
		s = Integer.valueOf(valueOf);
		Station station = stations.get(s - 1);
		System.out.println("Du valgte "+station.getName());
		return station;
	}

	private static void printHelpmessage() {
		System.out.println();
		System.out
				.println("Velkommen til ruteplanleggeren, velg en av følgende oppgaver: ");
		for (Entry<Character, Command> command : COMMANDS.entrySet()) {
			System.out.println(command.getKey() + " : " + command.getValue());
		}

		System.out.println();
	}

}
