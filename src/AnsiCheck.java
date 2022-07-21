/**
 * Abstract class that describes a method responsible of checking
 * wheter the terminal in which the app is run is capable of 
 * displaying ANSI color reading ANSI escape codes
 * 
 */
public interface AnsiCheck {
	
	/**
	* the following method is used to determine
	 * if the terminal can display, or not, colored text
	 * i was serching for a System method that can check this
	 * and i run into a comment on stack overflow:
	 * precisely this topic: https://stackoverflow.com/questions/41057014/check-if-console-supports-ansi-escape-codes-in-java
	 * 
	 * so i must credit the user @Trozen (https://stackoverflow.com/users/8622794/trozen)
	 * for this little, undirect, contribution
	 */

	/**
	 * @return true if the TERM environment variable exist else false;
	 * when that environment variable exists the terminal is ANSI CAPABLE 
	 * that is true almost on every unix-like term-emulators and on Alacritty term 
	 * on windows
	 * 
	 * the second check is useful in IDEs with integrated console:
	 * from Javadoc of Console class
	 * If this virtual machine has a console then it is represented by a unique instance of this class which
	 * can be obtained by invoking the System.console() method. 
	 * If no console device is available then an invocation of that method will return null.
	 */
	
	public static boolean termIsAnsiCapable(){
		if ((Game.TERM_VARIABLE != null) && (System.console()!=null)) {
		    return true;
		} 
		return false;
	}

	
}
