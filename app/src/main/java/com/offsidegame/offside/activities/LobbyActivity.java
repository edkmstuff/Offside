package com.offsidegame.offside.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.offsidegame.offside.R;
import com.offsidegame.offside.adapters.GroupsPagerAdapterFragment;

import com.offsidegame.offside.events.ActiveGameEvent;
import com.offsidegame.offside.events.AvailableGamesEvent;
import com.offsidegame.offside.events.AvailableLanguagesEvent;
import com.offsidegame.offside.events.ConnectionEvent;
import com.offsidegame.offside.events.JoinGameEvent;
import com.offsidegame.offside.events.PrivateGameGeneratedEvent;
import com.offsidegame.offside.events.SignalRServiceBoundEvent;
import com.offsidegame.offside.fragments.PrivateGroupsFragment;

import com.offsidegame.offside.fragments.PublicGroupsFragment;
import com.offsidegame.offside.helpers.ImageHelper;
import com.offsidegame.offside.models.AvailableGame;
import com.offsidegame.offside.models.GameInfo;
import com.offsidegame.offside.models.OffsideApplication;
import com.offsidegame.offside.models.PlayerAssets;
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
    private TextView createPrivateGroupButtonTextView;
    private LinearLayout privateGroupsRoot;
    private BottomNavigationView bottomNavigationView;
    private LinearLayout[] viewRoots = new LinearLayout[4];
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabLayout.OnTabSelectedListener listener;

    //loading
    private LinearLayout loadingGameRoot;
    private TextView versionTextView;

    //playerAssets
    private TextView balanceTextView;
    private TextView powerItemsTextView;


    //profile
    private TextView userNameTextView;
    private ImageView playerPictureImageView;
    private String playerId;
    private String playerDisplayName;
    private String playerProfilePictureUrl;
    private SharedPreferences settings;

    //create private group form
    private LinearLayout createPrivateGroupFormRoot;
    private Spinner availableLanguagesSpinner;
    private EditText privateGroupNameEditText;
    private String[] availableLanguages;
    private TextView noAvailableGamesReturnLaterTextView;
    private TextView savePrivateGroupButtonTextView;
    //</editor-fold>


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_lobby);

            settings = getSharedPreferences(getString(R.string.preference_name), 0);

            FirebaseUser player = FirebaseAuth.getInstance().getCurrentUser();
            playerDisplayName = player.getDisplayName();
            playerId = player.getUid();

//////////////////////////////////////////////////////////////////////////////////////////////

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


            versionTextView = (TextView) findViewById(R.id.l_version_text_view);
            versionTextView.setText(OffsideApplication.getVersion() == null ? "0.0" : OffsideApplication.getVersion());


            playerPictureImageView = (ImageView) findViewById(R.id.l_player_picture_image_view);
            balanceTextView = (TextView) findViewById(R.id.l_balance_text_view);
            powerItemsTextView = (TextView) findViewById(R.id.l_power_items_text_view);
            createPrivateGroupButtonTextView = (TextView) findViewById(R.id.l_create_private_group_button_text_view);
            savePrivateGroupButtonTextView = (TextView) findViewById(R.id.l_save_private_group_button_text_view);
            privateGroupsRoot = (LinearLayout) findViewById(R.id.l_private_groups_root);

            viewRoots[0] = (LinearLayout) findViewById(R.id.l_private_groups_root);
            viewRoots[1] = (LinearLayout) findViewById(R.id.l_profile_root);
            viewRoots[2] = (LinearLayout) findViewById(R.id.l_shop_root);
            viewRoots[3] = (LinearLayout) findViewById(R.id.l_play_root);

            createPrivateGroupFormRoot = (LinearLayout) findViewById(R.id.l_create_private_group_form_root);
            availableLanguagesSpinner = (Spinner) findViewById(R.id.l_available_language_spinner);
            privateGroupNameEditText = (EditText) findViewById(R.id.l_private_game_name_edit_text);
            bottomNavigationView = (BottomNavigationView) findViewById(R.id.l_bottom_navigation_view);

            resetVisibility();
            viewRoots[0].setVisibility(View.VISIBLE);

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


//            userNameTextView = (TextView) findViewById(R.id.jg_user_name_text_view);
//            userNameTextView.setText(playerDisplayName);
//
//            playerPictureImageView = (ImageView) findViewById(R.id.jg_player_picture_image_view);
//
//            playerProfilePictureUrl = settings.getString(getString(R.string.player_profile_picture_url_key), null);
//            playerProfilePictureUrl = playerProfilePictureUrl == null ? OffsideApplication.getDefaultProfilePictureUrl() : playerProfilePictureUrl;
//            ImageHelper.loadImage(thisActivity, playerProfilePictureUrl, playerPictureImageView, activityName);
//

