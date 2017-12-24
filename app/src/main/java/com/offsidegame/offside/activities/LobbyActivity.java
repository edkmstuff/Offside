package com.offsidegame.offside.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.RewardedVideoListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.offsidegame.offside.R;
import com.offsidegame.offside.events.AvailableGameEvent;
import com.offsidegame.offside.events.CannotJoinPrivateGameEvent;
import com.offsidegame.offside.events.ConnectionEvent;
import com.offsidegame.offside.events.EditValueEvent;
import com.offsidegame.offside.events.GroupInviteEvent;
import com.offsidegame.offside.events.JoinGameEvent;
import com.offsidegame.offside.events.JoinGameWithCodeEvent;
import com.offsidegame.offside.events.LoadingEvent;
import com.offsidegame.offside.events.NavigationEvent;
import com.offsidegame.offside.events.NetworkingErrorEvent;
import com.offsidegame.offside.events.NetworkingServiceBoundEvent;
import com.offsidegame.offside.events.NotEnoughAssetsEvent;
import com.offsidegame.offside.events.NotificationBubbleEvent;
import com.offsidegame.offside.events.PlayerModelEvent;
import com.offsidegame.offside.events.PlayerQuitFromPrivateGameEvent;
import com.offsidegame.offside.events.PlayerRewardedReceivedEvent;
import com.offsidegame.offside.events.PlayerSettingsChangedEvent;
import com.offsidegame.offside.events.PrivateGameGeneratedEvent;
import com.offsidegame.offside.events.PrivateGroupChangedEvent;
import com.offsidegame.offside.events.PrivateGroupEvent;
import com.offsidegame.offside.events.PrivateGroupUpdatedEvent;
import com.offsidegame.offside.fragments.ChatFragment;
import com.offsidegame.offside.fragments.GroupsFragment;
import com.offsidegame.offside.fragments.LuckyWheelFragment;
import com.offsidegame.offside.fragments.NewsFragment;
import com.offsidegame.offside.fragments.PlayerFragment;
import com.offsidegame.offside.fragments.SettingsFragment;
import com.offsidegame.offside.fragments.ShopFragment;
import com.offsidegame.offside.fragments.SingleGroupFragment;
import com.offsidegame.offside.helpers.Formatter;
import com.offsidegame.offside.helpers.ImageHelper;
import com.offsidegame.offside.models.AvailableGame;
import com.offsidegame.offside.models.GameInfo;
import com.offsidegame.offside.models.OffsideApplication;
import com.offsidegame.offside.models.PlayerAssets;
import com.offsidegame.offside.models.PlayerModel;
import com.offsidegame.offside.models.PrivateGameInfo;
import com.offsidegame.offside.models.PrivateGroup;
import com.offsidegame.offside.models.PrivateGroupsInfo;

import org.acra.ACRA;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;


public class LobbyActivity extends AppCompatActivity implements Serializable {

    //<editor-fold desc="*****************MEMBERS****************">

    //<editor-fold desc="*****************PRIVATE MEMBERS****************">

    //activity
    private final String activityName = "LobbyActivity";
    private final Context context = this;
    private final Activity thisActivity = this;
    private String androidDeviceId;
    private BottomNavigationViewEx bottomNavigation;
    private ImageView settingsButtonImageView;
    private LinearLayout loadingRoot;
    private TextView loadingMessageTextView;

    //playerAssets
    private LinearLayout playerInfoRoot;
    private LinearLayout balanceRoot;
    private TextView balanceTextView;
    private LinearLayout powerItemsRoot;
    private TextView powerItemsTextView;

    //Recent state
    private String currentGameId;
    private String currentPrivateGameId;


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
    Fragment luckyWheelFragment;
    private int chatNavigationItemNotificationCount = 0;

    private Badge qBadgeView = null;

    private int REQUEST_INVITE = 100;
    private String TAG = "SIDEKICK";

    //reward dialog
    private Dialog rewardDialog;

    private TextView dialogueRewardValueTextView;
    private ImageView rewardImageView;
    private Button dialogueCloseButton;

    //short in assets dialogue
    private Dialog shortInAssetsDialog;
    private LinearLayout dialogCoinsTitleRoot;
    private LinearLayout dialogPowerItemsTitleRoot;

    private Button notEnoughAssetsDialogueCloseButton;
    private boolean isPlayerCanJoinPrivateGame = false;

    private LinearLayout getAssetsShopRoot;
    private LinearLayout getAssetsWatchVideoRoot;
    private LinearLayout getAssetsSlotMachineRoot;


    private boolean isGroupInviteExecuted;
    private boolean isInGameInvite = false;
    private boolean doWhereToGoNext = true;

    //join with code dialog
    private Dialog joinGameWithCodeDialog;
    private Button dialogJoinGameButton;
    private EditText dialogJoinGamePrivateGameCodeEditText;

    //edit value dialog
    private Dialog editValueDialog;

    //</editor-fold>

