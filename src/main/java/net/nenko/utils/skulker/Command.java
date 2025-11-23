package net.nenko.utils.skulker;

public enum Command {
	ENCRYPT("a"),
	DECRYPT("x"),
	LIST("l"),
	FIND("f");

	private String letter;
	private Command(String letter) {
		this.letter = letter;
	}

	public String letter() {
		return letter;
	}

	public static Command valueOfLetter(String letter) {
		for(Command c: Command.values()) {
			if(c.letter.equals(letter)) {
				return c;
			}
		}
		return null;
	}
}
