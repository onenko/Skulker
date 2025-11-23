package net.nenko.utils.skulker;

import net.nenko.lib.NanoArgsParser;
import net.nenko.lib.NanoArgsParser.Option;
import net.nenko.lib.NanoLog;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

public class App {
	public static final NanoLog log = new NanoLog(NanoLog.LogLevel.DEBUG, NanoLog.LogStyle.CMD_LINE, null);
	public static final String TEST_PWD = "12";
	public static final String OPT_OUTPUT_DEFAULT = "by-default-the-output-file-overwrites-carrier-file";
	public static final String OPT_SOURCE_DEFAULT = "by-default-the-restored-file-is-written-under-its-original-file-name";
	public static final String TEMPORARY_SKULKED_FILE_SUFFIX = ".temporary-skulked-file";

	private static Option optPassword = new Option("p", null, "<password>", "sets the password for encryption");
	private static Option optOutput = new Option("o", OPT_OUTPUT_DEFAULT, "<output-skulked-file>", "defines the skulked file name (by default carrier is overwritten)");
	private static Option optSource = new Option("s", OPT_SOURCE_DEFAULT, "<output-skulked-file>", "defines the skulked file name (by default carrier is overwritten)");
	private static Option optCarrierReconstruct = new Option("c", NanoArgsParser.FLAG, null, "if set for the command 'd', activates reconstruction of carrier file from skulked file");
	private static Option optVerbose = new Option("v", NanoArgsParser.FLAG, null, "if set, activates verbose logging of the operations");
//	private static Option optHelp = new Option("h", NanoArgsParser.FLAG, null, "displays command line format and list of options");
	private static Option[] options = new Option[] {optPassword, optOutput, optSource, optVerbose, optCarrierReconstruct /*, optHelp*/ };
	private static NanoArgsParser parser = new NanoArgsParser(options);
	private static boolean toPrintHelp = false;
	private static String version = "0.0.1";