    //<editor-fold desc="ironSource">
    private RewardedVideoListener rewardedVideoListener = new RewardedVideoListener() {
        private String rewardName;
        private int rewardAmount;
        private String rewardType;
        private String rewardReason = "WATCH_VIDEO";
        @Override
        public void onRewardedVideoAdOpened() {

        }

        @Override
        public void onRewardedVideoAdClosed() {
            if(rewardAmount>0 && rewardName!=null)
                OffsideApplication.networkingService.requestToRewardPlayer(playerId, rewardType, rewardReason, rewardAmount);
            doWhereToGoNext = false;

        }

        @Override
        public void onRewardedVideoAvailabilityChanged(boolean b) {

        }

        @Override
        public void onRewardedVideoAdStarted() {
            Log.d(TAG, "onRewardedVideoAdStarted() called");

        }

        @Override
        public void onRewardedVideoAdEnded() {


        }

        @Override
        public void onRewardedVideoAdRewarded(Placement placement) {
            //Log.d(TAG, "onRewardedVideoAdRewarded: " + placement);
            rewardName = placement.getRewardName();
            rewardAmount = placement.getRewardAmount();
            rewardType=null;
            if(rewardName.equalsIgnoreCase("Coin")){
                rewardType = OffsideApplication.COINS;
            }
            else if(rewardName.equalsIgnoreCase("PowerItem")){
                rewardType = OffsideApplication.POWER_ITEMS;
            }

            doWhereToGoNext = false;

        }

        @Override
        public void onRewardedVideoAdShowFailed(IronSourceError ironSourceError) {
            doWhereToGoNext = false;
        }

        @Override
        public void onRewardedVideoAdClicked(Placement placement) {
            doWhereToGoNext = false;
        }
    };
    //</editor-fold>


    //</editor-fold>


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_lobby);

            settings = getSharedPreferences(getString(R.string.preference_name), 0);
            androidDeviceId = OffsideApplication.getAndroidDeviceId();

            FirebaseUser player = FirebaseAuth.getInstance().getCurrentUser();

            playerId = player.getUid();

            getIDs();
            setEvents();
            EventBus.getDefault().post(new LoadingEvent(true, "Starting"));

            togglePlayerAssetsVisibility(true);

            //Rewarded Video
            IronSource.init(this, "6aa86bd5", IronSource.AD_UNIT.REWARDED_VIDEO);

            //Init Offerwall
            //IronSource.init(this, "6aa86bd5", IronSource.AD_UNIT.OFFERWALL);

            //only run on development to verify if Ads-Network are configured correctly on IronSOurce management UI
            //IntegrationHelper.validateIntegration(this);


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }


    }

    private void getIDs() {

        playerInfoRoot = (LinearLayout) findViewById(R.id.l_player_info_root);

        playerPictureImageView = (ImageView) findViewById(R.id.l_player_picture_image_view);
        balanceRoot = (LinearLayout) findViewById(R.id.l_balance_root);
        powerItemsRoot =  findViewById(R.id.l_power_items_root);
        balanceTextView = (TextView) findViewById(R.id.l_balance_text_view);
        powerItemsTextView = (TextView) findViewById(R.id.l_power_items_text_view);

        settingsButtonImageView = findViewById(R.id.l_settings_button_image_view);
        loadingRoot = findViewById(R.id.shared_loading_root);
        loadingMessageTextView = findViewById(R.id.shared_loading_message_text_view);
        bottomNavigation = findViewById(R.id.l_bottom_navigation);

        bottomNavigation.enableAnimation(false);
        bottomNavigation.enableShiftingMode(false);
        bottomNavigation.enableItemShiftingMode(false);
        bottomNavigation.setTextVisibility(false);
        int iconSize = 36;
        bottomNavigation.setIconSize(iconSize, iconSize);
        bottomNavigation.setItemHeight(BottomNavigationViewEx.dp2px(this, iconSize + 16));


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
                onNotEnoughAssetsEventReceived(new NotEnoughAssetsEvent(0, 1, OffsideApplication.COINS, true));


            }
        });

        powerItemsRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNotEnoughAssetsEventReceived(new NotEnoughAssetsEvent(0, 1,OffsideApplication.POWER_ITEMS, true));


            }
        });

        settingsButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                toggleNavigationMenuVisibility(true);
