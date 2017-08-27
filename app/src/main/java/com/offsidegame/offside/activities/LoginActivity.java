package com.offsidegame.offside.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.FrameLayout;
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
import com.offsidegame.offside.models.OffsideApplication;
import com.offsidegame.offside.models.PlayerAssets;
import com.offsidegame.offside.models.User;

import org.acra.ACRA;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.Arrays;

import microsoft.aspnet.signalr.client.ConnectionState;


public class LoginActivity extends AppCompatActivity implements Serializable {

    private final Context context = this;
    private static final int RC_SIGN_IN = 123;
    private FirebaseUser firebaseUser;

    private String playerId;
    private String playerDisplayName;
    private String playerProfilePictureUrl;
    private String playerEmail;


    private FrameLayout loadingRoot;
    private boolean isInLoginProcess = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            loadingRoot = (FrameLayout) findViewById(R.id.shared_loading_root);
            loadingRoot.setVisibility(View.VISIBLE);

            // to allow exit by clicking on back doubleup_button , setting some flags on current intent
            Intent intent = this.getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }
    }

    @Override
    public void onResume() {

        super.onResume();

        EventBus.getDefault().post(new SignalRServiceBoundEvent(context));

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(context);

    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(context);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionEvent(ConnectionEvent connectionEvent) {
        try {
            boolean isConnected = connectionEvent.getConnected();
            if (isConnected) {
                Toast.makeText(context, R.string.lbl_you_are_connected, Toast.LENGTH_SHORT).show();
                //handleSuccessfulLogin();
            } else
                Toast.makeText(context, R.string.lbl_you_are_disconnected, Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSignalRServiceBinding(SignalRServiceBoundEvent signalRServiceBoundEvent) {
        try {

            if (OffsideApplication.signalRService == null)
                return;

            Context eventContext = signalRServiceBoundEvent.getContext();
            if (eventContext == context || eventContext == getApplicationContext()) {

                if (isSignalRConnected() && !isInLoginProcess) {

                    loadingRoot.setVisibility(View.GONE);
                    login();
                }

            }


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }
    }

    public boolean isSignalRConnected() {
        return (OffsideApplication.isBoundToSignalRService &&
                OffsideApplication.signalRService != null &&
                OffsideApplication.signalRService.hubConnection != null &&
                OffsideApplication.signalRService.hubConnection.getState() == ConnectionState.Connected
        );

    }


    private void login() {
        //taken from: https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md
        try {

            isInLoginProcess = true;

            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth == null)
                throw new Exception("LoginActivity -> login: auth is null");

            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                handleSuccessfulLogin();

            } else { //not signed in

                AuthUI.IdpConfig facebookIdp = new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER)
                        .setPermissions(Arrays.asList("user_friends")).build();

                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(
                                        Arrays.asList(facebookIdp,
                                                new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()
                                        ))
                                //.setTosUrl("https://superapp.example.com/terms-of-service.html")
                                //.setPrivacyPolicyUrl("https://superapp.example.com/privacy-policy.html")
                                .setIsSmartLockEnabled(false)
                                //.setIsSmartLockEnabled(!BuildConfig.DEBUG)
                                .setTheme(R.style.BlueTheme)
                                .setLogo(R.drawable.app_logo_25)
                                .build(),
                        RC_SIGN_IN);

//                startActivityForResult(AuthUI.getInstance()
//                        .createSignInIntentBuilder()
//                        .setProviders(Arrays.asList(
//                                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
//                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
//                                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
//                        //.setTosUrl("https://superapp.example.com/terms-of-service.html")
//                        .setIsSmartLockEnabled(false)
//                        //.setIsSmartLockEnabled(!BuildConfig.DEBUG)
//                        .setTheme(R.style.BlueTheme)
//                        .setLogo(R.drawable.app_logo_25)
//                        .build(), RC_SIGN_IN);
            }
        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }
    }

    private void handleSuccessfulLogin() {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null || OffsideApplication.signalRService == null)
            return;


        playerId = firebaseUser.getUid();
        playerDisplayName = (firebaseUser.getDisplayName() == null || firebaseUser.getDisplayName().equals("")) ? "NO NAME" : firebaseUser.getDisplayName();
        playerProfilePictureUrl = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() == null ? null : FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
        playerEmail = firebaseUser.getEmail();

        // in case user does not have profile picture, we generate image with Initials
        if (playerProfilePictureUrl == null) {

            String displayName = playerDisplayName.toUpperCase();
            String[] displayNameParts = displayName.trim().split(" ");
            String initials = displayNameParts.length > 1 ? displayNameParts[0].substring(0, 1) + displayNameParts[1].substring(0, 1) : displayNameParts[0].substring(0, 1);
            Bitmap profilePicture = ImageHelper.generateInitialsBasedProfileImage(initials, context);
            byte[] profilePictureToSave = ImageHelper.getBytesFromBitmap(profilePicture);
            String imageString = Base64.encodeToString(profilePictureToSave, Base64.NO_WRAP);

            OffsideApplication.signalRService.requestSaveImageInDatabase(playerId, imageString);


        }

        completeUserAccepted();
    }

    public void completeUserAccepted(){

        if(playerProfilePictureUrl== null)
            playerProfilePictureUrl = OffsideApplication.getInitialsProfilePictureUrl() + playerId;

        SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(getString(R.string.player_profile_picture_url_key), playerProfilePictureUrl);
        editor.commit();

        User user = new User(playerId, playerDisplayName, playerEmail, playerProfilePictureUrl);
        OffsideApplication.signalRService.requestSaveLoggedInUser(user);


        Intent intent = new Intent(context, LobbyActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);
        isInLoginProcess = false;


    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            super.onActivityResult(requestCode, resultCode, data);
            // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.

            if (requestCode == RC_SIGN_IN) {
                IdpResponse response = IdpResponse.fromResultIntent(data);

                // Successfully signed in
                if (resultCode == ResultCodes.OK) {
                    handleSuccessfulLogin();
                    return;

                } else if (resultCode == RESULT_CANCELED) {

                    onBackPressed();

                    Toast.makeText(context, "SIGN IN FAILED - CANCELLED", Toast.LENGTH_LONG);
                    return;

                } else {
                    // Sign in failed
                    if (response == null) {
                        // User pressed back doubleup_button
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

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveSavedPlayerImage(PlayerAssets playerAssets) {
        try {

            completeUserAccepted();


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }
    }


    @Override
    public void onBackPressed() {
        finish(); // finish activity

    }



}



