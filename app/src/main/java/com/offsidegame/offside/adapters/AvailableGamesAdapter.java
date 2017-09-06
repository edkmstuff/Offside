package com.offsidegame.offside.adapters;

import android.content.Context;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.offsidegame.offside.R;
import com.offsidegame.offside.events.JoinGameEvent;
import com.offsidegame.offside.events.PrivateGameGeneratedEvent;
import com.offsidegame.offside.helpers.ImageHelper;
import com.offsidegame.offside.models.AvailableGame;
import com.offsidegame.offside.models.OffsideApplication;
import com.offsidegame.offside.models.PrivateGroup;
import com.offsidegame.offside.models.PrivateGroupPlayer;

import org.acra.ACRA;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

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


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            final AvailableGamesAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.active_game_item, parent, false);
                viewHolder = new AvailableGamesAdapter.ViewHolder();

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

                convertView.setTag(viewHolder);


            } else
                viewHolder = (AvailableGamesAdapter.ViewHolder) convertView.getTag();


            viewHolder.availableGame = getItem(position);
            if (viewHolder.availableGame == null)
                return convertView;



            viewHolder.homeTeamNameTextView.setText(viewHolder.availableGame.getHomeTeam());
            viewHolder.awayTeamNameTextView.setText(viewHolder.availableGame.getAwayTeam());
            Uri homeTeamLogoUri = Uri.parse(viewHolder.availableGame.getHomeTeamLogoUrl());
            ImageHelper.loadImage(context, viewHolder.homeTeamLogoImageView, homeTeamLogoUri);
            Uri awayTeamLogoUri = Uri.parse(viewHolder.availableGame.getAwayTeamLogoUrl());
            ImageHelper.loadImage(context, viewHolder.awayTeamLogoImageView, awayTeamLogoUri);
            viewHolder.startTimeTextView.setText(viewHolder.availableGame.getStartTimeString());
            viewHolder.startDateTextView.setText(viewHolder.availableGame.getStartDateString());
            if (viewHolder.availableGame.getPrivateGroupPlayers() == null)
                viewHolder.availableGame.setPrivateGroupPlayers(new PrivateGroupPlayer[0]);
            viewHolder.playersCountTextView.setText(Integer.toString(viewHolder.availableGame.getPrivateGroupPlayers().length) + " " + context.getString(R.string.lbl_now_playing));


            viewHolder.playersPlayInGameRoot.removeAllViews();

            for (PrivateGroupPlayer privateGroupPlayer : viewHolder.availableGame.getPrivateGroupPlayers()) {

                ViewGroup playerLayout = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.player_playing_in_private_group_item, viewHolder.playersPlayInGameRoot, false);
                FrameLayout playerItemRoot = (FrameLayout) playerLayout.getChildAt(0);
                ImageView playerImageImageView = (ImageView) playerItemRoot.getChildAt(0);
                playerImageImageView.getLayoutParams().height = 70;
                playerImageImageView.getLayoutParams().width = 70;
                playerImageImageView.requestLayout();
                String imageUrl = privateGroupPlayer.getImageUrl() == null || privateGroupPlayer.getImageUrl().equals("") ? OffsideApplication.getDefaultProfilePictureUrl() : privateGroupPlayer.getImageUrl();
                Uri imageUri = Uri.parse(imageUrl);
                ImageHelper.loadImage(context, playerImageImageView, imageUri);

                ImageView isActiveIndicator = (ImageView) playerItemRoot.getChildAt(1);
                isActiveIndicator.getLayoutParams().height = 25;
                isActiveIndicator.getLayoutParams().width = 25;

                viewHolder.playersPlayInGameRoot.addView(playerLayout);
            }

            viewHolder.joinPrivateGameButtonTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OffsideApplication.setSelectedAvailableGame(viewHolder.availableGame);
//                    if(viewHolder.availableGame.getPrivateGameId() != null)
//                        OffsideApplication.setSelectedPrivateGameId(viewHolder.availableGame.getPrivateGameId());
                    EventBus.getDefault().post(new JoinGameEvent(null));
                }
            });

            viewHolder.createPrivateGameButtonTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OffsideApplication.setSelectedAvailableGame(viewHolder.availableGame);
                    String gameId = viewHolder.availableGame.getGameId();
                    String groupId = OffsideApplication.getSelectedPrivateGroup().getId();
                    String playerId = OffsideApplication.getPlayerAssets().getPlayerId() ;
                    String selectedLanguage = "עברית";
                    OffsideApplication.signalRService.requestCreatePrivateGame(gameId, groupId, playerId, selectedLanguage);

                }
            });

            resetVisibility(viewHolder);
            if(viewHolder.availableGame.getPrivateGroupPlayers().length==0){
                viewHolder.joinPrivateGameRoot.setVisibility(View.GONE);
                viewHolder.createPrivateGameRoot.setVisibility(View.VISIBLE);
            }
            else {
                viewHolder.joinPrivateGameRoot.setVisibility(View.VISIBLE);
                viewHolder.createPrivateGameRoot.setVisibility(View.GONE);

            }



            return convertView;

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

        return null;

    }

    private void resetVisibility(ViewHolder viewHolder){
        viewHolder.joinPrivateGameRoot.setVisibility(View.GONE);
        viewHolder.createPrivateGameRoot.setVisibility(View.GONE);

    }



}
