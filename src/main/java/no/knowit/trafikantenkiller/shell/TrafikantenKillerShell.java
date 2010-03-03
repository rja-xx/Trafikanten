package no.knowit.trafikantenkiller.shell;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import no.knowit.trafikantenkiller.TrafikantenKiller;
import no.knowit.trafikantenkiller.domain.Station;
import no.knowit.trafikantenkiller.route.Route;

public class TrafikantenKillerShell {

	private static final Map<String, Command> COMMANDS = new HashMap<String, Command>();

	static {
		COMMANDS.put("init", Command.INIT_COMMAND);
		COMMANDS.put("min_hop", Command.MIN_HOP_COMMAND);
		COMMANDS.put("finn", Command.FIND_STATION_COMMAND);
		COMMANDS.put("min_time", Command.MIN_TIME_COMMAND);
		COMMANDS.put("exit", Command.EXIT_COMMAND);
		COMMANDS.put("help", Command.HELP_COMMAND);
		COMMANDS.put("connected", Command.ROUTE_EXISTS_COMMAND);
	}

	private static TrafikantenKiller trafikantenKiller = new TrafikantenKiller();

	public static void main(String[] args) throws IOException {
		printHelpmessage();
		while (true) {
			try {
				System.out.println();
				System.out.print("trafikanten>");
				String read = readCommand();
				Command command = COMMANDS.get(read );
				if (command != null) {
					System.out.println("Du valgte: " + command);
					command.run();
				} else {
					System.out.println("Ukjent oppgave!");
					printHelpmessage();
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	static void getTimeOptimizedRoute() {
		try {
			Station to;
			Station from;
			List<Station> stations = trafikantenKiller.getAvailableStations();
			System.out.println("Velg startstasjon: ");
			from = pickStation(stations);
			System.out.println("Velg endestasjon: ");
			to = pickStation(stations);
			Route route = trafikantenKiller.planTimeOptimizedRoute(from, to);
			String result = route == null ? "Rute fanns ikke" : new Routeprinter(route).toString();
			System.out.println(result);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	static void getHopOptimizedRoute() {
		try {
			Station to;
			Station from;
			List<Station> stations = trafikantenKiller.getAvailableStations();
			System.out.println("Velg startstasjon: ");
			from = pickStation(stations);
			System.out.println("Velg endestasjon: ");
			to = pickStation(stations);
			Route route = trafikantenKiller.planHopOptimizedRoute(from, to);
			String result = route == null ? "Rute fanns ikke" : new Routeprinter(route).toString();
			System.out.println(result);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	static void initDatabase() {
		try {
			trafikantenKiller.initDatabase();
			System.out.println("Databasen ble initiert!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	static void findStation() {
		try {
			String searchTerm = enterSearchterm();

			System.out.println("Søker etter stasjoner som matcher '" + searchTerm + "'");
			List<Station> result = trafikantenKiller.searchForStation(searchTerm);

			presentSearchResult(result);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private static Station pickStation(List<Station> stations) throws IOException {
		int currentIndex = 0;
		for (Station station : stations) {
			System.out.println(++currentIndex + " " + station.getName());
		}
		String stationIndex = readCommand();
		int s = Integer.valueOf(stationIndex);
		Station station = stations.get(s - 1);
		System.out.println("Du valgte " + station.getName());
		return station;
	}

	public static void printHelpmessage() {
		System.out.println();
		System.out.println("Velkommen til ruteplanleggeren, velg en av følgende oppgaver: ");
		for (Entry<String, Command> command : COMMANDS.entrySet()) {
			String c = command.getKey();
			String padding = "                  ";
			int paddingLength = 12-c.length();
			System.out.println(c + padding.substring(0, paddingLength)+" : " + command.getValue());
		}

		System.out.println();
	}

	private static void presentSearchResult(List<Station> result) {
		System.out.println("Fant " + result.size() + " stasjoner:");
		for (Station station : result) {
			System.out.println(station.getName());
		}
	}

	private static String enterSearchterm() throws IOException {
		System.out.println("Skriv in søketermen: ");
		String searchTerm = readCommand();
		searchTerm.trim();
		searchTerm.replaceAll("\\*", ".*");
		searchTerm = ".*" + searchTerm + ".*";
		return searchTerm;
	}

	private static String readCommand() throws IOException {
		String searchTerm = "";
		char c;
		while ((c = (char) System.in.read()) != '\n') {
			searchTerm += c;
		}
		return searchTerm;
	}

	public static void routeExistsBetween() {
		try {
			Station to;
			Station from;
			List<Station> stations = trafikantenKiller.getAvailableStations();
			System.out.println("Velg startstasjon: ");
			from = pickStation(stations);
			System.out.println("Velg endestasjon: ");
			to = pickStation(stations);
			boolean connectionExists = trafikantenKiller.connectionExists(from, to);
			String svar = connectionExists ? "Ja" : "Nei";
			System.out.println("Svar: "+svar);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

}
