package com.offsidegame.offside.activities;

import android.app.Activity;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.offsidegame.offside.R;
import com.offsidegame.offside.adapters.CustomTabsFragmentPagerAdapter;
import com.offsidegame.offside.events.ConnectionEvent;
import com.offsidegame.offside.events.SignalRServiceBoundEvent;
import com.offsidegame.offside.fragments.AvailableGamesFragment;
import com.offsidegame.offside.fragments.PrivateGroupsFragment;
import com.offsidegame.offside.helpers.ImageHelper;
import com.offsidegame.offside.models.AvailableGame;
import com.offsidegame.offside.models.OffsideApplication;
import com.offsidegame.offside.models.PlayerAssets;
import com.offsidegame.offside.models.PrivateGroup;
import com.offsidegame.offside.models.PrivateGroupsInfo;

import org.acra.ACRA;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;


public class LobbyActivity extends AppCompatActivity implements Serializable {

    //<editor-fold desc="REGION PROPERTIES">


    //activity
    private final String activityName = "LobbyActivity";
    private final Context context = this;
    private final Activity thisActivity = this;

    private BottomNavigationView bottomNavigationView;
    private LinearLayout[] viewRoots = new LinearLayout[4];
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabLayout.OnTabSelectedListener listener;
    private String groupId;
    private TextView createPrivateGroupButtonTextView;

    private TabLayout leaguesSelectionTabLayout;
    private ViewPager leaguesPagesViewPager;
    private TabLayout.OnTabSelectedListener leaguesSelectionListener;
    private ImageView settingsButtonImageView;
    private LinearLayout singlePrivateGroupRoot;

    private LinearLayout privateGroupsRoot;

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



    //</editor-fold>


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_lobby);

            settings = getSharedPreferences(getString(R.string.preference_name), 0);

            FirebaseUser player = FirebaseAuth.getInstance().getCurrentUser();

            playerId = player.getUid();

