package com.offsidegame.offside.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.offsidegame.offside.R;
import com.offsidegame.offside.events.AvailableGameEvent;
import com.offsidegame.offside.events.ConnectionEvent;
import com.offsidegame.offside.events.FriendInviteReceivedEvent;
import com.offsidegame.offside.events.GroupInviteEvent;
import com.offsidegame.offside.events.JoinGameEvent;
import com.offsidegame.offside.events.NavigationEvent;
import com.offsidegame.offside.events.NotificationBubbleEvent;
import com.offsidegame.offside.events.PrivateGroupChangedEvent;
import com.offsidegame.offside.events.PrivateGroupEvent;
import com.offsidegame.offside.events.SignalRServiceBoundEvent;
import com.offsidegame.offside.fragments.ChatFragment;
import com.offsidegame.offside.fragments.GroupsFragment;
import com.offsidegame.offside.fragments.NewsFragment;
import com.offsidegame.offside.fragments.PlayerFragment;
import com.offsidegame.offside.fragments.SettingsFragment;
import com.offsidegame.offside.fragments.ShopFragment;
import com.offsidegame.offside.fragments.SingleGroupFragment;
import com.offsidegame.offside.helpers.ImageHelper;
import com.offsidegame.offside.models.AvailableGame;
import com.offsidegame.offside.models.GameInfo;
import com.offsidegame.offside.models.OffsideApplication;
import com.offsidegame.offside.models.PlayerAssets;
import com.offsidegame.offside.models.PrivateGroup;

import org.acra.ACRA;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;


public class LobbyActivity extends AppCompatActivity implements Serializable {

    //<editor-fold desc="*****************MEMBERS****************">

    //activity
    private final String activityName = "LobbyActivity";
    private final Context context = this;
    private final Activity thisActivity = this;

    private AHBottomNavigation bottomNavigation;

    private ImageView settingsButtonImageView;

    //playerAssets
    private LinearLayout playerInfoRoot;
    private LinearLayout balanceRoot;
    private TextView balanceTextView;
    private TextView powerItemsTextView;


    //profile
    private ImageView playerPictureImageView;
    private String playerId;
    private String playerProfilePictureUrl;
    private SharedPreferences settings;

    //using fragments
    private GroupsFragment groupsFragment;
    private PlayerFragment playerFragment;
    private SingleGroupFragment singleGroupFragment;
    private ChatFragment chatFragment;
    private ShopFragment shopFragment;
    private NewsFragment newsFragment;
    private SettingsFragment settingsFragment;
    private int chatNavigationItemNotificationCount =0;


