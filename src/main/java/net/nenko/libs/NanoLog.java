package net.nenko.libs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class NanoLog {
	private static final DateTimeFormatter FORMATTER1 = DateTimeFormatter.ofPattern("yyMMdd HHmmss");
	private static final Object[] DUMMY_ARG_ARRAY = new Object[0];
	private PrintStream printStream;
	private LogLevel level;

	public NanoLog(LogLevel level, String fileTemplate) {
		this.level = level;
		if(fileTemplate == null) {
			printStream = System.out;
		} else {
			File file = new File(fileTemplate);
			try {
				printStream = new PrintStream(file);
			} catch(FileNotFoundException e) {
				printStream = System.out;
				printStream.println("NanoLog error - failed to open/create file " + file);
			}
		}
	}

	public void error(String format) {
		if(LogLevel.ERROR.isEnabled(level)) {
			String record = buildRecord(format, LogLevel.ERROR, DUMMY_ARG_ARRAY);
			writeRecord(record);
		}
	}

	//TODO: add method with Throwable
	public void error(String format, Object... arguments) {
		String record = buildRecord(format, LogLevel.ERROR, arguments);
		writeRecord(record);
	}

	public void warn(String format) {
		if(LogLevel.WARN.isEnabled(level)) {
			String record = buildRecord(format, LogLevel.WARN, DUMMY_ARG_ARRAY);
			writeRecord(record);
		}
	}

	public void warn(String format, Object... arguments) {
		String record = buildRecord(format, LogLevel.WARN, arguments);
		writeRecord(record);
	}

	public void info(String format) {
		if(LogLevel.INFO.isEnabled(level)) {
			String record = buildRecord(format, LogLevel.INFO, DUMMY_ARG_ARRAY);
			writeRecord(record);
		}
	}

	public void info(String format, Object... arguments) {
		if(LogLevel.INFO.isEnabled(level)) {
			String record = buildRecord(format, LogLevel.INFO, arguments);
			writeRecord(record);
		}
	}

	public void debug(String format) {
		if(LogLevel.DEBUG.isEnabled(level)) {
			String record = buildRecord(format, LogLevel.DEBUG, DUMMY_ARG_ARRAY);
			writeRecord(record);
		}
	}

	public void debug(String format, Object... arguments) {
		if(LogLevel.DEBUG.isEnabled(level)) {
			String record = buildRecord(format, LogLevel.DEBUG, arguments);
			writeRecord(record);
		}
	}

	//TODO: create pluggable buildRecord with different template
	private String buildRecord(String template, LogLevel severity, Object[] args) {
		StringBuilder sb = new StringBuilder(template.length() + args.length * 10);
		LocalDateTime now = LocalDateTime.now();
		String formattedDateTime;
		if(printStream == System.out) {
			// output to the console - use local time in logged record
			formattedDateTime = now.format(FORMATTER1);
		} else {
			// output to a file - use UTC time in logged record
			formattedDateTime = now.atZone(ZoneId.of("UTC")).format(FORMATTER1);
		}
		sb.append(formattedDateTime);			
		sb.append(' ').append(severity.name()).append(" - ");
		if(args.length == 0) {
			sb.append(template);
		} else {
			appendMsg(sb, template, args);
		}
		sb.append('\n');
		return sb.toString();
	}


//	private String buildMsg(String template, Object[] arguments) {
//		if(arguments.length == 0) {
//			return template;
//		}
//		String[] parts = template.split("\\{\\}");
//		StringBuilder sb = new StringBuilder(template.length() + parts.length * 10);
//		for(int i = 0; i < parts.length; i++) {
//			sb.append(parts[i]);
//			if(i < parts.length - 1) {
//				sb.append( i < arguments.length ? arguments[i].toString() : "{}");
//			}
//		}
//		return sb.toString();
//	}

	private void appendMsg(StringBuilder sb, String template, Object[] arguments) {
		String[] parts = template.split("\\{\\}", 9999);
		for(int i = 0; i < parts.length; i++) {
			sb.append(parts[i]);
			if(i < parts.length - 1) {
				sb.append( i < arguments.length ? arguments[i].toString() : "{}");
			}
		}
	}

	private void writeRecord(String record) {
		printStream.print(record);
	}

	// TODO: design something like style of log record formatting, to avoid slow templating
	private static enum LogStyle {
		SIMPLE,
		COMPLEX;
//		LogStyle()
	};

	public static enum LogLevel {
		FATAL(0),
		ERROR(1),
		WARN(2),
		INFO(3),
		DEBUG(4),
		TRACE(5);
		private int lvl;
	
		private LogLevel(int lvl) {
			this.lvl = lvl;
		}
		
		public boolean isEnabled(LogLevel thresholdLevel) {
			return this.lvl <= thresholdLevel.lvl;
		}
	};


	private static File fileFromTemplate(String fileTemplate) {
		// TODO: elaborate nice date/time substitution
		return new File(fileTemplate);
	}
}
