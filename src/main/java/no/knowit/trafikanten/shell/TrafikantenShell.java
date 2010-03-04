package no.knowit.trafikanten.shell;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import no.knowit.trafikanten.Trafikanten;

/**
 * Mainklassen for trafikanten 2.
 * Denne klassen lar brukeren velge mellom noen kommandoer via et textbasert UI.
 *
 */
public class TrafikantenShell {

	private static final Map<String, Command> COMMANDS = new HashMap<String, Command>();
	static Trafikanten trafikanten;

	static {
		COMMANDS.put("create database", Command.INIT_COMMAND);
		COMMANDS.put("find route", Command.MIN_HOP_COMMAND);
		COMMANDS.put("find station", Command.FIND_STATION_COMMAND);
		COMMANDS.put("connected", Command.ROUTE_EXISTS_COMMAND);
		COMMANDS.put("help", Command.HELP_COMMAND);
		COMMANDS.put("exit", Command.EXIT_COMMAND);
	}

	public static void main(String[] args) throws IOException {
		if(args.length != 1){
			System.out.println("Usage: Trafikanten <path to db>");
			System.exit(-1);
		}
		trafikanten = new Trafikanten(args[0]);
		printHelpmessage();
		readExecuteLoop();
	}

	private static void readExecuteLoop() {
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

	public static void printHelpmessage() {
		System.out.println();
		System.out.println("Velkommen til ruteplanleggeren, velg en av følgende oppgaver: ");
		for (Entry<String, Command> command : COMMANDS.entrySet()) {
			String c = command.getKey();
			String padding = "                  ";
			int paddingLength = 16-c.length();
			System.out.println(c + padding.substring(0, paddingLength)+" : " + command.getValue());
		}

		System.out.println();
	}
	
	public static String readCommand() throws IOException {
		String searchTerm = "";
		char c;
		while ((c = (char) System.in.read()) != '\n') {
			searchTerm += c;
		}
		return searchTerm;
	}

}