	public static void main( String[] args) {
		log.info("Skulker v" + version + " started.");
		Cfg cfg = new Cfg("/App.properties");
// TODO: add configuration logging, if nessesary, later, in the Cfg.java		cfg.logProperties();

		parseArgumentsAndExecuteCommand(args);

		if(toPrintHelp) {
			log.info("");
			log.info("Command line format:");
			log.info("Skulker <command> <options> <other-command-specific-nonoptional-arguments>");
			log.info("\tOptional and positional arguments can be intermixed");
			log.info("Commands:");
            log.info("\ti <skulked-file> - retrieve all the information about skulked file content");
            log.info("\ts <source-file> <carrier-file> - hides the 'source-file' into 'carrier-file'");
            log.info("\th - prints this command line format and options.");
			log.info("Options:");
			log.info(parser.argsSynopsis());
		}

//		doTestCall(args);
		System.exit(0);






		Context context = new Context(cfg);
		String error = processCommandLine(args, context);
		if(error != null) {
			log.error("Error while parsing command line: {}", error);
			logCommandLineSyntax();
			System.exit(1);
		}
		log.info("Running context: {}", context.toString());
		Executor executor = new Executor();

		switch(context.command) {
			case ENCRYPT:
				log.info("enc");
////				executor.doEncrypt(context);
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

	private static void parseArgumentsAndExecuteCommand(String [] args){
		try {
			String[] nonOptArgs = parser.parse(args);
			if (nonOptArgs.length == 0) {
				toPrintHelp = true;
				return;
			}
			String command = nonOptArgs[0];
			if(command.length() != 1) {
				log.error("ERROR: first argument {} should be one-letter command.", command);
				toPrintHelp = true;
				return;
			}
			switch(command) {
				case "i":
					if( nonOptArgs.length != 2) {
                        log.error("ERROR: command i should have one additional argument - skulked file name.");
                        toPrintHelp = true;
                        break;
                    }
                    if( optPassword.value == null) {
                        log.error("ERROR: required by command 'i' option -p is missing.");
                        toPrintHelp = true;
                        break;
					}
					command_i(nonOptArgs[1]);
					break;
				case "s":
					if( nonOptArgs.length != 3) {
						log.error("ERROR: command s should have two additional arguments - source and carrier files names.");
						toPrintHelp = true;
                        break;
					}
                    if( optPassword.value == null) {
                        log.error("ERROR: required by command 's' option -p is missing.");
                        toPrintHelp = true;
                        break;
                    }
        			command_s(nonOptArgs[1], nonOptArgs[2]);
					break;
				case "d":
					if( nonOptArgs.length != 2) {
						log.error("ERROR: command d should have one additional argument - skulked file name.");
						toPrintHelp = true;
						break;
					}
					if( optPassword.value == null) {
						log.error("ERROR: required by command 's' option -p is missing.");
						toPrintHelp = true;
						break;
					}
					command_d(nonOptArgs[1]);
					break;
                case "h":
                    if( nonOptArgs.length != 1) {
                        log.error("ERROR: command h shouldn't have any additional arguments.");
                    }
                    toPrintHelp = true;
                    break;
				default:
					log.error("ERROR: unknown command '{}'", command);
					toPrintHelp = true;
			}
		} catch (NanoArgsParser.NanoArgsParserException napx) {
			log.error(napx.toString());
			toPrintHelp = true;
		}
	}

	/**
	 * command_i() executes i command
	 *
	 * @param skulkedFilePath input file to retrieve the information about it's content
    */
	private static void command_i(String skulkedFilePath) {
		SkulkerEncryptor encr = new SkulkerEncryptorPwdBitwise(optPassword.value);
		log.info("Command i: get info of file " + skulkedFilePath);
		try {
			SkulkedFile skulkedFile = SkulkedFile.deskulk(skulkedFilePath, encr);
			if(null == skulkedFile) {
				log.warn("WARN: structure error or the file is not skulked.");
			} else {
				log.info(skulkedFile.infoToString());
			}
		} catch (Exception e) {
			log.error("Command i: unexpected ex:", e);
		}
	}

	/**
	 * command_s() executes s command
	 *
	 * @param sourceFilePath input file to retrieve the information about it's content
	 * @param carrierFilePath input file to retrieve the information about it's content
	 */
	private static void command_s(String sourceFilePath, String carrierFilePath) {
		SkulkerEncryptor encr = new SkulkerEncryptorPwdBitwise(optPassword.value);
		log.info("Command s: skulk '{}' into carrier '{}'", sourceFilePath, carrierFilePath);
		String output = optOutput.value;
		if(OPT_OUTPUT_DEFAULT.equals(optOutput.value)) {
			log.info("Command s: in-place operation - skulked file will be placed into carrier '{}'", carrierFilePath);
			output = carrierFilePath + TEMPORARY_SKULKED_FILE_SUFFIX;
		} else {
			log.info("Command s: the output will be written into separate skulked file '{}'", output);
		}
		try {
			SkulkedFile skulkedFile = new SkulkedFile(carrierFilePath, sourceFilePath, output, encr);
			skulkedFile.skulk();
		} catch (Exception e) {
			log.error("Command s: unexpected ex:", e);
			return;
		}
		if(OPT_OUTPUT_DEFAULT.equals(optOutput.value)) {
			// without option -o we should overwrite carrier file with skulked file
			// Rename temporary output file with carrier file, carrier file is overwritten
			try {
				Path source = Paths.get(output);
				Path target = Paths.get(carrierFilePath);
				Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
			} catch(Exception e) {
				log.error("Command s without option -o: skulked file was generated into temp {}, and during the rename to {} unexpected exeption happened:",
						output, carrierFilePath, e);
				return;
			}
		}
		log.info("Command s completed successfully.");
	}

	/**
	 * command_d() executes d command
	 *
	 * @param skulkedFilePath input file to restore hidden source file
	 */
	private static void command_d(String skulkedFilePath) {
		SkulkerEncryptor encr = new SkulkerEncryptorPwdBitwise(optPassword.value);
		log.info("Command d: deskulk from '{}'", skulkedFilePath);

		try {
			SkulkedFile skulkedFile = SkulkedFile.deskulk(skulkedFilePath, encr);
			if(null == skulkedFile) {
				log.warn("WARN: structure error or the file '{}' is not skulked.", skulkedFilePath);
				return;
			}
			String outputSourceFileName = null;
			if( ! OPT_SOURCE_DEFAULT.equals(optSource.value)) {
				outputSourceFileName = optSource.value;
			}
			log.debug("Command d: start restoring the file '{}'", outputSourceFileName);
			skulkedFile.extract(outputSourceFileName);
			if(optCarrierReconstruct.isOn()) {
				log.debug("Command d: reconstructing original carrier file from the skulked file " + skulkedFilePath);
				skulkedFile.truncate();
			}
		} catch (Exception e) {
			log.error("Command d: unexpected ex:", e);
		}
		log.info("Command d completed successfully.");
	}



















	private static String processCommandLine(String[] args, Context context) {
		if(args.length < 1) {
			return "No arguments provided.";
		}
		context.command = Command.valueOfLetter(args[0]);
		if(context.command == null) {
			return "Wrong command '" + args[0] + "'.";
		}
		int optionArgIndex = 1;
		while(optionArgIndex < args.length) {
			switch(args[optionArgIndex]) {
				case "-s":						// Strategy
					if(optionArgIndex >= args.length - 1) {
						return "No strategy for option -s was specified.";
					}
					context.strategy = args[optionArgIndex + 1];
					optionArgIndex += 2;
					break;
				default:
					if(context.carrierPath == null) {
						context.carrierPath = args[optionArgIndex ++];
					} else if(context.skulkedPath == null) {
						context.skulkedPath = args[optionArgIndex ++];
					} else {
						return "Unidentified argument '" + args[optionArgIndex] + "' was specified. Exiting with error.";
					}
			}
		}
		return null;
	}

	private static void	doTestCall(String[] args) {
		log.info("doTestCall(args:" + Arrays.toString(args) + ")");
		if (args.length == 0) {
			log.error("doTestCall(args.length == 0");
			return;
		}
		if (args[0].length() != 1) {
			log.error("doTestCall(args[0].length must be 1 letter");
			return;
		}

		if ("i".equals(args[0]) && args.length == 2) {
			/*
			command: skulker i skulked-file
			retrieve all the information about skulked file content
			*/
			SkulkerEncryptor encr = new SkulkerEncryptorPwdBitwise(TEST_PWD);
			log.info("doTestCall: get info of file " + args[1]);
			try {
				SkulkedFile skulkedFile = SkulkedFile.deskulk(args[1], encr);
				if(null == skulkedFile) {
					log.warn("doTestCall: structure error or the file is not skulked: " + args[1]);
				} else {
					log.info("doTestCall(" + Arrays.toString(args) + ") result:\n" + skulkedFile.infoToString());
				}
			} catch (Exception e) {
				log.error("doTestCall(" + Arrays.toString(args) + ") ex:", e);
			}
			return;
		}

		if ("x".equals(args[0]) && args.length == 2) {
			/*
			command: skulker x skulked-file
			extract skulked content from skulked file, the restored file gets its original name and path
			*/
			SkulkerEncryptor encr = new SkulkerEncryptorPwdBitwise(TEST_PWD);
			log.info("doTestCall: extract skulked content from the file " + args[1]);
			try {
				SkulkedFile skulkedFile = SkulkedFile.deskulk(args[1], encr);
				if(null == skulkedFile) {
					log.warn("doTestCall: structure error or the file is not skulked: " + args[1]);
					return;
				} else {
					log.info("doTestCall(" + Arrays.toString(args) + ") info:\n" + skulkedFile.infoToString());
				}
				// We can extract the content of the file, provided:
				//  - skulkedFile contains all the nessessary information;
				//	- decryptor is in proper state to continue to decrypt further data
				//	- file stream on input file is in proper position
				log.info("doTestCall() - Xtracting " + skulkedFile.getSourceFullName());
				String err = skulkedFile.extract(null);
			} catch (Exception e) {
				log.error("doTestCall(" + Arrays.toString(args) + ") ex:", e);
			}
			return;
		}

		if ("s".equals(args[0]) && args.length == 4) {
			/*
		    command: skulker s input-carrier-file input-source-file output-skulked-file
		    skulk (encrypt) source file into carrier file, new skulked file is generated
			*/
			SkulkerEncryptor encr = new SkulkerEncryptorPwdBitwise(TEST_PWD);
			SkulkedFile skulkedFile = new SkulkedFile(args[1], args[2], args[3], encr);
			try {
				skulkedFile.skulk();
				log.info("doTestCall(" + Arrays.toString(args) + ") OK");
			} catch (Exception e) {
				log.error("doTestCall(" + Arrays.toString(args) + ") ex:", e);
			}
		}
		log.error("doTestCall(" + Arrays.toString(args) + ") - unrecognized or unsupported command");
	}

	private static void logCommandLineSyntax() {
		log.info("\nSyntax" +
				"\nSkulker <command> [-s <strategy>] <inputPathOrFile> <outputFileOrPath>" +
				"\n\tcommand may be (e)ncrypt, (d)ecrypt, (l)ist or (f)ind;" +
				"\n\tstrategy can be only 'Default' in this version.");
	}

}
