package com.offsidegame.offside.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.offsidegame.offside.R;
import com.offsidegame.offside.models.PrivateGroup;

import org.acra.ACRA;

import java.util.ArrayList;


/**
 * Created by user on 7/17/2017.
 */

public class PrivateGroupAdapter  extends ArrayAdapter<PrivateGroup> {

    private Context context;

    public PrivateGroupAdapter(Context context, ArrayList<PrivateGroup> privateGroups) {
        super(context, 0, privateGroups);
        this.context = context;

    }

    private class ViewHolder {

        PrivateGroup privateGroup;
        TextView groupNameTextView;
        TextView groupGameStatusTextView;
        LinearLayout playersPlayInGroupRoot;
        TextView totalPlayingPlayersInGroupTextView;



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

                convertView.setTag(viewHolder);



            } else
                viewHolder = (ViewHolder) convertView.getTag();

            viewHolder.privateGroup = getItem(position);
            if (viewHolder.privateGroup == null)
                return convertView;

            viewHolder.groupNameTextView.setText(viewHolder.privateGroup.getName());

            int countActivePlayersInPrivateGroup= viewHolder.privateGroup.getPlayersInfo().length;
            if(countActivePlayersInPrivateGroup>0){
                viewHolder.totalPlayingPlayersInGroupTextView.setText("playing now " + Integer.toString(countActivePlayersInPrivateGroup));
                viewHolder.groupGameStatusTextView.setText("ACTIVE");
            }

            else{
                viewHolder.totalPlayingPlayersInGroupTextView.setText("no players yet");
            }

            //ToDo: go over playerInfo and display the playing players profile pictures(like in the winners in chatmessage adapter

            return convertView;

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

        return null;

    }


}
