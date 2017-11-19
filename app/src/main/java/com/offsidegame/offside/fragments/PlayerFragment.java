package com.offsidegame.offside.fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.offsidegame.offside.R;
import com.offsidegame.offside.helpers.Formatter;
import com.offsidegame.offside.helpers.ImageHelper;
import com.offsidegame.offside.models.ExperienceLevel;
import com.offsidegame.offside.models.OffsideApplication;
import com.offsidegame.offside.models.PlayerAssets;
import com.offsidegame.offside.models.PlayerGame;
import com.offsidegame.offside.models.Reward;
import com.offsidegame.offside.models.UserProfileInfo;
import com.offsidegame.offside.models.Winner;

import org.acra.ACRA;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by user on 8/22/2017.
 */


public class PlayerFragment extends Fragment {

    //<editor-fold desc="****************MEMBERS**************">
    private PlayerAssets playerAssets;
    private String playerId;
    private ExperienceLevel playerCurrentExpLevel;

    private FrameLayout loadingRoot;
    private TextView versionTextView;

    private LinearLayout playerMainDetailsRoot;
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

    //private TextView powerItemsTextView;
    //private TextView balanceTextView;
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

    private TextView latestGameTabTextView;
    private TextView trophiesTabTextView;
    private TextView playerRecordsTabTextView;


    private ImageView[] winnersImageViews = new ImageView[3];

    private ImageView playerPictureImageView;
    //private ImageView playerExperienceLevelImageView;

    private ImageView[] experienceLevelImageViews = new ImageView[5];
    private TextView[] experienceLevelNameTextViews = new TextView[5];
    private TextView[] experienceLevelMinValueTextViews = new TextView[5];

    private HorizontalScrollView trophiesClosetScrollView;
    public ShareButton facebookShareButton;

    //</editor-fold>

    public static PlayerFragment newInstance() {
        PlayerFragment playerFragment = new PlayerFragment();
        return playerFragment;
    }

    @Override
    public void onResume(){
        super.onResume();
        EventBus.getDefault().register(this);
        playerId = OffsideApplication.getPlayerId();
        OffsideApplication.networkingService.requestUserProfileData(playerId);
    }

    @Override
    public void onPause(){
        super.onPause();
        EventBus.getDefault().unregister(this);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_player, container, false);

            Context context = getContext();

            getIDs(view, context);
            setEvents();

            versionTextView.setText(OffsideApplication.getVersion() == null ? "0.0" : OffsideApplication.getVersion());

            playerAssets = OffsideApplication.getPlayerAssets();

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            playerId = firebaseUser.getUid();
            String userDisplayName = firebaseUser.getDisplayName();
            userDisplayName = userDisplayName == null ? "No Name" : userDisplayName;

            ImageHelper.loadImage(context,playerPictureImageView,Uri.parse(playerAssets.getImageUrl()), true);
            playerNameTextView.setText(userDisplayName);

            resetVisibility();

