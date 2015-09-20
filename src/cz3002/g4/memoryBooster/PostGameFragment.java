package cz3002.g4.memoryBooster;

import cz3002.g4.util.Const;
import cz3002.g4.util.Const.GameMode;
import cz3002.g4.util.TimeUtil;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PostGameFragment extends FragmentActivity {
	
	// Game mode
	private GameMode _gameMode = null;
	
	// UI Elements
	private TextView _tv_gameMode = null;
	
	// UI Elements - Level Text for Campaign Mode
	private TextView _tv_postGame_levelText = null;
	
	// UI Elements - Time taken for Campaign Mode
	private TextView _tv_postGame_timeTakenText = null;
	private TextView _tv_postGame_timeTaken = null;
	
	// UI Elements - Correctly answered questions
	private TextView _tv_postGame_correctText = null;
	private TextView _tv_postGame_correctNum = null;
	
	// UI Elements - Incorrectly answered questions
	private TextView _tv_postGame_incorrectText = null;
	private TextView _tv_postGame_incorrectNum = null;
	
	// UI Elements - Score for Timed Challenge
	private TextView _tv_postGame_scoreText = null;
	private TextView _tv_postGame_score = null;
	
	// UI Elements - Stars gained for Campaign Mode
	private ImageView _iv_starsImage = null;
	
	// UI Elements - Buttons for sharing to social media and returning to main menu
	private Button _btn_socialShare = null;
	private Button _btn_backToMainMenu = null;
	
	// Statistics
    private int _numQnsAnswered = 0;
    private int _numCorrect = 0;
    private int _numWrong = 0;
    
    // Score for Timed Challenge
    private int _score = 0;
    private boolean _bNewHighscore = false;
    
    // Level, Stars and Time taken for Campaign Mode
    private int _gameLevel = 0;
    private int _levelStars = 0;
    private String _elapsedTime = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postgame_frag);
        
        Intent intent = getIntent();
        _gameMode = (GameMode) intent.getSerializableExtra(Const.GAME_MODE);
        _gameLevel = intent.getIntExtra(Const.GAME_LEVEL, 0);
        _elapsedTime = intent.getStringExtra(Const.TIME_TAKEN);
        
        _numCorrect = intent.getIntExtra(Const.QUESTIONS_CORRECT, 0);
        _numWrong = intent.getIntExtra(Const.QUESTIONS_INCORRECT, 0);
        _numQnsAnswered = _numCorrect + _numWrong;
        
        _score = intent.getIntExtra(Const.TC_SCORE, 0);
        _levelStars = intent.getIntExtra(Const.CM_STARS, 0);
        
        _bNewHighscore = intent.getBooleanExtra(Const.TC_NEW_HIGHSCORE, false);
        
		/*Log.d("PostGameFragment", _gameMode.toString());
		Log.d("PostGameFragment", _gameLevel + ", " + _elapsedTime);
		Log.d("PostGameFragment", "Correct: " + _numCorrect + "/" +
				_numQnsAnswered + ", Wrong: " + _numWrong + "/" +
				_numQnsAnswered);
		Log.d("PostGameFragment", _score + ", " + _levelStars + ", New HS: "
				+ (_bNewHighscore ? "Y" : "N"));*/
		
		getUIElements();
		setUpInteractiveElements();
		
		configurePostGameScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
    
    /** Getting UI Elements from layout */
	private void getUIElements() {
		
		_tv_gameMode = (TextView) findViewById(R.id.tv_gameMode);
		
		_tv_postGame_levelText = (TextView) findViewById(R.id.tv_postGame_levelText);
		
		_tv_postGame_timeTakenText = (TextView) findViewById(R.id.tv_postGame_timeTakenText);
		_tv_postGame_timeTaken = (TextView) findViewById(R.id.tv_postGame_timeTaken);
		
		_tv_postGame_correctText = (TextView) findViewById(R.id.tv_postGame_correctText);
		_tv_postGame_correctNum = (TextView) findViewById(R.id.tv_postGame_correctNum);
		
		_tv_postGame_incorrectText = (TextView) findViewById(R.id.tv_postGame_incorrectText);
		_tv_postGame_incorrectNum = (TextView) findViewById(R.id.tv_postGame_incorrectNum);
		
		_tv_postGame_scoreText = (TextView) findViewById(R.id.tv_postGame_scoreText);
		_tv_postGame_score = (TextView) findViewById(R.id.tv_postGame_score);
		
		_iv_starsImage = (ImageView) findViewById(R.id.iv_starsImage);
		
		_btn_socialShare = (Button) findViewById(R.id.btn_socialShare);
		_btn_backToMainMenu = (Button) findViewById(R.id.btn_backToMainMenu);
	}
	
	/** Set up interactive UI Elements */
	private void setUpInteractiveElements() {
		
		// For sharing to Social Media
		_btn_socialShare.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				//TODO: Add sharing to social media
				Toast.makeText(getApplicationContext(), "Share to Social Media",
						Toast.LENGTH_SHORT).show();
			}
		});
		
		// For returning to main menu
		_btn_backToMainMenu.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				// Go back main menu
				Intent intent = new Intent(
						getApplicationContext(), MainFragment.class);
				intent.putExtra(Const.BACK_TO_MAIN, true);
				startActivity(intent);
			}
		});
	}
	
	/** Configure post-game screen based on game mode */
	private void configurePostGameScreen() {
		
		if(_gameMode == Const.GameMode.TIMED_CHALLENGE) {
			
			_tv_gameMode.setText(R.string.timed_challenge);
			_tv_postGame_score.setText(String.valueOf(_score));
			_tv_postGame_levelText.setVisibility(View.GONE);
			
			_elapsedTime = TimeUtil.timeToString(Const.TIMED_CHALLENGE_DURATION);
			
			_iv_starsImage.setBackgroundResource(
					(_bNewHighscore ? R.drawable.tc_new_highscore:
						R.drawable.tc_try_harder));
		}
		else if(_gameMode == Const.GameMode.CAMPAIGN_MODE) {
			
			_tv_gameMode.setText(R.string.campaign_mode);
			_tv_postGame_levelText.setText("Level " + _gameLevel);
			_tv_postGame_levelText.setAlpha(0f);
			
			switch (_levelStars) {

			case 1:
				_iv_starsImage.setBackgroundResource(R.drawable.stars1large);
				break;
				
			case 2:
				_iv_starsImage.setBackgroundResource(R.drawable.stars2large);
				break;
				
			case 3:
				_iv_starsImage.setBackgroundResource(R.drawable.stars3large);
				break;

			default:
				_iv_starsImage.setBackgroundResource(R.drawable.stars0large);
				break;
			}
		}
		
		_tv_postGame_timeTaken.setText(_elapsedTime);
		_tv_postGame_correctNum.setText(_numCorrect + "/" + _numQnsAnswered);
		_tv_postGame_incorrectNum.setText(_numWrong + "/" + _numQnsAnswered);
		
		// Set everything to invisible initially
		_tv_gameMode.setAlpha(0f);
		_tv_postGame_timeTakenText.setAlpha(0f);
		_tv_postGame_timeTaken.setAlpha(0f);
		_tv_postGame_correctText.setAlpha(0f);
		_tv_postGame_correctNum.setAlpha(0f);
		_tv_postGame_incorrectText.setAlpha(0f);
		_tv_postGame_incorrectNum.setAlpha(0f);
		_tv_postGame_scoreText.setAlpha(0f);
		_tv_postGame_score.setAlpha(0f);
		_iv_starsImage.setAlpha(0f);
		_btn_socialShare.setAlpha(0f);
		_btn_backToMainMenu.setAlpha(0f);
		
		// Disable buttons
		_btn_socialShare.setEnabled(false);
		_btn_backToMainMenu.setEnabled(false);
		
		if(_gameMode == Const.GameMode.TIMED_CHALLENGE) {
			
			// Start animation for Timed Challenge details
			startTimedChallengeAnim();
		}
		else if(_gameMode == Const.GameMode.CAMPAIGN_MODE) {
			
			// Start animation for Campaign Mode details
			startCampaignModeAnim();
		}
	}
	
	/** Animation for Timed Challenge */
	private void startTimedChallengeAnim() {
		
		ValueAnimator gameModeFadeAnim = ObjectAnimator.ofFloat(
				_tv_gameMode, "alpha", 0f, 1f);
		gameModeFadeAnim.setStartDelay(200);
		gameModeFadeAnim.setDuration(500);
		gameModeFadeAnim.setInterpolator(new DecelerateInterpolator());
		
		ValueAnimator timeTakenTextFadeAnim = ObjectAnimator.ofFloat(
				_tv_postGame_timeTakenText, "alpha", 0f, 1f);
		timeTakenTextFadeAnim.setStartDelay(300);
		timeTakenTextFadeAnim.setDuration(250);
		timeTakenTextFadeAnim.setInterpolator(new DecelerateInterpolator());
		ValueAnimator timeTakenFadeAnim = ObjectAnimator.ofFloat(
				_tv_postGame_timeTaken, "alpha", 0f, 1f);
		timeTakenFadeAnim.setDuration(250);
		timeTakenFadeAnim.setInterpolator(new DecelerateInterpolator());
		
		ValueAnimator correctTextFadeAnim = ObjectAnimator.ofFloat(
				_tv_postGame_correctText, "alpha", 0f, 1f);
		correctTextFadeAnim.setDuration(250);
		correctTextFadeAnim.setInterpolator(new DecelerateInterpolator());
		ValueAnimator correctFadeAnim = ObjectAnimator.ofFloat(
				_tv_postGame_correctNum, "alpha", 0f, 1f);
		correctFadeAnim.setDuration(250);
		correctFadeAnim.setInterpolator(new DecelerateInterpolator());
		
		ValueAnimator incorrectTextFadeAnim = ObjectAnimator.ofFloat(
				_tv_postGame_incorrectText, "alpha", 0f, 1f);
		incorrectTextFadeAnim.setDuration(250);
		incorrectTextFadeAnim.setInterpolator(new DecelerateInterpolator());
		ValueAnimator incorrectFadeAnim = ObjectAnimator.ofFloat(
				_tv_postGame_incorrectNum, "alpha", 0f, 1f);
		incorrectFadeAnim.setDuration(250);
		incorrectFadeAnim.setInterpolator(new DecelerateInterpolator());
		
		ValueAnimator scoreTextFadeAnim = ObjectAnimator.ofFloat(
				_tv_postGame_scoreText, "alpha", 0f, 0.6f, 0.8f, 1f);
		scoreTextFadeAnim.setStartDelay(200);
		scoreTextFadeAnim.setDuration(400);
		scoreTextFadeAnim.setInterpolator(new DecelerateInterpolator());
		ValueAnimator scoreFadeAnim = ObjectAnimator.ofFloat(
				_tv_postGame_score, "alpha", 0f, 0.6f, 0.8f, 1f);
		scoreFadeAnim.setDuration(400);
		scoreFadeAnim.setInterpolator(new DecelerateInterpolator());
		
		ValueAnimator imageFadeAnim = ObjectAnimator.ofFloat(
				_iv_starsImage, "alpha", 0f, 0.7f, 1f);
		imageFadeAnim.setStartDelay(300);
		imageFadeAnim.setDuration(650);
		imageFadeAnim.setInterpolator(new DecelerateInterpolator());
		
		ValueAnimator btnSocialFadeAnim = ObjectAnimator.ofFloat(
				_btn_socialShare, "alpha", 0f, 0.6f, 1f);
		btnSocialFadeAnim.setStartDelay(300);
		btnSocialFadeAnim.setDuration(600);
		btnSocialFadeAnim.setInterpolator(new DecelerateInterpolator());
		
		ValueAnimator btnMainFadeAnim = ObjectAnimator.ofFloat(
				_btn_backToMainMenu, "alpha", 0f, 0.6f, 1f);
		btnMainFadeAnim.setDuration(600);
		btnMainFadeAnim.setInterpolator(new DecelerateInterpolator());
		btnMainFadeAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                
            	// Enable buttons
        		_btn_socialShare.setEnabled(true);
        		_btn_backToMainMenu.setEnabled(true);
            }
        });
		
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.play(gameModeFadeAnim).before(timeTakenTextFadeAnim);
		
		animatorSet.play(timeTakenTextFadeAnim).with(timeTakenFadeAnim);
		
		animatorSet.play(correctTextFadeAnim).after(timeTakenTextFadeAnim);
		animatorSet.play(correctTextFadeAnim).with(correctFadeAnim);
		
		animatorSet.play(incorrectTextFadeAnim).after(correctTextFadeAnim);
		animatorSet.play(incorrectTextFadeAnim).with(incorrectFadeAnim);
		
		animatorSet.play(scoreTextFadeAnim).after(incorrectTextFadeAnim);
		animatorSet.play(scoreTextFadeAnim).with(scoreFadeAnim);
		
		animatorSet.play(imageFadeAnim).after(scoreTextFadeAnim);
		
		animatorSet.play(btnSocialFadeAnim).after(imageFadeAnim);
		animatorSet.play(btnSocialFadeAnim).with(btnMainFadeAnim);
		
		animatorSet.start();
	}
	
	/** Animation for Campaign Mode */
	private void startCampaignModeAnim() {
		
		ValueAnimator gameModeFadeAnim = ObjectAnimator.ofFloat(
				_tv_gameMode, "alpha", 0f, 1f);
		gameModeFadeAnim.setStartDelay(200);
		gameModeFadeAnim.setDuration(500);
		gameModeFadeAnim.setInterpolator(new DecelerateInterpolator());
		
		ValueAnimator gameLevelFadeAnim = ObjectAnimator.ofFloat(
				_tv_postGame_levelText, "alpha", 0f, 0.6f, 0.8f, 1f);
		gameLevelFadeAnim.setStartDelay(200);
		gameLevelFadeAnim.setDuration(500);
		gameLevelFadeAnim.setInterpolator(new DecelerateInterpolator());
		
		ValueAnimator timeTakenTextFadeAnim = ObjectAnimator.ofFloat(
				_tv_postGame_timeTakenText, "alpha", 0f, 1f);
		timeTakenTextFadeAnim.setStartDelay(300);
		timeTakenTextFadeAnim.setDuration(250);
		timeTakenTextFadeAnim.setInterpolator(new DecelerateInterpolator());
		ValueAnimator timeTakenFadeAnim = ObjectAnimator.ofFloat(
				_tv_postGame_timeTaken, "alpha", 0f, 1f);
		timeTakenFadeAnim.setDuration(250);
		timeTakenFadeAnim.setInterpolator(new DecelerateInterpolator());
		
		ValueAnimator correctTextFadeAnim = ObjectAnimator.ofFloat(
				_tv_postGame_correctText, "alpha", 0f, 1f);
		correctTextFadeAnim.setDuration(250);
		correctTextFadeAnim.setInterpolator(new DecelerateInterpolator());
		ValueAnimator correctFadeAnim = ObjectAnimator.ofFloat(
				_tv_postGame_correctNum, "alpha", 0f, 1f);
		correctFadeAnim.setDuration(250);
		correctFadeAnim.setInterpolator(new DecelerateInterpolator());
		
		ValueAnimator incorrectTextFadeAnim = ObjectAnimator.ofFloat(
				_tv_postGame_incorrectText, "alpha", 0f, 1f);
		incorrectTextFadeAnim.setDuration(250);
		incorrectTextFadeAnim.setInterpolator(new DecelerateInterpolator());
		ValueAnimator incorrectFadeAnim = ObjectAnimator.ofFloat(
				_tv_postGame_incorrectNum, "alpha", 0f, 1f);
		incorrectFadeAnim.setDuration(250);
		incorrectFadeAnim.setInterpolator(new DecelerateInterpolator());
		
		ValueAnimator imageFadeAnim = ObjectAnimator.ofFloat(
				_iv_starsImage, "alpha", 0f, 0.7f, 1f);
		imageFadeAnim.setStartDelay(300);
		imageFadeAnim.setDuration(750);
		imageFadeAnim.setInterpolator(new DecelerateInterpolator());
		
		ValueAnimator btnSocialFadeAnim = ObjectAnimator.ofFloat(
				_btn_socialShare, "alpha", 0f, 0.6f, 1f);
		btnSocialFadeAnim.setStartDelay(300);
		btnSocialFadeAnim.setDuration(600);
		btnSocialFadeAnim.setInterpolator(new DecelerateInterpolator());
		
		ValueAnimator btnMainFadeAnim = ObjectAnimator.ofFloat(
				_btn_backToMainMenu, "alpha", 0f, 0.6f, 1f);
		btnMainFadeAnim.setDuration(600);
		btnMainFadeAnim.setInterpolator(new DecelerateInterpolator());
		btnMainFadeAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                
            	// Enable buttons
        		_btn_socialShare.setEnabled(true);
        		_btn_backToMainMenu.setEnabled(true);
            }
        });
		
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.play(gameModeFadeAnim).before(gameLevelFadeAnim);
		
		animatorSet.play(gameLevelFadeAnim);
		
		animatorSet.play(timeTakenTextFadeAnim).after(gameLevelFadeAnim);
		animatorSet.play(timeTakenTextFadeAnim).with(timeTakenFadeAnim);
		
		animatorSet.play(correctTextFadeAnim).after(timeTakenTextFadeAnim);
		animatorSet.play(correctTextFadeAnim).with(correctFadeAnim);
		
		animatorSet.play(incorrectTextFadeAnim).after(correctTextFadeAnim);
		animatorSet.play(incorrectTextFadeAnim).with(incorrectFadeAnim);
		
		animatorSet.play(imageFadeAnim).after(incorrectTextFadeAnim);
		
		animatorSet.play(btnSocialFadeAnim).after(imageFadeAnim);
		animatorSet.play(btnSocialFadeAnim).with(btnMainFadeAnim);
		
		animatorSet.start();
	}
}