//////////////////////////////////////////////////////////////////////////////////////////////
            loadingRoot = (LinearLayout) findViewById(R.id.l_loading_root);
            playerInfoRoot = (LinearLayout) findViewById(R.id.l_player_info_root);
            privateGroupsRoot = (LinearLayout) findViewById(R.id.l_private_groups_root);


            //<editor-fold desc="REGION TABS ELEMENTS">

            //set up viewPager
            viewPager = (ViewPager) findViewById(R.id.l_tabs_container_view_pager);


            //setup tabLayout
            tabLayout = (TabLayout) findViewById(R.id.l_groups_tab_layout);
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            tabLayout.setupWithViewPager(viewPager);

            listener = new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {

                    viewPager.setCurrentItem(tab.getPosition());

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }
            };


            //</editor-fold>

            //<editor-fold desc="REGION TABS LEAGUES SELECTION">

            //set up viewPager
            leaguesPagesViewPager = (ViewPager) findViewById(R.id.l_leagues_pages_view_pager);


            //setup tabLayout
            leaguesSelectionTabLayout = (TabLayout) findViewById(R.id.l_leagues_selection_tab_layout);
            leaguesSelectionTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            leaguesSelectionTabLayout.setupWithViewPager(leaguesPagesViewPager);

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


            //</editor-fold>

            settingsButtonImageView = (ImageView) findViewById(R.id.l_settings_button_image_view);
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

            versionTextView = (TextView) findViewById(R.id.l_version_text_view);
            versionTextView.setText(OffsideApplication.getVersion() == null ? "0.0" : OffsideApplication.getVersion());


            playerPictureImageView = (ImageView) findViewById(R.id.l_player_picture_image_view);
            balanceTextView = (TextView) findViewById(R.id.l_balance_text_view);
            powerItemsTextView = (TextView) findViewById(R.id.l_power_items_text_view);


            viewRoots[0] = (LinearLayout) findViewById(R.id.l_private_groups_root);
            viewRoots[1] = (LinearLayout) findViewById(R.id.l_profile_root);
            viewRoots[2] = (LinearLayout) findViewById(R.id.l_shop_root);
            viewRoots[3] = (LinearLayout) findViewById(R.id.l_play_root);


            createPrivateGroupButtonTextView = (TextView) findViewById(R.id.l_create_private_group_button_text_view);
            bottomNavigationView = (BottomNavigationView) findViewById(R.id.l_bottom_navigation_view);
            singlePrivateGroupRoot = (LinearLayout) findViewById(R.id.l_single_group_root);

            resetVisibility();
            viewRoots[0].setVisibility(View.VISIBLE);

            createPrivateGroupButtonTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context,CreatePrivateGroupActivity.class);
                    startActivity(intent);

                }});

            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    resetVisibility();
                    switch (item.getItemId()) {
                        case R.id.nav_action_groups:
                            viewRoots[0].setVisibility(View.VISIBLE);
                            //privateGroupsScrollView.setVisibility(View.VISIBLE);

                            break;
                        case R.id.nav_action_profile:
                            viewRoots[1].setVisibility(View.VISIBLE);
                            break;
                        case R.id.nav_action_shop:
                            viewRoots[2].setVisibility(View.VISIBLE);
                            break;
                        case R.id.nav_action_play:
                            viewRoots[3].setVisibility(View.VISIBLE);
                            break;


                    }

                    return true;
                }
            });





        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }


    }

    private void resetVisibility() {

        loadingRoot.setVisibility(View.VISIBLE);
        playerInfoRoot.setVisibility(View.GONE);
        privateGroupsRoot.setVisibility(View.VISIBLE);

        singlePrivateGroupRoot.setVisibility(View.GONE);

        for (LinearLayout layout : viewRoots) {
            layout.setVisibility(View.GONE);

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
        tabLayout.removeOnTabSelectedListener(listener);
        leaguesSelectionTabLayout.removeOnTabSelectedListener(leaguesSelectionListener);
        super.onStop();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivePrivateGroupsInfo(PrivateGroupsInfo privateGroupsInfo) {
        try {
            if (privateGroupsInfo == null || privateGroupsInfo.getPrivateGroups() == null || privateGroupsInfo.getPlayerAssets() == null)
                return;

            OffsideApplication.setPrivateGroupsInfo(privateGroupsInfo);


            //update player stuff
            PlayerAssets playerAssets = privateGroupsInfo.getPlayerAssets();
            int balance = playerAssets.getBalance();
            int powerItems = playerAssets.getPowerItems();
            String playerAssetImageUrl = playerAssets.getImageUrl();
            balanceTextView.setText(Integer.toString(balance));
            powerItemsTextView.setText(Integer.toString(powerItems));
            String pictureUrlFromSettings = settings.getString(getString(R.string.player_profile_picture_url_key), null);
            playerProfilePictureUrl = pictureUrlFromSettings != null ? pictureUrlFromSettings : playerAssetImageUrl;
            ImageHelper.loadImage(thisActivity, playerProfilePictureUrl, playerPictureImageView, activityName);

            playerInfoRoot.setVisibility(View.VISIBLE);


            //update groups stuff

            this.addGroupsCategories();
            tabLayout.addOnTabSelectedListener(listener);
            loadingRoot.setVisibility(View.GONE);


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


            privateGroupsRoot.setVisibility(View.GONE);
            singlePrivateGroupRoot.setVisibility(View.VISIBLE);


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    private void addGroupsCategories() {

        //todo: check if we can reduce duplicate code

        CustomTabsFragmentPagerAdapter pagerAdapterFragment = new CustomTabsFragmentPagerAdapter(this.getSupportFragmentManager());
        PrivateGroupsFragment privateGroupsFragment = new PrivateGroupsFragment();
        Bundle privateGroupsFragmentBundle = new Bundle();
        privateGroupsFragmentBundle.putString(getString(R.string.key_group_type), getString(R.string.key_private_group_name));
        privateGroupsFragment.setArguments(privateGroupsFragmentBundle);
        pagerAdapterFragment.addFragment(privateGroupsFragment);


        PrivateGroupsFragment publicGroupsFragment = new PrivateGroupsFragment();
        Bundle publicGroupsFragmentBundle = new Bundle();
        publicGroupsFragmentBundle.putString(getString(R.string.key_group_type), getString(R.string.key_public_group_name));
        publicGroupsFragment.setArguments(publicGroupsFragmentBundle);

        pagerAdapterFragment.addFragment(publicGroupsFragment);
        //set adapter to ViePager
        viewPager.setAdapter(pagerAdapterFragment);
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


        //set adapter to ViePager

        leaguesPagesViewPager.setAdapter(pagerAdapterFragment1);
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
                String gameCode = settings.getString(getString(R.string.game_code_key), "");


                if (OffsideApplication.isBoundToSignalRService) {
                    if (gameId != null && gameId.length() > 0 && gameCode != null && gameCode.length() > 0)
                        OffsideApplication.signalRService.isGameActive(gameId, gameCode);
                    //OffsideApplication.signalRService.getAvailableGames();
                    OffsideApplication.signalRService.requestPrivateGamesInfo(playerId);


                    //OffsideApplication.signalRService.getAvailableLanguages();

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


}
