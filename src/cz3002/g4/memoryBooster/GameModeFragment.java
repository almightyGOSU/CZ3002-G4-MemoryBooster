package cz3002.g4.memoryBooster;

import android.content.Intent;
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
	
	// UI Elements for timed challenge
	private LinearLayout _ll_timedChallenge = null;
	private TextView _tv_timedChallenge_highscore = null;
	private Button _btn_startTimedChallenge = null;
	
	// UI Elements for campaign mode
	// --------

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

				Toast.makeText(getApplicationContext(), "LAI CAMPAIGN MODE!",
						Toast.LENGTH_SHORT).show();
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
	}
}
