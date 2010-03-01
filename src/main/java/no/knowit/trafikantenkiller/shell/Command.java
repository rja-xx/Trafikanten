package no.knowit.trafikantenkiller.shell;

public abstract class Command {

		private String name;

		public Command(String name) {
			this.name = name;
		}

		public abstract void run();

		@Override
		public String toString() {
			return name;
		}


		static final Command EXIT_COMMAND = new Command("Avslutt") {
			@Override
			public void run() {
				System.out.println("Heidå!");
				System.exit(0);
			}
		};
		static final Command MIN_TIME_COMMAND = new Command("Planlegg rute som tar kortest tid") {
			@Override
			public void run() {
				TrafikantenKillerShell.getTimeOptimizedRoute();
			}
		};
		static final Command MIN_HOP_COMMAND = new Command("Planlegg rute med minimalt antall stopp") {
			@Override
			public void run() {
				TrafikantenKillerShell.getHopOptimizedRoute();
			}
		};

		static final Command INIT_COMMAND = new Command("Initiering av grafdatabase") {
			@Override
			public void run() {
				TrafikantenKillerShell.initDatabase();
			}
		};

		static final Command HELP_COMMAND = new Command("Hjelp") {
			@Override
			public void run() {
				
			}
		};
		
		static final Command FIND_STATION_COMMAND = new Command("Finn stasjon") {
			@Override
			public void run() {
				TrafikantenKillerShell.findStation();
			}
		};

		
		
		
}


