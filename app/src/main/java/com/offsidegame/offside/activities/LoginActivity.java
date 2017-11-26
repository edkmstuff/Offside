package com.offsidegame.offside.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.appinvite.FirebaseAppInvite;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.offsidegame.offside.R;
import com.offsidegame.offside.events.CompletedHttpRequestEvent;
import com.offsidegame.offside.events.ConnectionEvent;
import com.offsidegame.offside.events.PlayerImageSavedEvent;
import com.offsidegame.offside.events.PlayerJoinPrivateGroupEvent;
import com.offsidegame.offside.events.NetworkingServiceBoundEvent;
import com.offsidegame.offside.helpers.HttpHelper;
import com.offsidegame.offside.helpers.ImageHelper;
import com.offsidegame.offside.models.OffsideApplication;
import com.offsidegame.offside.models.PlayerAssets;
import com.offsidegame.offside.models.User;

import org.acra.ACRA;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class LoginActivity extends AppCompatActivity implements Serializable {

    private final Context context = this;
    private static final int RC_SIGN_IN = 123;
    private FirebaseUser firebaseUser;
    private String playerId;
    private String playerDisplayName;
    private String playerProfilePictureUrl;
    private boolean isUserImageUrlValid = false;
    private String playerEmail;
    private FrameLayout loadingRoot;
    private TextView versionTextView;
    private boolean isInLoginProcess = false;
    private FirebaseAnalytics analytics;
    private String TAG = "SIDEKICK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            getIds();
            loadingRoot.setVisibility(View.VISIBLE);

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }

    }

    private void getIds() {
        loadingRoot = findViewById(R.id.shared_loading_root);
        versionTextView = findViewById(R.id.shared_version_text_view);
        versionTextView.setText(OffsideApplication.getVersion() == null ? "0.0" : OffsideApplication.getVersion());
    }

    @Override
    public void onBackPressed() {
        finish(); // close the application
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new NetworkingServiceBoundEvent(context));
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionEvent(ConnectionEvent connectionEvent) {
        try {
            boolean isConnected = connectionEvent.getConnected();
            if (isConnected) {
                Toast.makeText(context, R.string.lbl_you_are_connected, Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(context, R.string.lbl_you_are_disconnected, Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkingServiceBinding(NetworkingServiceBoundEvent networkingServiceBoundEvent) {
        try {
            if (OffsideApplication.networkingService == null)
                return;

            Context eventContext = networkingServiceBoundEvent.getContext();
            if (eventContext == context || eventContext == getApplicationContext()) {
                loadingRoot.setVisibility(View.GONE);
                if (isNetworkingServiceConnected() && !isInLoginProcess) {
                    login();
                }
            }

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }
    }

    public boolean isNetworkingServiceConnected() {
        return true;
//        return (OffsideApplication.isBoundToNetworkingService &&
//                OffsideApplication.networkingService != null &&
//                OffsideApplication.networkingService.hubConnection != null &&
//                OffsideApplication.networkingService.hubConnection.getState() == ConnectionState.Connected
//        );

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
                                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                                new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                                                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()
                                        ))
                                //.setTosUrl("https://superapp.example.com/terms-of-service.html")
                                .setPrivacyPolicyUrl("http://sidekickgame.com/privacy_policy.html")
                                .setIsSmartLockEnabled(false)
                                //.setIsSmartLockEnabled(!BuildConfig.DEBUG)
                                .setTheme(R.style.BlueTheme)
                                .setLogo(R.drawable.app_logo_10)
                                .build(),
                        RC_SIGN_IN);


            }
        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }
    }

    private void handleSuccessfulLogin() throws InterruptedException {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null || OffsideApplication.networkingService == null)
            return;

        playerId = firebaseUser.getUid();

        if (playerId != null && OffsideApplication.networkingService != null) {
            CountDownLatch latch = new CountDownLatch(1);
            OffsideApplication.networkingService.createListenerQueue(playerId, latch);
            latch.await();


        }
        if (playerId != null && OffsideApplication.networkingService != null) {
            CountDownLatch latch = new CountDownLatch(1);
            OffsideApplication.networkingService.listenToExchange(playerId, latch);
            latch.await();


        }

        playerDisplayName = (firebaseUser.getDisplayName() == null || firebaseUser.getDisplayName().equals("")) ? "NO NAME" : firebaseUser.getDisplayName();
        playerProfilePictureUrl = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() == null ? null : FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
        playerEmail = firebaseUser.getEmail();

        if(playerProfilePictureUrl==null){
            playerProfilePictureUrl = OffsideApplication.getInitialsProfilePictureUrl() + playerId;
        }

        HttpHelper httpHelper = new HttpHelper(playerProfilePictureUrl);
        httpHelper.execute();



    }

    public void saveLoggedInUser() {
        SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(getString(R.string.player_profile_picture_url_key), playerProfilePictureUrl);
        editor.commit();
        User user = new User(playerId, playerDisplayName, playerEmail, playerProfilePictureUrl);
        OffsideApplication.networkingService.requestSaveLoggedInUser(user.getId(), user.getName(), user.getEmail(), user.getProfilePictureUri());
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
    public void onReceivePlayerAssets(PlayerAssets playerAssets) {
        try {
            isInLoginProcess = false;
            OffsideApplication.setPlayerAssets(playerAssets);
            analyzeDynamicLink();
        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }
    }

    public void analyzeDynamicLink() {

//        Intent intent = getIntent();
//        String action = intent.getAction();
//        Uri data = intent.getData();
//        if(data==null){
//            startLobbyActivity();
//            return;
//        }
//        Log.d(TAG, "------action-----: " + action);
//        Log.d(TAG, "------data-----: " + (data != null ? data.toString() : "empty"));


        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            analytics = FirebaseAnalytics.getInstance(context);
                            deepLink = pendingDynamicLinkData.getLink();
//                            if(deepLink==null)
//                                Log.d(TAG, "*****deepLink***** = null");
//                            else
//                                Log.d(TAG, "*****deepLink*****"+deepLink.toString());
                            FirebaseAppInvite invite = FirebaseAppInvite.getInvitation(pendingDynamicLinkData);
                            if (invite != null) {
                                String inviteId = invite.getInvitationId();
//                                if(!TextUtils.isEmpty(inviteId))
//                                    Log.d(TAG, "ACCPET invitation Id" + inviteId);

                                URL url;
                                try {
                                    url = new URL("http", Uri.parse(deepLink.getQuery()).getHost(), deepLink.getQuery().toString());
                                    try {

                                        Map<String, List<String>> dynamicLinkQueryPairs = HttpHelper.splitQuery(url);
                                        String groupIdFromInvitation = dynamicLinkQueryPairs.get("groupId").get(0);
                                        String gameIdFromInvitation = dynamicLinkQueryPairs.get("gameId").get(0);
                                        String privateGameIdFromInvitation = dynamicLinkQueryPairs.get("privateGameId").get(0);

                                        //Add player to the group from which he was invited
                                        if (groupIdFromInvitation != null)


                                            //OffsideApplication.signalRService.requestJoinPrivateGroup(playerId, groupIdFromInvitation);

                                            OffsideApplication.networkingService.requestJoinPrivateGroup(playerId,groupIdFromInvitation);
                                            OffsideApplication.networkingService.requestJoinPrivateGroup(playerId, groupIdFromInvitation);


                                        //Override userPreferences, as theses will be used when tryJoinSelectedAvailableGame will be executed (Lobby Activity)
                                        if (gameIdFromInvitation != null && privateGameIdFromInvitation != null)
                                            OffsideApplication.setUserPreferences(groupIdFromInvitation, gameIdFromInvitation, privateGameIdFromInvitation);

                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }

                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }

                            }

                        } else {
                            startLobbyActivity();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "analyzeDynamicLink:onFailure", e);
                        startLobbyActivity();
                    }
                });


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImageValidationCompleted(CompletedHttpRequestEvent completedHttpRequestEvent) {

        //image from firebase is not a valid image
        isUserImageUrlValid = completedHttpRequestEvent.isUrlValid();
        //check if we have an image for this user already stored

        // in case user does not have profile picture, we generate image with Initials
        if (!isUserImageUrlValid ) {

            String displayName = playerDisplayName.toUpperCase();
            String[] displayNameParts = displayName.trim().split(" ");
            String initials = displayNameParts.length > 1 ? displayNameParts[0].substring(0, 1) + displayNameParts[1].substring(0, 1) : displayNameParts[0].substring(0, 1);
            Bitmap profilePicture = ImageHelper.generateInitialsBasedProfileImage(initials, context);
            byte[] profilePictureToSave = ImageHelper.getBytesFromBitmap(profilePicture);
            String imageString = Base64.encodeToString(profilePictureToSave, Base64.NO_WRAP);

            playerProfilePictureUrl = OffsideApplication.getInitialsProfilePictureUrl() + playerId;
            OffsideApplication.networkingService.requestSaveImageInDatabase(playerId, imageString);


        } else {
            saveLoggedInUser();
        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveSavedPlayerImage(PlayerImageSavedEvent playerImageSavedEvent) {
        try {
            saveLoggedInUser();

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayerJoinedPrivateGroup(PlayerJoinPrivateGroupEvent playerJoinPrivateGroupEvent) {
        if (!OffsideApplication.isLobbyActivityVisible())
            startLobbyActivity();
    }

    public void startLobbyActivity() {

        Intent intent = new Intent(context, LobbyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }


}



