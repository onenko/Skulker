package net.nenko.utils.skulker;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import net.nenko.libs.NanoLog;

/**
 * Cfg - maps properties read from property file
 */
public class Cfg {
	private static final NanoLog log = new NanoLog(NanoLog.LogLevel.DEBUG, null);
	private Properties properties = new Properties();

	public Cfg(String propertiesResource) {
		log.debug("Cfg: loading resources from " + propertiesResource);
		try {
			InputStream iStream = getClass().getResourceAsStream(propertiesResource);
			if(iStream == null) {
				log.error("Cfg: resource '{}' not found in classpath", propertiesResource);
			} else {
				properties.load(iStream);
			}
		} catch(IOException e) {
			log.error("Cfg: error on loading resources from '{}'", propertiesResource, e);
		}
	}

	public String getStrategy() {
		return properties.getProperty("Strategy");
	}

}
