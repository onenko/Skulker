package net.nenko.utils.skulker;

import net.nenko.libs.NanoLog;

/**
 * Starting application with slf4j/logback, and properties configuration
 */
public class App {
	private static final NanoLog log = new NanoLog(NanoLog.LogLevel.DEBUG, null);

	public static void main( String[] args) {
		log.info("App start.");
		Cfg cfg = new Cfg("/App.properties");
// TODO: add configuration logging, if nessesary, later, in the Cfg.java		cfg.logProperties();

		Cntx cntx = new Cntx(cfg);
		String error = processCommandLine(args, cntx);
		if(error != null) {
			log.error("Error while parsing command line: {}", error);
			logCommandLineSyntax();
			System.exit(1);
		}
		log.info("Running context: {}", cntx.toString());
		Strategy strategy = loadStrategy(cntx.strategy);

		switch(cntx.command) {
			case ENCRYPT:
				log.info("enc");
				strategy.doEncrypt(cntx);
				break;
			case DECRYPT:
				log.info("dec");
				break;
			case LIST:
				log.info("list");
				break;
			case FIND:
				log.info("find");
				break;
		}
		
		log.info("App finished successfully.");
    }

	
	private static String processCommandLine(String[] args, Cntx cntx) {
		if(args.length < 1) {
			return "No arguments provided.";
		}
		cntx.command = Command.valueOfLetter(args[0]);
		if(cntx.command == null) {
			return "Wrong command '" + args[0] + "'.";
		}
		int optionArgIndex = 1;
		while(optionArgIndex < args.length) {
			switch(args[optionArgIndex]) {
				case "-s":						// Strategy
					if(optionArgIndex >= args.length - 1) {
						return "No strategy for option -s was specified.";
					}
					cntx.strategy = args[optionArgIndex + 1];
					optionArgIndex += 2;
					break;
				default:
					if(cntx.carrierPath == null) {
						cntx.carrierPath = args[optionArgIndex ++];
					} else if(cntx.skulkedPath == null) {
						cntx.skulkedPath = args[optionArgIndex ++];
					} else {
						return "Unidentified argument '" + args[optionArgIndex] + "' was specified. Exiting with error.";
					}
			}
		}
		return null;
	}

	private static void logCommandLineSyntax() {
		log.info("\nSyntax" +
				"\nSkulker <command> [-s <strategy>] <inputPathOrFile> <outputFileOrPath>" +
				"\n\tcommand may be (e)ncrypt, (d)ecrypt, (l)ist or (f)ind;" +
				"\n\tstrategy can be only 'Default' in this version.");
	}

	private static Strategy loadStrategy(String strategyName) {
		return new StrategyDefault();
	}
}
