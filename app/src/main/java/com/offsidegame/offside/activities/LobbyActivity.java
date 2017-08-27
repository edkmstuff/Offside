package com.offsidegame.offside.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.offsidegame.offside.R;
import com.offsidegame.offside.adapters.CustomTabsFragmentPagerAdapter;
import com.offsidegame.offside.adapters.LeagueAdapter;
import com.offsidegame.offside.events.ConnectionEvent;
import com.offsidegame.offside.events.SignalRServiceBoundEvent;
import com.offsidegame.offside.fragments.AvailableGamesFragment;
import com.offsidegame.offside.fragments.GroupsFragment;
import com.offsidegame.offside.fragments.PlayerFragment;
import com.offsidegame.offside.fragments.PrivateGroupsFragment;
import com.offsidegame.offside.helpers.ImageHelper;
import com.offsidegame.offside.models.AvailableGame;
import com.offsidegame.offside.models.ExperienceLevel;
import com.offsidegame.offside.models.LeagueRecord;
import com.offsidegame.offside.models.OffsideApplication;
import com.offsidegame.offside.models.PlayerAssets;
import com.offsidegame.offside.models.PlayerGame;
import com.offsidegame.offside.models.PrivateGroup;
import com.offsidegame.offside.models.PrivateGroupsInfo;
import com.offsidegame.offside.models.Reward;
import com.offsidegame.offside.models.UserProfileInfo;
import com.offsidegame.offside.models.Winner;

import org.acra.ACRA;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class LobbyActivity extends AppCompatActivity implements Serializable  {

    //<editor-fold desc="*****************MEMBERS****************">

    //activity
    private final String activityName = "LobbyActivity";
    private final Context context = this;
    private final Activity thisActivity = this;

    private BottomNavigationView bottomNavigationView;
    private TabLayout.OnTabSelectedListener listener;
    private String groupId;
    //private TextView createPrivateGroupButtonTextView;

    private TabLayout leaguesSelectionTabLayout;
    private ViewPager leaguesPagesViewPager;
    private TabLayout.OnTabSelectedListener leaguesSelectionListener;
    private ImageView settingsButtonImageView;

    // single group view
    private LinearLayout singlePrivateGroupRoot;
    private LinearLayout singleGroupGamesTabRoot;
    private LinearLayout singleGroupLeagueTabRoot;
    private LinearLayout singleGroupGamesRoot;
    private LinearLayout singleGroupLeagueRoot;
    private TextView singleGroupPositionOutOfTextView;
    private ListView singleGroupLeagueListView;

    //loading
    private LinearLayout loadingRoot;
    private TextView versionTextView;

    //playerAssets
    private LinearLayout playerInfoRoot;
    private TextView balanceTextView;
    private TextView powerItemsTextView;


    //profile
    private ImageView playerPictureImageView;
    private String playerId;
    private String playerProfilePictureUrl;
    private SharedPreferences settings;

    //using fragments
    private LinearLayout fragmentContainerRoot;
    private GroupsFragment groupsFragment;
    PlayerFragment playerFragment;


    //</editor-fold>


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_lobby);

            settings = getSharedPreferences(getString(R.string.preference_name), 0);

            FirebaseUser player = FirebaseAuth.getInstance().getCurrentUser();

            playerId = player.getUid();

            getIds();
            setEvents();
            resetVisibility();


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }


    }

    private void getIds() {


        playerInfoRoot = (LinearLayout) findViewById(R.id.l_player_info_root);
        fragmentContainerRoot = (LinearLayout) findViewById(R.id.l_fragment_container_root);
        //groupsFragment = (GroupsFragment) this.getSupportFragmentManager().findFragmentById(R.id.l_groups_fragment);

        settingsButtonImageView = (ImageView) findViewById(R.id.l_settings_button_image_view);

        playerPictureImageView = (ImageView) findViewById(R.id.l_player_picture_image_view);
        balanceTextView = (TextView) findViewById(R.id.l_balance_text_view);
        powerItemsTextView = (TextView) findViewById(R.id.l_power_items_text_view);

        //createPrivateGroupButtonTextView = (TextView) findViewById(R.id.l_create_private_group_button_text_view);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.l_bottom_navigation_view);

        //single view
        singlePrivateGroupRoot = (LinearLayout) findViewById(R.id.l_single_group_root);
        singleGroupGamesTabRoot = (LinearLayout) findViewById(R.id.l_single_group_games_tab_root);
        singleGroupLeagueTabRoot = (LinearLayout) findViewById(R.id.l_single_group_league_tab_root);
        singleGroupGamesRoot = (LinearLayout) findViewById(R.id.l_single_group_games_root);
        singleGroupLeagueRoot = (LinearLayout) findViewById(R.id.l_single_group_league_root);
        singleGroupPositionOutOfTextView = (TextView) findViewById(R.id.l_single_group_position_out_of_text_view);
        singleGroupLeagueListView = (ListView) findViewById(R.id.l_single_group_league_list_view);


        //leagues
        leaguesPagesViewPager = (ViewPager) findViewById(R.id.l_leagues_pages_view_pager);
        //setup tabLayout
        leaguesSelectionTabLayout = (TabLayout) findViewById(R.id.l_leagues_selection_tab_layout);
        leaguesSelectionTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        leaguesSelectionTabLayout.setupWithViewPager(leaguesPagesViewPager);

    }

    private void setEvents() {

        settingsButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthUI.getInstance()
                        .signOut((FragmentActivity) context)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // user is now signed out
                                startActivity(new Intent(context, LoginActivity.class));
                                finish();
                            }
                        });
            }
        });

        leaguesSelectionListener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                leaguesPagesViewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //leaguesPagesViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //leaguesPagesViewPager.setCurrentItem(tab.getPosition());
            }
        };

