package cz3002.g4.util;

public class TimeUtil {

	/** Converts milliseconds to seconds */
	public static long millisecondsToSeconds(long milliseconds) {
		
		return (milliseconds / 1000);
	}
	
	/** Converts seconds to milliseconds */
	public static long secondsToMilliseconds(long seconds) {
		
		return (seconds * 1000);
	}
	
	/**
	 * Returns the string equivalent of a given time
	 * 
	 * @param timeInSeconds Time in seconds
	 * @return String in MM:SS format
	 */
	public static String timeToString(long timeInSeconds) {
		
		int minutes = (int) (timeInSeconds / 60);
		int seconds = (int) (timeInSeconds % 60);
		
		return minutes + ((seconds < 10) ? ":0" : ":") + seconds;
	}
}
