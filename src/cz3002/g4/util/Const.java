package cz3002.g4.util;

public class Const {

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
}
