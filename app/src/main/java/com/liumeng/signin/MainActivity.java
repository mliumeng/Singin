package com.liumeng.signin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.liumeng.signin.utils.SnackbarUtil;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TWITTER_KEY = "25IEDFV6vE2UwbnaiR4NNY2zU";
    private static final String TWITTER_SECRET = "UH8NAE4HWqius0yPJR0h3O0Mt8fGYtRj0bQbteMcZhFWOrZw31";
    private static final int RC_SIGN_IN = 3222;
    private static final String TAG = "GoogleLogin";

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private TwitterLoginButton loginButtonTW;
    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // init facebook begin
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        // init facebook end

        // init twitter begin
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        // init twitter end

        // init Google+ begin
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // init Google+ end
        setContentView(R.layout.activity_main);
        loginButton = (LoginButton) findViewById(R.id.login_button_fb);
        loginWithFacebook();
        loginWithTwitter();
        loginWithGoogle();
    }

    private void loginWithGoogle() {
        // Build a GoogleApiClient with access to the Google Sign-In API and the
// options specified by gso.
        /* FragmentActivity *//* OnConnectionFailedListener */
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(MainActivity.this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // Set the dimensions of the sign-in button.
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button_gl);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        findViewById(R.id.sign_in_button_gl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void loginWithTwitter() {
        loginButtonTW = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButtonTW.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()
                TwitterSession session = result.data;
                // TODO: Remove toast and use the TwitterSession's userID
                // with your app's user model
                String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

                SnackbarUtil.ShortSnackbar(loginButton, "Login Success with Twitter !", SnackbarUtil.Twitter).show();
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
                SnackbarUtil.ShortSnackbar(loginButton, "Login failure with Twitter !", SnackbarUtil.Alert).show();
            }
        });

    }

    private void loginWithFacebook() {
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                SnackbarUtil.ShortSnackbar(loginButton, "Login Success with Facebook !", SnackbarUtil.Facebook).show();
            }

            @Override
            public void onCancel() {
                SnackbarUtil.ShortSnackbar(loginButton, "Login Cancel with Facebook!", SnackbarUtil.Warning).show();
            }

            @Override
            public void onError(FacebookException error) {
                SnackbarUtil.ShortSnackbar(loginButton, "Login Error with Facebook!", SnackbarUtil.Alert).show();
            }
        });
    }


    /*This is very important if it does not cause any callbacks to be received */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        loginButtonTW.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            SnackbarUtil.ShortSnackbar(loginButton, "Login Success with Google!", SnackbarUtil.Google).show();
        } else {
            SnackbarUtil.ShortSnackbar(loginButton, "Login Error with Google!", SnackbarUtil.Alert).show();
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
