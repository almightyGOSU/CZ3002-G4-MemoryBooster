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
	
	/** Duration in seconds for the Timed Challenge */
	public static final int TIMED_CHALLENGE_DURATION = 15;
	
	/** Number of pre-generated questions for Timed Challenge */
	public static final int TIMED_CHALLENGE_QUESTIONS = 15;
	
	/** For calculating highscore for Timed Challenge */
	public static final int TC_CORRECT_MULTIPLIER = 150;
	public static final int TC_INCORRECT_MULTIPLIER = -25;
	
	/** Number of levels for Campaign Mode */
	public static final int CAMPAIGN_LEVELS = 12;
}
