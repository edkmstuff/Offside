package com.offsidegame.offside.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.offsidegame.offside.R;
import com.offsidegame.offside.events.AvailableGamesEvent;
import com.offsidegame.offside.events.ConnectionEvent;
import com.offsidegame.offside.events.PrivateGameGeneratedEvent;
import com.offsidegame.offside.helpers.RoundImage;
import com.offsidegame.offside.helpers.SignalRService;
import com.offsidegame.offside.events.ActiveGameEvent;
import com.offsidegame.offside.events.JoinGameEvent;
import com.offsidegame.offside.events.SignalRServiceBoundEvent;
import com.offsidegame.offside.models.AvailableGame;
import com.offsidegame.offside.models.GameInfo;
import com.offsidegame.offside.models.OffsideApplication;
import com.squareup.picasso.Picasso;

import org.acra.ACRA;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;

public class JoinGameActivity extends AppCompatActivity implements Serializable {
    private String activityName = "JoinGameActivity";
    private final Context context = this;
    private SignalRService signalRService;
    private boolean isBoundToSignalRService = false;

    EditText gameCodeEditText;
    TextView join;
    //Button createGame;
    TextView userNameTextView;
    ImageView profilePicture;
    private LinearLayout joinGameRoot;
    private LinearLayout loadingGameRoot;

    private Toolbar toolbar;


    private TextView createPrivateGameButton;
    private LinearLayout createPrivateGameRoot;
    private Spinner availableGamesSpinner;
    private EditText privateGameNameEditText;
    private TextView generatePrivateGameCodeButton;
    private TextView privateGameCodeTextView;
    private String[] gameTitles;
    private String[] gameIds;
    private String userName;


    private final ServiceConnection signalRServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
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
            setContentView(R.layout.activity_join_game);

            toolbar = (Toolbar) findViewById((R.id.app_bar));
            setSupportActionBar(toolbar);

           // final SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
           // userName = settings.getString(getString(R.string.user_name_key), "");
            userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

            userNameTextView = (TextView) findViewById(R.id.join_game_user_name_text_view);
            userNameTextView.setText(userName);

            profilePicture = (ImageView) findViewById(R.id.userPictureImageView);
            //final String userPictureUrl = settings.getString(getString(R.string.user_profile_picture_url_key), "");
            final Uri userPictureUri = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();


            Picasso.with(context).load(userPictureUri).into(profilePicture, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    Bitmap bm = ((BitmapDrawable) profilePicture.getDrawable()).getBitmap();
                    RoundImage roundedImage = new RoundImage(bm);
                    profilePicture.setImageDrawable(roundedImage);
                }

