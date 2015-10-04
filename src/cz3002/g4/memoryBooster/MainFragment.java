package cz3002.g4.memoryBooster;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.*;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;

import cz3002.g4.util.AnswerTagPair;
import cz3002.g4.util.BitmapUtil;
import cz3002.g4.util.Const;
import cz3002.g4.util.GeneralDataSource;
import cz3002.g4.util.StringUtil;
import cz3002.g4.util.Const.UserStatus;
import cz3002.g4.util.FacebookDataSource;

public class MainFragment extends FragmentActivity {

    private final String PENDING_ACTION_BUNDLE_KEY =
            "cz3002.g4.memoryBooster:PendingAction";

    // For displaying current user information
    private ProfilePictureView _profilePictureView = null;
    private TextView _tv_profileName = null;
    private LoginButton _btn_fbLogin = null;
    
    // For getting Facebook data
    private ArrayList<String> _friendsProfIDList = null;
    private ArrayList<String> _friendsProfNameList = null;
    private ProgressDialog _pd_dataRetrieval = null;
    
    // FacebookDataSource
    private FacebookDataSource _fbDataSrc = null;
    
    // GeneralDataSource
    private GeneralDataSource _genDataSrc = null;
    
    // Facebook components
    private PendingAction pendingAction = PendingAction.NONE;
    private CallbackManager _callbackManager = null;
    private ProfileTracker _profileTracker = null;
    
    // For getting Facebook data
    private Bundle _params = null;
    
    // Main menu buttons
    private Button _btn_viewTutorial = null;
    private Button _btn_playGame = null;
    private Button _btn_viewHighscores = null;
    private Button _btn_settings = null;
    
    // Status of the current user
    private UserStatus _userStatus = UserStatus.GUEST;
    private String _userProfileName = null;
    
    // For downloading Facebook profile information
    private int _startedFbDownloads = 0;
    private int _completedFbDownloads = 0;
    
    // For downloading 'general' dataset
    private boolean _bFbUpdating = false;
    private int _startedGenDownloads = 0;
    private int _completedGenDownloads = 0;
    
    // Fake Google+ sign in
    private RelativeLayout _rl_fake_google = null;

    private enum PendingAction {
        NONE,
        POST_PHOTO,
        POST_STATUS_UPDATE
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.main_frag);
        
		// Get key hash for Facebook development
        /*getKeyHash();*/

        if (savedInstanceState != null) {
            String name = savedInstanceState.getString(PENDING_ACTION_BUNDLE_KEY);
            pendingAction = PendingAction.valueOf(name);
        }
        
        // Initialize Facebook SDK and its related components
        initFacebook();
        
        // Get UI elements
        getUIElements();
        
        // Set up interactive components
        setUpInteractiveElements();
        
        _fbDataSrc = new FacebookDataSource(getApplicationContext());
        _genDataSrc = new GeneralDataSource(getApplicationContext());
        
        // Check if application is navigating back to main menu from other screens
        boolean bNavigatingBackToMain = false;
        Intent intent = getIntent();
        if(intent != null) {
        	if(intent.hasExtra(Const.BACK_TO_MAIN)) {
        		
        		bNavigatingBackToMain = intent.getBooleanExtra(
        				Const.BACK_TO_MAIN, false);
        	}
        }
        
        // Do stuff required for new launch
        if(!bNavigatingBackToMain) {
        	
        	Log.d("MainFragment onCreate", "I am here due to new launch!");
        	updateFbFriendsList();
        	
        	if(!_bFbUpdating) {
        		// Update general dataset
        		new UpdateGenDatasetTask().execute();
        	}
        		
        }
        else {
        	Log.d("MainFragment onCreate", "Navigated back to main menu!");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateUI();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(PENDING_ACTION_BUNDLE_KEY, pendingAction.name());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        _callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _profileTracker.stopTracking();
    }