    //</editor-fold>


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_lobby);

            settings = getSharedPreferences(getString(R.string.preference_name), 0);

            FirebaseUser player = FirebaseAuth.getInstance().getCurrentUser();

            playerId = player.getUid();

            getIDs();
            setEvents();
            createNavigationMenu();
            togglePlayerAssetsVisibility(true);


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }


    }

    private void getIDs() {


        playerInfoRoot = (LinearLayout) findViewById(R.id.l_player_info_root);

        playerPictureImageView = (ImageView) findViewById(R.id.l_player_picture_image_view);
        balanceRoot = (LinearLayout) findViewById(R.id.l_balance_root);
        balanceTextView = (TextView) findViewById(R.id.l_balance_text_view);
        powerItemsTextView = (TextView) findViewById(R.id.l_power_items_text_view);

        //createPrivateGroupButtonTextView = (TextView) findViewById(R.id.l_create_private_group_button_text_view);

        settingsButtonImageView = findViewById(R.id.l_settings_button_image_view);
        bottomNavigation = findViewById(R.id.l_bottom_navigation);

    }

    private void setEvents() {


        playerPictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new NavigationEvent(R.id.nav_action_profile));
            }
        });

        balanceRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new NavigationEvent(R.id.nav_action_shop));

            }
        });

        settingsButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                settingsFragment = SettingsFragment.newInstance();
                replaceFragment(settingsFragment);
                togglePlayerAssetsVisibility(true);
            }
        });

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

                try {
                    if (wasSelected){
                        return true;
                    }

                    switch (position) {
                        case 0:

                            groupsFragment = GroupsFragment.newInstance();
                            replaceFragment(groupsFragment);
                            togglePlayerAssetsVisibility(true);
                            return true;

                        case 1:
                            playerFragment = PlayerFragment.newInstance();
                            replaceFragment(playerFragment);
                            togglePlayerAssetsVisibility(true);
                            return true;

                        case 2:
                            chatFragment = ChatFragment.newInstance();
                            replaceFragment(chatFragment);
                            togglePlayerAssetsVisibility(false);

                            // remove notification badge
                            bottomNavigation.setNotification(new AHNotification(), position);
                            chatNavigationItemNotificationCount =0;

                            return true;

                        case 3:
                            newsFragment = NewsFragment.newInstance();
                            replaceFragment(newsFragment);
                            togglePlayerAssetsVisibility(false);
                            return true;

                        case 4:
                            shopFragment = ShopFragment.newInstance();
                            replaceFragment(shopFragment);
                            togglePlayerAssetsVisibility(true);
                            return true;


                    }


                } catch (Exception ex) {
                    ACRA.getErrorReporter().handleSilentException(ex);
                    return false;

                }
                return true;
            }
        });
        bottomNavigation.setOnNavigationPositionListener(new AHBottomNavigation.OnNavigationPositionListener() {
            @Override
            public void onPositionChange(int position) {
                Log.d("SIDEKICK_GAME", "onNavigationPositionListenre");


            }
        });
    }

    private void createNavigationMenu() {

        int[] navigationItemsIds = {R.id.nav_action_groups, R.id.nav_action_profile, R.id.nav_action_play, R.id.nav_action_news, R.id.nav_action_shop};
        AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.bottom_navigation_menu);
        navigationAdapter.setupWithBottomNavigation(bottomNavigation, navigationItemsIds);

        // Set current item programmatically
        bottomNavigation.setCurrentItem(1);

        // Set background color
        bottomNavigation.setDefaultBackgroundColor(ContextCompat.getColor(context, R.color.navigationMenu));

        // Change colors
        bottomNavigation.setAccentColor(ContextCompat.getColor(context, R.color.navigationMenuSelectedItem));
        bottomNavigation.setInactiveColor(ContextCompat.getColor(context, R.color.navigationMenuUnSelectedItem));

        bottomNavigation.setBehaviorTranslationEnabled(true);


        // Force to tint the drawable (useful for font with icon for example)
        bottomNavigation.setForceTint(false);

        // Display color under navigation bar (API 21+)
        // Don't forget these lines in your style-v21
        // <item name="android:windowTranslucentNavigation">true</item>
        // <item name="android:fitsSystemWindows">true</item>
        //bottomNavigation.setTranslucentNavigationEnabled(true);

        // Manage titles
        //bottomNavigation.setTitleState(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE);
        //bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_HIDE);

        // Use colored navigation with circle reveal effect
        bottomNavigation.setColored(false);

        // Customize notification (title, background, typeface)
        bottomNavigation.setNotificationBackgroundColor(Color.parseColor("#F63D2B"));

        // Add or remove notification for each item
        //bottomNavigation.setNotification("5", 2);
        // OR
        // AHNotification notification = new AHNotification.Builder()
        //.setText("1")
        //.setBackgroundColor(ContextCompat.getColor(DemoActivity.this, R.color.color_notification_back))
        //.setTextColor(ContextCompat.getColor(DemoActivity.this, R.color.color_notification_text))
        //.build();
        //bottomNavigation.setNotification(notification, 1);

    // Enable / disable item & set disable color
    // bottomNavigation.enableItemAtPosition(2);
    // bottomNavigation.disableItemAtPosition(2);
    // bottomNavigation.setItemDisableColor(Color.parseColor("#3A000000"));

    }

    private void togglePlayerAssetsVisibility(boolean isVisible) {
        if (isVisible)
            playerInfoRoot.setVisibility(View.VISIBLE);
        else
            playerInfoRoot.setVisibility(View.GONE);
    }


    private void replaceFragment(Fragment fragment) {

        try {
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.l_fragment_container_root, fragment, fragment.getTag()).commit();

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }


    }

    @Override
    public void onResume() {

        super.onResume();
        OffsideApplication.setIsLobbyActivityVisible(true);

        EventBus.getDefault().post(new SignalRServiceBoundEvent(context));
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(context);

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(context);

    }

    //<editor-fold desc="Eventbus subscriptions">

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSignalRServiceBinding(SignalRServiceBoundEvent signalRServiceBoundEvent) {
        try {
            if (OffsideApplication.signalRService == null)
                return;

            Context eventContext = signalRServiceBoundEvent.getContext();
            if (eventContext == context) {

                if (OffsideApplication.isBoundToSignalRService) {
                    PlayerAssets playerAssets = OffsideApplication.getPlayerAssets();
                    if (playerAssets == null)
                        OffsideApplication.signalRService.requestPlayerAssets(playerId);
                    else
                        onReceivePlayerAssets(playerAssets);

                } else
                    throw new RuntimeException(activityName + " - onSignalRServiceBinding - Error: SignalRIsNotBound");

//                String[] emptyAvailableGames = new String[]{getString(R.string.lbl_no_available_games)};
///;                setAvailableGamesSpinnerAdapter(emptyAvailableGames);

            }

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivePlayerAssets(PlayerAssets playerAssets) {

        try {
            if (playerAssets == null)
                return;

            OffsideApplication.setPlayerAssets(playerAssets);

            //update player stuff
            int balance = playerAssets.getBalance();
            int powerItems = playerAssets.getPowerItems();
            playerProfilePictureUrl = playerAssets.getImageUrl();
            balanceTextView.setText(Integer.toString(balance));
            powerItemsTextView.setText(Integer.toString(powerItems));

            ImageHelper.loadImage(thisActivity, playerProfilePictureUrl, playerPictureImageView, activityName, true);

            playerInfoRoot.setVisibility(View.VISIBLE);

            //check if player is already playing
            String lastKnownGameId = settings.getString(getString(R.string.game_id_key), null);
            String lastKnownPrivateGameId = settings.getString(getString(R.string.private_game_id_key), null);
            String playerId = OffsideApplication.getPlayerId();
            boolean userRequiresRejoin = (lastKnownGameId != null && lastKnownPrivateGameId!=null && playerId !=null);

            Intent intent = getIntent();
            boolean showGroups = intent.getBooleanExtra("showGroups", false);
            boolean showNewsFeed = OffsideApplication.isBackFromNewsFeed();
            if (showGroups || !userRequiresRejoin) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(new NavigationEvent(R.id.nav_action_groups));
                    }
                }, 500);

            } else if (showNewsFeed) {
                OffsideApplication.setIsBackFromNewsFeed(false);
            } else {
                tryRejoinGameForReturningPlayer(playerId, lastKnownGameId, lastKnownPrivateGameId);
            }


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    public void tryRejoinGameForReturningPlayer(String playerId, String lastKnownGameId, String lastKnownPrivateGameId) {

        OffsideApplication.setSelectedPrivateGameId(lastKnownPrivateGameId);

        OffsideApplication.signalRService.requestAvailableGame(playerId, lastKnownGameId, lastKnownPrivateGameId);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveAvailableGame(AvailableGameEvent availableGameEvent) {
        try {
            AvailableGame availableGame = availableGameEvent.getAvailableGame();
            if (availableGame != null) {
                String groupId = availableGame.getGroupId();
                OffsideApplication.signalRService.requestPrivateGroup(playerId, groupId);
                OffsideApplication.setSelectedAvailableGame(availableGame);
            } else {
                EventBus.getDefault().post(new NavigationEvent(R.id.nav_action_groups));

            }


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivePrivateGroup(PrivateGroupEvent privateGroupEvent) {
        try {
            if (privateGroupEvent == null)
                return;

            PrivateGroup privateGroup = privateGroupEvent.getPrivateGroup();
            OffsideApplication.setSelectedPrivateGroup(privateGroup);
            EventBus.getDefault().post(new NavigationEvent(R.id.nav_action_play));

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivePrivateGroupChanged(PrivateGroupChangedEvent privateGroupChangedEvent) {
        try {
            if (privateGroupChangedEvent == null)
                return;

            PrivateGroup updatedPrivateGroup = privateGroupChangedEvent.getPrivateGroup();

            OffsideApplication.getPrivateGroupsInfo().replace(updatedPrivateGroup);


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveSelectedPrivateGroup(PrivateGroup privateGroup) {
        try {
            if (privateGroup == null || privateGroup.getId() == null)
                return;

            singleGroupFragment = SingleGroupFragment.newInstance();
            replaceFragment(singleGroupFragment);

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveNavigation(NavigationEvent navigationEvent) {
        try {

            bottomNavigation.setCurrentItem(navigationEvent.getNavigationItemId());


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
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
        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayerJoinedPrivateGame(JoinGameEvent joinGameEvent) {
        GameInfo gameInfo = joinGameEvent.getGameInfo();
        if (gameInfo != null) {
            return;
        }

        onReceiveNavigation(new NavigationEvent(R.id.nav_action_play));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGroupInvite(GroupInviteEvent groupInviteEvent) {

        String groupId = groupInviteEvent.getGroupId();
        String gameId = groupInviteEvent.getGameId();
        String privateGameId = groupInviteEvent.getPrivateGamaId();
        String playerId = groupInviteEvent.getInviterPlayerId();

        OffsideApplication.signalRService.requestInviteFriend(playerId, groupId, gameId, privateGameId);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFriendInviteReceived(FriendInviteReceivedEvent friendInviteReceivedEvent) {

        String invitationUrl = friendInviteReceivedEvent.getInvitationUrl();
        String inviterName = OffsideApplication.getPlayerAssets().getPlayerName();

        String shareMessage = String.format("%s invited you to join a group in SideKick. Click to join %s", inviterName, invitationUrl);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);


        startActivity(Intent.createChooser(sendIntent, "Invite friendS"));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotificationBubbleReceived(NotificationBubbleEvent notificationBubbleEvent) {
        if(bottomNavigation.getCurrentItem()==2)
            return;
        String notificationBubbleName = notificationBubbleEvent.getNavigationItemName();
        if(notificationBubbleName.equalsIgnoreCase(NotificationBubbleEvent.navigationItemChat)){
            chatNavigationItemNotificationCount +=1;
            bottomNavigation.setNotification(String.format("%d",chatNavigationItemNotificationCount),2);
        }

    }



    //</editor-fold>

    @Override
    public void onBackPressed() {
        if (bottomNavigation.getCurrentItem() == 0) //groups
            finish();
        else
            EventBus.getDefault().post(new NavigationEvent(R.id.nav_action_groups));

    }


}
