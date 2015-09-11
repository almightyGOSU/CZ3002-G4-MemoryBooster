package cz3002.g4.memoryBooster;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.TextView;

import com.facebook.*;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;

import cz3002.g4.util.BitmapUtil;
import cz3002.g4.util.FacebookDataSource;

public class MainFragment extends FragmentActivity {

    private final String PENDING_ACTION_BUNDLE_KEY =
            "cz3002.g4.memoryBooster:PendingAction";

    // For displaying current user information
    private ProfilePictureView _profilePictureView = null;
    private TextView _profileName = null;
    private LoginButton _btn_fbLogin = null;
    
    // For getting Facebook data
    private ArrayList<String> _friendsProfIDList = null;
    private ArrayList<String> _friendsProfNameList = null;
    private ProgressDialog _pd_FbFriends = null;
    
    // FacebookDataSource
    private FacebookDataSource _fbDataSrc = null;
    
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
    
    // Play game
    public static final String USER_STATUS = "USER_STATUS";
    public static final String GUEST_USER = "GUEST";
    public static final String FB_USER = "FACEBOOK";
    private static String _userStatus = GUEST_USER;

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
        getKeyHash();

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
        updateFbFriendsList();
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
            _profileName.setText(profile.getName());
            
            _userStatus = FB_USER;
            
        } else {
        	
            _profilePictureView.setProfileId(null);
            _profileName.setText(R.string.guest);
            
            _userStatus = GUEST_USER;
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

	private void getKeyHash() {
		// Add code to print out the key hash
		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					"cz3002.g4.memoryBooster", PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
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
				
				try {
					JSONArray rawData = response.getJSONObject().getJSONArray(
							"data");
					
					for (int i = 0; i < rawData.length(); i++) {

						JSONObject jsonObject = rawData.getJSONObject(i);

						String friendID = jsonObject.getString("id");
						String friendName = jsonObject.getString("name");

						_friendsProfIDList.add(friendID);
						_friendsProfNameList.add(friendName);
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
        
        Log.d("updateFbFriendsList", "I AM HERE!");
        
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
		_pd_FbFriends = new ProgressDialog(MainFragment.this);
		_pd_FbFriends.setTitle(R.string.fb_friends);
		_pd_FbFriends.setMessage("Retriving Facebook Friends List..");
		_pd_FbFriends.setCancelable(false);
		_pd_FbFriends.setCanceledOnTouchOutside(false);
		_pd_FbFriends.show();
    }
    
    private class UpdateFbFriendsTask extends AsyncTask<String [], Void, String> {
		
		protected void onPreExecute() {
			
			Log.d("UpdateFbFriendsTask", "I am here in onPreExecute!");
			
			_pd_FbFriends.setTitle(R.string.fb_friends);
			_pd_FbFriends.setMessage("Updating Facebook Friends Information..");
		}
		
		// Get Facebook friends information, store to database
		protected String doInBackground(String[] ... params) {
			
			Log.d("UpdateFbFriendsTask", "I am here in doInBackground!");
			
			// Open connection to database
			_fbDataSrc.open();
			
			// Get all existing profile IDs
			ArrayList<String> existingProfIDs = _fbDataSrc.getAllProfileID();
			
			for(String [] userInfo : params) {
				
				if(existingProfIDs.contains(userInfo[0]))
					continue;
				
				Log.d("UpdateFbFriendsTask", userInfo[0] + ", " + userInfo[1]);
				
				Bitmap bm = getFacebookProfilePicture(userInfo[0]);
				if(bm != null) {
					_fbDataSrc.insertFbFriend(userInfo[0], userInfo[1],
							BitmapUtil.getBytes(bm));
				}
			}
			
			// Close connection to database
			_fbDataSrc.close();
			
			return "Success";
		}

		protected void onPostExecute(String result) {
			
			Log.d("UpdateFbFriendsTask", "I am here in onPostExecute!");
			
			_pd_FbFriends.dismiss();
			return;
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
	
	/** Getting UI Elements from layout */
	private void getUIElements() {
		
		// For displaying current user information
		_profilePictureView = (ProfilePictureView) findViewById(R.id.profilePicture);
        _profileName = (TextView) findViewById(R.id.profileName);
        _btn_fbLogin = (LoginButton) findViewById(R.id.btn_fbLogin);
        
        // Main menu buttons
        _btn_viewTutorial = (Button) findViewById(R.id.btn_viewTutorial);
        _btn_playGame = (Button) findViewById(R.id.btn_playGame);
        _btn_viewHighscores = (Button) findViewById(R.id.btn_viewHighscores);
        _btn_settings = (Button) findViewById(R.id.btn_settings);
	}
	
	/** Set up interactive UI Elements */
	private void setUpInteractiveElements() {
		
		// Resize Facebook login/logout button
        resizeFbLoginBtn();
        
        // Set Facebook login permissions
        _btn_fbLogin.setReadPermissions(
        		Arrays.asList("public_profile", "user_friends"));
		
		// For viewing tutorial
        _btn_viewTutorial.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				
			}
		});
        
        // For starting game
        _btn_playGame.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				Intent gameIntent = new Intent(
						getApplicationContext(), GameFragment.class);
				gameIntent.putExtra(USER_STATUS, _userStatus);
	        	startActivity(gameIntent);
			}
		});
        
        // For viewing highscores
        _btn_viewHighscores.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				
			}
		});
        
        // For adjusting application settings
        _btn_settings.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				
			}
		});
	}
}
