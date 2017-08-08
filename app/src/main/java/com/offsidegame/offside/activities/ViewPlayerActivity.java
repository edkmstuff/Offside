package com.offsidegame.offside.activities;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.offsidegame.offside.R;
import com.offsidegame.offside.events.ConnectionEvent;
import com.offsidegame.offside.events.SignalRServiceBoundEvent;
import com.offsidegame.offside.helpers.ImageHelper;
import com.offsidegame.offside.models.OffsideApplication;
import com.offsidegame.offside.models.Player;
import com.offsidegame.offside.models.PlayerAssets;
import com.offsidegame.offside.models.Reward;
import com.offsidegame.offside.models.UserProfileInfo;
import com.offsidegame.offside.models.PlayerInGameRecord;

import org.acra.ACRA;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ViewPlayerActivity extends AppCompatActivity {

    private final String activityName = "ViewPlayerActivity";
    private final Context context = this;
    private final Activity thisActivity = this;
    private String playerId;
    private String playerDisplayName;



    private LinearLayout root;
    private LinearLayout loadingRoot;
    private LinearLayout playerAssetsRoot;
    private LinearLayout playerDetailsRoot;
    private LinearLayout latestGameTabRoot;
    private LinearLayout trophiesTabRoot;
    private LinearLayout recordsTabRoot;
    private LinearLayout trophiesDetailsRoot;
    private LinearLayout latestGameDetailsRoot;
    private LinearLayout recordsDetailsRoot;
    private LinearLayout podiumRoot;

    private TextView powerItemsTextView;
    private TextView balanceTextView;
    private TextView playerNameTextView;
    private TextView playerExperienceLevelTextView;

    private TextView latestGamePrivateGroupTextView;
    private TextView latestGameTitleTextView;
    private TextView latestGameStartDateTextView;
    private TextView latestGamePositionTextView;
    private TextView latestGameAnswersSummaryTextView;
    private TextView latestGameBalanceSummaryTextView;
    private TextView[] winnersNamesTextViews = new TextView[3];
    private TextView[] winnersCoinsTextViews = new TextView[3];

    private ImageView[] winnersImageViews = new ImageView[3];
    private ImageView settingsButtonImageView;
    private ImageView playerPictureImageView;
    private ImageView playerExperienceLevelImageView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_player);

        FirebaseUser player = FirebaseAuth.getInstance().getCurrentUser();
        playerDisplayName = player.getDisplayName();
        playerId = player.getUid();

        root = (LinearLayout) findViewById(R.id.vp_view_player_root);
        loadingRoot = (LinearLayout) findViewById(R.id.shared_loading_root);
        playerAssetsRoot =  (LinearLayout) findViewById(R.id.vp_player_assets_root);
        latestGameTabRoot = (LinearLayout) findViewById(R.id.vp_latest_game_tab_root);
        trophiesTabRoot = (LinearLayout) findViewById(R.id.vp_trophies_tab_root);
        recordsTabRoot = (LinearLayout) findViewById(R.id.vp_records_tab_root);
        playerDetailsRoot = (LinearLayout) findViewById(R.id.vp_player_details_root);
        trophiesDetailsRoot = (LinearLayout) findViewById(R.id.vp_trophies_details_root);
        latestGameDetailsRoot = (LinearLayout) findViewById(R.id.vp_latest_game_details_root);
        recordsDetailsRoot = (LinearLayout) findViewById(R.id.vp_records_details_root);
        podiumRoot = (LinearLayout) findViewById(R.id.vp_latest_game_podium_root);

        powerItemsTextView = (TextView) findViewById(R.id.vp_power_items_text_view);
        balanceTextView = (TextView) findViewById(R.id.vp_balance_text_view);
        playerNameTextView = (TextView) findViewById(R.id.vp_player_name_text_view);
        playerExperienceLevelTextView = (TextView) findViewById(R.id.vp_player_experience_level_text_view);
        latestGamePrivateGroupTextView = (TextView) findViewById(R.id.vp_latest_game_private_group_text_view);
        latestGameTitleTextView = (TextView) findViewById(R.id.vp_latest_game_title_text_view);
        latestGameStartDateTextView = (TextView) findViewById(R.id.vp_latest_game_start_date_text_view);
        latestGamePositionTextView = (TextView) findViewById(R.id.vp_latest_game_position_text_view);
        latestGameAnswersSummaryTextView = (TextView) findViewById(R.id.vp_latest_game_answers_summary_text_view);
        latestGameBalanceSummaryTextView = (TextView) findViewById(R.id.vp_latest_game_balance_summary_text_view);

        settingsButtonImageView = (ImageView) findViewById(R.id.vp_settings_button_image_view);
        playerPictureImageView = (ImageView) findViewById(R.id.vp_player_picture_image_view);
        playerExperienceLevelImageView = (ImageView) findViewById(R.id.vp_player_experience_level_image_view);

        for(int i=0;i<3;i++){
            String imageViewId = "vp_latest_game_winner"+(i+1)+"_image_view";
            int winnerImageViewId = context.getResources().getIdentifier(imageViewId, "id", context.getPackageName());
            winnersImageViews[i] = (ImageView) findViewById(winnerImageViewId);

            String nameTextViewId = "vp_latest_game_winner"+(i+1)+"_text_view";
            int winnerNameTextViewId = context.getResources().getIdentifier(nameTextViewId, "id", context.getPackageName());
            winnersNamesTextViews[i] = (TextView) findViewById(winnerNameTextViewId);

            String coinsTextViewId = "vp_latest_game_winner"+(i+1)+"_coins_text_view";
            int winnerCoinsTextViewId = context.getResources().getIdentifier(coinsTextViewId, "id", context.getPackageName());
            winnersNamesTextViews[i] = (TextView) findViewById(winnerCoinsTextViewId);

        }


        latestGameTabRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                latestGameDetailsRoot.setVisibility(View.VISIBLE);
                trophiesDetailsRoot.setVisibility(View.GONE);
                recordsDetailsRoot.setVisibility(View.GONE);

            }
        });

        trophiesDetailsRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                latestGameDetailsRoot.setVisibility(View.GONE);
                trophiesDetailsRoot.setVisibility(View.VISIBLE);
                recordsDetailsRoot.setVisibility(View.GONE);
            }
        });

        recordsDetailsRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                latestGameDetailsRoot.setVisibility(View.GONE);
                trophiesDetailsRoot.setVisibility(View.GONE);
                recordsDetailsRoot.setVisibility(View.VISIBLE);
            }
        });

        resetVisibility();

    }

    public void resetVisibility(){
        loadingRoot.setVisibility(View.VISIBLE);
        root.setVisibility(View.GONE);
        latestGameTabRoot.setBackgroundResource(R.color.navigationMenu);
        latestGameDetailsRoot.setVisibility(View.GONE);
        recordsTabRoot.setBackgroundResource(R.color.navigationMenu);
        recordsDetailsRoot.setVisibility(View.GONE);
        //default set to trophies tab
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveUserProfileInfo(UserProfileInfo userProfileInfo) {
        try {
            if (userProfileInfo == null )
                return;

            OffsideApplication.setUserProfileInfo(userProfileInfo);
            PlayerAssets playerAssets;
            int balance;
            int powerItems;
            String playerProfilePictureUrl;

            //set player assets
            if(OffsideApplication.getPrivateGroupsInfo()!=null){
                playerAssets = OffsideApplication.getPrivateGroupsInfo().getPlayerAssets();
                balance = playerAssets.getBalance();
                powerItems = playerAssets.getPowerItems();
                playerProfilePictureUrl = playerAssets.getImageUrl();
            }
            else{
                balance = userProfileInfo.getPlayer().getBalance();
                powerItems = userProfileInfo.getPlayer().getPowerItems();
                playerProfilePictureUrl = userProfileInfo.getPlayer().getImageUrl();
            }

            balanceTextView.setText(Integer.toString(balance));
            powerItemsTextView.setText(Integer.toString(powerItems));

            playerNameTextView.setText(userProfileInfo.getPlayer().getUserName());
            playerExperienceLevelTextView.setText(userProfileInfo.getPlayer().getLevelOfExperience());
            playerExperienceLevelImageView.setImageResource(userProfileInfo.getPlayer().getLevelOfExperienceImageResourceIdByLevelOfExperience());
            ImageHelper.loadImage(thisActivity, playerProfilePictureUrl, playerPictureImageView, activityName);

            //set latest game tab details

            PlayerInGameRecord mostRecentGamePlayed = userProfileInfo.getMostRecentPlayedGame();
            latestGamePrivateGroupTextView.setText(mostRecentGamePlayed.getPrivateGroup().getName());
            latestGameTitleTextView.setText(mostRecentGamePlayed.getGameTitle());
            latestGameStartDateTextView.setText(mostRecentGamePlayed.getDateUpdated().toString());

            String latestGamePositionOutOfText = Integer.toString(mostRecentGamePlayed.getPosition().getPrivateGamePosition()) + "Out of" + Integer.toString(mostRecentGamePlayed.getPosition().getPrivateGameTotalPlayers());
            latestGamePositionTextView.setText(latestGamePositionOutOfText);

            String latestGameAnswersSummaryOutOfText = Integer.toString(mostRecentGamePlayed.getTotalAnsweredQuestions()) + "Out of" + Integer.toString(mostRecentGamePlayed.getTotalQuestions());
            latestGameAnswersSummaryTextView.setText(latestGameAnswersSummaryOutOfText);

            latestGameBalanceSummaryTextView.setText(Integer.toString(mostRecentGamePlayed.getOffsideCoins()));

            //podium stuff
            List<Player> winners = userProfileInfo.getMostRecentPlayedGameWinners();

            //option 1 - podium
            int j=0;
            for (Player winner : winners){
                Uri winnerProfilePictureUri = Uri.parse(winner.getImageUrl());
                ImageHelper.loadImage(thisActivity, winnersImageViews[j], winnerProfilePictureUri);
                winnersNamesTextViews[j].setText(winner.getUserName());
                winnersCoinsTextViews[j].setText(winner.getOffsideCoins());
                j++;

            }
            //end of option 1

            //option 2------like the winner chat message
            podiumRoot.removeAllViews();

            for (Player winner : winners) {

                ViewGroup podiumViewGroup = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.winner_item, podiumRoot, false);
                TextView winnerScoreTextView = (TextView) podiumViewGroup.getChildAt(0);
                ImageView winnerPrizeImageView = (ImageView) podiumViewGroup.getChildAt(1);
                ImageView winnerPictureImageView = (ImageView) podiumViewGroup.getChildAt(2);
                TextView winnerNameTextView = (TextView) podiumViewGroup.getChildAt(3);

                Uri winnerProfilePictureUri = Uri.parse(winner.getImageUrl());
                winnerScoreTextView.setText(Integer.toString(winner.getOffsideCoins()));
                int resourceId = winner.getPrivateGamePosition() == 1 ? R.mipmap.trophy_gold : winner.getPrivateGamePosition() == 2 ? R.mipmap.trophy_silver : R.mipmap.trophy_bronze;
                if (winner.getPrivateGamePosition() == 2)
                    podiumViewGroup.setPadding(0, 30, 0, 0);


                if (winner.getPrivateGamePosition() == 3)
                    podiumViewGroup.setPadding(0, 60, 0, 0);

                winnerPrizeImageView.setImageResource(resourceId);
                ImageHelper.loadImage(thisActivity, winnerPictureImageView, winnerProfilePictureUri);
                winnerNameTextView.setText(winner.getUserName());
                podiumRoot.addView(podiumViewGroup);
            }
            //end of option 2

            //set trophies tab
            trophiesDetailsRoot.removeAllViews();

            ArrayList<Reward> playerRewards = userProfileInfo.getRewards();

            Collections.sort(playerRewards, new Comparator<Reward>() {
                public int compare(Reward r1, Reward r2) {
                    int result=0;
                    if(r1.getGameStartDate().after(r2.getGameStartDate()))
                        result=1;
                    return result;
                }
            });

            for(Reward reward: playerRewards){

                ViewGroup trophiesLayout = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.trophy_item, trophiesDetailsRoot,false);

                TextView groupNameTextView = (TextView) trophiesLayout.getChildAt(0);
                groupNameTextView.setText(reward.getPrivateGroupName());

                TextView positionOutOfTextView = (TextView) trophiesLayout.getChildAt(1);
                String positionOutOfText = Integer.toString(reward.getPosition().getPrivateGamePosition()) + "Out of" + Integer.toString(reward.getPosition().getPrivateGameTotalPlayers());
                positionOutOfTextView.setText(positionOutOfText);

                ImageView trophyImageImageView = (ImageView) trophiesLayout.getChildAt(2);
//                trophyImageImageView.getLayoutParams().height = 70;
//                trophyImageImageView.getLayoutParams().width = 70;
                trophyImageImageView.requestLayout();
                int trophyResourceId = reward.getRewardImageResourceIdByRewardType();
                ImageHelper.loadImage(context,trophyImageImageView,trophyResourceId);

                TextView gameTitleTextView = (TextView) trophiesLayout.getChildAt(3);
                gameTitleTextView.setText(reward.getGameTitle());

                TextView gameDateTextView = (TextView) trophiesLayout.getChildAt(4);
                gameDateTextView.setText((CharSequence) reward.getGameStartDate());

                trophiesDetailsRoot.addView(trophiesLayout);
            }


            //set player records tab details
            //todo: add code here after decide what we put there



            loadingRoot.setVisibility(View.GONE);
            root.setVisibility(View.VISIBLE);

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

                    if(playerId==null)
                        return;
                    UserProfileInfo userProfileInfo = OffsideApplication.getUserProfileInfo();
                    if(userProfileInfo==null)
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