//        singleGroupGamesTabRoot.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                singleGroupTabSwitched(view);
//            }
//        });
//
//        singleGroupLeagueTabRoot.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                singleGroupTabSwitched(view);
//            }
//        });

//        createPrivateGroupButtonTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent intent = new Intent(context, CreatePrivateGroupActivity.class);
//                startActivity(intent);
//
//            }
//        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                try {
                    switch (item.getItemId()) {
                        case R.id.nav_action_groups:
                            groupsFragment = new GroupsFragment();
                            replaceFragment(groupsFragment);
                            OffsideApplication.signalRService.requestPrivateGroupsInfo(playerId);
                            return true;

                        case R.id.nav_action_profile:
                            playerFragment = new PlayerFragment();
                            replaceFragment(playerFragment);
                            OffsideApplication.signalRService.RequestUserProfileData(playerId);
                            return true;

                        case R.id.nav_action_shop:
                            return true;

                        case R.id.nav_action_play:
                            Intent chatIntent = new Intent(context, ChatActivity.class);
                            startActivity(chatIntent);
                            return true;
                    }

                    return true;

                }
                catch (Exception ex) {
                    ACRA.getErrorReporter().handleSilentException(ex);
                    return false;
                }

            }
        });


    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.l_fragment_container_root, fragment, fragment.getTag()).commit();

    }

    private void singleGroupTabSwitched(View view) {
        if (singleGroupGamesTabRoot == view) {

            showAvailableGames();

        } else if (singleGroupLeagueTabRoot == view) {
            showLeague();
        }
    }

    private void showAvailableGames() {
        //update groups stuff
        //todo: create distinct of league types to send to fragment creator - they will define the tabs

        this.addLeaguesCategories();
        leaguesSelectionTabLayout.addOnTabSelectedListener(leaguesSelectionListener);
        loadingRoot.setVisibility(View.GONE);


        //privateGroupsRoot.setVisibility(View.GONE);
        //createPrivateGroupButtonTextView.setVisibility(View.GONE);
        singleGroupLeagueRoot.setVisibility(View.GONE);

        singleGroupGamesTabRoot.setBackgroundResource(R.color.navigationMenuSelectedItem);
        singleGroupLeagueTabRoot.setBackgroundResource(R.color.navigationMenu);


        singlePrivateGroupRoot.setVisibility(View.VISIBLE);
        singleGroupGamesRoot.setVisibility(View.VISIBLE);


    }

    private void showLeague() {

        HashMap<String, LeagueRecord[]> leaguesRecords = OffsideApplication.getLeaguesRecords();
        if (!leaguesRecords.containsKey(groupId)) {
            getLeagueRecords(groupId);
            return;
        }
        LeagueRecord[] leagueRecords = leaguesRecords.get(groupId);
        singleGroupGamesRoot.setVisibility(View.GONE);
        singleGroupLeagueTabRoot.setBackgroundResource(R.color.navigationMenuSelectedItem);
        singleGroupGamesTabRoot.setBackgroundResource(R.color.navigationMenu);

        singleGroupLeagueRoot.setVisibility(View.VISIBLE);

        LeagueAdapter leagueAdapter = new LeagueAdapter(context, new ArrayList<>(Arrays.asList(leagueRecords)));
        singleGroupLeagueListView.setAdapter(leagueAdapter);
    }

    private void getLeagueRecords(String groupId) {
        OffsideApplication.signalRService.requestLeagueRecords(playerId, groupId);
    }

    private void resetVisibility() {

        playerInfoRoot.setVisibility(View.GONE);
        singlePrivateGroupRoot.setVisibility(View.GONE);
        fragmentContainerRoot.setVisibility(View.VISIBLE);
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
        super.onStop();
        EventBus.getDefault().unregister(context);
        leaguesSelectionTabLayout.removeOnTabSelectedListener(leaguesSelectionListener);

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivePlayerAssets(PlayerAssets playerAssets) {

        try {
            if (playerAssets == null )
                return;

            OffsideApplication.setPlayerAssets(playerAssets);

            //update player stuff
            int balance = playerAssets.getBalance();
            int powerItems = playerAssets.getPowerItems();
            playerProfilePictureUrl = playerAssets.getImageUrl();
            balanceTextView.setText(Integer.toString(balance));
            powerItemsTextView.setText(Integer.toString(powerItems));

            ImageHelper.loadImage(thisActivity, playerProfilePictureUrl, playerPictureImageView, activityName);

            playerInfoRoot.setVisibility(View.VISIBLE);

            bottomNavigationView.setSelectedItemId(R.id.nav_action_groups);
            //bottomNavigationView.getMenu().findItem(R.id.nav_action_groups).setChecked(true);

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivePrivateGroupsInfo(PrivateGroupsInfo privateGroupsInfo) {
        try {
            if (privateGroupsInfo == null || privateGroupsInfo.getPrivateGroups() == null)
                return;

            OffsideApplication.setPrivateGroupsInfo(privateGroupsInfo);

            groupsFragment.addPagesToGroupsFragment();

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveUserProfileInfo(UserProfileInfo userProfileInfo) {
        try {
            if (userProfileInfo == null)
                return;

            OffsideApplication.setUserProfileInfo(userProfileInfo);
            playerFragment.updateUserProfileFragment(userProfileInfo);


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveSelectedPrivateGroup(PrivateGroup privateGroup) {
        try {
            if (privateGroup == null || privateGroup.getId() == null)
                return;

            groupId = privateGroup.getId();
            OffsideApplication.signalRService.requestAvailableGames(playerId, groupId);

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveAvailableGames(AvailableGame[] availableGames) {
        try {
            if (availableGames == null || availableGames.length == 0)
                return;

            OffsideApplication.setAvailableGames(availableGames);

            //update groups stuff
            //todo: create distinct of league types to send to fragment creator - they will define the tabs

            this.addLeaguesCategories();
            leaguesSelectionTabLayout.addOnTabSelectedListener(leaguesSelectionListener);
            loadingRoot.setVisibility(View.GONE);


            //privateGroupsRoot.setVisibility(View.GONE);
            singlePrivateGroupRoot.setVisibility(View.VISIBLE);
            //createPrivateGroupButtonTextView.setVisibility(View.GONE);


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    private void addLeaguesCategories() {

        CustomTabsFragmentPagerAdapter pagerAdapterFragment1 = new CustomTabsFragmentPagerAdapter(this.getSupportFragmentManager());
        AvailableGamesFragment ChampionsLeagueAvailableGameFragment = new AvailableGamesFragment();
        Bundle ChampionsLeagueAvailableGameFragmentBundle = new Bundle();
        ChampionsLeagueAvailableGameFragmentBundle.putString(getString(R.string.key_league_type), "PL");
        ChampionsLeagueAvailableGameFragment.setArguments(ChampionsLeagueAvailableGameFragmentBundle);
        pagerAdapterFragment1.addFragment(ChampionsLeagueAvailableGameFragment);

        AvailableGamesFragment IsraeliLeagueAvailableGameFragment = new AvailableGamesFragment();
        Bundle IsraeliLeagueAvailableGameFragmentBundle = new Bundle();
        IsraeliLeagueAvailableGameFragmentBundle.putString(getString(R.string.key_league_type), "IL");
        IsraeliLeagueAvailableGameFragment.setArguments(IsraeliLeagueAvailableGameFragmentBundle);
        pagerAdapterFragment1.addFragment(IsraeliLeagueAvailableGameFragment);
        loadingRoot.setVisibility(View.GONE);


        //set adapter to ViewPager

        leaguesPagesViewPager.setAdapter(pagerAdapterFragment1);
        leaguesSelectionTabLayout.addOnTabSelectedListener(leaguesSelectionListener);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveLeagueRecords(LeagueRecord[] leagueRecords) {
        try {
            if (leagueRecords == null || leagueRecords.length == 0)
                return;

            OffsideApplication.getLeaguesRecords().put(groupId, leagueRecords);

            showLeague();

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }


    private Boolean exit = false;

    @Override
    public void onBackPressed() {

        resetVisibility();
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, R.string.lbl_press_back_again_to_exit,
                    Toast.LENGTH_LONG).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSignalRServiceBinding(SignalRServiceBoundEvent signalRServiceBoundEvent) {
        try {
            if (OffsideApplication.signalRService == null)
                return;

            Context eventContext = signalRServiceBoundEvent.getContext();
            if (eventContext == context || eventContext == getApplicationContext()) {

//                if (OffsideApplication.isPlayerQuitGame()) {
//                    loadingGameRoot.setVisibility(View.GONE);
//                    joinGameRoot.setVisibility(View.VISIBLE);
//                    return;
//                }

                SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
                String gameId = settings.getString(getString(R.string.game_id_key), "");
                String privateGameId = settings.getString(getString(R.string.private_game_id_key), "");


                if (OffsideApplication.isBoundToSignalRService) {
                    if (gameId != null && gameId.length() > 0 && privateGameId != null && privateGameId.length() > 0)
                        OffsideApplication.signalRService.isGameActive(gameId, privateGameId);

                    OffsideApplication.signalRService.requestPlayerAssets(playerId);


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


}
