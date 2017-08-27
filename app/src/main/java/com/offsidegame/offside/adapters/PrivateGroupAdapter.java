package com.offsidegame.offside.adapters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.offsidegame.offside.R;
import com.offsidegame.offside.fragments.GroupsFragment;
import com.offsidegame.offside.fragments.SingleGroupFragment;
import com.offsidegame.offside.helpers.ImageHelper;
import com.offsidegame.offside.models.OffsideApplication;
import com.offsidegame.offside.models.PrivateGroup;
import com.offsidegame.offside.models.PrivateGroupPlayer;

import org.acra.ACRA;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by user on 7/20/2017.
 */

public class PrivateGroupAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<PrivateGroup> privateGroups;


    public PrivateGroupAdapter(Context context, ArrayList<PrivateGroup> privateGroups) {
        this.context = context;
        this.privateGroups = privateGroups;
    }

    @Override
    public int getCount() {
        return privateGroups.size();
    }

    @Override
    public PrivateGroup getItem(int position) {
        return privateGroups.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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
            final PrivateGroupAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.private_group_item, parent, false);
                viewHolder = new PrivateGroupAdapter.ViewHolder();

                //viewHolder.incomingProfilePictureImageView = (ImageView) convertView.findViewById(R.id.cm_incoming_profile_picture_image_view);
                viewHolder.groupNameTextView = (TextView) convertView.findViewById(R.id.pg_group_name_text_view);
                viewHolder.groupGameStatusTextView = (TextView) convertView.findViewById(R.id.pg_group_game_status_text_view);
                viewHolder.playersPlayInGroupRoot = (LinearLayout) convertView.findViewById(R.id.pg_players_play_in_group_root);
                viewHolder.totalPlayingPlayersInGroupTextView = (TextView) convertView.findViewById(R.id.pg_total_playing_players_in_group_text_view);
                viewHolder.playersPlayInGroupRoot = (LinearLayout) convertView.findViewById(R.id.pg_players_play_in_group_root);

                convertView.setTag(viewHolder);


            } else
                viewHolder = (PrivateGroupAdapter.ViewHolder) convertView.getTag();

            viewHolder.privateGroup = getItem(position);
            if (viewHolder.privateGroup == null)
                return convertView;

            viewHolder.groupNameTextView.setText(viewHolder.privateGroup.getName());


            if (viewHolder.privateGroup.isActive()) {

                viewHolder.playersPlayInGroupRoot.removeAllViews();

                //PrivateGroupPlayer[] players = viewHolder.privateGroup.getPrivateGroupPlayers();
                ArrayList<PrivateGroupPlayer> players = viewHolder.privateGroup.getPrivateGroupPlayers();

                Collections.sort(players, new Comparator<PrivateGroupPlayer>() {
                    public int compare(PrivateGroupPlayer p1, PrivateGroupPlayer p2) {
                        if(  p1.getDisplayPriority() < p2.getDisplayPriority())
                            return -1;
                        if(  p1.getDisplayPriority() > p2.getDisplayPriority())
                            return 1;

                        return 0;

                    }
                });



                for(PrivateGroupPlayer privateGroupPlayer: players){

                    ViewGroup playerLayout = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.player_playing_in_private_group_item, viewHolder.playersPlayInGroupRoot,false);
                    FrameLayout playerItemRoot = (FrameLayout) playerLayout.getChildAt(0);
                    ImageView playerImageImageView = (ImageView) playerItemRoot.getChildAt(0);
                    playerImageImageView.getLayoutParams().height = 70;
                    playerImageImageView.getLayoutParams().width = 70;
                    playerImageImageView.requestLayout();
                    String imageUrl = privateGroupPlayer.getImageUrl() == null || privateGroupPlayer.getImageUrl().equals("")  ? OffsideApplication.getDefaultProfilePictureUrl(): privateGroupPlayer.getImageUrl();
                    Uri imageUri = Uri.parse(imageUrl);
                    ImageHelper.loadImage(context,playerImageImageView,imageUri);

                    //View isActiveIndicator = (View) playerLayout.getChildAt(1);
                    //LinearLayout isActiveIndicator = (LinearLayout) playerLayout.getChildAt(1);
                    ImageView isActiveIndicator = (ImageView) playerItemRoot.getChildAt(1);
                    isActiveIndicator.getLayoutParams().height = 25;
                    isActiveIndicator.getLayoutParams().width = 25;


                    if(privateGroupPlayer.getActive().booleanValue())
                        isActiveIndicator.setVisibility(View.VISIBLE);
                    else
                        isActiveIndicator.setVisibility(View.GONE);



                    viewHolder.playersPlayInGroupRoot.addView(playerLayout);
                }


                int countActivePlayersInPrivateGroup = viewHolder.privateGroup.getActivePlayersCount();
                viewHolder.totalPlayingPlayersInGroupTextView.setText(Integer.toString(countActivePlayersInPrivateGroup) +" "+context.getString(R.string.lbl_now_playing));
                viewHolder.groupGameStatusTextView.setText(R.string.lbl_game_is_active);
                //viewHolder.groupGameStatusTextView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.totalPlayingPlayersInGroupTextView.setText(R.string.lbl_no_playing_players);
                //viewHolder.groupGameStatusTextView.setVisibility(View.GONE);
                viewHolder.groupGameStatusTextView.setText(R.string.lbl_start_a_game);
                viewHolder.groupGameStatusTextView.setBackgroundResource(R.color.navigationMenuSelectedItem);

            }

            convertView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    //Toast.makeText(context,"item clicked" ,Toast.LENGTH_SHORT).show();
                    OffsideApplication.setSelectedPrivateGroup(viewHolder.privateGroup);
                    SingleGroupFragment singleGroupFragment = new SingleGroupFragment();
                    replaceFragment(singleGroupFragment);
                }
            });





            return convertView;

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

        return null;

    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.l_fragment_container_root, fragment, fragment.getTag()).commit();

    }



}
