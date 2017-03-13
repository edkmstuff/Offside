package com.offsidegame.offside.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.offsidegame.offside.R;
import com.offsidegame.offside.helpers.ImageHelper;
import com.offsidegame.offside.models.OffsideApplication;

import org.acra.ACRA;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

//import com.google.firebase.messaging.FirebaseMessaging;

//import static com.offsidegame.offside.R.string.fbProfile_key;

public class LoginActivity extends AppCompatActivity implements Serializable {

    private final Context context = this;
    private static final int RC_SIGN_IN = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            login();
        } catch (Exception ex) {
            ACRA.getErrorReporter().handleException(ex);
        }
    }

    private void login() {
        //taken from: https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md
        try {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth == null)
                throw new Exception("LoginActivity -> login: auth is null");

            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                handleSuccessfulLogin();

            } else { //not signed in
                startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setProviders(Arrays.asList(
                                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                        //.setTosUrl("https://superapp.example.com/terms-of-service.html")
                        .setIsSmartLockEnabled(false)
                        //.setIsSmartLockEnabled(!BuildConfig.DEBUG)
                        .setTheme(R.style.GreenTheme)
                        .setLogo(R.drawable.logo)
                        .build(), RC_SIGN_IN);
            }
        } catch (Exception ex) {
            ACRA.getErrorReporter().handleException(ex);
        }
    }

    private void handleSuccessfulLogin() {




        //String playerId = FirebaseAuth.getInstance().getCurrentUser().getProviderData().get(1).getUid();
        String playerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //String playerId = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String playerDisplayName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String playerProfilePictureUrl = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()==null ? null : FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString() ;

        // in case user does not have profile picture, we generate image with Initials
        if (playerProfilePictureUrl == null){
            String displayName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName().toUpperCase();
            String[] displayNameParts = displayName.trim().split(" ");
            String initials = displayNameParts.length > 1? displayNameParts[0].substring(0,1) + displayNameParts[1].substring(0,1) : displayNameParts[0].substring(0,1);
            Bitmap profilePicture = ImageHelper.generateInitialsBasedProfileImage(initials, context);
            ImageHelper.storeImage(profilePicture, context);
            playerProfilePictureUrl = OffsideApplication.getProfileImageFileName();
        }



        SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);

        SharedPreferences.Editor editor = settings.edit();

        editor.putString(getString(R.string.player_display_name_key), playerDisplayName);
        editor.putString(getString(R.string.player_profile_picture_url_key), playerProfilePictureUrl);

        editor.commit();

        Intent intent = new Intent(context, JoinGameActivity.class);
        // intent.putExtra("gameCodeFromNotification", gameCodeFromNotification);
        startActivity(intent);
    }






    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == ResultCodes.OK)
            {
                handleSuccessfulLogin();
                return;

            }
            else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Toast.makeText(context, "SIGN IN FAILED - CANCELLED", Toast.LENGTH_LONG);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(context, "SIGN IN FAILED - NO NETWORK", Toast.LENGTH_LONG);
                    //showSnackbar(R.string.no_internet_connection);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(context, "SIGN IN FAILED - UNKNOWN ERROR", Toast.LENGTH_LONG);
                    return;
                }
            }

            Toast.makeText(context, "SIGN IN FAILED - UNKNOWN RESPONSE", Toast.LENGTH_LONG);


        }
    }

