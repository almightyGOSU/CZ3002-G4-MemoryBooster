package cz3002.g4.memoryBooster;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cz3002.g4.util.Const;
import cz3002.g4.util.Const.GameMode;
import cz3002.g4.util.Const.UserStatus;

public class GameModeFragment extends FragmentActivity {
	
	private UserStatus _userStatus;
	private GameMode _gameMode;
	
	// UI Elements for selecting game modes
	private LinearLayout _ll_gameMode = null;
	private Button _btn_timedChallengeMode = null;
	private Button _btn_campaignMode = null;
	private Button _btn_ffMode = null;
	
	// UI Elements for Timed Challenge
	private LinearLayout _ll_timedChallenge = null;
	private TextView _tv_timedChallenge_highscore = null;
	private Button _btn_startTimedChallenge = null;
	
	// UI Elements for Campaign Mode
	private LinearLayout _ll_campaignModeLevels = null;
	private Button [] _btn_campaignModeLevels = null;
	
	// Highscore (Timed Challenge) and Progress (Campaign Mode)
	private int _tc_highscore = 0;
	private int [] _cmProgress = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gamemode_frag);
        
		Intent intent = getIntent();
		_userStatus = (UserStatus) intent
				.getSerializableExtra(Const.USER_STATUS);
		
		getUIElements();
		setUpInteractiveElements();
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        _tc_highscore = 0;
    	_cmProgress = new int[Const.CAMPAIGN_LEVELS];
        
        SharedPreferences preferences = getSharedPreferences(
				Const.SHARED_PREF, Activity.MODE_PRIVATE);
        
        _tc_highscore = preferences.getInt(
        		Const.GameMode.TIMED_CHALLENGE.toString(), 0);
        _tv_timedChallenge_highscore.setText("" + _tc_highscore);
        
        for(int i = 0; i < _cmProgress.length; i++) {
        	_cmProgress[i] = preferences.getInt(
            		Const.GameMode.CAMPAIGN_MODE.toString() + (i+1), -1);
        }
        
        setCampaignModeLevels();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
    
    /** Getting UI Elements from layout */
	private void getUIElements() {
		
		// For selecting game mode
		_ll_gameMode = (LinearLayout) findViewById(R.id.ll_gameMode);
		_btn_timedChallengeMode = (Button) findViewById(R.id.btn_timedChallengeMode);
		_btn_campaignMode = (Button) findViewById(R.id.btn_campaignMode);
		_btn_ffMode = (Button) findViewById(R.id.btn_ffMode);
		
		// For Timed Challenge mode
		_ll_timedChallenge = (LinearLayout) findViewById(R.id.ll_timedChallenge);
		_tv_timedChallenge_highscore = (TextView) findViewById(R.id.tv_timedChallenge_highscore);
		_btn_startTimedChallenge = (Button) findViewById(R.id.btn_startTimedChallenge);
		
		// For Campaign Mode -- Level Selection
		_ll_campaignModeLevels = (LinearLayout) findViewById(R.id.ll_campaignModeLevels);
		_btn_campaignModeLevels = new Button[Const.CAMPAIGN_LEVELS];
		
		_btn_campaignModeLevels[0] = (Button) findViewById(R.id.btn_cm_level1);
		_btn_campaignModeLevels[1] = (Button) findViewById(R.id.btn_cm_level2);
		_btn_campaignModeLevels[2] = (Button) findViewById(R.id.btn_cm_level3);
		
		_btn_campaignModeLevels[3] = (Button) findViewById(R.id.btn_cm_level4);
		_btn_campaignModeLevels[4] = (Button) findViewById(R.id.btn_cm_level5);
		_btn_campaignModeLevels[5] = (Button) findViewById(R.id.btn_cm_level6);
		
		_btn_campaignModeLevels[6] = (Button) findViewById(R.id.btn_cm_level7);
		_btn_campaignModeLevels[7] = (Button) findViewById(R.id.btn_cm_level8);
		_btn_campaignModeLevels[8] = (Button) findViewById(R.id.btn_cm_level9);
		
		_btn_campaignModeLevels[9] = (Button) findViewById(R.id.btn_cm_level10);
		_btn_campaignModeLevels[10] = (Button) findViewById(R.id.btn_cm_level11);
		_btn_campaignModeLevels[11] = (Button) findViewById(R.id.btn_cm_level12);
	}
	
	/** Set up interactive UI Elements */
	private void setUpInteractiveElements() {
		
		// For selecting 'Timed Challenge'
		_btn_timedChallengeMode.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				// Switch layouts
				_ll_gameMode.setVisibility(View.INVISIBLE);
				_ll_timedChallenge.setVisibility(View.VISIBLE);
			}
		});
        
		// For selecting 'Campaign Mode'
		_btn_campaignMode.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				// Switch layouts
				_ll_gameMode.setVisibility(View.INVISIBLE);
				_ll_campaignModeLevels.setVisibility(View.VISIBLE);
			}
		});
		
		// For selecting 'Fast & Furious'
		_btn_ffMode.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				Toast.makeText(getApplicationContext(), "Fast & Furious",
						Toast.LENGTH_SHORT).show();
			}
		});
		
		// For starting a new 'Timed Challenge' game
		_btn_startTimedChallenge.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				
				_gameMode = GameMode.TIMED_CHALLENGE;

				Intent gameIntent = new Intent(
						getApplicationContext(), GamePlayFragment.class);
				
				gameIntent.putExtra(Const.USER_STATUS, _userStatus);
				gameIntent.putExtra(Const.GAME_MODE, _gameMode);
				
	        	startActivity(gameIntent);
			}
		});
		
		// For selecting Campaign Mode level
		Button.OnClickListener ocl = new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				final Button btn = (Button) v;
				String cmLevel = (String) btn.getText();
				
				_gameMode = GameMode.CAMPAIGN_MODE;

				Intent gameIntent = new Intent(
						getApplicationContext(), GamePlayFragment.class);
				
				gameIntent.putExtra(Const.USER_STATUS, _userStatus);
				gameIntent.putExtra(Const.GAME_MODE, _gameMode);
				gameIntent.putExtra(Const.GAME_LEVEL, Integer.parseInt(cmLevel));
				
	        	startActivity(gameIntent);
			}
		};
		
		for(Button b : _btn_campaignModeLevels) {
			b.setOnClickListener(ocl);
		}
	}
	
	private void setCampaignModeLevels() {
		
		// Ensure that level 1 is always unlocked
		if(_cmProgress[0] == -1) {
			_cmProgress[0] = 0;
		}
		
		for(int i = 0; i < _cmProgress.length; i++) {
			
			switch(_cmProgress[i]) {
			
			case 0:
				// Unlocked
				_btn_campaignModeLevels[i].setClickable(true);
				_btn_campaignModeLevels[i].setText(Integer.toString(i + 1));
				_btn_campaignModeLevels[i].setBackgroundResource(
						R.drawable.btn_level_0stars);
				break;
			case 1:
				// 1 star
				_btn_campaignModeLevels[i].setClickable(true);
				_btn_campaignModeLevels[i].setText(Integer.toString(i + 1));
				_btn_campaignModeLevels[i].setBackgroundResource(
						R.drawable.btn_level_1stars);
				break;
			case 2:
				// 2 stars
				_btn_campaignModeLevels[i].setClickable(true);
				_btn_campaignModeLevels[i].setText(Integer.toString(i + 1));
				_btn_campaignModeLevels[i].setBackgroundResource(
						R.drawable.btn_level_2stars);
				break;
			case 3:
				// 3 stars
				_btn_campaignModeLevels[i].setClickable(true);
				_btn_campaignModeLevels[i].setText(Integer.toString(i + 1));
				_btn_campaignModeLevels[i].setBackgroundResource(
						R.drawable.btn_level_3stars);
				break;
			default:
				// Locked
				_btn_campaignModeLevels[i].setClickable(false);
				_btn_campaignModeLevels[i].setText(R.string.empty);
				_btn_campaignModeLevels[i].setBackgroundResource(
						R.drawable.btn_level_locked);
				break;
			}
        }
	}
}