//




            createPrivateGroupButtonTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    for (LinearLayout layout : viewRoots) {
                        layout.setVisibility(View.GONE);

                    }

                    privateGroupNameEditText.setText(playerDisplayName.split(" ")[0] + "'s" + " friends");
                    createPrivateGroupButtonTextView.setVisibility(View.GONE);
                    createPrivateGroupFormRoot.setVisibility(View.VISIBLE);
                }
            });
//
            savePrivateGroupButtonTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //get language
                    String selectedLanguage = availableLanguagesSpinner.getSelectedItem().toString();

                    String groupName = privateGroupNameEditText.getText().toString();
                    groupName = groupName.length() > 20 ? groupName.substring(0, 20) : groupName;

                    //todo: change this call to a method that create the group only , no game entry is required

                    if (OffsideApplication.isBoundToSignalRService)
                        OffsideApplication.signalRService.createPrivateGroup(groupName, playerId, selectedLanguage);
                    else
                        throw new RuntimeException(activityName + " - generatePrivateGameCodeButtonTextView - onClick - Error: SignalRIsNotBound");

                    createPrivateGroupFormRoot.setVisibility(View.GONE);
                    privateGroupsRoot.setVisibility(View.VISIBLE);
                }
            });




        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }


    }

    private void resetVisibility() {

        privateGroupsRoot.setVisibility(View.VISIBLE);
        createPrivateGroupButtonTextView.setVisibility(View.VISIBLE);
        createPrivateGroupFormRoot.setVisibility(View.GONE);

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
        super.onStop();
    }


//    private void joinGame(String privateGameCode, boolean isPrivateGameCreator) {
//        if (OffsideApplication.isBoundToSignalRService) {
//
//            OffsideApplication.setIsPlayerQuitGame(false);
//            String androidDeviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
//
//
//            OffsideApplication.signalRService.joinGame(privateGameCode, playerId, playerDisplayName, playerProfilePictureUrl, isPrivateGameCreator, androidDeviceId);
//            loadingGameRoot.setVisibility(View.VISIBLE);
//            joinGameRoot.setVisibility(View.GONE);
//            createPrivateGroupFormRoot.setVisibility(View.GONE);
//        }
//    }



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
                    OffsideApplication.signalRService.RequestPrivateGamesInfo(playerId);


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






    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveAvailableLanguages(AvailableLanguagesEvent availableLanguagesEvent) {
        try {
            availableLanguages = availableLanguagesEvent.getAvailableLanquages();


            setAvailableLanguageSpinnerAdapter(availableLanguages);
        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }
    }




    private void setAvailableLanguageSpinnerAdapter(String[] languages) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, languages);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        availableLanguagesSpinner.setAdapter(adapter);
        if (languages.length > 0)
            availableLanguagesSpinner.setSelection(0);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPrivateGameGenerated(PrivateGameGeneratedEvent privateGameGeneratedEvent) {
        try {
        //todo:update GameInfo
        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }

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
            String playerImageUrl = playerAssets.getImageUrl();
            balanceTextView.setText(Integer.toString(balance));
            powerItemsTextView.setText(Integer.toString(powerItems));
            playerProfilePictureUrl = settings.getString(getString(R.string.player_profile_picture_url_key), null);
            playerProfilePictureUrl = playerProfilePictureUrl == null ? playerImageUrl : playerProfilePictureUrl;
            ImageHelper.loadImage(thisActivity, playerProfilePictureUrl, playerPictureImageView, activityName);

            //update groups stuff
//            PrivateGroup[] privateGroups = privateGroupsInfo.getPrivateGroups();
//
//            privateGroupsArrayList = new ArrayList(Arrays.asList(privateGroups));
//
//            privateGroupAdapter = new PrivateGroupAdapter(context, privateGroupsArrayList);
//
//            privateGroupsListView.setAdapter(privateGroupAdapter);

            this.addGroupsCategories();
            tabLayout.addOnTabSelectedListener(listener);



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

    private void addGroupsCategories(){

        GroupsPagerAdapterFragment pagerAdapterFragment = new GroupsPagerAdapterFragment(this.getSupportFragmentManager());
        pagerAdapterFragment.addFragment(new PrivateGroupsFragment());
        pagerAdapterFragment.addFragment(new PublicGroupsFragment());
        //set adapter to ViePager
        viewPager.setAdapter(pagerAdapterFragment);
    }




}
