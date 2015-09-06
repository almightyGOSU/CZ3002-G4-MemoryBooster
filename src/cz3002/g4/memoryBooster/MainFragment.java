package cz3002.g4.memoryBooster;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.*;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;

public class MainFragment extends FragmentActivity {

    private final String PENDING_ACTION_BUNDLE_KEY =
            "cz3002.g4.memoryBooster:PendingAction";
    private static final String LINE_SEP = System.getProperty("line.separator");

    private ProfilePictureView _profilePictureView;
    private TextView _profileName;
    private LoginButton _btn_fbLogin;
    private Button _btn_testGetFriends;
    
    private PendingAction pendingAction = PendingAction.NONE;
    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    
    private Bundle _params = null;

    private enum PendingAction {
        NONE,
        POST_PHOTO,
        POST_STATUS_UPDATE
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        
		// Get key hash
        getKeyHash();

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handlePendingAction();
                        updateUI();
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

        if (savedInstanceState != null) {
            String name = savedInstanceState.getString(PENDING_ACTION_BUNDLE_KEY);
            pendingAction = PendingAction.valueOf(name);
        }

        setContentView(R.layout.main_frag);
        
        // Get UI elements
        _profilePictureView = (ProfilePictureView) findViewById(R.id.profilePicture);
        _profileName = (TextView) findViewById(R.id.profileName);
        _btn_fbLogin = (LoginButton) findViewById(R.id.btn_fbLogin);
        _btn_testGetFriends = (Button) findViewById(R.id.btn_testGetFriends);
        
        // Resize Facebook login/logout button
        resizeFbLoginBtn();
        
        // Set Facebook login permissions
        _btn_fbLogin.setReadPermissions(Arrays.asList("public_profile", "user_friends"));

        // For Facebook profile
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                updateUI();
                // It's possible that we were waiting for Profile to be populated in order to
                // post a status update.
                handlePendingAction();
            }
        };
        
        // For testing Facebook get friends
        _params = new Bundle();
        _params.putString("fields", "id,picture");
        _params.putInt("limit", 5);
        
		// For testing Facebook get friends
		// Setup a general callback for each graph request sent, this callback
		// will launch the next request if it exists
		final GraphRequest.Callback graphCallback = new GraphRequest.Callback() {
			@Override
			public void onCompleted(GraphResponse response) {
				
				Log.d("GetFriends", "Completed! " + response.getRawResponse());
				Toast.makeText(getApplicationContext(),
						"Completed! " + response.getRawResponse(),
						Toast.LENGTH_LONG).show();
				
				try {
					JSONArray rawData = response.getJSONObject().getJSONArray(
							"data");
					
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < rawData.length(); i++) {

						try {
							JSONObject jsonObject = rawData.getJSONObject(i);
							sb.append(jsonObject.toString()).append(LINE_SEP);
							Log.d("GetFriends", jsonObject.toString());

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					
					if(sb.length() > 0) {
						Toast.makeText(getApplicationContext(),
								"JSON Data: " + sb.toString(),
								Toast.LENGTH_LONG).show();
					}

					// Get next batch of results if it exists
					GraphRequest nextRequest = response
							.getRequestForPagedResults(GraphResponse.PagingDirection.NEXT);
					if (nextRequest != null) {
						nextRequest.setCallback(this);
						nextRequest.executeAsync();
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
        
		// For testing Facebook get friends
		_btn_testGetFriends.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				// Send first request, the rest should be called by the callback
				new GraphRequest(AccessToken.getCurrentAccessToken(), 
				        "me/friends", _params, HttpMethod.GET, graphCallback).executeAsync();
			}
		});
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
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        profileTracker.stopTracking();
    }

    private void updateUI() {
        boolean enableButtons = AccessToken.getCurrentAccessToken() != null;

        Profile profile = Profile.getCurrentProfile();
        if (enableButtons && profile != null) {
            _profilePictureView.setProfileId(profile.getId());
            _profileName.setText(profile.getName());
        } else {
            _profilePictureView.setProfileId(null);
            _profileName.setText(R.string.guest);
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
    
	/** Old code that does not work somehow.. */
	@SuppressWarnings("unused")
	private void testGetFriends() {

		Bundle param = new Bundle();
		param.putString("fields", "id,picture");
		param.putInt("limit", 5);

		new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/friends",
				param, HttpMethod.GET, new GraphRequest.Callback() {
					public void onCompleted(GraphResponse response) {

						/* Handle the result */
						Log.d("TestGetFriends", "Completed! " + response.getRawResponse());
						
						JSONObject respJsonObject = response.getJSONObject();
						JSONArray jsonArray = null;
						try {
							jsonArray = respJsonObject.getJSONArray("data");
						} catch (JSONException e) {
							e.printStackTrace();
						}

						if (jsonArray == null) {
							Toast.makeText(getApplicationContext(),
									"Response JSONArray is null!",
									Toast.LENGTH_LONG).show();
							Log.e("TestGetFriends", "Response JSONArray is null!");
							return;
						} else {
							Toast.makeText(getApplicationContext(),
									"Response data JSONArray is good!!",
									Toast.LENGTH_LONG).show();
						}

						int jsonArrayLen = jsonArray.length();
						for (int i = 0; i < jsonArrayLen; i++) {

							try {
								JSONObject jsonObject = jsonArray
										.getJSONObject(i);

								Log.d("TestGetFriends", jsonObject.toString());

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

					}
				}
		).executeAsync();
	}
}
