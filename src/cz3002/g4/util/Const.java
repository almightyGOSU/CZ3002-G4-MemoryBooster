package cz3002.g4.util;

public class Const {
	
	public static final String BACK_TO_MAIN = "BACK TO MAIN MENU";

	public static final String USER_STATUS = "USER STATUS";
	public static enum UserStatus {
		GUEST,
		FACEBOOK,
		GOOGLEPLUS;
	}
    
	public static final String GAME_MODE = "GAME MODE";
	public static enum GameMode {
		TIMED_CHALLENGE,
		CAMPAIGN_MODE,
		FF;
	}
	
	/** Used for storing name to be used for highscores */
	public static final String USER_NAME = "USER NAME";
	
	/** Only used for Campaign Mode */
	public static final String GAME_LEVEL = "GAME LEVEL";
	
	/** Duration in seconds for the Timed Challenge */
	public static final int TIMED_CHALLENGE_DURATION = 15;
	
	/** Number of pre-generated questions for Timed Challenge */
	public static final int TIMED_CHALLENGE_QUESTIONS = 15;
	
	/** For calculating highscore for Timed Challenge */
	public static final int TC_CORRECT_MULTIPLIER = 150;
	public static final int TC_INCORRECT_MULTIPLIER = -25;
	public static final int TC_QNS_ANSWERED_BONUS = 10;
	
	/** Number of levels for Campaign Mode */
	public static final int CAMPAIGN_LEVELS = 12;
	
	/** For shared preferences */
	public static final String SHARED_PREF = "MemoryBoosterPref";
	
	/** For passing information to Post-Game Page */
	public static final String TIME_TAKEN = "TIME TAKEN";
	public static final String NUM_QUESTIONS = "NUMBER OF QUESTIONS";
	public static final String QUESTIONS_CORRECT = "QUESTIONS CORRECT";
	public static final String QUESTIONS_INCORRECT = "QUESTIONS INCORRECT";
	public static final String TC_SCORE = "TIMED CHALLENGE SCORE";
	public static final String CM_STARS = "CAMPAIGN MODE STARS";
	public static final String TC_NEW_HIGHSCORE = "TIMED CHALLENGE NEW HIGHSCORE";
}