//                bottomNavigation.setCurrentItem(0);

                settingsFragment = SettingsFragment.newInstance();
                replaceFragment(settingsFragment);
                togglePlayerAssetsVisibility(true);
            }
        });


        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                try {

                    for (int i = 0; i < bottomNavigation.getItemCount(); i++) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            bottomNavigation.setIconTintList(i, getResources().getColorStateList(R.color.colorWhiteSemiTransparent, context.getTheme()));
                        } else {
                            bottomNavigation.setIconTintList(i, getResources().getColorStateList(R.color.colorWhiteSemiTransparent));
                        }


                    }

                    int itemPosition = bottomNavigation.getMenuItemPosition(item);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        bottomNavigation.setIconTintList(itemPosition, getResources().getColorStateList(R.color.colorWhite, context.getTheme()));
                    } else {
                        bottomNavigation.setIconTintList(itemPosition, getResources().getColorStateList(R.color.colorWhite));
                    }

                    switch (item.getItemId()) {
                        case R.id.nav_action_groups:

                            groupsFragment = GroupsFragment.newInstance();
                            replaceFragment(groupsFragment);
                            togglePlayerAssetsVisibility(true);
                            toggleNavigationMenuVisibility(true);
                            return true;

                        case R.id.nav_action_profile:

                            playerFragment = PlayerFragment.newInstance();
                            replaceFragment(playerFragment);
                            togglePlayerAssetsVisibility(true);
                            toggleNavigationMenuVisibility(true);
                            return true;

                        case R.id.nav_action_play:
                            //bottomNavigation.setItemBackground(itemPosition, R.color.navigationMenuSelectedItem);
                            PrivateGroup selectedGroup = OffsideApplication.getSelectedPrivateGroup();
                            if (selectedGroup == null) {
                                EventBus.getDefault().post(new CannotJoinPrivateGameEvent(R.string.lbl_no_group_selected));
                                return true;
                            }

                            if (isPlayerCanJoinPrivateGame) {
                                isPlayerCanJoinPrivateGame = false;
                                if (qBadgeView != null)
                                    qBadgeView.hide(true);
                                chatNavigationItemNotificationCount = 0;
                                chatFragment = ChatFragment.newInstance();
                                replaceFragment(chatFragment);
                                togglePlayerAssetsVisibility(false);
                                toggleNavigationMenuVisibility(false);
                                return true;

                            } else {
                                whereToGoNext();
                                //EventBus.getDefault().post(new CannotJoinPrivateGameEvent(R.string.lbl_no_active_private_game));
                                return true;
                            }


                        case R.id.nav_action_news:
                            //bottomNavigation.setItemBackground(itemPosition, R.color.navigationMenuSelectedItem);
                            newsFragment = NewsFragment.newInstance();
                            replaceFragment(newsFragment);
                            togglePlayerAssetsVisibility(false);
                            toggleNavigationMenuVisibility(true);
                            return true;

                        case R.id.nav_action_shop:
                            //bottomNavigation.setItemBackground(itemPosition, R.color.navigationMenuSelectedItem);
                            shopFragment = ShopFragment.newInstance();
                            replaceFragment(shopFragment);
                            togglePlayerAssetsVisibility(true);
                            toggleNavigationMenuVisibility(true);
                            return true;

                    }


                } catch (Exception ex) {
                    ACRA.getErrorReporter().handleSilentException(ex);
                    return false;

                }
                return true;
            }
        });

        IronSource.setRewardedVideoListener(rewardedVideoListener);
    }

    private void togglePlayerAssetsVisibility(boolean isVisible) {
        if (isVisible)
            playerInfoRoot.setVisibility(View.VISIBLE);
        else
            playerInfoRoot.setVisibility(View.GONE);
    }

    private void toggleNavigationMenuVisibility(boolean isVisible) {
        if (isVisible)
            bottomNavigation.setVisibility(View.VISIBLE);
        else
            bottomNavigation.setVisibility(View.GONE);
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

        try {
            super.onResume();
            IronSource.onResume(this);
            OffsideApplication.setIsLobbyActivityVisible(true);

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
    public void onPause() {
        try {
            super.onPause();
            IronSource.onPause(this);
            OffsideApplication.setIsLobbyActivityVisible(false);

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }


    }

    @Override
    public void onStop() {

        try {

            super.onStop();
            EventBus.getDefault().unregister(context);
        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }


    }

    //<editor-fold desc="Eventbus subscriptions">

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkingServiceBinding(NetworkingServiceBoundEvent networkingServiceBoundEvent) {
        try {
            if (OffsideApplication.networkingService == null)
                return;

            Context eventContext = networkingServiceBoundEvent.getContext();
            if (eventContext == context) {
                PlayerAssets playerAssets = OffsideApplication.getPlayerAssets();

                if (playerAssets == null) {
                    OffsideApplication.networkingService.requestPlayerAssets(playerId);

                } else
                    onReceivePlayerAssets(playerAssets);
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

            updatePlayerAssets(playerAssets);

            Intent intent = getIntent();
            String privateGameCode=null;
            if(intent!=null)
                privateGameCode= intent.getStringExtra("code");
            if(privateGameCode!=null)
                OffsideApplication.networkingService.RequestPrivateGameInfoByCode(playerId, privateGameCode);

            else if (!isGroupInviteExecuted && doWhereToGoNext)
                whereToGoNext();
            else
                EventBus.getDefault().post(new LoadingEvent(false, null));

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    public void updatePlayerAssets(PlayerAssets playerAssets) {

        try {
            int currentPowerItems=0,currentBalance =0;

            if(playerAssets==null)
                return;

            if(OffsideApplication.getPlayerAssets()!=null){
                currentPowerItems = OffsideApplication.getPlayerAssets().getPowerItems();
                currentBalance = OffsideApplication.getPlayerAssets().getBalance();
            }

            OffsideApplication.setPlayerAssets(playerAssets);

            //update player stuff
            int balance = playerAssets.getBalance();
            int powerItems = playerAssets.getPowerItems();
            playerProfilePictureUrl = playerAssets.getImageUrl();

            String formattedBalance = Formatter.formatNumber(balance, Formatter.intCommaSeparator);
            balanceTextView.setText(formattedBalance);
            String formattedPowerItems = Formatter.formatNumber(powerItems, Formatter.intCommaSeparator);
            powerItemsTextView.setText(formattedPowerItems);

            ImageHelper.loadImage(thisActivity, playerProfilePictureUrl, playerPictureImageView, activityName, true);

            if (!isInGameInvite) {
                playerInfoRoot.setVisibility(View.VISIBLE);

            }
            if(currentBalance!=balance)
                YoYo.with(Techniques.StandUp.Bounce).duration(1000).playOn(balanceRoot);
            if(currentPowerItems!=powerItems)
                YoYo.with(Techniques.StandUp.Bounce).duration(1000).playOn(powerItemsRoot);

            //check balance
            int minRequiredBalance = OffsideApplication.getMinRequiredBalance();

            boolean isLowCoinsInventory = balance < minRequiredBalance;

            if (isLowCoinsInventory) {
                EventBus.getDefault().post(new NotEnoughAssetsEvent(balance, minRequiredBalance,OffsideApplication.COINS, true ));

            }


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }


    }

    public void whereToGoNext() {
        try {
            //check if player is already playing
            String lastKnownGameId = settings.getString(getString(R.string.game_id_key), null);
            String lastKnownPrivateGameId = settings.getString(getString(R.string.private_game_id_key), null);

            boolean userRequiresRejoin = (lastKnownGameId != null && lastKnownPrivateGameId != null && playerId != null);

            Intent intent = getIntent();
            boolean showGroups = intent.getBooleanExtra("showGroups", false);
            boolean showNewsFeed = OffsideApplication.isBackFromNewsFeed();
            boolean isBackFromCreatePrivateGroup = OffsideApplication.isIsBackFromCreatePrivateGroup();

            if (showGroups || !userRequiresRejoin) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(new NavigationEvent(R.id.nav_action_groups));
                        EventBus.getDefault().post(new LoadingEvent(false, null));
                    }
                }, 500);

            } else if (showNewsFeed) {
                OffsideApplication.setIsBackFromNewsFeed(false);
                EventBus.getDefault().post(new NavigationEvent(R.id.nav_action_news));
                EventBus.getDefault().post(new LoadingEvent(false, null));

            } else if (isBackFromCreatePrivateGroup) {
                OffsideApplication.setIsBackFromCreatePrivateGroup(false);
                EventBus.getDefault().post(new LoadingEvent(false, null));
            } else {
                tryJoinSelectedAvailableGame(playerId, lastKnownGameId, lastKnownPrivateGameId);
            }


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }
    }

    public void tryJoinSelectedAvailableGame(String playerId, String gameId, String privateGameId) {

        try {
            currentGameId = gameId;
            currentPrivateGameId = privateGameId;
            OffsideApplication.networkingService.requestAvailableGame(playerId, gameId, privateGameId);
            EventBus.getDefault().post(new LoadingEvent(true, "Try join to your last played game"));


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveAvailableGame(AvailableGameEvent availableGameEvent) {
        try {
            AvailableGame availableGame = availableGameEvent.getAvailableGame();
            if (availableGame != null) {
                OffsideApplication.setSelectedAvailableGame(availableGame);
                String groupId = availableGame.getGroupId();
                OffsideApplication.networkingService.requestPrivateGroup(playerId, groupId);
                EventBus.getDefault().post(new LoadingEvent(true, "Checking game"));

            } else {
                EventBus.getDefault().post(new LoadingEvent(false, null));
                EventBus.getDefault().post(new CannotJoinPrivateGameEvent(R.string.lbl_no_active_private_game));
                EventBus.getDefault().post(new NavigationEvent(R.id.nav_action_groups));
            }

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivePrivateGroup(PrivateGroupEvent privateGroupEvent) {
        try {
            EventBus.getDefault().post(new LoadingEvent(false, null));
            if (privateGroupEvent == null)
                return;

            PrivateGroup privateGroup = privateGroupEvent.getPrivateGroup();

            if (privateGroup == null || currentPrivateGameId == null) {
                EventBus.getDefault().post(new CannotJoinPrivateGameEvent(R.string.lbl_no_active_private_game));

            } else if (currentGameId != null && currentPrivateGameId != null && privateGroup.getId() != null && androidDeviceId != null && playerId != null) {

                OffsideApplication.setSelectedPrivateGroup(privateGroup);
                String groupId = privateGroup.getId();
                OffsideApplication.networkingService.requestJoinPrivateGame(playerId, currentGameId, groupId, currentPrivateGameId, androidDeviceId);
                EventBus.getDefault().post(new LoadingEvent(true, "Joining"));
            }


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayerJoinedPrivateGame(JoinGameEvent joinGameEvent) {

        try {
            EventBus.getDefault().post(new LoadingEvent(false, null));
            GameInfo gameInfo = joinGameEvent.getGameInfo();

            if (gameInfo == null) {
                return;
            }

            OffsideApplication.setGameInfo(gameInfo);

            String gameId = gameInfo.getGameId();
            String privateGameId = gameInfo.getPrivateGameId();
            String privateGroupId = OffsideApplication.getSelectedPrivateGroupId();

            OffsideApplication.setSelectedPrivateGameId(privateGameId);

            OffsideApplication.setUserPreferences(privateGroupId, gameId, privateGameId);

            isPlayerCanJoinPrivateGame = true;

            PlayerAssets playerAssets = OffsideApplication.getPlayerAssets();
            playerAssets.setBalance(gameInfo.getPlayer().getBalance());
            playerAssets.setPowerItems(gameInfo.getPlayer().getPowerItems());
            updatePlayerAssets(playerAssets);


            onReceiveNavigation(new NavigationEvent(R.id.nav_action_play));
        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivePrivateGroupChanged(PrivateGroupChangedEvent privateGroupChangedEvent) {
        try {
            EventBus.getDefault().post(new LoadingEvent(false, null));
            if (privateGroupChangedEvent == null)
                return;

            PrivateGroup updatedPrivateGroup = privateGroupChangedEvent.getPrivateGroup();

            PrivateGroupsInfo privateGroupsInfo = OffsideApplication.getPrivateGroupsInfo();
            if (privateGroupsInfo != null)
                OffsideApplication.getPrivateGroupsInfo().replace(updatedPrivateGroup);


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveSelectedPrivateGroup(PrivateGroup privateGroup) {
        try {
            EventBus.getDefault().post(new LoadingEvent(false, null));
            toggleNavigationMenuVisibility(true);
            bottomNavigation.setCurrentItem(0);

            if (privateGroup == null || privateGroup.getId() == null)
                return;

            singleGroupFragment = SingleGroupFragment.newInstance();
            replaceFragment(singleGroupFragment);

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPrivateGameGenerated(PrivateGameGeneratedEvent privateGameGeneratedEvent) {
        try {

            EventBus.getDefault().post(new LoadingEvent(false, null));
            String privateGameId = privateGameGeneratedEvent.getPrivateGameId();
            String gameId = OffsideApplication.getSelectedAvailableGame().getGameId();
            String groupId = OffsideApplication.getSelectedPrivateGroupId();
            OffsideApplication.setUserPreferences(groupId, gameId, privateGameId);

            tryJoinSelectedAvailableGame(playerId, gameId, privateGameId);


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveNavigation(NavigationEvent navigationEvent) {
        try {

            bottomNavigation.setSelectedItemId(navigationEvent.getNavigationItemId());


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
    public void onGroupInvite(GroupInviteEvent groupInviteEvent) {

        try {
            isGroupInviteExecuted = true;
            String groupId = groupInviteEvent.getGroupId();
            String groupName = groupInviteEvent.getGroupName();
            String gameId = groupInviteEvent.getGameId();
            String privateGameId = groupInviteEvent.getPrivateGamaId();
            String playerId = groupInviteEvent.getInviterPlayerId();

            //OffsideApplication.networkingService.requestInviteFriend(playerId, groupId, gameId, privateGameId);
            startFirebaseInvite(groupId, groupName, gameId, privateGameId, playerId);
            //shareShortDynamicLink();


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    private void startFirebaseInvite(String groupId, String groupName, String gameId, String privateGameId, String inviterId) {

        try {
            StringBuilder sb = new StringBuilder(500); // Using default 16 character size
            sb.append("https://tmg9s.app.goo.gl/?link=app://sidekickgame.com?");
            sb.append("groupId=");
            sb.append(groupId);
            sb.append("&gameId=");
            sb.append(gameId);
            sb.append("&privateGameId=");
            sb.append(privateGameId);
            sb.append("&referrer=");
            sb.append(inviterId);
            sb.append("&apn=com.offsidegame.offside&ibi=com.offsidegame.offside.ios&isi=12345");

            String deepLink = sb.toString();

            String invitationMessage;
            String playerName = OffsideApplication.getPlayerAssets().getPlayerName();
            if (privateGameId != null) {
                String gameTitle = String.format("%s vs. %s", OffsideApplication.getGameInfo().getHomeTeam(), OffsideApplication.getGameInfo().getAwayTeam());
                invitationMessage = String.format("\nOur group %s is watching\n %s. \nCome play Sidekick with us ", groupName, gameTitle);
                isInGameInvite = true;
            } else if (groupId != null) {
                invitationMessage = String.format("\nJoin my group %s \nand Let's play Sidekick", groupName);
            } else
                invitationMessage = String.format("\nLets' play Sidekick", playerName);

            if (invitationMessage.length() > 90)
                invitationMessage = invitationMessage.substring(0, 90);

            Intent intent = new AppInviteInvitation.IntentBuilder("Invite friends")
                    .setMessage(invitationMessage)
                    //.setDeepLink(Uri.parse("https://tmg9s.app.goo.gl/?link=app://sidekickgame.com?groupId="+groupId+"&referrer="+inviterId+"&apn=com.offsidegame.offside&ibi=com.offsidegame.offside.ios&isi=12345"))
                    .setDeepLink(Uri.parse(deepLink))
                    .setCustomImage(Uri.parse(OffsideApplication.getAppLogoPictureUrl()))
                    .setCallToActionText("Join Now!")
                    .build();
            startActivityForResult(intent, REQUEST_INVITE);

            EventBus.getDefault().post(new LoadingEvent(false, null));

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }
    }

//    private void shareShortDynamicLink() {
//        try {
//            String link = "http://www.sidekickgame.com";
//            String description = "LinkToAppOnGooglePlay";
//            String titleSocial = "Invite friends to play with you";
//            String source = "Sidekick Soccer";
//
//            Task<ShortDynamicLink> createLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
//                    .setDynamicLinkDomain("tmg9s.app.goo.gl")
//                    .setLink(Uri.parse(DynamicLinkHelper.buildDynamicLink(link, description, titleSocial, source)))
//                    .buildShortDynamicLink()
//                    .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
//                        @Override
//                        public void onComplete(@NonNull Task<ShortDynamicLink> task) {
//                            if (task.isSuccessful()) {
//                                Uri shortLink = task.getResult().getShortLink();
//                                Uri flowchartLink = task.getResult().getPreviewLink();
//                                Log.d(TAG, "Short link: " + shortLink.toString());
//                                Log.d(TAG, "flowchartLink link: " + flowchartLink.toString());
//                                Intent intent = new Intent();
//                                String message = String.format("Invitation from %s to play Sidekick. /nWant to join ? follow this link /n %s ", OffsideApplication.getPlayerAssets().getPlayerName(), shortLink.toString());
//
//                                intent.setAction(Intent.ACTION_SEND);
//                                intent.putExtra(Intent.EXTRA_TEXT, message);
//                                intent.setType("text/plain");
//                                startActivity(intent);
//                            } else {
//                                Log.d(TAG, "Error building short link");
//                            }
//                        }
//                    });
//
//
//        } catch (Exception ex) {
//            ACRA.getErrorReporter().handleSilentException(ex);
//        }
//
//    }

    @Override
    //result of Invite activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

            if (requestCode == REQUEST_INVITE) {
                if (resultCode == RESULT_OK) {
                    // Get the invitation IDs of all sent messages
                    String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                    int numberOfInvitations = ids.length;
                    String rewardType = "COINS";
                    String rewardReason = "INVITE";
                    if (numberOfInvitations > 0)
                        OffsideApplication.networkingService.requestToRewardPlayer(playerId, rewardType, rewardReason, numberOfInvitations);
                    for (String id : ids) {
                        Log.d(TAG, "onActivityResult: sent invitation " + id);
                    }
                } else {
                    // Sending failed or it was canceled, show failure message to the user
                    Log.d(TAG, "invitation send failed");
                }
            }


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotificationBubbleReceived(NotificationBubbleEvent notificationBubbleEvent) {
        try {
            if (bottomNavigation.getSelectedItemId() == R.id.nav_action_play)
                return;
            String notificationBubbleName = notificationBubbleEvent.getNavigationItemName();
            if (notificationBubbleName.equalsIgnoreCase(NotificationBubbleEvent.navigationItemChat)) {
                chatNavigationItemNotificationCount += 1;

                MenuItem chatItem = bottomNavigation.getMenu().findItem(R.id.nav_action_play);


                int position = bottomNavigation.getMenuItemPosition(chatItem);
                if (qBadgeView != null) {
                    qBadgeView.hide(false);
                    qBadgeView = null;
                }

                qBadgeView = new QBadgeView(this)
                        .setBadgeNumber(chatNavigationItemNotificationCount)
                        .setGravityOffset(12, 2, true)
                        .bindTarget(bottomNavigation.getBottomNavigationItemView(position));


                //new QBadgeView(context).bindTarget(textview).setBadgeNumber(5);
                //bottomNavigation.setNotification(String.format("%d",chatNavigationItemNotificationCount),2);
            }


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayerRewardedReceived(PlayerRewardedReceivedEvent playerRewardedReceivedEvent) {

        try {
            doWhereToGoNext = false;
            onLoadingEventReceived(new LoadingEvent(false, null));
            if (playerRewardedReceivedEvent == null || playerRewardedReceivedEvent.getRewardedPlayer()==null)
                return;
            final PlayerAssets playerAssets = playerRewardedReceivedEvent.getRewardedPlayer().getPlayerAssets();
            String rewardType = playerRewardedReceivedEvent.getRewardedPlayer().getRewardType();
            int rewardValue = playerRewardedReceivedEvent.getRewardedPlayer().getRewardValue();

            int soundResource = R.raw.jackpot_1;
            final MediaPlayer mediaPlayer = MediaPlayer.create(context, soundResource);

            rewardDialog = new Dialog(context);
            rewardDialog.setContentView(R.layout.dialog_reward);

            dialogueRewardValueTextView = rewardDialog.findViewById(R.id.dr_reward_value_text_view);
            rewardImageView = rewardDialog.findViewById(R.id.dr_reward_image_view);
            if(rewardType.equalsIgnoreCase(OffsideApplication.POWER_ITEMS))
                rewardImageView.setImageResource(R.mipmap.ic_soccer_ball);
            else
                rewardImageView.setImageResource(R.drawable.ic_coins_heap);

            dialogueCloseButton = rewardDialog.findViewById(R.id.dr_close_button);

            dialogueRewardValueTextView.setText(String.format("%d", rewardValue));

            dialogueCloseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    rewardDialog.cancel();
                    mediaPlayer.stop();
                    onReceivePlayerAssets(playerAssets);
                    //OffsideApplication.networkingService.requestPlayerAssets(playerId);

                }
            });

            adjustDialogWidthToWindow(rewardDialog);
            rewardDialog.show();
            mediaPlayer.start();


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCannotJoinPrivateGameReceived(CannotJoinPrivateGameEvent cannotJoinPrivateGameEvent) {

        try {
            Integer reason = cannotJoinPrivateGameEvent.getReason();
            Toast.makeText(context, reason, Toast.LENGTH_SHORT).show();
            EventBus.getDefault().post(new NavigationEvent(R.id.nav_action_groups));

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }
    //</editor-fold>

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotEnoughAssetsEventReceived(NotEnoughAssetsEvent notEnoughAssetsEvent) {

        try {
            if (notEnoughAssetsEvent == null)
                return;

            boolean hasEnoughAssets = notEnoughAssetsEvent.hasEnoughAssets();

            String assetName = notEnoughAssetsEvent.getAssetName();
            boolean showLuckyWheel = notEnoughAssetsEvent.isIncludeLuckyWheel();

            if (!hasEnoughAssets) {

                shortInAssetsDialog = new Dialog(context);
                shortInAssetsDialog.setContentView(R.layout.dialog_short_in_assets);

                dialogCoinsTitleRoot = shortInAssetsDialog.findViewById(R.id.dsia_dialog_coins_title_root);
                dialogCoinsTitleRoot.setVisibility(View.GONE);
                dialogPowerItemsTitleRoot = shortInAssetsDialog.findViewById(R.id.dsia_dialog_power_items_title_root);
                dialogPowerItemsTitleRoot.setVisibility(View.GONE);

                notEnoughAssetsDialogueCloseButton = shortInAssetsDialog.findViewById(R.id.dsia_close_button);
                getAssetsShopRoot = shortInAssetsDialog.findViewById(R.id.dsia_get_assets_shop_root);
                getAssetsWatchVideoRoot = shortInAssetsDialog.findViewById(R.id.dsia_get_assets_watch_video_root);
                getAssetsSlotMachineRoot = shortInAssetsDialog.findViewById(R.id.dsia_get_assets_slot_machine_root);

                getAssetsShopRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EventBus.getDefault().post(new NavigationEvent(R.id.nav_action_shop));
                        shortInAssetsDialog.cancel();
                    }
                });

                getAssetsSlotMachineRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        luckyWheelFragment = LuckyWheelFragment.newInstance();
                        replaceFragment(luckyWheelFragment);

                        shortInAssetsDialog.cancel();

                    }
                });

                if(assetName == OffsideApplication.COINS){
                    dialogCoinsTitleRoot.setVisibility(View.VISIBLE);
                    final String placementName="GetMoreCoins";
                    getAssetsWatchVideoRoot.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            loadRewardVideo(placementName);
                            shortInAssetsDialog.cancel();

                        }
                    });

                }

                else if(assetName == OffsideApplication.POWER_ITEMS){
                    dialogPowerItemsTitleRoot.setVisibility(View.VISIBLE);
                    final String placementName="GetMorePowerItems";
                    getAssetsWatchVideoRoot.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            loadRewardVideo(placementName);
                            shortInAssetsDialog.cancel();

                        }
                    });

                }

                notEnoughAssetsDialogueCloseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shortInAssetsDialog.cancel();
                    }
                });

                if(showLuckyWheel)
                    getAssetsSlotMachineRoot.setVisibility(View.VISIBLE);
                else
                    getAssetsSlotMachineRoot.setVisibility(View.GONE);


                adjustDialogWidthToWindow(shortInAssetsDialog);
                shortInAssetsDialog.show();

            }


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }


    }

    private void loadRewardVideo(String placementName) {

        String message;
        boolean isRewardedVideoPlacementCapped = IronSource.isRewardedVideoPlacementCapped(placementName);
        if(!isRewardedVideoPlacementCapped)
            IronSource.showRewardedVideo(placementName);
        else{
            message="Exceed video watches per day for " + placementName;
            Snackbar.make(playerInfoRoot, message, Snackbar.LENGTH_SHORT).show();
        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayerQuitFromPrivateGameEventReceived(PlayerQuitFromPrivateGameEvent playerQuitFromPrivateGameEvent) {
        try {
            if (playerQuitFromPrivateGameEvent == null)
                return;
            boolean isPlayerWasRemovedFromPrivateGame = playerQuitFromPrivateGameEvent.isPlayerWasRemovedFromPrivateGame();
            OffsideApplication.setUserPreferences(null, null, null);
            OffsideApplication.setSelectedPrivateGroup(null);
            OffsideApplication.setSelectedAvailableGame(null);
            OffsideApplication.setSelectedPrivateGameId(null);

            final String message;
            if (isPlayerWasRemovedFromPrivateGame)
                message = getString(R.string.lbl_player_Successfully_quit_game);
            else
                message = getString(R.string.lbl_playe_or_game_not_found);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(playerInfoRoot, message, Snackbar.LENGTH_SHORT).show();
                }
            }, 2000);

            EventBus.getDefault().post(new NavigationEvent(R.id.nav_action_groups));


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadingEventReceived(LoadingEvent loadingEvent) {
        try {
            if (loadingEvent == null)
                return;
            String loadingMessage = loadingEvent.getLoadingMessage();
            if (loadingMessage != null)
                loadingMessageTextView.setText(loadingMessage);

            boolean isLoading = loadingEvent.isLoading();
            if (isLoading)
                loadingRoot.setVisibility(View.VISIBLE);
            else
                loadingRoot.setVisibility(View.GONE);


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onJoinGameWithCodeReceived(JoinGameWithCodeEvent joinGameWithCodeEvent) {

        try {
            onLoadingEventReceived(new LoadingEvent(false, null));
            if (joinGameWithCodeEvent == null)
                return;


            joinGameWithCodeDialog = new Dialog(context);
            joinGameWithCodeDialog.setContentView(R.layout.dialog_join_with_code);

            dialogJoinGamePrivateGameCodeEditText = joinGameWithCodeDialog.findViewById(R.id.djwc_private_game_code_edit_text);
            dialogJoinGameButton = joinGameWithCodeDialog.findViewById(R.id.djwc_enter_game_button);

            dialogJoinGamePrivateGameCodeEditText.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (s.toString().trim().length() < 6) {
                        dialogJoinGameButton.setEnabled(false);
                    } else {
                        dialogJoinGameButton.setEnabled(true);
                    }

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    if (s.toString().trim().length() < 6) {
                        dialogJoinGameButton.setEnabled(false);
                    } else {
                        dialogJoinGameButton.setEnabled(true);
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().trim().length() < 6) {
                        dialogJoinGameButton.setEnabled(false);
                    } else {
                        dialogJoinGameButton.setEnabled(true);
                    }

                }
            });


            dialogJoinGameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String privateGameCode = dialogJoinGamePrivateGameCodeEditText.getText().toString();
                    OffsideApplication.networkingService.RequestPrivateGameInfoByCode(playerId, privateGameCode);
                    joinGameWithCodeDialog.cancel();
                }
            });

            adjustDialogWidthToWindow(joinGameWithCodeDialog);
            joinGameWithCodeDialog.show();


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }


    }

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
                        String playerId = OffsideApplication.getPlayerId();

                        if (playerId == null)
                            return;
                        OffsideApplication.networkingService.requestUpdatePlayerName(playerId, newValue);

                    } else if (key == EditValueEvent.updateGroupName) {
                        PrivateGroup privateGroup = OffsideApplication.getSelectedPrivateGroup();
                        if (privateGroup == null)
                            return;

                        OffsideApplication.getSelectedPrivateGroup().setName(newValueEditText.getText().toString());
                        OffsideApplication.networkingService.RequestUpdatePrivateGroup(playerId, privateGroup.getId(), newValue);

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
    public void onPrivateGameInfoReceived(PrivateGameInfo privateGameInfo) {
        try {
            if (privateGameInfo == null)
                return;

            String gameId = privateGameInfo.getGameId();
            String groupId = privateGameInfo.getGroupId();
            String privateGameId = privateGameInfo.getPrivateGameId();

            OffsideApplication.setUserPreferences(groupId, gameId, privateGameId);
            whereToGoNext();


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkingErrorReceived(NetworkingErrorEvent networkingErrorEvent) {
        try {
            EventBus.getDefault().post(new LoadingEvent(false, null));
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPrivateGroupUpdatedReceived(PrivateGroupUpdatedEvent privateGroupUpdatedEvent) {
        try {
            EventBus.getDefault().post(new LoadingEvent(false, null));
            if (privateGroupUpdatedEvent == null)
                return;

            PrivateGroup privateGroup = privateGroupUpdatedEvent.getPrivateGroup();

            PrivateGroup selectedPrivateGroup = OffsideApplication.getSelectedPrivateGroup();

            if (selectedPrivateGroup != null)
                OffsideApplication.setSelectedPrivateGroup(privateGroup);

            PrivateGroupsInfo privateGroupsInfo = OffsideApplication.getPrivateGroupsInfo();
            if (privateGroupsInfo != null)

                privateGroupsInfo.replace(privateGroup);

            editValueDialog.cancel();

            //to update the ui SingleGroupFragment;
            EventBus.getDefault().post(new PrivateGroupChangedEvent(privateGroup));


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

            //update playerName in playerAseets
            PlayerAssets playerAssets = OffsideApplication.getPlayerAssets();
            if (playerAssets != null)
                playerAssets.setPlayerName(updatedPlayer.getUserName());

            //update player in gameInfo , in case there is gameInfo
            GameInfo gameInfo = OffsideApplication.getGameInfo();
            if (gameInfo != null) {
                PlayerModel currentPlayer = gameInfo.getPlayer();
                if (currentPlayer != null)
                    currentPlayer.setUserName(updatedPlayer.getUserName());

            }

            editValueDialog.cancel();

            //to update the ui PlayerFragment;
            EventBus.getDefault().post(new PlayerSettingsChangedEvent(updatedPlayer));


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


    @Override
    public void onBackPressed() {
        try {
            if (bottomNavigation.getSelectedItemId() == R.id.nav_action_groups) //groups
                finish();
            else
                EventBus.getDefault().post(new NavigationEvent(R.id.nav_action_groups));
            EventBus.getDefault().post(new LoadingEvent(false, null));


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }


    }


}