//    public void startup() {
//
//
//        if (!FacebookSdk.isInitialized()) {
//            FacebookSdk.sdkInitialize(getApplicationContext());
//        }
//
//        AppEventsLogger.activateApp(getApplication());
//
//        toolbar = (Toolbar) findViewById((R.id.app_bar));
//        setSupportActionBar(toolbar);
//
//        //FirebaseMessaging.getInstance().subscribeToTopic("offside");
//
//        SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
//        boolean isLoggedIn = settings.getBoolean(getString(R.string.is_logged_in_key), false);
//
//        String loginExpirationTimeAsString = (String) settings.getString(getString(R.string.login_expiration_time_key), "");
//
//        DateHelper dateHelper = new DateHelper();
//        Date loginExpirationTime = dateHelper.formatAsDate(loginExpirationTimeAsString, context);
//        if (loginExpirationTime == null)
//            loginExpirationTime = dateHelper.getCurrentDate();
//
//        Date current = dateHelper.getCurrentDate();
//
//
//        if (isLoggedIn  /*|| current.after(loginExpirationTime)*/) {
//
//            Intent intent = new Intent(context, JoinGameActivity.class);
//            intent.putExtra("gameCodeFromNotification", gameCodeFromNotification);
//            startActivity(intent);
//            return;
//        }
//
//
//        email = (EditText) findViewById(R.id.email);
//        login = (Button) findViewById(R.id.email_login_button);
//        login.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                if (isBoundToSignalRService) {
//                    signalRService.login(email.getText().toString());
//                }
//            }
//
//        });
//
//        loadingRoot = (LinearLayout) findViewById(R.id.l_loading_root);
//        contentRoot = (LinearLayout) findViewById(R.id.l_content_root);
//        loadingRoot.setVisibility(View.GONE);
//        contentRoot.setVisibility(View.VISIBLE);
//
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        Intent intent = new Intent();
//        intent.setClass(context, SignalRService.class);
//        bindService(intent, signalRServiceConnection, Context.BIND_AUTO_CREATE);
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        EventBus.getDefault().register(context);
//    }
//
//    @Override
//    public void onStop() {
//        EventBus.getDefault().unregister(context);
//        // Unbind from the service
//        if (isBoundToSignalRService) {
//            unbindService(signalRServiceConnection);
//            isBoundToSignalRService = false;
//        }
//
//        super.onStop();
//    }
//
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onConnectionEvent(ConnectionEvent connectionEvent) {
//        boolean isConnected = connectionEvent.getConnected();
//        if (isConnected)
//            Toast.makeText(context, R.string.lbl_you_are_connected, Toast.LENGTH_SHORT).show();
//        else
//            Toast.makeText(context, R.string.lbl_you_are_disconnected, Toast.LENGTH_SHORT).show();
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onLogin(LoginEvent loginEvent) {
//        loadingRoot.setVisibility(View.VISIBLE);
//        contentRoot.setVisibility(View.GONE);
//        boolean isFacebookLogin = loginEvent.getIsFacebookLogin();
//        String id = loginEvent.getId();
//        String name = loginEvent.getName();
//        //ToDo: get the user email from facebook profile
//        String email = isFacebookLogin ? null : loginEvent.getId();
//        String profilePictureUrl = isFacebookLogin ? Profile.getCurrentProfile().getProfilePictureUri(150, 150).toString() : "http://offside.somee.com/images/defaultImage.jpg";
//        String password = isFacebookLogin ? null : loginEvent.getPassword();
//
//
//        SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
//
//        String recentToken = settings.getString(getString(R.string.recent_token_key), "");
//
//        User user = new User(id, name, email, profilePictureUrl, password, recentToken);
//
//        signalRService.saveUser(user);
//
//        SharedPreferences.Editor editor = settings.edit();
//
//        editor.putBoolean(getString(R.string.is_logged_in_key), true);
//        editor.putString(getString(R.string.user_id_key), id);
//        editor.putString(getString(R.string.user_name_key), name);
//        editor.putString(getString(R.string.user_profile_picture_url_key), profilePictureUrl);
//
//
//        DateHelper dateHelper = new DateHelper();
//        Date current = dateHelper.getCurrentDate();
//        Date expirationTime = dateHelper.addHours(current, 3);
//        String expirationTimeAsString = dateHelper.formatAsString(expirationTime, context);
//        editor.putString(getString(R.string.login_expiration_time_key), expirationTimeAsString);
//        editor.commit();
//
//        Intent intent = new Intent(context, JoinGameActivity.class);
//        intent.putExtra("gameCodeFromNotification", gameCodeFromNotification);
//        startActivity(intent);
//
//    }
//
//    protected void onNewIntent(Intent intent) {
//        OffsideApplication.setIsPlayerQuitGame(false);
//        gameCodeFromNotification = intent.getExtras().getString(getString(R.string.game_code_key));
//        startup();
//
//
//    }


}


//        Button logoutButton = (Button)findViewById(R.id.logout_button);
//
//
//        logoutButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                AuthUI.getInstance()
//                        .signOut(LoginActivity.this)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            public void onComplete(@NonNull Task<Void> task) {
//                                // user is now signed out
//                                //startActivity(new Intent(LoginActivity.this, LoginActivity.class));
//                               // finish();
//                            }
//                        });
//            }


//        });


//        Intent callingIntent = getIntent();
//        if (callingIntent != null && callingIntent.getExtras() != null)
//            gameCodeFromNotification = callingIntent.getExtras().getString("gameCodeEditText");
//
//        startup();
