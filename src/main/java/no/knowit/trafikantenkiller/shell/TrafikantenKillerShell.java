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

	private static final Map<Character, Command> COMMANDS = new HashMap<Character, Command>();

	static {
		COMMANDS.put('i', Command.INIT_COMMAND);
		COMMANDS.put('s', Command.MIN_HOP_COMMAND);
		COMMANDS.put('f', Command.FIND_STATION_COMMAND);
		COMMANDS.put('t', Command.MIN_TIME_COMMAND);
		COMMANDS.put('a', Command.EXIT_COMMAND);
		COMMANDS.put('h', Command.HELP_COMMAND);
	}

	private static TrafikantenKiller trafikantenKiller = new TrafikantenKiller();

	public static void main(String[] args) throws IOException {
		printHelpmessage();
		while (true) {
			try {
				byte read = (byte) System.in.read();
				Command command = COMMANDS.get(Character.valueOf((char) read));
				if (command != null) {
					System.out.println("Du valgte: " + command);
					command.run();
				} else if (read != '\n') {
					System.out.println("Ukjent oppgave!");
				} else {
					continue;
				}		
				printHelpmessage();
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
			System.out.println(new Routeprinter(route));
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
			System.out.println(new Routeprinter(route).toString());
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

			System.out.println("Søker etter stasjoner som matcher '"
					+ searchTerm + "'");
			List<Station> result = trafikantenKiller
					.searchForStation(searchTerm);

			presentSearchResult(result);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private static Station pickStation(List<Station> stations) throws IOException {
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
		System.out.println("Du valgte " + station.getName());
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

	private static void presentSearchResult(List<Station> result) {
		System.out.println("Fant " + result.size() + " stasjoner:");
		for (Station station : result) {
			System.out.println(station.getName());
		}
	}

	private static String enterSearchterm() throws IOException {
		System.out.println("Skriv in søketermen: ");
		String searchTerm = "";
		char c;
		System.in.read();
		while ((c = (char) System.in.read()) != '\n') {
			searchTerm += c;
		}
		searchTerm.trim();
		searchTerm.replaceAll("\\*", ".*");
		searchTerm = ".*" + searchTerm + ".*";
		return searchTerm;
	}

}
