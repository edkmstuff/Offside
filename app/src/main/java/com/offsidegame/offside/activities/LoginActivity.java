package com.offsidegame.offside.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.offsidegame.offside.R;
import com.offsidegame.offside.events.ConnectionEvent;
import com.offsidegame.offside.events.SignalRServiceBoundEvent;
import com.offsidegame.offside.helpers.ImageHelper;
import com.offsidegame.offside.helpers.SignalRService;
import com.offsidegame.offside.models.OffsideApplication;
import com.offsidegame.offside.models.User;
import org.acra.ACRA;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.Arrays;


public class LoginActivity extends AppCompatActivity implements Serializable {

    private final Context context = this;
    private static final int RC_SIGN_IN = 123;

    private SignalRService signalRService;
    private boolean isBoundToSignalRService = false;
    private LinearLayout loadingRoot;

    private final ServiceConnection signalRServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to SignalRService, cast the IBinder and get SignalRService instance
            SignalRService.LocalBinder binder = (SignalRService.LocalBinder) service;
            signalRService = binder.getService();
            isBoundToSignalRService = true;
            EventBus.getDefault().post(new SignalRServiceBoundEvent(context));
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            isBoundToSignalRService = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            loadingRoot = (LinearLayout) findViewById(R.id.l_loading_root);
            loadingRoot.setVisibility(View.VISIBLE);


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleException(ex);
        }
    }

    @Override
    public void onResume() {

        //todo:why this is in on resume and not in onStart????
        super.onResume();
        Intent intent = new Intent();
        intent.setClass(context, SignalRService.class);
        bindService(intent, signalRServiceConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(context);

    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(context);
        // Unbind from the service
        if (isBoundToSignalRService) {
            unbindService(signalRServiceConnection);
            isBoundToSignalRService = false;
        }

        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionEvent(ConnectionEvent connectionEvent) {
        try {
            boolean isConnected = connectionEvent.getConnected();
            if (isConnected){
                Toast.makeText(context, R.string.lbl_you_are_connected, Toast.LENGTH_SHORT).show();
                handleSuccessfulLogin();
            }
            else
                Toast.makeText(context, R.string.lbl_you_are_disconnected, Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            ACRA.getErrorReporter().handleException(ex);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSignalRServiceBinding(SignalRServiceBoundEvent signalRServiceBoundEvent) {
        try {
            Context eventContext = signalRServiceBoundEvent.getContext();

            if (eventContext == null) {
                return;
            }
            if (eventContext == context) {

                loadingRoot.setVisibility(View.GONE);
                login();
            }
        }
        catch (Exception ex){
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

        FirebaseUser player = FirebaseAuth.getInstance().getCurrentUser();
        String playerId = player.getUid();
        String playerDisplayName = player.getDisplayName();
        String playerProfilePictureUrl = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() == null ? null : FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
        String playerEmail = player.getEmail();

        boolean isUserImageSaved= true;
        boolean isUserDetailsSaved= true;

        // in case user does not have profile picture, we generate image with Initials
        if (playerProfilePictureUrl == null) {
            String displayName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName().toUpperCase();
            String[] displayNameParts = displayName.trim().split(" ");
            String initials = displayNameParts.length > 1 ? displayNameParts[0].substring(0, 1) + displayNameParts[1].substring(0, 1) : displayNameParts[0].substring(0, 1);
            Bitmap profilePicture = ImageHelper.generateInitialsBasedProfileImage(initials, context);
            byte [] profilePictureToSave = ImageHelper.getBytesFromBitmap(profilePicture);
            String imageString = Base64.encodeToString(profilePictureToSave, Base64.NO_WRAP);

            isUserImageSaved =  signalRService.saveImageInDatabase(playerId, imageString);
            //ImageHelper.storeImage(profilePicture, context);
            playerProfilePictureUrl = OffsideApplication.getInitialsProfilePictureUrl()+playerId;

        }

        SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(getString(R.string.player_profile_picture_url_key),playerProfilePictureUrl);
        editor.commit();

        User user = new User(playerId, playerDisplayName, playerEmail, playerProfilePictureUrl);
        isUserDetailsSaved = signalRService.saveLoggedInUser(user);

        if(isUserDetailsSaved && isUserImageSaved){
            Intent intent = new Intent(context, JoinGameActivity.class);
            startActivity(intent);
        }



    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try
        {
            super.onActivityResult(requestCode, resultCode, data);
            // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.

            if (requestCode == RC_SIGN_IN) {
                IdpResponse response = IdpResponse.fromResultIntent(data);

                // Successfully signed in
                if (resultCode == ResultCodes.OK) {
                    handleSuccessfulLogin();
                    return;

                } else if (resultCode == RESULT_CANCELED) {

                    Toast.makeText(context, "SIGN IN FAILED - CANCELLED", Toast.LENGTH_LONG);
                    return;

                } else {
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
        catch (Exception ex)
        {
            ACRA.getErrorReporter().handleException(ex);
        }

    }

}



