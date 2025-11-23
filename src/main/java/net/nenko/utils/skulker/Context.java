package net.nenko.utils.skulker;

/**
 * Run context of the application
 *
 * Contains all the information, gathered from configuration and from command line,
 * needed to perform the run of the application
 */
public class Context {
	public boolean oneFileMode;
	public String carrierPath;			// file path if oneFileMode = true
	public String skulkedPath;			// file path if oneFileMode = true
	public String strategy = "Default";	// defines the algorithms of encryption/decryption
	public Command command;

	public Context(Cfg cfg) {
		if(cfg.getStrategy() != null) {
			strategy = cfg.getStrategy();
		}
	}

	public String toString() {
		return "Cntx:{ command: " + command.letter() + ", oneFileMode:" + oneFileMode + ", " + 
	"carrierPath:'" + carrierPath + "', " +
	"skulkedPath:'" + skulkedPath + "', " +
	"strategy:'" + strategy + "'}";	
	}

}