    private void updateUI() {
        boolean enableButtons = AccessToken.getCurrentAccessToken() != null;

        Profile profile = Profile.getCurrentProfile();
        if (enableButtons && profile != null) {
        	
            _profilePictureView.setProfileId(profile.getId());
            _tv_profileName.setText(profile.getName());
            
            _userStatus = UserStatus.FACEBOOK;
            _userProfileName = profile.getName();
            
            // Hide fake Google+ sign in
            _rl_fake_google.setVisibility(View.INVISIBLE);
            
        } else {
        	
            _profilePictureView.setProfileId(null);
            _tv_profileName.setText(R.string.guest);
            
            _userStatus = UserStatus.GUEST;
            _userProfileName = null;
            
            // Show fake Google+ sign in
            _rl_fake_google.setVisibility(View.VISIBLE);
        }
    }

    private void handlePendingAction() {
    	
        PendingAction previouslyPendingAction = pendingAction;
        
        // These actions may re-set pendingAction if they are still pending,
        // but we assume they will succeed.
        pendingAction = PendingAction.NONE;

        switch (previouslyPendingAction) {
            case NONE:
                break;
			default:
				break;
        }
    }

	@SuppressWarnings("unused")
	private void getKeyHash() {
		// Add code to print out the key hash
		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					"cz3002.g4.memoryBooster", PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA-1");
				md.update(signature.toByteArray());
				Log.d("KeyHash:",
						Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {

		} catch (NoSuchAlgorithmException e) {

		}
    }
    
    /** Initialize Facebook SDK and its related components */
    private void initFacebook() {
    	
    	_callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(_callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handlePendingAction();
                        updateUI();
                        
                        Log.d("initFacebook", "I am here onSuccess!");
                        
                        // Login success, update Facebook friends list
                        updateFbFriendsList();
                    }

                    @Override
                    public void onCancel() {
                        if (pendingAction != PendingAction.NONE) {
                            showAlert();
                            pendingAction = PendingAction.NONE;
                        }
                        updateUI();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        if (pendingAction != PendingAction.NONE
                                && exception instanceof FacebookAuthorizationException) {
                            showAlert();
                            pendingAction = PendingAction.NONE;
                        }
                        updateUI();
                    }

                    private void showAlert() {
                        new AlertDialog.Builder(MainFragment.this)
                                .setTitle(R.string.fb_cancelled)
                                .setMessage(R.string.fb_permission_not_granted)
                                .setPositiveButton(R.string.fb_ok, null)
                                .show();
                    }
                });
        
