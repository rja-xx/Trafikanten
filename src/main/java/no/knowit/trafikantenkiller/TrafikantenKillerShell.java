package no.knowit.trafikantenkiller;

import java.io.IOException;
import java.util.List;

import no.knowit.trafikantenkiller.model.nodes.Station;

public class TrafikantenKillerShell {


	
	private static TrafikantenKiller trafikantenKiller;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		printHelpmessage();

		trafikantenKiller = TrafikantenKiller.getInstance();

		byte read;
		programloop: while(true){
			read = (byte) System.in.read();
			switch (read) {
			case 'p':
				getHopOptimizedRoute();
				printHelpmessage();
				break;
			case 'a':
				break programloop;
			case 'h':
				printHelpmessage();
				break;
			case '\n':
				break;
			default:
				System.out.println("Ukjent oppgave!");
				printHelpmessage();
				break;
			}
		}
		System.out.println("Hejdå!");
		System.exit(0);
	}

	private static void getHopOptimizedRoute() throws IOException {
		System.out.println("Velg startstasjon: ");
		List<Station> stations = trafikantenKiller.getAvailableStations();
		int currentIndex = 0;
		for (Station station : stations) {
			System.out.println(++currentIndex + " " + station);
		}
		System.in.read();
		char read = (char)System.in.read();
		String valueOf = String.valueOf(read);
		int from = Integer.valueOf(valueOf);
		System.in.read();
		System.out.println("Velg endestasjon: ");
		currentIndex = 0;
		for (Station station : stations) {
			System.out.println(++currentIndex + " " + station);
		}
		read = (char)System.in.read();
		valueOf = String.valueOf(read);
		int to = Integer.valueOf(valueOf);
		Route route = trafikantenKiller.planHopOptimizedRoute(stations.get(from-1), stations.get(to-1));
		System.out.println(route);
	}

	private static void printHelpmessage() {
		System.out.println("Velkommen til ruteplanleggeren, velg en av følgende oppgaver: ");
		System.out.println("p : Planlegg rute med minimalt antall stopp");
		System.out.println("a : Avslutt");
		System.out.println("h : Hjelp");
	}

}
