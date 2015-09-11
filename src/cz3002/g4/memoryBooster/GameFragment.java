package cz3002.g4.memoryBooster;

import java.util.ArrayList;

import cz3002.g4.util.FacebookDataSource;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class GameFragment extends FragmentActivity {
	
	private static String _userStatus;
	
	// FacebookDataSource
    private FacebookDataSource _fbDataSrc = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playgame_frag);
        
        Intent intent = getIntent();
		if(intent != null)
		{
			if(intent.hasExtra(MainFragment.USER_STATUS))
			{
				_userStatus = intent.getStringExtra(MainFragment.USER_STATUS);
				Log.d("GameFragment onCreate", _userStatus);
			}
		}
		
		_fbDataSrc = new FacebookDataSource(getApplicationContext());
		_fbDataSrc.open();
		
		ArrayList<String> randomIDs = _fbDataSrc.getRandomProfileIDs(8);
		_fbDataSrc.close();
		
		for(String randomID : randomIDs) {
			
			Log.d("GameFragment fbDataSrc", randomID);
		}
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