        // For Facebook profile
        _profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                updateUI();
                // It's possible that we were waiting for Profile to be populated in order to
                // post a status update
                handlePendingAction();
            }
        };
    }
    
    /** Resize Facebook login/logout button */
    private void resizeFbLoginBtn() {
    	
    	float fbIconScale = 1.6F;
        Drawable drawable = getResources()
        		.getDrawable(com.facebook.R.drawable.com_facebook_button_icon);
        drawable.setBounds(0, 0, (int)(drawable.getIntrinsicWidth()*fbIconScale),
                                 (int)(drawable.getIntrinsicHeight()*fbIconScale));
		_btn_fbLogin.setCompoundDrawables(drawable, null, null, null);
		_btn_fbLogin.setCompoundDrawablePadding(getResources()
				.getDimensionPixelSize(R.dimen.fb_margin_override_textpadding));
		_btn_fbLogin.setPadding(
				getResources().getDimensionPixelSize(R.dimen.fb_margin_override_lr),
				getResources().getDimensionPixelSize(R.dimen.fb_margin_override_top),
				getResources().getDimensionPixelSize(R.dimen.fb_margin_override_lr),
				getResources().getDimensionPixelSize(R.dimen.fb_margin_override_bottom));
		_btn_fbLogin.setTextSize(20);
    }
    
    private GraphRequest.Callback setFbGraphCallback() {
        
    	return new GraphRequest.Callback() {
			@Override
			public void onCompleted(GraphResponse response) {
				
				Log.d("setFbGraphCallback", "Completed once!");
				try {
					JSONObject jsonObject = response.getJSONObject();
					
					if(jsonObject != null) {
					
						JSONArray rawData = jsonObject.getJSONArray("data");
						
						for (int i = 0; i < rawData.length(); i++) {
	
							jsonObject = rawData.getJSONObject(i);
	
							String friendID = jsonObject.getString("id");
							String friendName = jsonObject.getString("name");
	
							_friendsProfIDList.add(friendID);
							_friendsProfNameList.add(friendName);
						}
					}

					// Get next batch of results if it exists
					GraphRequest nextRequest = response
							.getRequestForPagedResults(
									GraphResponse.PagingDirection.NEXT);
					
					if (nextRequest != null) {
						nextRequest.setParameters(_params);
						nextRequest.setCallback(this);
						nextRequest.executeAsync();
					}
					else {
						
						String [][] params = 
								new String[_friendsProfIDList.size()][2];
						
						Log.d("GetFriends", "Params length: " + params.length);
						for(int i = 0; i < params.length; i++) {
							
							params[i] = new String[2];
							params[i][0] = _friendsProfIDList.get(i);
							params[i][1] = _friendsProfNameList.get(i);
						}
						
						// Update number of facebook friends
						SharedPreferences preferences = getSharedPreferences(
								Const.SHARED_PREF, Activity.MODE_PRIVATE);
						SharedPreferences.Editor editor = preferences.edit();
						editor.putInt(Const.NUM_FB_FRIENDS, params.length);
						editor.apply();
						
						// Start async task to update profile
						// information of Facebook friends
						new UpdateFbFriendsTask().execute(params);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
    }
    
    private void updateFbFriendsList() {
    	
    	boolean bHasAccessToken = AccessToken.getCurrentAccessToken() != null;
        boolean bLoggedIn = Profile.getCurrentProfile() != null;
        
        if(!bHasAccessToken || !bLoggedIn)
        	return;
        
        Log.d("updateFbFriendsList", "Go go go");
        
        // Initialize list to store profile ID and name of facebook friends
        _friendsProfIDList = new ArrayList<String>();
        _friendsProfNameList = new ArrayList<String>();
        
        // Send first request, the rest should be called by the callback
        _params = new Bundle();
        _params.putString("fields", "id,name");
        _params.putInt("limit", 30);
        final GraphRequest.Callback _graphCallback = setFbGraphCallback();
		new GraphRequest(AccessToken.getCurrentAccessToken(), 
		        "me/friends", _params, HttpMethod.GET, _graphCallback).executeAsync();
		
		// Show dialog
		_pd_dataRetrieval = new ProgressDialog(MainFragment.this);
		_pd_dataRetrieval.setTitle(R.string.fb_friends);
		_pd_dataRetrieval.setMessage(StringUtil.enlargeString(
				"Retriving Facebook Friends List..", 1.3f));
		_pd_dataRetrieval.setCancelable(false);
		_pd_dataRetrieval.setCanceledOnTouchOutside(false);
		_pd_dataRetrieval.show();
		
		Log.d("updateFbFriendsList", "Function over!");
    }
    
    private class UpdateFbFriendsTask extends AsyncTask<String [], Void, String> {
		
		protected void onPreExecute() {
			
			Log.d("UpdateFbFriendsTask", "I am here in onPreExecute!");
			
			_pd_dataRetrieval.setTitle(R.string.fb_friends);
			_pd_dataRetrieval.setMessage(StringUtil.enlargeString(
					"Updating Facebook Friends Information..", 1.3f));
		}
		
		// Get Facebook friends information, store to database
		protected String doInBackground(String[] ... params) {
			
			Log.d("UpdateFbFriendsTask", "I am here in doInBackground!");
			
			// Open connection to database
			Log.d("UpdateFbFriendsTask", "Opening FB db connection!");
			_fbDataSrc.open();
			
			// Get all existing profile IDs
			ArrayList<String> existingProfIDs = _fbDataSrc.getAllProfileID();
			
			for(final String [] userInfo : params) {
				
				if(existingProfIDs.contains(userInfo[0]))
					continue;
				
				_startedFbDownloads++;
				new Thread() {
					public void run() {
						
						//Log.d("UpdateFbFriendsTask", userInfo[0] + ", " + userInfo[1]);
						
						Bitmap bm = getFacebookProfilePicture(userInfo[0]);
						if(bm != null) {
							_fbDataSrc.insertFbFriend(userInfo[0], userInfo[1],
									BitmapUtil.getBytes(bm));
							
							_completedFbDownloads++;
							
							Log.d("Completed Dl", userInfo[0] + ", " + userInfo[1]);
						}
					}
				}.start();
			}
			
			while(_completedFbDownloads != _startedFbDownloads);
			
			// Close connection to database
			Log.d("UpdateFbFriendsTask", "Closing FB db connection!");
			_fbDataSrc.close();
			
			return "Success";
		}

		protected void onPostExecute(String result) {
			
			Log.d("UpdateFbFriendsTask", "I am here in onPostExecute!");
			new UpdateGenDatasetTask().execute();
		}
	}
    
    /** Blocking function to get Facebook profile picture */
	private Bitmap getFacebookProfilePicture(String userID) {
		
		URL imageURL = null;
		Bitmap bitmap = null; 
		try {
			imageURL = new URL("https://graph.facebook.com/" + userID
					+ "/picture?width=240&height=240");
			bitmap = BitmapFactory.decodeStream(
					imageURL.openConnection().getInputStream());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bitmap;
	}
	
	private class UpdateGenDatasetTask extends AsyncTask<Void, Void, String> {
		
		protected void onPreExecute() {
			
			Log.d("UpdateGenDatasetTask", "I am here in onPreExecute!");
			
			if(_pd_dataRetrieval == null) {
				_pd_dataRetrieval = new ProgressDialog(MainFragment.this);
				_pd_dataRetrieval.setCancelable(false);
				_pd_dataRetrieval.setCanceledOnTouchOutside(false);
			}
			_pd_dataRetrieval.setTitle(R.string.gen_dataset);
			_pd_dataRetrieval.setMessage(StringUtil.enlargeString(
					"Updating 'General' Question Bank..", 1.3f));
			_pd_dataRetrieval.show();
		}
		
		// Get Facebook friends information, store to database
		protected String doInBackground(Void... params) {
			
			Log.d("UpdateGenDatasetTask", "I am here in doInBackground!");
			
			// Open connection to database
			Log.d("UpdateGenDatasetTask", "Opening Gen db connection!");
			_genDataSrc.open();
			
			// Get all existing (answer, tag) pairs
			ArrayList<AnswerTagPair> answerTagPairs =
					_genDataSrc.getAllAnswerTag();
			
			String jsonData = getJSONData(Const.GEN_DATA_URL);
			if(jsonData != null) {
				try {
					
					JSONArray jsonArr = new JSONArray(jsonData);
					for(int i = 0; i < jsonArr.length(); i++) {
						
						JSONObject jsonObj = jsonArr.getJSONObject(i);
						String answer = jsonObj.getString("answer");
						String tag = jsonObj.getString("tag");
						String img_url = jsonObj.getString("image_url");
						boolean bExists = false;
						
						for(AnswerTagPair atPair : answerTagPairs) {
							if(atPair.getAnswer().equals(answer) &&
									atPair.getTag().equals(tag)) {
								bExists = true;
								break;
							}
						}
						
						if(!bExists) {
							getGeneralQuestion(answer, tag, img_url);
						}
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			while(_completedGenDownloads != _startedGenDownloads);
			
			// Close connection to database
			Log.d("UpdateGenDatasetTask", "Closing Gen db connection!");
			_genDataSrc.close();
			
			return "Success";
		}

		protected void onPostExecute(String result) {
			
			Log.d("UpdateGenDatasetTask", "I am here in onPostExecute!");
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
	
	private void getGeneralQuestion(final String answer, final String tag, final String img_url) {
		_startedGenDownloads++;
		new Thread() {
			public void run() {
				
				Bitmap bm = getGeneralImage(img_url);
				if(bm != null) {
					_genDataSrc.insertGenData(answer, tag,
							BitmapUtil.getBytes(bm));
					
					_completedGenDownloads++;
					
					Log.d("Completed Dl", answer + ", " + tag);
				}
			}
		}.start();
	}
	
	/** Blocking function to get image for a 'general' question*/
	private Bitmap getGeneralImage(String img_url) {
		
		URL imageURL = null;
		Bitmap bitmap = null; 
		try {
			imageURL = new URL(img_url);
			bitmap = BitmapFactory.decodeStream(
					imageURL.openConnection().getInputStream());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bitmap;
	}
	
	/** Getting UI Elements from layout */
	private void getUIElements() {
		
		// For displaying current user information
		_profilePictureView = (ProfilePictureView) findViewById(R.id.profilePicture);
        _tv_profileName = (TextView) findViewById(R.id.profileName);
        _btn_fbLogin = (LoginButton) findViewById(R.id.btn_fbLogin);
        
        // Main menu buttons
        _btn_viewTutorial = (Button) findViewById(R.id.btn_viewTutorial);
        _btn_playGame = (Button) findViewById(R.id.btn_playGame);
        _btn_viewHighscores = (Button) findViewById(R.id.btn_viewHighscores);
        _btn_settings = (Button) findViewById(R.id.btn_settings);
        
        // Fake Google+ Sign In
        _rl_fake_google = (RelativeLayout) findViewById(R.id.rl_fake_google);
	}
	
	/** Set up interactive UI Elements */
	private void setUpInteractiveElements() {
		
		// Resize Facebook login/logout button
        resizeFbLoginBtn();
        
        // Set Facebook login permissions
        _btn_fbLogin.setReadPermissions(
        		Arrays.asList("public_profile", "user_friends"));
        
        // Fake Google+ Sign In
        _rl_fake_google.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				Toast.makeText(getApplicationContext(),
						"'Google+ Sign In' is not yet available!",
						Toast.LENGTH_LONG).show();
			}
		});
		
		// For viewing game tutorial
        _btn_viewTutorial.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				Intent tutorialIntent = new Intent(
						getApplicationContext(), TutorialFragment.class);
	        	startActivity(tutorialIntent);
			}
		});
        
        // For starting game
        _btn_playGame.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				
				// Check if user has enough facebook friends!
				if(_userStatus == UserStatus.FACEBOOK) {
					
					SharedPreferences preferences = getSharedPreferences(
							Const.SHARED_PREF, Activity.MODE_PRIVATE);
					int numFbFriends = preferences.getInt(
							Const.NUM_FB_FRIENDS, 0);
					
					Log.d("PlayGameBtn", "# of FB friends: " + numFbFriends);
				
					if(numFbFriends < Const.MIN_FB_FRIENDS) {
						
						_userStatus = UserStatus.GUEST;
			            //_userProfileName = null;
			            
			            Toast.makeText(getApplicationContext(),
			            		"Not enough Facebook friends!\n"
			            		+ "Playing using 'general' dataset..",
			            		Toast.LENGTH_LONG).show();
			            Log.d("PlayGameBtn", "Not enough FB friends");
					}
				}

				Intent gameIntent = new Intent(
						getApplicationContext(), GameModeFragment.class);
				
				gameIntent.putExtra(Const.USER_STATUS, _userStatus);
				gameIntent.putExtra(Const.USER_NAME, _userProfileName);
				
	        	startActivity(gameIntent);
			}
		});
        
        // For viewing highscores
        _btn_viewHighscores.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				
				Intent highscoreIntent = new Intent(
						getApplicationContext(), HighscoreFragment.class);
	        	startActivity(highscoreIntent);
			}
		});
        
        // For adjusting application settings
        _btn_settings.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				//TODO: Application settings
				Toast.makeText(getApplicationContext(),
						"'Application settings' is not yet available!",
						Toast.LENGTH_LONG).show();
			}
		});
	}
}
