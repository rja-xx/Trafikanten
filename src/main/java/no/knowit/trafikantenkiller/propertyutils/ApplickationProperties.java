package no.knowit.trafikantenkiller.propertyutils;

import java.util.Properties;

public class ApplickationProperties {

	
	
	private static final ApplickationProperties instance = new ApplickationProperties();
	private final Properties applicationProperties;

	public static ApplickationProperties getInstance() {
		return instance;
	}

	private ApplickationProperties(){
		super();
		applicationProperties = PropertyLoader.loadProperties("app_settings");
	}

	public String getDatabaseLocation() {
		return applicationProperties.getProperty("db.path");
	}
}
