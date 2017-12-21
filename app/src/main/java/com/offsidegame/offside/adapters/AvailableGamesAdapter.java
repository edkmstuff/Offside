package com.offsidegame.offside.adapters;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.offsidegame.offside.R;
import com.offsidegame.offside.events.LoadingEvent;
import com.offsidegame.offside.events.NotEnoughAssetsEvent;
import com.offsidegame.offside.helpers.AnimationHelper;
import com.offsidegame.offside.helpers.Formatter;
import com.offsidegame.offside.helpers.ImageHelper;
import com.offsidegame.offside.models.AvailableGame;
import com.offsidegame.offside.models.OffsideApplication;
import com.offsidegame.offside.models.PrivateGroupPlayer;

import org.acra.ACRA;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import static android.view.View.GONE;

/**
 * Created by user on 7/20/2017.
 */

public class AvailableGamesAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<AvailableGame> availableGames;


    public AvailableGamesAdapter(Context context, ArrayList<AvailableGame> availableGames) {
        this.context = context;
        this.availableGames = availableGames;

    }

    @Override
    public int getCount() {
        return availableGames.size();
    }

    @Override
    public AvailableGame getItem(int position) {
        return availableGames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {

        LinearLayout root;
        AvailableGame availableGame;
        ImageView homeTeamLogoImageView;
        ImageView awayTeamLogoImageView;
        TextView homeTeamNameTextView;
        TextView awayTeamNameTextView;

        TextView startTimeTextView;
        TextView startDateTextView;
        TextView playersCountTextView;
        LinearLayout playersPlayInGameRoot;
        LinearLayout joinPrivateGameRoot;
        TextView joinPrivateGameButtonTextView;
        LinearLayout createPrivateGameRoot;
        TextView createPrivateGameButtonTextView;
        LinearLayout gameEnterFeeRoot;
        TextView moreTextView;
        TextView createPrivateGameButtonEntranceFeeTextView;
        TextView joinPrivateGameButtonEntranceFeeTextView;
        boolean userIsAlreadyInGame;




    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            final AvailableGamesAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.active_game_item, parent, false);
                viewHolder = new AvailableGamesAdapter.ViewHolder();

                viewHolder.root = convertView.findViewById(R.id.ag_root);
                viewHolder.homeTeamNameTextView = (TextView) convertView.findViewById(R.id.ag_home_team_name_text_view);
                viewHolder.awayTeamNameTextView = (TextView) convertView.findViewById(R.id.ag_away_team_name_text_view);
                viewHolder.homeTeamLogoImageView = (ImageView) convertView.findViewById(R.id.ag_home_team_logo_image_view);
                viewHolder.awayTeamLogoImageView = (ImageView) convertView.findViewById(R.id.ag_away_team_logo_image_view);
                viewHolder.startTimeTextView = (TextView) convertView.findViewById(R.id.ag_start_time_text_view);
                viewHolder.startDateTextView = (TextView) convertView.findViewById(R.id.ag_start_date_text_view);
                viewHolder.playersPlayInGameRoot = (LinearLayout) convertView.findViewById(R.id.ag_players_play_in_game_root);
                viewHolder.playersCountTextView = (TextView) convertView.findViewById(R.id.ag_players_count_text_view);
                viewHolder.joinPrivateGameRoot = (LinearLayout) convertView.findViewById(R.id.ag_join_private_game_root);
                viewHolder.joinPrivateGameButtonTextView = (TextView) convertView.findViewById(R.id.ag_join_private_game_button_text_view);
                viewHolder.createPrivateGameRoot = (LinearLayout) convertView.findViewById(R.id.ag_create_private_game_root);
                viewHolder.createPrivateGameButtonTextView = (TextView) convertView.findViewById(R.id.ag_create_private_game_button_text_view);
                viewHolder.gameEnterFeeRoot =  convertView.findViewById(R.id.ag_game_enter_fee_root);
                viewHolder.moreTextView = convertView.findViewById(R.id.ag_more_text_view);
                viewHolder.createPrivateGameButtonEntranceFeeTextView = convertView.findViewById(R.id.ag_create_private_game_button_entrance_fee_text_view);
                viewHolder.joinPrivateGameButtonEntranceFeeTextView = convertView.findViewById(R.id.ag_join_private_game_button_entrance_fee_text_view);


                convertView.setTag(viewHolder);


            } else
                viewHolder = (AvailableGamesAdapter.ViewHolder) convertView.getTag();


            viewHolder.availableGame = getItem(position);
            if (viewHolder.availableGame == null)
                return convertView;