            return view;



    }

    private void getIDs(View view, Context context) {

        loadingRoot = (FrameLayout) view.findViewById(R.id.shared_loading_root);
        versionTextView = (TextView) view.findViewById(R.id.shared_version_text_view);
        playerMainDetailsRoot = (LinearLayout) view.findViewById(R.id.vp_player_main_details_root);
        latestGameTabRoot = (LinearLayout) view.findViewById(R.id.vp_latest_game_tab_root);
        trophiesTabRoot = (LinearLayout) view.findViewById(R.id.vp_trophies_tab_root);
        playerRecordsTabRoot = (LinearLayout) view.findViewById(R.id.vp_records_tab_root);
        playerDetailsRoot = (LinearLayout) view.findViewById(R.id.vp_player_details_root);
        trophiesDetailsRoot = (LinearLayout) view.findViewById(R.id.vp_trophies_details_root);
        trophiesClosetRoot = (LinearLayout) view.findViewById(R.id.vp_trophies_closet_root);
        latestGameDetailsRoot = (LinearLayout) view.findViewById(R.id.vp_latest_game_details_root);
        latestGameDetailsElementsRoot = (LinearLayout) view.findViewById(R.id.vp_latest_game_details_elements_root);
        playerRecordsDetailsRoot = (LinearLayout) view.findViewById(R.id.vp_player_records_details_root);
        podiumRoot = (LinearLayout) view.findViewById(R.id.vp_latest_game_podium_root);

        //powerItemsTextView = (TextView) view.findViewById(R.id.vp_power_items_text_view);
        //balanceTextView = (TextView) view.findViewById(R.id.vp_balance_text_view);
        playerNameTextView = (TextView) view.findViewById(R.id.vp_player_name_text_view);
        playerExperienceLevelTextView = (TextView) view.findViewById(R.id.vp_player_experience_level_text_view);

        latestGameNotExistTextView = (TextView) view.findViewById(R.id.vp_latest_game_not_exist_text_view);
        latestGamePrivateGroupTextView = (TextView) view.findViewById(R.id.vp_latest_game_private_group_text_view);
        latestGameTitleTextView = (TextView) view.findViewById(R.id.vp_latest_game_title_text_view);
        latestGameStartDateTextView = (TextView) view.findViewById(R.id.vp_latest_game_start_date_text_view);
        latestGamePositionTextView = (TextView) view.findViewById(R.id.vp_latest_game_position_text_view);
        latestGameAnswersSummaryTextView = (TextView) view.findViewById(R.id.vp_latest_game_answers_summary_text_view);
        latestGameBalanceSummaryTextView = (TextView) view.findViewById(R.id.vp_latest_game_balance_summary_text_view);
        trophiesClosetNoTitlesTextView = (TextView) view.findViewById(R.id.vp_trophies_closet_no_titles_text_view);
        playerRecordsNumberOfGamesTextView = (TextView) view.findViewById(R.id.vp_player_records_number_of_games_text_view);
        playerRecordsNumberOfTrophiesTextView = (TextView) view.findViewById(R.id.vp_player_records_number_of_trophies_text_view);
        playerRecordsAverageProfitPerGameTextView = (TextView) view.findViewById(R.id.vp_player_records_average_profit_per_game_text_view);

        latestGameTabTextView = view.findViewById(R.id.vp_latest_game_tab_text_view);
        trophiesTabTextView = view.findViewById(R.id.vp_trophies_tab_text_view);
        playerRecordsTabTextView = view.findViewById(R.id.vp_records_tab_text_view);


        playerPictureImageView = (ImageView) view.findViewById(R.id.vp_player_picture_image_view);
        //playerExperienceLevelImageView = (ImageView) view.findViewById(R.id.vp_player_experience_level_image_view);

        facebookShareButton = (ShareButton) view.findViewById(R.id.vp_facebook_share_button);
        trophiesClosetScrollView = (HorizontalScrollView) view.findViewById(R.id.vp_trophies_closet_scroll_view);

        for (int i = 0; i < 3; i++) {

            String podiumItemLinearLayoutId = "vp_latest_game_podium_" + (i + 1) + "_root";
            int winnerPodiumItemResourceId = getResources().getIdentifier(podiumItemLinearLayoutId, "id", context.getPackageName());
            winnersPodiumRoots[i] = (LinearLayout) view.findViewById(winnerPodiumItemResourceId);

            String imageViewId = "vp_latest_game_winner" + (i + 1) + "_image_view";
            int winnerImageViewResourceId = getResources().getIdentifier(imageViewId, "id", context.getPackageName());
            winnersImageViews[i] = (ImageView) view.findViewById(winnerImageViewResourceId);

            String nameTextViewId = "vp_latest_game_winner" + (i + 1) + "_text_view";
            int winnerNameTextViewResourceId = getResources().getIdentifier(nameTextViewId, "id", context.getPackageName());
            winnersNamesTextViews[i] = (TextView) view.findViewById(winnerNameTextViewResourceId);

            String coinsTextViewId = "vp_latest_game_winner" + (i + 1) + "_coins_text_view";
            int winnerCoinsTextViewResourceId = getResources().getIdentifier(coinsTextViewId, "id", context.getPackageName());
            winnersCoinsTextViews[i] = (TextView) view.findViewById(winnerCoinsTextViewResourceId);

        }

        for (int i = 0; i < experienceLevelImageViews.length; i++) {
            String imageViewId = "vp_records_details_level_" + (i + 1) + "_image_view";
            int expLevelImageViewResourceId = context.getResources().getIdentifier(imageViewId, "id", context.getPackageName());
            experienceLevelImageViews[i] = (ImageView) view.findViewById(expLevelImageViewResourceId);

            String nameTextViewId = "vp_records_details_level_" + (i + 1) + "_name_text_view";
            int expLevelNameTextViewResourceId = context.getResources().getIdentifier(nameTextViewId, "id", context.getPackageName());
            experienceLevelNameTextViews[i] = (TextView) view.findViewById(expLevelNameTextViewResourceId);

            String minValueTextViewId = "vp_records_details_level_" + (i + 1) + "_min_value_text_view";
            int expLevelMinValueTextViewResourceId = context.getResources().getIdentifier(minValueTextViewId, "id", context.getPackageName());
            experienceLevelMinValueTextViews[i] = (TextView) view.findViewById(expLevelMinValueTextViewResourceId);
        }

    }


    private void setEvents() {

        final int selectedTabColor = ContextCompat.getColor(getContext(),R.color.navigationMenuSelectedItem);
        final int unSelectedTabColor = ContextCompat.getColor(getContext(),R.color.navigationMenuUnSelectedItem);

        latestGameTabRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                latestGameDetailsRoot.setVisibility(View.VISIBLE);
                //latestGameTabRoot.setBackgroundResource(R.color.navigationMenuSelectedItem);
                latestGameTabTextView.setTextColor(selectedTabColor);

                trophiesDetailsRoot.setVisibility(View.GONE);
                //trophiesTabRoot.setBackgroundResource(R.color.navigationMenu);
                trophiesTabTextView.setTextColor(unSelectedTabColor);
                playerRecordsDetailsRoot.setVisibility(View.GONE);
                //playerRecordsTabRoot.setBackgroundResource(R.color.navigationMenu);
                playerRecordsTabTextView.setTextColor(unSelectedTabColor);

            }
        });

        trophiesTabRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                latestGameDetailsRoot.setVisibility(View.GONE);
                //latestGameTabRoot.setBackgroundResource(R.color.navigationMenu);
                latestGameTabTextView.setTextColor(unSelectedTabColor);
                trophiesDetailsRoot.setVisibility(View.VISIBLE);
                //trophiesTabRoot.setBackgroundResource(R.color.navigationMenuSelectedItem);
                trophiesTabTextView.setTextColor(selectedTabColor);
                playerRecordsDetailsRoot.setVisibility(View.GONE);
                //playerRecordsTabRoot.setBackgroundResource(R.color.navigationMenu);
                playerRecordsTabTextView.setTextColor(unSelectedTabColor);
            }
        });

        playerRecordsTabRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                latestGameDetailsRoot.setVisibility(View.GONE);
                //latestGameTabRoot.setBackgroundResource(R.color.navigationMenu);
                latestGameTabTextView.setTextColor(unSelectedTabColor);
                trophiesDetailsRoot.setVisibility(View.GONE);
                //trophiesTabRoot.setBackgroundResource(R.color.navigationMenu);
                trophiesTabTextView.setTextColor(unSelectedTabColor);
                playerRecordsDetailsRoot.setVisibility(View.VISIBLE);
                //playerRecordsTabRoot.setBackgroundResource(R.color.navigationMenuSelectedItem);
                playerRecordsTabTextView.setTextColor(selectedTabColor);
                playerRecordsDetailsRoot.setVisibility(View.VISIBLE);
            }
        });

    }

    public void resetVisibility() {

        final int selectedTabColor = ContextCompat.getColor(getContext(),R.color.navigationMenuSelectedItem);
        final int unSelectedTabColor = ContextCompat.getColor(getContext(),R.color.navigationMenuUnSelectedItem);

        playerDetailsRoot.setVisibility(View.GONE);

        //latestGameTabRoot.setBackgroundResource(R.color.navigationMenu);
        latestGameTabTextView.setTextColor(unSelectedTabColor);
        latestGameDetailsRoot.setVisibility(View.GONE);
        latestGameDetailsElementsRoot.setVisibility(View.GONE);
        latestGameNotExistTextView.setVisibility(View.GONE);
        podiumRoot.setVisibility(View.GONE);
        for (int i = 0; i < winnersPodiumRoots.length; i++) {
            winnersPodiumRoots[i].setVisibility(View.GONE);
        }

        //playerRecordsTabRoot.setBackgroundResource(R.color.navigationMenu);
        playerRecordsTabTextView.setTextColor(unSelectedTabColor);
        playerRecordsDetailsRoot.setVisibility(View.GONE);
        //default set to trophies tab
        trophiesClosetNoTitlesTextView.setVisibility(View.GONE);
        trophiesClosetScrollView.setVisibility(View.GONE);
        //trophiesTabRoot.setBackgroundResource(R.color.navigationMenuSelectedItem);
        trophiesTabTextView.setTextColor(selectedTabColor);
        trophiesDetailsRoot.setVisibility(View.VISIBLE);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveUserProfileInfo(UserProfileInfo userProfileInfo) {
        try {
            if (userProfileInfo == null)
                return;

            OffsideApplication.setUserProfileInfo(userProfileInfo);
            updateUserProfileFragment(userProfileInfo);


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }



    public void updateUserProfileFragment(UserProfileInfo userProfileInfo) {
        try {
            if (userProfileInfo == null)
                return;
            playerCurrentExpLevel = ExperienceLevel.findByName(userProfileInfo.getExperienceLevelName());
            playerExperienceLevelTextView.setText(playerCurrentExpLevel.getName());

            //<editor-fold desc="---------RECENT GAME-----------">

            PlayerGame mostRecentGamePlayed = userProfileInfo.getMostRecentGamePlayed();
            if (mostRecentGamePlayed != null) {
                latestGamePrivateGroupTextView.setText(mostRecentGamePlayed.getGroupName());
                latestGameTitleTextView.setText(mostRecentGamePlayed.getGameTitle());

                PrettyTime p = new PrettyTime();


                latestGameStartDateTextView.setText(p.format(mostRecentGamePlayed.getGameStartTime()));


                String formattedPosition = Formatter.formatNumber(mostRecentGamePlayed.getPosition(),Formatter.intCommaSeparator);
                String formattedTotalPlayers = Formatter.formatNumber(mostRecentGamePlayed.getTotalPlayers(),Formatter.intCommaSeparator);

                String latestGamePositionOutOfText = String.format("%s %s %s", formattedPosition,getString(R.string.lbl_out_of),formattedTotalPlayers);
                latestGamePositionTextView.setText(latestGamePositionOutOfText);

                //String latestGameAnswersSummaryOutOfText = Integer.toString(mostRecentGamePlayed.getCorrectAnswersCount()) + " " + getString(R.string.lbl_out_of) + " " + Integer.toString(mostRecentGamePlayed.getTotalQuestionsAsked());
                String latestGameAnswersSummaryOutOfText = String.format("%s %s %s",mostRecentGamePlayed.getCorrectAnswersCount(), getString(R.string.lbl_out_of),mostRecentGamePlayed.getTotalQuestionsAsked());
                latestGameAnswersSummaryTextView.setText(latestGameAnswersSummaryOutOfText);

                String formattedOffsideCoins = Formatter.formatNumber(mostRecentGamePlayed.getOffsideCoins(),Formatter.intCommaSeparator);

                latestGameBalanceSummaryTextView.setText(formattedOffsideCoins);

                List<Winner> winners = userProfileInfo.getMostRecentGamePlayed().getWinners();

                int j = 0;

                for (Winner winner : winners) {
                    Uri winnerProfilePictureUri = Uri.parse(winner.getImageUrl());
                    ImageHelper.loadImage(getContext(), winnersImageViews[j], winnerProfilePictureUri, true);
                    String playerName = winner.getPlayerName().split(" ")[0];

                    winnersNamesTextViews[j].setText(playerName);
                    String formattedWinnerOffsideCoins = Formatter.formatNumber(winner.getOffsideCoins(),Formatter.intCommaSeparator);
                    winnersCoinsTextViews[j].setText(formattedWinnerOffsideCoins);
                    winnersPodiumRoots[j].setVisibility(View.VISIBLE);
                    j++;
                }
                podiumRoot.setVisibility(View.VISIBLE);


                latestGameDetailsElementsRoot.setVisibility(View.VISIBLE);
                latestGameNotExistTextView.setVisibility(View.GONE);

            } else {
                latestGameDetailsElementsRoot.setVisibility(View.GONE);
                latestGameNotExistTextView.setVisibility(View.VISIBLE);
            }

            //</editor-fold>

            //<editor-fold desc="---------TROPHIES CLOSET-----------">

            trophiesClosetRoot.removeAllViews();

            ArrayList<Reward> playerRewards = userProfileInfo.getRewards();

            Collections.sort(playerRewards, new Comparator<Reward>() {
                public int compare(Reward r1, Reward r2) {
                    int result = 0;
                    if (r1.getGameStartDate().after(r2.getGameStartDate()))
                        result = 1;
                    return result;
                }
            });

            Boolean hasReward = false;

            for (Reward reward : playerRewards) {

                if (!(reward.getRewardTypeName() == null || reward.getRewardTypeName().equals("NONE"))) {

                    hasReward = true;
                    ViewGroup trophiesViewGroup = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.trophy_item, trophiesClosetRoot, false);

                    LinearLayout trophiesLayout = (LinearLayout) trophiesViewGroup.getChildAt(0);

                    TextView groupNameTextView = (TextView) trophiesLayout.getChildAt(0);
                    groupNameTextView.setText(reward.getGroupName());

                    TextView positionOutOfTextView = (TextView) trophiesLayout.getChildAt(1);

                    String formattedPosition = Formatter.formatNumber(reward.getPosition(),Formatter.intCommaSeparator);
                    String formattedTotalPlayers = Formatter.formatNumber(reward.getTotalPlayers(),Formatter.intCommaSeparator);

                    String positionOutOfText = String.format("%s %s %s",formattedPosition, getString(R.string.lbl_out_of),formattedTotalPlayers);
                    positionOutOfTextView.setText(positionOutOfText);

                    ImageView trophyImageImageView = (ImageView) trophiesLayout.getChildAt(2);
//                trophyImageImageView.getLayoutParams().height = 70;
//                trophyImageImageView.getLayoutParams().width = 70;
                    trophyImageImageView.requestLayout();

                    int trophyResourceId = reward.getRewardImageResourceIdByRewardType();
                    ImageHelper.loadImage(getContext(), trophyImageImageView, trophyResourceId, false);

                    TextView gameTitleTextView = (TextView) trophiesLayout.getChildAt(3);
                    gameTitleTextView.setText(reward.getGameTitle());

                    TextView gameDateTextView = (TextView) trophiesLayout.getChildAt(4);

                    Date gameStartDate = reward.getGameStartDate();
                    PrettyTime pt = new PrettyTime();
                    gameDateTextView.setText(pt.format(gameStartDate));

                    trophiesClosetRoot.addView(trophiesViewGroup);
                }

            }
            if (!hasReward) {
                trophiesClosetNoTitlesTextView.setVisibility(View.VISIBLE);
                trophiesClosetScrollView.setVisibility(View.GONE);

            } else {
                trophiesClosetNoTitlesTextView.setVisibility(View.GONE);
                trophiesClosetScrollView.setVisibility(View.VISIBLE);

            }


            trophiesDetailsRoot.setVisibility(View.VISIBLE);


            //</editor-fold>

            //<editor-fold desc="---------PLAYER RECORDS-----------">

            int numberOfGames = userProfileInfo.getTotalGamesPlayed();
            int numberOfTrophies = userProfileInfo.getTotalTrophies();
            int averageProfitPerGame = (int) userProfileInfo.getAverageProfitPerGame();

            String formattedNumberOfGames = Formatter.formatNumber(numberOfGames, Formatter.intCommaSeparator);
            String formattedNumberOfTrophies = Formatter.formatNumber(numberOfTrophies, Formatter.intCommaSeparator);
            String formattedAverageProfitPerGame = Formatter.formatNumber(averageProfitPerGame, Formatter.intCommaSeparator);

            playerRecordsNumberOfGamesTextView.setText(formattedNumberOfGames);
            playerRecordsNumberOfTrophiesTextView.setText(formattedNumberOfTrophies);
            playerRecordsAverageProfitPerGameTextView.setText(formattedAverageProfitPerGame);

            for (int i = 0; i < experienceLevelImageViews.length; i++) {
                ExperienceLevel currentExpLevel = ExperienceLevel.findByIndex(i);

                experienceLevelNameTextViews[i].setText(currentExpLevel.getName());
                String formattedMinValue = Formatter.formatNumber(currentExpLevel.getMinValue(),Formatter.intCommaSeparator);
                experienceLevelMinValueTextViews[i].setText(formattedMinValue);

                if (playerCurrentExpLevel.getName().equals(currentExpLevel.getName()))
                    //ImageHelper.loadImage(context, experienceLevelImageViews[i], currentExpLevel.getImageViewResourceIdCurrent());
                    experienceLevelImageViews[i].setImageResource(currentExpLevel.getImageViewResourceIdCurrent());
                else
                    //ImageHelper.loadImage(context, experienceLevelImageViews[i], currentExpLevel.getImageViewResourceId());
                    experienceLevelImageViews[i].setImageResource(currentExpLevel.getImageViewResourceId());
            }

            final Bitmap bitmapImage = BitmapFactory.decodeResource(getContext().getResources(), playerCurrentExpLevel.getImageViewResourceId());
            String messageText = "I am a " + playerCurrentExpLevel.getName() + " on the Sidekick game";
            shareOnFacebook(facebookShareButton, bitmapImage, messageText);


            //</editor-fold>

            loadingRoot.setVisibility(View.GONE);
            //root.setVisibility(View.VISIBLE);

            playerMainDetailsRoot.setVisibility(View.VISIBLE);
            playerDetailsRoot.setVisibility(View.VISIBLE);

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    public void shareOnFacebook(ShareButton facebookShareButton, Bitmap bitmapImage, String quote) {

        try {
            StringBuilder sb = new StringBuilder();

            sb.append(quote);
            sb.append("\r\n");
            sb.append("\r\n");
            sb.append("think you can do better than me? come join!!!");

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


}
