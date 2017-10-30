package com.offsidegame.offside.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
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

import com.offsidegame.offside.helpers.DynamicLinkHelper;
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

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;


public class LobbyActivity extends AppCompatActivity implements Serializable {

    //<editor-fold desc="*****************MEMBERS****************">

    //activity
    private final String activityName = "LobbyActivity";
    private final Context context = this;
    private final Activity thisActivity = this;

    private BottomNavigationViewEx bottomNavigation;


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

    private Badge qBadgeView = null;

    private int REQUEST_INVITE = 100;
    private String TAG = "SIDEKICK";


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
            //createNavigationMenu();
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

        bottomNavigation.enableAnimation(false);
        bottomNavigation.enableShiftingMode(false);
        bottomNavigation.enableItemShiftingMode(false);
        bottomNavigation.setTextVisibility(false);
        int iconSize = 36;
        bottomNavigation.setIconSize(iconSize,iconSize);
        bottomNavigation.setItemHeight(BottomNavigationViewEx.dp2px(this, iconSize + 16));



        //bottomNavigation.setIconTintList();
        //bottomNavigationItemBackgroundResourceId = bottomNavigation.getItemBackgroundResource();







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


        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                try {

                    for(int i=0; i< bottomNavigation.getItemCount();i++){

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            bottomNavigation.setIconTintList(i,getResources().getColorStateList(R.color.colorWhiteSemiTransparent, context.getTheme()));
                        } else {
                            bottomNavigation.setIconTintList(i,getResources().getColorStateList(R.color.colorWhiteSemiTransparent));
                        }




                    }

                    int itemPosition = bottomNavigation.getMenuItemPosition(item);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        bottomNavigation.setIconTintList(itemPosition,getResources().getColorStateList(R.color.colorWhite, context.getTheme()));
                    } else {
                        bottomNavigation.setIconTintList(itemPosition,getResources().getColorStateList(R.color.colorWhite));
                    }


                    //int itemPosition = bottomNavigation.getMenuItemPosition(item);


//                    for(MenuItem item: bottomNavigation.getItemCount())
//                        bottomNavigation.setIconTintList();
//                    bottomNavigation.setItemBackgroundResource(bottomNavigationItemBackgroundResourceId);


                    switch (item.getItemId()) {
                        case R.id.nav_action_groups :


                            groupsFragment = GroupsFragment.newInstance();
                            replaceFragment(groupsFragment);
                            togglePlayerAssetsVisibility(true);
                            return true;

                        case R.id.nav_action_profile:

                            playerFragment = PlayerFragment.newInstance();
                            replaceFragment(playerFragment);
                            togglePlayerAssetsVisibility(true);
                            return true;

                        case R.id.nav_action_play:
                            //bottomNavigation.setItemBackground(itemPosition, R.color.navigationMenuSelectedItem);
                            if (qBadgeView != null)
                                qBadgeView.hide(true);
                            chatNavigationItemNotificationCount = 0;
                            chatFragment = ChatFragment.newInstance();
                            replaceFragment(chatFragment);
                            togglePlayerAssetsVisibility(false);
                            return true;

                        case R.id.nav_action_news:
                            //bottomNavigation.setItemBackground(itemPosition, R.color.navigationMenuSelectedItem);
                            newsFragment = NewsFragment.newInstance();
                            replaceFragment(newsFragment);
                            togglePlayerAssetsVisibility(false);
                            return true;

                        case R.id.nav_action_shop:
                            //bottomNavigation.setItemBackground(itemPosition, R.color.navigationMenuSelectedItem);
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

            //bottomNavigation.setCurrentItem(navigationEvent.getNavigationItemId());
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

        //OffsideApplication.signalRService.requestInviteFriend(playerId, groupId, gameId, privateGameId);
        startFirebaseInvite(groupId, playerId);
        //shareShortDynamicLink();

    }

    private void startFirebaseInvite(String groupId,String playerId) {

        Intent intent = new AppInviteInvitation.IntentBuilder("Invite friends")
                .setMessage(String.format("Invitation from %s to play Sidekick",OffsideApplication.getPlayerAssets().getPlayerName()))
                .setDeepLink(Uri.parse("https://tmg9s.app.goo.gl/?link=app://sidekickgame.com?groupId="+groupId+"&referrer="+playerId+"&apn=com.offsidegame.offside&ibi=com.offsidegame.offside.ios&isi=12345"))
                .setCustomImage(Uri.parse(OffsideApplication.getAppLogoPictureUrl()))
                .setCallToActionText("Download Now!")
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    private void shareShortDynamicLink(){
        String link = "http://www.sidekickgame.com";
        String description = "LinkToAppOnGooglePlay";
        String titleSocial = "Invite friends to play with you";
        String source = "Sidekick Soccer";

        Task<ShortDynamicLink> createLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setDynamicLinkDomain("tmg9s.app.goo.gl")
                .setLink(Uri.parse(DynamicLinkHelper.buildDynamicLink(link,description,titleSocial,source)))
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if(task.isSuccessful()){
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            Log.d(TAG,"Short link: "+shortLink.toString());
                            Log.d(TAG,"flowchartLink link: "+flowchartLink.toString());
                            Intent intent = new Intent();
                            String message = String.format("Invitation from %s to play Sidekick. /nWant to join ? follow this link /n %s ",OffsideApplication.getPlayerAssets().getPlayerName(),shortLink.toString());

                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT,message);
                            intent.setType("text/plain");
                            startActivity(intent);
                        }
                        else {
                            Log.d(TAG,"Error building short link");
                        }
                    }
                });
                

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.d(TAG, "onActivityResult: sent invitation " + id);
                }
            } else {
                // Sending failed or it was canceled, show failure message to the user
                Log.d(TAG, "invitation send failed");
            }
        }
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
        if(bottomNavigation.getSelectedItemId() == R.id.nav_action_play)
            return;
        String notificationBubbleName = notificationBubbleEvent.getNavigationItemName();
        if(notificationBubbleName.equalsIgnoreCase(NotificationBubbleEvent.navigationItemChat)){
            chatNavigationItemNotificationCount +=1;

            MenuItem chatItem = bottomNavigation.getMenu().findItem(R.id.nav_action_play);


            int position  = bottomNavigation.getMenuItemPosition(chatItem);
            if (qBadgeView != null){
                qBadgeView.hide(false);
                qBadgeView = null;
            }

            qBadgeView =  new QBadgeView(this)
                    .setBadgeNumber(chatNavigationItemNotificationCount)
                    .setGravityOffset(12, 2, true)
                    .bindTarget(bottomNavigation.getBottomNavigationItemView(position));





            //new QBadgeView(context).bindTarget(textview).setBadgeNumber(5);
            //bottomNavigation.setNotification(String.format("%d",chatNavigationItemNotificationCount),2);
        }

    }



    //</editor-fold>

    @Override
    public void onBackPressed() {
        if (bottomNavigation.getSelectedItemId() == R.id.nav_action_groups) //groups
            finish();
        else
            EventBus.getDefault().post(new NavigationEvent(R.id.nav_action_groups));

    }


}
