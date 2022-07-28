package net.nenko.utils.skulker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Starting application with slf4j/logback, and properties configuration
 */
public class App {
	private static final Logger log = LoggerFactory.getLogger(App.class);

	public static void main( String[] args) {
		log.info("App start.");
		Cfg cfg = new Cfg("/App.properties");
		log.info("Configuration: key1={}", cfg.getProp1());

		log.info("App finished successfully.");
    }

}
