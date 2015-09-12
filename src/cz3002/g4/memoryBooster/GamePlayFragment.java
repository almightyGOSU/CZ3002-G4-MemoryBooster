package cz3002.g4.memoryBooster;

import java.util.ArrayList;

import cz3002.g4.util.Const;
import cz3002.g4.util.Const.GameMode;
import cz3002.g4.util.Const.UserStatus;
import cz3002.g4.util.FacebookDataSource;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GamePlayFragment extends FragmentActivity {
	
	private UserStatus _userStatus = null;
	private GameMode _gameMode = null;
	
	// UI Elements
	private TextView _tv_gameTimeText = null;
	private ImageView _iv_questionImage = null;
	private LinearLayout _ll_userChoices = null;
	private Button _btn_option1 = null;
	private Button _btn_option2 = null;
	private Button _btn_option3 = null;
	private Button _btn_option4 = null;
	private TextView _tv_numCorrect = null;
	private TextView _tv_numWrong = null;
	
	private ProgressDialog _pd_gameStatus = null;
	
	// FacebookDataSource
    private FacebookDataSource _fbDataSrc = null;
    
    // Gameplay
    private ArrayList<Question> _questionSet = null;
    private Question _currQuestion = null;
    private int _currQuestionNum = 0;
    private int _numQuestions = 0;
    private int _numCorrect = 0;
    private int _numWrong = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gameplay_frag);
        
        Intent intent = getIntent();
        _userStatus = (UserStatus) intent.getSerializableExtra(Const.USER_STATUS);
        _gameMode = (GameMode) intent.getSerializableExtra(Const.GAME_MODE);
        
		Log.d("GamePlayFragment", _userStatus.toString());
		Log.d("GamePlayFragment", _gameMode.toString());
		
		getUIElements();
		setUpInteractiveElements();
		setUpTimeText();
		
		// Testing using 'Timed Challenge' mode
		_numQuestions = 10;
		new GenerateQuestionsTask().execute(String.valueOf(_numQuestions));
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
		
		_tv_gameTimeText = (TextView) findViewById(R.id.tv_gameTimeText);
		_iv_questionImage = (ImageView) findViewById(R.id.iv_questionImage);
		_ll_userChoices = (LinearLayout) findViewById(R.id.ll_userChoices);
		_btn_option1 = (Button) findViewById(R.id.btn_option1);
		_btn_option2 = (Button) findViewById(R.id.btn_option2);
		_btn_option3 = (Button) findViewById(R.id.btn_option3);
		_btn_option4 = (Button) findViewById(R.id.btn_option4);
		_tv_numCorrect = (TextView) findViewById(R.id.tv_numCorrect);
		_tv_numWrong = (TextView) findViewById(R.id.tv_numWrong);
	}
	
	/** Set up interactive UI Elements */
	private void setUpInteractiveElements() {
		
		Button.OnClickListener btnOnClickListener = new Button.OnClickListener() {
			public void onClick(View view) {
				
				// Disable all buttons first
				_ll_userChoices.setClickable(false);

				// Process chosen option
				final Button btn = (Button) view;
				String userChoice = (String) btn.getText();
				
				// Check answer, update button
				boolean bCorrect = checkAnswer(userChoice);
				btn.setBackgroundResource(
						bCorrect ? R.drawable.btn_correct :
							R.drawable.btn_wrong);
				
				// Get next question after slight delay of 750ms
				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						
						btn.setBackgroundResource(R.drawable.btn_user_choice);
						getNextQuestion();
						
						// Enable all buttons
						_ll_userChoices.setClickable(true);
					}
				}, 750);
			}
		};
		
		_btn_option1.setOnClickListener(btnOnClickListener);
		_btn_option2.setOnClickListener(btnOnClickListener);
		_btn_option3.setOnClickListener(btnOnClickListener);
		_btn_option4.setOnClickListener(btnOnClickListener);
	}
	
	/** Set up the time text based on chosen game mode */
	private void setUpTimeText() {
		
		switch(_gameMode) {
		case CAMPAIGN_MODE:
			_tv_gameTimeText.setText(R.string.elapsed_time);
			break;
		case FF:
			_tv_gameTimeText.setText(R.string.elapsed_time);
			break;
		case TIMED_CHALLENGE:
			_tv_gameTimeText.setText(R.string.time_left);
			break;
		default:
			break;
		}
	}
	
	/** Checks user choice against answer */
	private boolean checkAnswer(String userChoice) {
		
		boolean bCorrect = _currQuestion.checkAnswer(userChoice);
		if(bCorrect) {
			
			_numCorrect++;
			_tv_numCorrect.setText(String.valueOf(_numCorrect));
		}
		else {
			
			_numWrong++;
			_tv_numWrong.setText(String.valueOf(_numWrong));
		}
		
		return bCorrect;
	}
	
	/** Gets the next question! */
	private void getNextQuestion() {
		
		// FOR HACKING
		if(_currQuestionNum >= _numQuestions)
			return;
		
		_currQuestion = _questionSet.get(_currQuestionNum);
		
		_iv_questionImage.setImageBitmap(_currQuestion.getQuestionImage());
		ArrayList<String> questionOptions = _currQuestion.getQuestionOptions();
		_btn_option1.setText(questionOptions.get(0));
		_btn_option2.setText(questionOptions.get(1));
		_btn_option3.setText(questionOptions.get(2));
		_btn_option4.setText(questionOptions.get(3));
		
		_currQuestionNum++;
		if(_currQuestionNum >= _numQuestions) {
			
			Log.d("GamePlayFragment", "GAME OVER!!");
		}
	}
	
	private class GenerateQuestionsTask extends AsyncTask<String, Void, String> {
		
		protected void onPreExecute() {
			
			Log.d("GenerateQuestionsTask", "I am here in onPreExecute!");
			
			_pd_gameStatus = new ProgressDialog(GamePlayFragment.this);
			_pd_gameStatus.setIndeterminate(true);
			_pd_gameStatus.setMessage("Generating questions..");
			_pd_gameStatus.setCancelable(false);
			_pd_gameStatus.setCanceledOnTouchOutside(false);
			_pd_gameStatus.show();
		}
		
		// Generate questions
		// params[0]: Number of questions to generate
		protected String doInBackground(String... params) {
			
			Log.d("GenerateQuestionsTask", "I am here in doInBackground!");
			
			int numQuestions = Integer.parseInt(params[0]);
			_fbDataSrc = new FacebookDataSource(getApplicationContext());
			
			// Generate questions
			_questionSet = QuestionGenerator.generateFbQuestions(
					_fbDataSrc, numQuestions);
			
			// Gets the first question (Hidden behind dialog at this time)
			runOnUiThread(new Runnable() {
				public void run() {
					
					getNextQuestion();
				}
			});
			
			try {	
				for(int i = 3; i > 0; i--) {
					
					final int time = i;
					runOnUiThread(new Runnable() {
						public void run() {
							_pd_gameStatus.setMessage(
									"Game starting in " + time + "..");
						}
					});
					Thread.sleep(900);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			return "Success";
		}

		protected void onPostExecute(String result) {
			
			Log.d("GenerateQuestionsTask", "I am here in onPostExecute!");
			
			_pd_gameStatus.dismiss();
			return;
		}
	}
}
