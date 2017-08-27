package com.offsidegame.offside.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.offsidegame.offside.R;
import com.offsidegame.offside.events.ConnectionEvent;
import com.offsidegame.offside.events.SignalRServiceBoundEvent;
import com.offsidegame.offside.models.ExperienceLevel;
import com.offsidegame.offside.models.OffsideApplication;
import com.offsidegame.offside.models.UserProfileInfo;

import org.acra.ACRA;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ViewPlayerActivity extends AppCompatActivity {

    //<editor-fold desc="****************MEMBERS**************">

    private final String activityName = "ViewPlayerActivity";
    private final Context context = this;
    private final Activity thisActivity = this;
    private BottomNavigationView bottomNavigationView;

    private String playerId;
    private ExperienceLevel playerCurrentExpLevel;

    private LinearLayout root;
    private LinearLayout loadingRoot;
    private LinearLayout playerMainDetailsRoot;
    private LinearLayout playerAssetsRoot;
    private LinearLayout playerDetailsRoot;
    private LinearLayout latestGameTabRoot;
    private LinearLayout trophiesTabRoot;
    private LinearLayout playerRecordsTabRoot;
    private LinearLayout trophiesDetailsRoot;
    private LinearLayout trophiesClosetRoot;
    private LinearLayout latestGameDetailsRoot;
    private LinearLayout latestGameDetailsElementsRoot;
    private LinearLayout playerRecordsDetailsRoot;
    private LinearLayout podiumRoot;
    private LinearLayout[] winnersPodiumRoots = new LinearLayout[3];

    private TextView versionTextView;
    private TextView powerItemsTextView;
    private TextView balanceTextView;
    private TextView playerNameTextView;
    private TextView playerExperienceLevelTextView;

    private TextView latestGameNotExistTextView;
    private TextView latestGamePrivateGroupTextView;
    private TextView latestGameTitleTextView;
    private TextView latestGameStartDateTextView;
    private TextView latestGamePositionTextView;
    private TextView latestGameAnswersSummaryTextView;
    private TextView latestGameBalanceSummaryTextView;
    private TextView[] winnersNamesTextViews = new TextView[3];
    private TextView[] winnersCoinsTextViews = new TextView[3];
    private TextView trophiesClosetNoTitlesTextView;
    private TextView playerRecordsNumberOfGamesTextView;
    private TextView playerRecordsNumberOfTrophiesTextView;
    private TextView playerRecordsAverageProfitPerGameTextView;


    private ImageView[] winnersImageViews = new ImageView[3];
    private ImageView settingsButtonImageView;
    private ImageView playerPictureImageView;
    //private ImageView playerExperienceLevelImageView;

    private ImageView[] experienceLevelImageViews = new ImageView[5];
    private TextView[] experienceLevelNameTextViews = new TextView[5];
    private TextView[] experienceLevelMinValueTextViews = new TextView[5];

    private HorizontalScrollView trophiesClosetScrollView;
    public ShareButton facebookShareButton;


    //</editor-fold>


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_player);
        FirebaseUser player = FirebaseAuth.getInstance().getCurrentUser();
        playerId = player.getUid();

        //<editor-fold desc="**************FIND***************">

        versionTextView = (TextView) findViewById(R.id.shared_version_text_view);
        versionTextView.setText(OffsideApplication.getVersion() == null ? "0.0" : OffsideApplication.getVersion());

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.l_bottom_navigation_view);
        root = (LinearLayout) findViewById(R.id.vp_view_player_root);

        playerMainDetailsRoot = (LinearLayout) findViewById(R.id.vp_player_main_details_root);
        playerAssetsRoot = (LinearLayout) findViewById(R.id.vp_player_assets_root);
        latestGameTabRoot = (LinearLayout) findViewById(R.id.vp_latest_game_tab_root);
        trophiesTabRoot = (LinearLayout) findViewById(R.id.vp_trophies_tab_root);
        playerRecordsTabRoot = (LinearLayout) findViewById(R.id.vp_records_tab_root);
        playerDetailsRoot = (LinearLayout) findViewById(R.id.vp_player_details_root);
        trophiesDetailsRoot = (LinearLayout) findViewById(R.id.vp_trophies_details_root);
        trophiesClosetRoot = (LinearLayout) findViewById(R.id.vp_trophies_closet_root);
        latestGameDetailsRoot = (LinearLayout) findViewById(R.id.vp_latest_game_details_root);
        latestGameDetailsElementsRoot = (LinearLayout) findViewById(R.id.vp_latest_game_details_elements_root);
        playerRecordsDetailsRoot = (LinearLayout) findViewById(R.id.vp_player_records_details_root);
        podiumRoot = (LinearLayout) findViewById(R.id.vp_latest_game_podium_root);

        powerItemsTextView = (TextView) findViewById(R.id.vp_power_items_text_view);
        balanceTextView = (TextView) findViewById(R.id.vp_balance_text_view);
        playerNameTextView = (TextView) findViewById(R.id.vp_player_name_text_view);
        playerExperienceLevelTextView = (TextView) findViewById(R.id.vp_player_experience_level_text_view);

        latestGameNotExistTextView = (TextView) findViewById(R.id.vp_latest_game_not_exist_text_view);
        latestGamePrivateGroupTextView = (TextView) findViewById(R.id.vp_latest_game_private_group_text_view);
        latestGameTitleTextView = (TextView) findViewById(R.id.vp_latest_game_title_text_view);
        latestGameStartDateTextView = (TextView) findViewById(R.id.vp_latest_game_start_date_text_view);
        latestGamePositionTextView = (TextView) findViewById(R.id.vp_latest_game_position_text_view);
        latestGameAnswersSummaryTextView = (TextView) findViewById(R.id.vp_latest_game_answers_summary_text_view);
        latestGameBalanceSummaryTextView = (TextView) findViewById(R.id.vp_latest_game_balance_summary_text_view);
        trophiesClosetNoTitlesTextView = (TextView) findViewById(R.id.vp_trophies_closet_no_titles_text_view);
        playerRecordsNumberOfGamesTextView = (TextView) findViewById(R.id.vp_player_records_number_of_games_text_view);
        playerRecordsNumberOfTrophiesTextView = (TextView) findViewById(R.id.vp_player_records_number_of_trophies_text_view);
        playerRecordsAverageProfitPerGameTextView = (TextView) findViewById(R.id.vp_player_records_average_profit_per_game_text_view);

        settingsButtonImageView = (ImageView) findViewById(R.id.vp_settings_button_image_view);
        playerPictureImageView = (ImageView) findViewById(R.id.vp_player_picture_image_view);
        //playerExperienceLevelImageView = (ImageView) findViewById(R.id.vp_player_experience_level_image_view);

        facebookShareButton = (ShareButton) findViewById(R.id.vp_facebook_share_button);
        trophiesClosetScrollView = (HorizontalScrollView) findViewById(R.id.vp_trophies_closet_scroll_view);

        for (int i = 0; i < 3; i++) {

            String podiumItemLinearLayoutId = "vp_latest_game_podium_" + (i + 1) + "_root";
            int winnerPodiumItemResourceId = context.getResources().getIdentifier(podiumItemLinearLayoutId, "id", context.getPackageName());
            winnersPodiumRoots[i] = (LinearLayout) findViewById(winnerPodiumItemResourceId);

            String imageViewId = "vp_latest_game_winner" + (i + 1) + "_image_view";
            int winnerImageViewResourceId = context.getResources().getIdentifier(imageViewId, "id", context.getPackageName());
            winnersImageViews[i] = (ImageView) findViewById(winnerImageViewResourceId);

            String nameTextViewId = "vp_latest_game_winner" + (i + 1) + "_text_view";
            int winnerNameTextViewResourceId = context.getResources().getIdentifier(nameTextViewId, "id", context.getPackageName());
            winnersNamesTextViews[i] = (TextView) findViewById(winnerNameTextViewResourceId);

            String coinsTextViewId = "vp_latest_game_winner" + (i + 1) + "_coins_text_view";
            int winnerCoinsTextViewResourceId = context.getResources().getIdentifier(coinsTextViewId, "id", context.getPackageName());
            winnersCoinsTextViews[i] = (TextView) findViewById(winnerCoinsTextViewResourceId);

        }

        for (int i = 0; i < experienceLevelImageViews.length; i++) {
            String imageViewId = "vp_records_details_level_" + (i + 1) + "_image_view";
            int expLevelImageViewResourceId = context.getResources().getIdentifier(imageViewId, "id", context.getPackageName());
            experienceLevelImageViews[i] = (ImageView) findViewById(expLevelImageViewResourceId);

            String nameTextViewId = "vp_records_details_level_" + (i + 1) + "_name_text_view";
            int expLevelNameTextViewResourceId = context.getResources().getIdentifier(nameTextViewId, "id", context.getPackageName());
            experienceLevelNameTextViews[i] = (TextView) findViewById(expLevelNameTextViewResourceId);

            String minValueTextViewId = "vp_records_details_level_" + (i + 1) + "_min_value_text_view";
            int expLevelMinValueTextViewResourceId = context.getResources().getIdentifier(minValueTextViewId, "id", context.getPackageName());
            experienceLevelMinValueTextViews[i] = (TextView) findViewById(expLevelMinValueTextViewResourceId);
        }

        //</editor-fold>

        //<editor-fold desc="************SET CLICKS*************">

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.nav_action_groups:
                        Intent intent = new Intent(context, LobbyActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_action_shop:
                        break;
                    case R.id.nav_action_play:
                        Intent chatIntent = new Intent(context, ChatActivity.class);
                        startActivity(chatIntent);
                        break;

                }

                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_action_profile);

        latestGameTabRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                latestGameDetailsRoot.setVisibility(View.VISIBLE);
                latestGameTabRoot.setBackgroundResource(R.color.navigationMenuSelectedItem);
                trophiesDetailsRoot.setVisibility(View.GONE);
                trophiesTabRoot.setBackgroundResource(R.color.navigationMenu);
                playerRecordsDetailsRoot.setVisibility(View.GONE);
                playerRecordsTabRoot.setBackgroundResource(R.color.navigationMenu);

            }
        });

        trophiesTabRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                latestGameDetailsRoot.setVisibility(View.GONE);
                latestGameTabRoot.setBackgroundResource(R.color.navigationMenu);
                trophiesDetailsRoot.setVisibility(View.VISIBLE);
                trophiesTabRoot.setBackgroundResource(R.color.navigationMenuSelectedItem);
                playerRecordsDetailsRoot.setVisibility(View.GONE);
                playerRecordsTabRoot.setBackgroundResource(R.color.navigationMenu);
            }
        });

        playerRecordsTabRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                latestGameDetailsRoot.setVisibility(View.GONE);
                latestGameTabRoot.setBackgroundResource(R.color.navigationMenu);
                trophiesDetailsRoot.setVisibility(View.GONE);
                trophiesTabRoot.setBackgroundResource(R.color.navigationMenu);
                playerRecordsDetailsRoot.setVisibility(View.VISIBLE);
                playerRecordsTabRoot.setBackgroundResource(R.color.navigationMenuSelectedItem);

                playerRecordsDetailsRoot.setVisibility(View.VISIBLE);
            }
        });

        //</editor-fold>

        resetVisibility();

    }

    public void resetVisibility() {
        loadingRoot.setVisibility(View.VISIBLE);
        //root.setVisibility(View.GONE);
        playerAssetsRoot.setVisibility(View.GONE);
        playerMainDetailsRoot.setVisibility(View.GONE);
        playerDetailsRoot.setVisibility(View.GONE);


        latestGameTabRoot.setBackgroundResource(R.color.navigationMenu);
        latestGameDetailsRoot.setVisibility(View.GONE);
        latestGameDetailsElementsRoot.setVisibility(View.GONE);
        latestGameNotExistTextView.setVisibility(View.GONE);
        podiumRoot.setVisibility(View.GONE);
        for (int i = 0; i < winnersPodiumRoots.length; i++) {
            winnersPodiumRoots[i].setVisibility(View.GONE);
        }


        playerRecordsTabRoot.setBackgroundResource(R.color.navigationMenu);
        playerRecordsDetailsRoot.setVisibility(View.GONE);
        //default set to trophies tab
        trophiesClosetNoTitlesTextView.setVisibility(View.GONE);
        trophiesClosetScrollView.setVisibility(View.GONE);
        trophiesTabRoot.setBackgroundResource(R.color.navigationMenuSelectedItem);
        trophiesDetailsRoot.setVisibility(View.VISIBLE);

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

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onReceiveUserProfileInfo(UserProfileInfo userProfileInfo) {
//        try {
//            if (userProfileInfo == null)
//                return;
//
//            OffsideApplication.setUserProfileInfo(userProfileInfo);
//            PlayerAssets playerAssets;
//            int balance;
//            int powerItems;
//            String playerProfilePictureUrl;
//
//            //set player assets
//            if (OffsideApplication.getPrivateGroupsInfo() == null)
//                return;
//
//            playerAssets = OffsideApplication.getPrivateGroupsInfo().getPlayerAssets();
//            balance = playerAssets.getBalance();
//            powerItems = playerAssets.getPowerItems();
//            playerProfilePictureUrl = playerAssets.getImageUrl();
//
//
//            balanceTextView.setText(Integer.toString(balance));
//            powerItemsTextView.setText(Integer.toString(powerItems));
//
//            playerNameTextView.setText(userProfileInfo.getPlayerName());
//            playerExperienceLevelTextView.setText(userProfileInfo.getExperienceLevelName());
//            playerCurrentExpLevel = ExperienceLevel.findByName(userProfileInfo.getExperienceLevelName());
//            if (playerCurrentExpLevel == null)
//                playerCurrentExpLevel = ExperienceLevel.expLevel1;
//            //playerExperienceLevelImageView.setImageResource(playerCurrentExpLevel.getImageViewResourceId());
//            ImageHelper.loadImage(thisActivity, playerProfilePictureUrl, playerPictureImageView, activityName);
//
//            //<editor-fold desc="---------RECENT GAME-----------">
//
//            PlayerGame mostRecentGamePlayed = userProfileInfo.getMostRecentGamePlayed();
//            if (mostRecentGamePlayed != null) {
//                latestGamePrivateGroupTextView.setText(mostRecentGamePlayed.getGroupName());
//                latestGameTitleTextView.setText(mostRecentGamePlayed.getGameTitle());
//
//                PrettyTime p = new PrettyTime();
//
//
//                latestGameStartDateTextView.setText(p.format(mostRecentGamePlayed.getGameStartTime()));
//
//
//                String latestGamePositionOutOfText = Integer.toString(mostRecentGamePlayed.getPosition()) + " " + getString(R.string.lbl_out_of) + " " + Integer.toString(mostRecentGamePlayed.getTotalPlayers());
//                latestGamePositionTextView.setText(latestGamePositionOutOfText);
//
//                String latestGameAnswersSummaryOutOfText = Integer.toString(mostRecentGamePlayed.getCorrectAnswersCount()) + " " + getString(R.string.lbl_out_of) + " " + Integer.toString(mostRecentGamePlayed.getTotalQuestionsAsked());
//                latestGameAnswersSummaryTextView.setText(latestGameAnswersSummaryOutOfText);
//
//                latestGameBalanceSummaryTextView.setText(Integer.toString(mostRecentGamePlayed.getOffsideCoins()));
//
//                List<Winner> winners = userProfileInfo.getMostRecentGamePlayed().getWinners();
//
//                int j = 0;
//
//                for (Winner winner : winners) {
//                    Uri winnerProfilePictureUri = Uri.parse(winner.getImageUrl());
//                    ImageHelper.loadImage(thisActivity, winnersImageViews[j], winnerProfilePictureUri);
//                    winnersNamesTextViews[j].setText(winner.getPlayerName());
//                    winnersCoinsTextViews[j].setText(Integer.toString(winner.getOffsideCoins()));
//                    winnersPodiumRoots[j].setVisibility(View.VISIBLE);
//                    j++;
//                }
//                podiumRoot.setVisibility(View.VISIBLE);
//
//
//                latestGameDetailsElementsRoot.setVisibility(View.VISIBLE);
//                latestGameNotExistTextView.setVisibility(View.GONE);
//
//            }
//            else {
//                latestGameDetailsElementsRoot.setVisibility(View.GONE);
//                latestGameNotExistTextView.setVisibility(View.VISIBLE);
//            }
//
//        //</editor-fold>
//
//            //<editor-fold desc="---------TROPHIES CLOSET-----------">
//
//            trophiesClosetRoot.removeAllViews();
//
//            ArrayList<Reward> playerRewards = userProfileInfo.getRewards();
//
//            Collections.sort(playerRewards, new Comparator<Reward>() {
//                public int compare(Reward r1, Reward r2) {
//                    int result = 0;
//                    if (r1.getGameStartDate().after(r2.getGameStartDate()))
//                        result = 1;
//                    return result;
//                }
//            });
//
//            Boolean hasReward = false;
//
//            for (Reward reward : playerRewards) {
//
//                if (!(reward.getRewardTypeName() == null || reward.getRewardTypeName().equals("NONE"))) {
//
//                    hasReward = true;
//                    ViewGroup trophiesLayout = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.trophy_item, trophiesClosetRoot, false);
//
//                    TextView groupNameTextView = (TextView) trophiesLayout.getChildAt(0);
//                    groupNameTextView.setText(reward.getGroupName());
//
//                    TextView positionOutOfTextView = (TextView) trophiesLayout.getChildAt(1);
//                    String positionOutOfText = Integer.toString(reward.getPosition()) +" "+ getString(R.string.lbl_out_of)+" " + Integer.toString(reward.getTotalPlayers());
//                    positionOutOfTextView.setText(positionOutOfText);
//
//                    ImageView trophyImageImageView = (ImageView) trophiesLayout.getChildAt(2);
////                trophyImageImageView.getLayoutParams().height = 70;
////                trophyImageImageView.getLayoutParams().width = 70;
//                    trophyImageImageView.requestLayout();
//
//                    int trophyResourceId = reward.getRewardImageResourceIdByRewardType();
//                    ImageHelper.loadImage(context, trophyImageImageView, trophyResourceId);
//
//                    TextView gameTitleTextView = (TextView) trophiesLayout.getChildAt(3);
//                    gameTitleTextView.setText(reward.getGameTitle());
//
//                    TextView gameDateTextView = (TextView) trophiesLayout.getChildAt(4);
//                    //gameDateTextView.setText(reward.getGameStartDate().toString());
//                    Date gameStartDate = reward.getGameStartDate();
//                    PrettyTime pt = new PrettyTime();
//                    gameDateTextView.setText(pt.format(gameStartDate));
//
//                    trophiesClosetRoot.addView(trophiesLayout);
//                }
//
//            }
//            if (!hasReward) {
//                trophiesClosetNoTitlesTextView.setVisibility(View.VISIBLE);
//                trophiesClosetScrollView.setVisibility(View.GONE);
//
//            } else {
//                trophiesClosetNoTitlesTextView.setVisibility(View.GONE);
//                trophiesClosetScrollView.setVisibility(View.VISIBLE);
//
//            }
//
//
//            trophiesDetailsRoot.setVisibility(View.VISIBLE);
//
//
//            //</editor-fold>
//
//            //<editor-fold desc="---------PLAYER RECORDS-----------">
//
//            int numberOfGames = userProfileInfo.getTotalGamesPlayed();
//            int numberOfTrophies = userProfileInfo.getTotalTrophies();
//            int averageProfitPerGame = (int) userProfileInfo.getAverageProfitPerGame();
//            playerRecordsNumberOfGamesTextView.setText(Integer.toString(numberOfGames));
//            playerRecordsNumberOfTrophiesTextView.setText(Integer.toString(numberOfTrophies));
//            playerRecordsAverageProfitPerGameTextView.setText(Integer.toString(averageProfitPerGame));
//
//            for (int i = 0; i < experienceLevelImageViews.length; i++) {
//                ExperienceLevel currentExpLevel = ExperienceLevel.expLevels.get(i);
//
//                experienceLevelNameTextViews[i].setText(currentExpLevel.getName());
//                experienceLevelMinValueTextViews[i].setText(Integer.toString(currentExpLevel.getMinValue()));
//
//                if (playerCurrentExpLevel.getName().equals(currentExpLevel.getName()))
//                    //ImageHelper.loadImage(context, experienceLevelImageViews[i], currentExpLevel.getImageViewResourceIdCurrent());
//                    experienceLevelImageViews[i].setImageResource(currentExpLevel.getImageViewResourceIdCurrent());
//                else
//                    //ImageHelper.loadImage(context, experienceLevelImageViews[i], currentExpLevel.getImageViewResourceId());
//                    experienceLevelImageViews[i].setImageResource(currentExpLevel.getImageViewResourceId());
//            }
//
//            final Bitmap bitmapImage = BitmapFactory.decodeResource(context.getResources(), playerCurrentExpLevel.getImageViewResourceId());
//            String messageText = "I am a " + playerCurrentExpLevel.getName() + " on the Sidekick game";
//            shareOnFacebook(facebookShareButton, bitmapImage, messageText);
//
//
//            //</editor-fold>
//
//            loadingRoot.setVisibility(View.GONE);
//            //root.setVisibility(View.VISIBLE);
//            playerAssetsRoot.setVisibility(View.VISIBLE);
//            playerMainDetailsRoot.setVisibility(View.VISIBLE);
//            playerDetailsRoot.setVisibility(View.VISIBLE);
//
//        } catch (Exception ex) {
//            ACRA.getErrorReporter().handleSilentException(ex);
//
//        }
//
//    }

    public void shareOnFacebook(ShareButton facebookShareButton, Bitmap bitmapImage, String quote) {

        try {
            StringBuilder sb = new StringBuilder();

            sb.append(quote);
            sb.append("\r\n");
            sb.append("\r\n");
            sb.append("think you can bit me? come join!!!");

//    Share link content
            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(OffsideApplication.getAppLogoPictureUrl()))
                    .setShareHashtag(new ShareHashtag.Builder()
                            .setHashtag("#Sidekick#soccer#livegame")
                            .build())
                    //.setQuote(quote+"\r\n"+ "think you can bit me? come join!!!" )
                    .setQuote(sb.toString())
                    .build();
            facebookShareButton.setShareContent(content);

////    Share photo - not working
//            SharePhoto photo = new SharePhoto.Builder()
//                    .setBitmap(bitmapImage)
//                    .build();
//
//            SharePhotoContent content = new SharePhotoContent.Builder()
//                    .addPhoto(photo)
//                    .build();
//
//            //ShareDialog.show(thisActivity, content);
//            facebookShareButton.setShareContent(content);


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

                if (OffsideApplication.isPlayerQuitGame()) {
                    loadingRoot.setVisibility(View.GONE);
                    return;
                }


                if (OffsideApplication.isBoundToSignalRService) {

                    if (playerId == null)
                        return;
                    UserProfileInfo userProfileInfo = OffsideApplication.getUserProfileInfo();
                    if (userProfileInfo == null)
                        OffsideApplication.signalRService.RequestUserProfileData(playerId);
                    else

                        EventBus.getDefault().post(userProfileInfo);

                } else
                    throw new RuntimeException(activityName + " - onSignalRServiceBinding - Error: SignalRIsNotBound");


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
