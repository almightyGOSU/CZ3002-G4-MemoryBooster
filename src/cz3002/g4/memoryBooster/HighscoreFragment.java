package cz3002.g4.memoryBooster;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cz3002.g4.util.Const;
import cz3002.g4.util.StringUtil;

public class HighscoreFragment extends FragmentActivity {
	
	// UI Elements
	private Button _btn_viewSocial = null;
	private Button _btn_viewGeneral = null;
	
	// UI Elements - Highscore table
	private TextView _tv_highscore_user1 = null;
	private TextView _tv_highscore_value1 = null;
	private TextView _tv_highscore_user2 = null;
	private TextView _tv_highscore_value2 = null;
	private TextView _tv_highscore_user3 = null;
	private TextView _tv_highscore_value3 = null;
	private TextView _tv_highscore_user4 = null;
	private TextView _tv_highscore_value4 = null;
	private TextView _tv_highscore_user5 = null;
	private TextView _tv_highscore_value5 = null;
	private TextView [] _tv_highscores = null;
	
	// UI Elements - Back to Main Menu
	private Button _btn_highscore_backToMain = null;
	
	// Progress Dialog
	private ProgressDialog _pd_dataRetrieval = null;
	
	// Highscores Information
	private String [] _socialHighscores = null;
	private String [] _generalHighscores = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.highscore_frag);
        
        getUIElements();
        setUpInteractiveElements();
        
        _socialHighscores = new String [10];
		_generalHighscores = new String [10];
		
		for(int i = 0; i < 5; i++) {
			_socialHighscores[i*2] = "-";
			_socialHighscores[i*2 + 1] = "0";
			_generalHighscores[i*2] = "-";
			_generalHighscores[i*2 + 1] = "0";
		}
		
		setHighscoreTable(true);
        
        new GetHighscoresTask().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
	
	private class GetHighscoresTask extends AsyncTask<Void, Void, String> {
		
		protected void onPreExecute() {
			
			Log.d("GetHighscoresTask", "I am here in onPreExecute!");
			_pd_dataRetrieval = new ProgressDialog(HighscoreFragment.this);
			_pd_dataRetrieval.setCancelable(false);
			_pd_dataRetrieval.setCanceledOnTouchOutside(false);

			_pd_dataRetrieval.setTitle(R.string.global_highscores);
			_pd_dataRetrieval.setMessage(StringUtil.enlargeString(
					"Retrieving Highscores..", 1.3f));
			_pd_dataRetrieval.show();
		}
		
		// Retrieve highscores
		protected String doInBackground(Void... params) {
			
			Log.d("GetHighscoresTask", "I am here in doInBackground!");
			
			String jsonData = getJSONData(Const.HIGHSCORES_URL);
			if(jsonData != null) {
				try {
					
					JSONArray jsonArr = new JSONArray(jsonData);
					JSONArray socialArr = jsonArr.getJSONArray(0);
					JSONArray generalArr = jsonArr.getJSONArray(1);
					
					ArrayList<HighscoreEntry> _entries = new ArrayList<>();
					for (int i = 0; i < socialArr.length(); i++) {
						JSONObject socialEntry = socialArr.getJSONObject(i);
						
						_entries.add(new HighscoreEntry(
								socialEntry.getString("name"),
								socialEntry.getInt("score")));
					}
					
					Collections.sort(_entries);
					for(int i = 0; i < Math.min(5, _entries.size()); i++) {
						_socialHighscores[i*2] = _entries.get(i).getName();
						_socialHighscores[i*2 + 1] =
								_entries.get(i).getScoreStr();
					}
					
					_entries = new ArrayList<>();
					for (int i = 0; i < generalArr.length(); i++) {
						JSONObject generalEntry = generalArr.getJSONObject(i);
						
						_entries.add(new HighscoreEntry(
								generalEntry.getString("name"),
								generalEntry.getInt("score")));
					}
					
					Collections.sort(_entries);
					for(int i = 0; i < Math.min(5, _entries.size()); i++) {
						_generalHighscores[i*2] = _entries.get(i).getName();
						_generalHighscores[i*2 + 1] =
								_entries.get(i).getScoreStr();
					}
					
					Thread.sleep(750);
					
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			return "Success";
		}

		protected void onPostExecute(String result) {
			
			Log.d("GetHighscoresTask", "I am here in onPostExecute!");
			
			setHighscoreTable(true);
			_pd_dataRetrieval.dismiss();
		}
	}
	
	private String getJSONData(String url) {
		InputStream inputStream = null;
		String result = null;
		try {

			// Create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// Make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

			// Receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// Convert inputstream to string
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);

		} catch (Exception e) {
			Log.d("getJSONData", e.getLocalizedMessage());
		}

		return result;
	}
	
	private String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;
	}
	
	/** Updates highscore table */
	private void setHighscoreTable(boolean bSocial) {
		
		if(bSocial) {
			
			for(int i = 0; i < 5; i++) {
				_tv_highscores[i*2].setText(_socialHighscores[i*2]);
				_tv_highscores[i*2 + 1].setText(_socialHighscores[i*2 + 1]);
			}
			
			_btn_viewSocial.setBackgroundResource(
					R.drawable.btn_highscore_selected);
			_btn_viewGeneral.setBackgroundResource(
					R.drawable.btn_highscore_unselected);
			_btn_viewSocial.setTextColor(
					getResources().getColor(R.color.black));
			_btn_viewGeneral.setTextColor(
					getResources().getColor(R.color.gray));
		}
		else {
			
			for(int i = 0; i < 5; i++) {
				_tv_highscores[i*2].setText(_generalHighscores[i*2]);
				_tv_highscores[i*2 + 1].setText(_generalHighscores[i*2 + 1]);
			}
			
			_btn_viewGeneral.setBackgroundResource(
					R.drawable.btn_highscore_selected);
			_btn_viewSocial.setBackgroundResource(
					R.drawable.btn_highscore_unselected);
			_btn_viewGeneral.setTextColor(
					getResources().getColor(R.color.black));
			_btn_viewSocial.setTextColor(
					getResources().getColor(R.color.gray));
		}
	}
	
	/** Getting UI Elements from layout */
	private void getUIElements() {
		
		_btn_viewSocial = (Button) findViewById(R.id.btn_viewSocial);
		_btn_viewGeneral = (Button) findViewById(R.id.btn_viewGeneral);
		
		_tv_highscore_user1 = (TextView) findViewById(R.id.tv_highscore_user1);
		_tv_highscore_value1 = (TextView) findViewById(R.id.tv_highscore_value1);
		_tv_highscore_user2 = (TextView) findViewById(R.id.tv_highscore_user2);
		_tv_highscore_value2 = (TextView) findViewById(R.id.tv_highscore_value2);
		_tv_highscore_user3 = (TextView) findViewById(R.id.tv_highscore_user3);
		_tv_highscore_value3 = (TextView) findViewById(R.id.tv_highscore_value3);
		_tv_highscore_user4 = (TextView) findViewById(R.id.tv_highscore_user4);
		_tv_highscore_value4 = (TextView) findViewById(R.id.tv_highscore_value4);
		_tv_highscore_user5 = (TextView) findViewById(R.id.tv_highscore_user5);
		_tv_highscore_value5 = (TextView) findViewById(R.id.tv_highscore_value5);
		
		_tv_highscores = new TextView [10];
		_tv_highscores[0] = _tv_highscore_user1;
		_tv_highscores[1] = _tv_highscore_value1;
		_tv_highscores[2] = _tv_highscore_user2;
		_tv_highscores[3] = _tv_highscore_value2;
		_tv_highscores[4] = _tv_highscore_user3;
		_tv_highscores[5] = _tv_highscore_value3;
		_tv_highscores[6] = _tv_highscore_user4;
		_tv_highscores[7] = _tv_highscore_value4;
		_tv_highscores[8] = _tv_highscore_user5;
		_tv_highscores[9] = _tv_highscore_value5;
		
		_btn_highscore_backToMain = (Button) findViewById(
				R.id.btn_highscore_backToMain);
	}
	
	/** Set up interactive UI Elements */
	private void setUpInteractiveElements() {
        
		// For viewing 'Social' highscores
		_btn_viewSocial.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				
				setHighscoreTable(true);
			}
		});
        
        // For viewing 'General' highscores
		_btn_viewGeneral.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				
				setHighscoreTable(false);
			}
		});
        
        // For returning to main menu
		_btn_highscore_backToMain.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				
				// Go back main menu
				Intent intent = new Intent(
						getApplicationContext(), MainFragment.class);
				intent.putExtra(Const.BACK_TO_MAIN, true);
				startActivity(intent);
			}
		});
	}
	
	private class HighscoreEntry implements Comparable<HighscoreEntry> {
		
		private String _name;
		private int _score;
		
		public HighscoreEntry(String name, int score) {
			_name = name;
			_score = score;
		}
		
		public String getName() {
			return _name;
		}
		
		public int getScore() {
			return _score;
		}
		
		public String getScoreStr() {
			return String.valueOf(_score);
		}

		@Override
		public int compareTo(HighscoreEntry entry) {		
			return entry.getScore() - this.getScore();
		}
	}
}