//            int lightBackGround = R.drawable.shape_bg_league_record_even;
//            int darkBackground = R.drawable.shape_bg_league_record_odd;
//            viewHolder.root.setBackgroundResource(position%2 == 0? lightBackGround : darkBackground);

            if(position%2 == 0){
                viewHolder.root.setBackgroundResource(R.color.leagueRecordEvenSolid);
                //viewHolder.root.setAlpha(0.2f);

            }
            else
            {
                viewHolder.root.setBackgroundResource(R.color.leagueRecordOddSolid);
                //viewHolder.root.setAlpha(0.05f);

            }


            viewHolder.homeTeamNameTextView.setText(viewHolder.availableGame.getHomeTeam());
            viewHolder.awayTeamNameTextView.setText(viewHolder.availableGame.getAwayTeam());
            Uri homeTeamLogoUri = Uri.parse(viewHolder.availableGame.getHomeTeamLogoUrl());
            ImageHelper.loadImage(context, viewHolder.homeTeamLogoImageView, homeTeamLogoUri, false);
            Uri awayTeamLogoUri = Uri.parse(viewHolder.availableGame.getAwayTeamLogoUrl());
            ImageHelper.loadImage(context, viewHolder.awayTeamLogoImageView, awayTeamLogoUri, false);

            viewHolder.startTimeTextView.setText(viewHolder.availableGame.getStartTimeString());
            viewHolder.startDateTextView.setText(viewHolder.availableGame.getStartDateString());
            String formattedEntranceFee = Formatter.formatNumber(viewHolder.availableGame.getEntranceFee(),Formatter.intCommaSeparator);
            viewHolder.createPrivateGameButtonEntranceFeeTextView.setText(formattedEntranceFee);
            viewHolder.joinPrivateGameButtonEntranceFeeTextView.setText(formattedEntranceFee);
            if (viewHolder.availableGame.getPrivateGroupPlayers() == null)
                viewHolder.availableGame.setPrivateGroupPlayers(new PrivateGroupPlayer[0]);


            viewHolder.playersCountTextView.setText(Integer.toString(viewHolder.availableGame.getPrivateGroupPlayers().length) + " " + context.getString(R.string.lbl_now_playing));

            viewHolder.playersPlayInGameRoot.removeAllViews();

            viewHolder.userIsAlreadyInGame =false;

            int maxPlayersToDisplay = 7;
            int playersCount = 0;
            PrivateGroupPlayer[] privateGroupPlayers  = viewHolder.availableGame.getPrivateGroupPlayers();
            if(privateGroupPlayers!=null && privateGroupPlayers.length > 7)
                viewHolder.moreTextView.setVisibility(View.VISIBLE);
            else
                viewHolder.moreTextView.setVisibility(View.GONE);

            for (PrivateGroupPlayer privateGroupPlayer : privateGroupPlayers) {

                if(privateGroupPlayer.getPlayerId().equals(OffsideApplication.getPlayerId()))
                    viewHolder.userIsAlreadyInGame=true;

                if (playersCount < maxPlayersToDisplay) {
                    playersCount++;

                    ViewGroup playerLayout = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.player_playing_in_private_group_item, viewHolder.playersPlayInGameRoot, false);
                    FrameLayout playerItemRoot = (FrameLayout) playerLayout.getChildAt(0);
                    ImageView playerImageImageView = (ImageView) playerItemRoot.getChildAt(0);
                    playerImageImageView.getLayoutParams().height = 70;
                    playerImageImageView.getLayoutParams().width = 70;
                    playerImageImageView.requestLayout();
                    String imageUrl = privateGroupPlayer.getImageUrl() == null || privateGroupPlayer.getImageUrl().equals("") ? OffsideApplication.getDefaultProfilePictureUrl() : privateGroupPlayer.getImageUrl();
                    Uri imageUri = Uri.parse(imageUrl);
                    ImageHelper.loadImage(context, playerImageImageView, imageUri, true);

                    ImageView isActiveIndicator = (ImageView) playerItemRoot.getChildAt(1);
                    isActiveIndicator.getLayoutParams().height = 25;
                    isActiveIndicator.getLayoutParams().width = 25;

                    viewHolder.playersPlayInGameRoot.addView(playerLayout);

                }

            }

            viewHolder.joinPrivateGameRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int duration =500;
                    int delay = 0;
                    AnimationHelper.animateButtonClick(view,duration);

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            int balance = OffsideApplication.getPlayerAssets().getBalance();
                            int entranceFee = viewHolder.availableGame.getEntranceFee();
                            if(entranceFee<=balance || viewHolder.userIsAlreadyInGame ){
                                OffsideApplication.setSelectedAvailableGame(viewHolder.availableGame);
                                String playerId = OffsideApplication.getPlayerAssets().getPlayerId();
                                String gameId = viewHolder.availableGame.getGameId();
                                String groupId = OffsideApplication.getSelectedPrivateGroup().getId();
                                String privateGameId = viewHolder.availableGame.getPrivateGameId();
                                String androidDeviceId = OffsideApplication.getAndroidDeviceId();
                                OffsideApplication.networkingService.requestJoinPrivateGame(playerId, gameId, groupId, privateGameId,  androidDeviceId);

                            }
                            else
                            {
                                EventBus.getDefault().post(new NotEnoughAssetsEvent(balance,entranceFee,OffsideApplication.COINS, true));

                            }


                        }
                    }, delay);

                }
            });

            viewHolder.createPrivateGameRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {

                        int duration =500;
                        int delay =0;

                        AnimationHelper.animateButtonClick(view,duration);

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                int balance = OffsideApplication.getPlayerAssets().getBalance();
                                int entranceFee = viewHolder.availableGame.getEntranceFee();
                                if(entranceFee<=balance){
                                    OffsideApplication.setSelectedAvailableGame(viewHolder.availableGame);
                                    String gameId = viewHolder.availableGame.getGameId();
                                    String groupId = OffsideApplication.getSelectedPrivateGroup().getId();
                                    String playerId = OffsideApplication.getPlayerAssets().getPlayerId();
                                    String privateGameContentLanguage;
                                    //create the private game in hebrew if user's device locale set to hebrew, otherwise english
                                    if(context.getResources().getConfiguration().locale.getDisplayLanguage().toString().equalsIgnoreCase(OffsideApplication.availableLanguages.get("he")))
                                        privateGameContentLanguage = OffsideApplication.availableLanguages.get("he");
                                    else
                                        privateGameContentLanguage = OffsideApplication.availableLanguages.get("en");
                                    OffsideApplication.networkingService.requestCreatePrivateGame(playerId, gameId, groupId,  privateGameContentLanguage);
                                    EventBus.getDefault().post(new LoadingEvent(true,"Creating and joining to game"));

                                }
                                else
                                {
                                    EventBus.getDefault().post(new NotEnoughAssetsEvent(balance,entranceFee, OffsideApplication.COINS, true));

                                }


                            }
                        }, delay);

                    } catch (Exception ex) {
                        ACRA.getErrorReporter().handleSilentException(ex);
                    }


                }
            });

            resetVisibility(viewHolder);
            if (viewHolder.availableGame.getPrivateGroupPlayers().length == 0) {

                viewHolder.joinPrivateGameRoot.setVisibility(GONE);

                viewHolder.createPrivateGameRoot.setBackgroundResource(R.drawable.bg_button_start_game);
                viewHolder.createPrivateGameRoot.setVisibility(View.VISIBLE);
                viewHolder.createPrivateGameButtonTextView.setVisibility(GONE);
            } else {

                viewHolder.createPrivateGameRoot.setVisibility(GONE);

                viewHolder.joinPrivateGameRoot.setVisibility(View.VISIBLE);
                viewHolder.joinPrivateGameRoot.setBackgroundResource(R.drawable.bg_button_start_game);
                viewHolder.joinPrivateGameButtonTextView.setVisibility(GONE);

                if(viewHolder.userIsAlreadyInGame){
                    viewHolder.gameEnterFeeRoot.setVisibility(GONE);
                    viewHolder.joinPrivateGameRoot.setBackgroundResource(R.drawable.bg_button_game_is_on);
                    viewHolder.joinPrivateGameButtonTextView.setText(R.string.lbl_resume_game);
                    viewHolder.joinPrivateGameButtonTextView.setVisibility(View.VISIBLE);
                }


            }



            return convertView;

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

        return null;

    }

    private void resetVisibility(ViewHolder viewHolder) {
        viewHolder.joinPrivateGameRoot.setVisibility(GONE);
        viewHolder.createPrivateGameRoot.setVisibility(GONE);
        viewHolder.gameEnterFeeRoot.setVisibility(View.VISIBLE);

    }

}
