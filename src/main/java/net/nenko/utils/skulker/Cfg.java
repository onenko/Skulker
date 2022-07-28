package net.nenko.utils.skulker;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cfg - maps properties read from property file
 */
public class Cfg {
	private static final Logger log = LoggerFactory.getLogger(Cfg.class);
	private Properties properties = new Properties();

	public Cfg(String propName) {
		log.debug("Cfg: loading resources from " + propName);
		try {
			InputStream iStream = getClass().getResourceAsStream(propName);
			if(iStream == null) {
				log.error("Cfg: resource '{}' not found in classpath", propName);
			} else {
				properties.load(iStream);
			}
		} catch(IOException e) {
			log.error("Cfg: error on loading resources from '{}'", propName, e);
		}
	}

	public String getProp1() {
		return properties.getProperty("key1");
	}

}
