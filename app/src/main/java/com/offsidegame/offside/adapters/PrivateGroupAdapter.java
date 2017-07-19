package com.offsidegame.offside.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.offsidegame.offside.R;
import com.offsidegame.offside.helpers.ImageHelper;
import com.offsidegame.offside.models.OffsideApplication;
import com.offsidegame.offside.models.PrivateGroup;
import com.offsidegame.offside.models.PrivateGroupPlayer;

import org.acra.ACRA;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * Created by user on 7/17/2017.
 */

public class PrivateGroupAdapter extends ArrayAdapter<PrivateGroup> {

    private Context context;

    public PrivateGroupAdapter(Context context, ArrayList<PrivateGroup> privateGroups) {
        super(context, 0, privateGroups);
        this.context = context;

    }

    private class ViewHolder {

        PrivateGroup privateGroup;
        TextView groupNameTextView;
        TextView groupGameStatusTextView;

        TextView totalPlayingPlayersInGroupTextView;
        LinearLayout playersPlayInGroupRoot;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.private_group_item_1, parent, false);
                viewHolder = new ViewHolder();

                //viewHolder.incomingProfilePictureImageView = (ImageView) convertView.findViewById(R.id.cm_incoming_profile_picture_image_view);
                viewHolder.groupNameTextView = (TextView) convertView.findViewById(R.id.pg_group_name_text_view);
                viewHolder.groupGameStatusTextView = (TextView) convertView.findViewById(R.id.pg_group_game_status_text_view);
                viewHolder.playersPlayInGroupRoot = (LinearLayout) convertView.findViewById(R.id.pg_players_play_in_group_root);
                viewHolder.totalPlayingPlayersInGroupTextView = (TextView) convertView.findViewById(R.id.pg_total_playing_players_in_group_text_view);
                viewHolder.playersPlayInGroupRoot = (LinearLayout) convertView.findViewById(R.id.pg_players_play_in_group_root);

                convertView.setTag(viewHolder);


            } else
                viewHolder = (ViewHolder) convertView.getTag();

            viewHolder.privateGroup = getItem(position);
            if (viewHolder.privateGroup == null)
                return convertView;

            viewHolder.groupNameTextView.setText(viewHolder.privateGroup.getName());


            if (viewHolder.privateGroup.isActive()) {

                viewHolder.playersPlayInGroupRoot.removeAllViews();
                for(PrivateGroupPlayer privateGroupPlayer: viewHolder.privateGroup.getPrivateGroupPlayers()){

                    ViewGroup playerLayout = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.player_playing_in_private_group_item, viewHolder.playersPlayInGroupRoot,false);
                    ImageView playerImageImageView = (ImageView) playerLayout.getChildAt(0);
                    playerImageImageView.getLayoutParams().height = 100;
                    playerImageImageView.getLayoutParams().width = 100;
                    playerImageImageView.requestLayout();
                    String imageUrl = privateGroupPlayer.getImageUrl() == null || privateGroupPlayer.getImageUrl().equals("")  ? OffsideApplication.getDefaultProfilePictureUrl(): privateGroupPlayer.getImageUrl();
                    Uri imageUri = Uri.parse(imageUrl);
                    ImageHelper.loadImage(context,playerImageImageView,imageUri);

                    viewHolder.playersPlayInGroupRoot.addView(playerLayout);
                }


                int countActivePlayersInPrivateGroup = viewHolder.privateGroup.getActivePlayersCount();
                viewHolder.totalPlayingPlayersInGroupTextView.setText(Integer.toString(countActivePlayersInPrivateGroup) +" "+context.getString(R.string.lbl_now_playing));
                viewHolder.groupGameStatusTextView.setText(R.string.lbl_game_is_active);
                //viewHolder.groupGameStatusTextView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.totalPlayingPlayersInGroupTextView.setText(R.string.lbl_no_playing_players);
                //viewHolder.groupGameStatusTextView.setVisibility(View.GONE);
                viewHolder.groupGameStatusTextView.setText(R.string.lbl_invite_friends);
                viewHolder.groupGameStatusTextView.setBackgroundResource(R.color.navigationMenuSelectedItem);

            }





            return convertView;

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

        return null;

    }


}