                @Override
                public void onError() {
                    throw new RuntimeException(activityName + " - onCreate - Error loading image with url: " + userPictureUri != null ? userPictureUri.toString() : "null");
                }
            });


            gameCodeEditText = (EditText) findViewById(R.id.gameCode);
            join = (TextView) findViewById(R.id.join_text_view);

            join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String gameCodeString = gameCodeEditText.getText().toString();
                    joinGame(gameCodeString);
                }
            });


            joinGameRoot = (LinearLayout) findViewById(R.id.jg_join_game_root);
            loadingGameRoot = (LinearLayout) findViewById(R.id.jg_loading_root);


            createPrivateGameButton = (TextView) findViewById(R.id.jg_create_private_game_text_view);
            createPrivateGameRoot = (LinearLayout) findViewById(R.id.jg_create_private_game_root);
            availableGamesSpinner = (Spinner) findViewById(R.id.jg_available_games_spinner);
            privateGameNameEditText = (EditText) findViewById(R.id.jg_private_game_name_edit_text);
            generatePrivateGameCodeButton = (TextView) findViewById(R.id.jg_generate_private_game_code_text_view);
            privateGameCodeTextView = (TextView) findViewById(R.id.jg_private_game_code_text_view);

            loadingGameRoot.setVisibility(View.VISIBLE);
            joinGameRoot.setVisibility(View.GONE);
            createPrivateGameRoot.setVisibility(View.GONE);
            privateGameCodeTextView.setVisibility(View.GONE);

            createPrivateGameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isBoundToSignalRService)
                        signalRService.getAvailableGames();
                    else
                        throw new RuntimeException(activityName + " - createPrivateGameButton - onClick - Error: SignalRIsNotBound");

                    privateGameNameEditText.setText(userName.split(" ")[0] + "'s" + " gang");
                    joinGameRoot.setVisibility(View.GONE);
                    createPrivateGameRoot.setVisibility(View.VISIBLE);
                }
            });

            generatePrivateGameCodeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //get game id
                    int selectedGamePosition = availableGamesSpinner.getSelectedItemPosition();
                    String gameId = getGameId(selectedGamePosition);
                    //get group messageText
                    String groupName = privateGameNameEditText.getText().toString();

                    if (isBoundToSignalRService)
                        signalRService.generatePrivateGame(gameId, groupName);
                    else
                        throw new RuntimeException(activityName + " - generatePrivateGameCodeButton - onClick - Error: SignalRIsNotBound");



                }
            });


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleException(ex);
        }


    }

    private void joinGame( String privateGameCode) {
        if (isBoundToSignalRService) {

            OffsideApplication.setIsPlayerQuitGame(false);
            signalRService.joinGame(privateGameCode);
            loadingGameRoot.setVisibility(View.VISIBLE);
            joinGameRoot.setVisibility(View.GONE);
            createPrivateGameRoot.setVisibility(View.GONE);
        }
    }

    private String getGameId(int selectedGamePosition) {
        return gameIds[selectedGamePosition];
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
    public void onJoinGame(JoinGameEvent joinGameEvent) {
        try {

            GameInfo gameInfo = joinGameEvent.getGameInfo();
            if (gameInfo == null) {
                Toast.makeText(context, R.string.lbl_no_such_game, Toast.LENGTH_LONG).show();
                loadingGameRoot.setVisibility(View.GONE);
                joinGameRoot.setVisibility(View.VISIBLE);
                return;
            }
            String gameId = gameInfo.getGameId();
            String gameCode = gameInfo.getPrivateGameCode();
            String privateGameTitle = gameInfo.getPrivateGameTitle();
            String homeTeam = gameInfo.getHomeTeam();
            String awayTeam = gameInfo.getAwayTeam();
            int offsideCoins = gameInfo.getOffsideCoins();
            int balance = gameInfo.getBalance();
            int totalPlayers = gameInfo.getTotalPlayers();

            SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
            SharedPreferences.Editor editor = settings.edit();

            editor.putString(getString(R.string.game_id_key), gameId);
            editor.putString(getString(R.string.game_code_key), gameCode);
            editor.putString(getString(R.string.private_game_title_key), privateGameTitle);
            editor.putString(getString(R.string.home_team_key), homeTeam);
            editor.putString(getString(R.string.away_team_key), awayTeam);
            editor.putInt(getString(R.string.offside_coins_key), offsideCoins);
            editor.putInt(getString(R.string.balance_key), balance);
            editor.putInt(getString(R.string.total_players_key), totalPlayers);


            editor.commit();


            Intent intent = new Intent(context, ChatActivity.class);
            startActivity(intent);
        }
        catch(Exception ex){
            ACRA.getErrorReporter().handleException(ex);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionEvent(ConnectionEvent connectionEvent) {
        try {
            boolean isConnected = connectionEvent.getConnected();
            if (isConnected)
                Toast.makeText(context, R.string.lbl_you_are_connected, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, R.string.lbl_you_are_disconnected, Toast.LENGTH_SHORT).show();
        }
        catch(Exception ex){
            ACRA.getErrorReporter().handleException(ex);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSignalRServiceBinding(SignalRServiceBoundEvent signalRServiceBoundEvent) {
        try {
            Context eventContext = signalRServiceBoundEvent.getContext();


            if (eventContext == null) {
                Intent intent = new Intent(context, JoinGameActivity.class);
                context.startActivity(intent);
                return;
            }

            if (eventContext == context) {
                if (OffsideApplication.isPlayerQuitGame()) {
                    loadingGameRoot.setVisibility(View.GONE);
                    joinGameRoot.setVisibility(View.VISIBLE);
                    return;
                }


                Intent intent = getIntent();
                String gameCodeFromNotification = "";
                //String gameCodeFromNotification = intent.getExtras().getString("gameCodeFromNotification");
                if (!(gameCodeFromNotification.equals("") || gameCodeFromNotification == null)) {
                    String gameCodeString = gameCodeFromNotification.toString();
                    signalRService.joinGame(gameCodeString);
                    loadingGameRoot.setVisibility(View.VISIBLE);
                    joinGameRoot.setVisibility(View.GONE);
                } else {
                    SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
                    String gameId = settings.getString(getString(R.string.game_id_key), "");
                    String gameCode = settings.getString(getString(R.string.game_code_key), "");
                    signalRService.isGameActive(gameId, gameCode);

                }


            }
        }
        catch (Exception ex){
            ACRA.getErrorReporter().handleException(ex);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveIsGameActive(ActiveGameEvent activeGameEvent) {
        try {
            Boolean isGameActive = activeGameEvent.getIsGameActive();

            if (isGameActive) {
                //Intent intent = new Intent(context, ViewPlayerScoreActivity.class);
                SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
                String gameCode = settings.getString(getString(R.string.game_code_key), "");
                signalRService.joinGame(gameCode);

            } else {
                loadingGameRoot.setVisibility(View.GONE);
                joinGameRoot.setVisibility(View.VISIBLE);
            }
        }
        catch (Exception ex){
            ACRA.getErrorReporter().handleException(ex);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveAvailableGames(AvailableGamesEvent availableGamesEvent) {
        try {
            AvailableGame[] availableGames = availableGamesEvent.getAvailableGames();
            if (availableGames.length == 0)
                throw new Exception(activityName+ " - onReceiveAvailableGames - Error: available games is empty ");

            gameTitles = new String[availableGames.length];
            gameIds = new String[availableGames.length];
            for (int i = 0; i < availableGames.length; i++) {
                gameIds[i] = availableGames[i].getGameId();
                gameTitles[i] = availableGames[i].getGameTitle();
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, gameTitles);
// Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
            availableGamesSpinner.setAdapter(adapter);
            availableGamesSpinner.setSelection(0);
        }
        catch (Exception ex){
            ACRA.getErrorReporter().handleException(ex);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPrivateGameGenerated(PrivateGameGeneratedEvent privateGameGeneratedEvent) {
        try {
            String privateGameCode = privateGameGeneratedEvent.getPrivateGameCode();
            joinGame(privateGameCode);
            //privateGameCodeTextView.setText(privateGameCode);
            //gameCodeEditText.setText(privateGameCode);
            //privateGameCodeTextView.setVisibility(View.VISIBLE);

        }
        catch (Exception ex){
            ACRA.getErrorReporter().handleException(ex);
        }

    }


}
