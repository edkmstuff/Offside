package com.offsidegame.offside.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.appinvite.FirebaseAppInvite;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.iid.FirebaseInstanceId;
import com.offsidegame.offside.R;
import com.offsidegame.offside.events.CompletedHttpRequestEvent;
import com.offsidegame.offside.events.ConnectionEvent;
import com.offsidegame.offside.events.EditValueEvent;
import com.offsidegame.offside.events.LoadingEvent;
import com.offsidegame.offside.events.NetworkingServiceBoundEvent;
import com.offsidegame.offside.events.PlayerImageSavedEvent;
import com.offsidegame.offside.events.PlayerJoinPrivateGroupEvent;
import com.offsidegame.offside.events.PlayerModelEvent;
import com.offsidegame.offside.helpers.Formatter;
import com.offsidegame.offside.helpers.HttpHelper;
import com.offsidegame.offside.helpers.ImageHelper;
import com.offsidegame.offside.models.OffsideApplication;
import com.offsidegame.offside.models.PlayerAssets;
import com.offsidegame.offside.models.PlayerModel;
import com.offsidegame.offside.models.User;

import org.acra.ACRA;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jsoup.helper.StringUtil;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
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
    private int playerColorId = 0;
    private boolean isUserImageUrlValid = false;
    private String playerEmail;
    private LinearLayout loadingRoot;
    private TextView versionTextView;
    private boolean isInLoginProcess = false;
    private FirebaseAnalytics analytics;
    private String TAG = "SIDEKICK";
    //edit value dialog
    private Dialog editValueDialog;
    private String firebaseDeviceToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            getIds();
            loadingRoot.setVisibility(View.VISIBLE);
            versionTextView.setVisibility(View.VISIBLE);

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
        try {
            super.onResume();
            EventBus.getDefault().post(new NetworkingServiceBoundEvent(context));

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    @Override
    public void onStart() {
        try {
            super.onStart();
            EventBus.getDefault().register(context);

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    @Override
    public void onStop() {
        try {
            EventBus.getDefault().unregister(context);
            super.onStop();
        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

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
                //loadingRoot.setVisibility(View.GONE);
                versionTextView.setVisibility(View.GONE);

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


                //loadingRoot.setVisibility(View.GONE);
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

    private void handleSuccessfulLogin() throws InterruptedException {

        try {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser == null || OffsideApplication.networkingService == null)
                return;

            playerId = firebaseUser.getUid();
            firebaseDeviceToken = FirebaseInstanceId.getInstance().getToken();
            //Log.d(TAG,"-----------firebaseDeviceToken: "+ firebaseDeviceToken);

            if (playerId != null && OffsideApplication.networkingService != null) {
                CountDownLatch latch = new CountDownLatch(1);
                OffsideApplication.networkingService.createQueue(playerId, latch);
                latch.await();


            }
            if (playerId != null && OffsideApplication.networkingService != null) {
                CountDownLatch latch = new CountDownLatch(1);
                OffsideApplication.networkingService.bindToRoutingKey(playerId, latch);
                latch.await();

            }

            handleUserImage();


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    public void handleUserImage() {

        playerProfilePictureUrl = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() == null ? null : FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();

        if (playerProfilePictureUrl == null) {
            playerProfilePictureUrl = OffsideApplication.getInitialsProfilePictureUrl() + playerId;
        }

        try {
            HttpHelper httpHelper = new HttpHelper(playerProfilePictureUrl);
            httpHelper.execute();
        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImageValidationCompleted(CompletedHttpRequestEvent completedHttpRequestEvent) {

        try {
            //pick a color for player to be used in his messages on the chat bubble.
            playerColorId = ImageHelper.getRandomColor();
            playerDisplayName = firebaseUser.getDisplayName();

            isUserImageUrlValid = completedHttpRequestEvent.isUrlValid();

            boolean userHasDisplayName = !(playerDisplayName == null || playerDisplayName.equals("") || playerDisplayName.length() == 0);

            if (!isUserImageUrlValid && userHasDisplayName)

                generateProfileImageAndSaveToFirebase();

            else

                 saveLoggedInUser();



        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }


    }

    private void generateProfileImageAndSaveToFirebase() {

        if (playerDisplayName.equals(""))
            playerDisplayName = "NO NAME";
        String displayName = playerDisplayName.toUpperCase();
        String[] displayNameParts = displayName.trim().split(" ");
        String initials = displayNameParts.length > 1 ? displayNameParts[0].substring(0, 1) + displayNameParts[1].substring(0, 1) : displayNameParts[0].substring(0, 1);
        Bitmap profilePicture = ImageHelper.generateInitialsBasedProfileImage(initials, context, playerColorId);
        byte[] profilePictureToSave = ImageHelper.getBytesFromBitmap(profilePicture);
        String imageString = Base64.encodeToString(profilePictureToSave, Base64.NO_WRAP);

        playerProfilePictureUrl = OffsideApplication.getInitialsProfilePictureUrl() + playerId;
        OffsideApplication.networkingService.requestSaveImageInDatabase(playerId, imageString);

        //update user image in firebase
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(playerProfilePictureUrl))
                .build();

        firebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    //Log.d(TAG, "User profile image updated.");
//                                }
                    }
                });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveSavedPlayerImage(PlayerImageSavedEvent playerImageSavedEvent) {
        try {
            saveLoggedInUser();

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }
    }

    public void saveLoggedInUser() {
        try {
            SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(getString(R.string.player_profile_picture_url_key), playerProfilePictureUrl);
            editor.commit();
            String playerColor;
            if (playerColorId == 0) {
                playerColorId = ImageHelper.getRandomColor();

            }
            playerEmail = firebaseUser.getEmail();
            playerColor = Formatter.colorNumberToHexaValue(context, playerColorId);
            User user = new User(playerId, playerDisplayName, playerEmail, playerProfilePictureUrl, playerColor, firebaseDeviceToken);
            OffsideApplication.networkingService.requestSaveLoggedInUser(user.getId(), user.getName(), user.getEmail(), user.getProfilePictureUri(), user.getUserColor(), user.getDeviceToken());


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivePlayerAssets(PlayerAssets playerAssets) {
        try {
            isInLoginProcess = false;
            if (playerAssets.getPlayerColor() == null || StringUtil.isBlank(playerAssets.getPlayerColor())) {
                playerColorId = ImageHelper.getRandomColor();
                String playerColor = Formatter.colorNumberToHexaValue(context, playerColorId);
                playerAssets.setPlayerColor(playerColor);

            }

            OffsideApplication.setPlayerAssets(playerAssets);

            //handle user display name
            playerDisplayName = firebaseUser.getDisplayName();
            playerEmail = firebaseUser.getEmail();

            if (firebaseUser.getDisplayName() == null || firebaseUser.getDisplayName().equals("")) {
                String dialogTitle = getString(R.string.lbl_set_player_nickname);
                String dialogInstructions = getString(R.string.lbl_pick_you_nickname);
                EventBus.getDefault().post(new EditValueEvent(dialogTitle, dialogInstructions, null, EditValueEvent.updatePlayerName));

            } else
                analyzeDynamicLink();

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }
    }


    public void analyzeDynamicLink() {
        try {
            //        Intent intent = getIntent();
//        String action = intent.getAction();
//        Uri data = intent.getData();
//        if(data==null){
//            startLobbyActivity();
//            return;
//        }
//        Log.d(TAG, "------action-----: " + action);
//        Log.d(TAG, "------data-----: " + (data != null ? data.toString() : "empty"));
//        ACRA.getErrorReporter().putCustomData("action_1", action);
//        if(data!=null){
//            ACRA.getErrorReporter().putCustomData("intentDataQuery_2", data.getQuery());
//            ACRA.getErrorReporter().putCustomData("intentDataEncodedPath_3", data.getEncodedPath());
//        }

            FirebaseDynamicLinks.getInstance()
                    .getDynamicLink(getIntent())
                    .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                        @Override
                        public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                            // Get deep link from result (may be null if no link is found)
                            Uri deepLink = null;
                            if (pendingDynamicLinkData != null) {
//                            ACRA.getErrorReporter().putCustomData("pendingDynamicLinkData_4", pendingDynamicLinkData.getLink().toString());
                                analytics = FirebaseAnalytics.getInstance(context);
                                deepLink = pendingDynamicLinkData.getLink();
//                            if(deepLink==null)
//                                Log.d(TAG, "*****deepLink***** = null");
//                            else
//                                Log.d(TAG, "*****deepLink*****"+deepLink.toString());
                                FirebaseAppInvite invite = FirebaseAppInvite.getInvitation(pendingDynamicLinkData);
                                if (invite != null) {
//                                ACRA.getErrorReporter().putCustomData("inviteId_5",invite.getInvitationId() );
//                                String inviteId = invite.getInvitationId();
//                                if(!TextUtils.isEmpty(inviteId))
//                                    Log.d(TAG, "ACCPET invitation Id" + inviteId);
                                    URL url;
                                    try {
                                        url = new URL("http", Uri.parse(deepLink.getQuery()).getHost(), deepLink.getQuery().toString());
                                        try {

//                                        ACRA.getErrorReporter().putCustomData("utl_6",url.toString() );
                                            Map<String, List<String>> dynamicLinkQueryPairs = HttpHelper.splitQuery(url);
                                            String groupIdFromInvitation = dynamicLinkQueryPairs.get("groupId").get(0);
                                            groupIdFromInvitation = groupIdFromInvitation.equalsIgnoreCase("null") ? null : groupIdFromInvitation;
                                            String gameIdFromInvitation = dynamicLinkQueryPairs.get("gameId").get(0);
                                            gameIdFromInvitation = gameIdFromInvitation.equalsIgnoreCase("null") ? null : gameIdFromInvitation;
                                            String privateGameIdFromInvitation = dynamicLinkQueryPairs.get("privateGameId").get(0);
                                            privateGameIdFromInvitation = privateGameIdFromInvitation.equalsIgnoreCase("null") ? null : privateGameIdFromInvitation;

//                                        ACRA.getErrorReporter().putCustomData("groupIdFromInvitation_6",groupIdFromInvitation );
//                                        ACRA.getErrorReporter().putCustomData("gameIdFromInvitation_7",gameIdFromInvitation );
//                                        ACRA.getErrorReporter().putCustomData("privateGameIdFromInvitation_8",privateGameIdFromInvitation );

                                            //Add player to the group from which he was invited
                                            if (groupIdFromInvitation == null)
                                                return;

                                            //Override userPreferences, as theses will be used when tryJoinSelectedAvailableGame will be executed (Lobby Activity)
                                            if (gameIdFromInvitation != null && privateGameIdFromInvitation != null)
                                                OffsideApplication.setUserPreferences(groupIdFromInvitation, gameIdFromInvitation, privateGameIdFromInvitation);

                                            OffsideApplication.networkingService.requestJoinPrivateGroup(playerId, groupIdFromInvitation);

                                        } catch (UnsupportedEncodingException ex) {
                                            ACRA.getErrorReporter().handleSilentException(ex);
                                        }

                                    } catch (MalformedURLException ex) {
                                        ACRA.getErrorReporter().handleSilentException(ex);
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

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayerJoinedPrivateGroup(PlayerJoinPrivateGroupEvent playerJoinPrivateGroupEvent) {
        try {
            if (!OffsideApplication.isLobbyActivityVisible())
                startLobbyActivity();

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }


    public void startLobbyActivity() {

        loadingRoot.setVisibility(View.GONE);

        //check if user clicked on notifcation
        Intent notificationIntent = getIntent();
        //Log.d(TAG,"notificationIntnet extra code: "+ notificationIntent.getStringExtra("code"));
        String privateGameCode = null;
        String action = null;
        if (notificationIntent != null){
            privateGameCode = notificationIntent.getStringExtra("code");
            action = notificationIntent.getStringExtra("action");
        }
        Intent intent = new Intent(context, LobbyActivity.class);
        if (privateGameCode != null)
            intent.putExtra("code", privateGameCode);
        if (action != null)
            intent.putExtra("action", action);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }

    //update player name in case of empty- phone login
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEditValueReceived(EditValueEvent editValueEvent) {

        try {
            //onLoadingEventReceived(new LoadingEvent(false, null));
            if (editValueEvent == null)
                return;

            String dialogTitle = editValueEvent.getDialogTitle();
            String dialogInstructions = editValueEvent.getDialogInstructions();
            String currentValue = editValueEvent.getCurrentValue();
            final String key = editValueEvent.getKey();

            editValueDialog = new Dialog(context);
            editValueDialog.setContentView(R.layout.dialog_edit_value);

            TextView titleTextView = editValueDialog.findViewById(R.id.dev_title_text_view);
            titleTextView.setText(dialogTitle);
            TextView instructionsTextView = editValueDialog.findViewById(R.id.dev_instructions_text_view);
            instructionsTextView.setText(dialogInstructions);
            final EditText newValueEditText = editValueDialog.findViewById(R.id.dev_new_value_edit_text);
            newValueEditText.setText(currentValue);
            final Button submitButton = editValueDialog.findViewById(R.id.dev_submit_button);

            newValueEditText.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (s.toString().trim().length() == 0) {
                        submitButton.setEnabled(false);
                    } else {
                        submitButton.setEnabled(true);
                    }

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    if (s.toString().trim().length() == 0) {
                        submitButton.setEnabled(false);
                    } else {
                        submitButton.setEnabled(true);
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().trim().length() == 0) {
                        submitButton.setEnabled(false);
                    } else {
                        submitButton.setEnabled(true);
                    }

                }
            });


            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String newValue = newValueEditText.getText().toString();
                    if (key == EditValueEvent.updatePlayerName) {

                        if (playerId == null)
                            return;
                        playerDisplayName = newValue;
                        OffsideApplication.networkingService.requestUpdatePlayerName(playerId, playerDisplayName);

                    }

                }
            });

            adjustDialogWidthToWindow(editValueDialog);
            editValueDialog.show();


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayerModelReceived(PlayerModelEvent playerModelEvent) {
        try {
            EventBus.getDefault().post(new LoadingEvent(false, null));
            PlayerModel updatedPlayer = playerModelEvent.getPlayerModel();
            if (updatedPlayer == null)
                return;

            if(editValueDialog!=null){
                editValueDialog.cancel();

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(updatedPlayer.getUserName())
                        .build();

                firebaseUser.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()) {
//                                Log.d(TAG, "User profile updated.");
//                            }
                            }
                        });

                generateProfileImageAndSaveToFirebase();
            }

            analyzeDynamicLink();


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    public void adjustDialogWidthToWindow(Dialog dialog) {

        try {
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = dialog.getWindow();
            lp.copyFrom(window.getAttributes());
            //This makes the dialog take up the full width
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }
}
