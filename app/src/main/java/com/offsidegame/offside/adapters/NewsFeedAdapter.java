package com.offsidegame.offside.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.offsidegame.offside.R;
import com.offsidegame.offside.activities.NewsDetailsActivity;
import com.offsidegame.offside.helpers.DateHelper;
import com.offsidegame.offside.helpers.ImageHelper;
import com.offsidegame.offside.models.FeedItem;
import com.offsidegame.offside.models.OffsideApplication;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by user on 10/3/2017.
 */

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.ViewHolder> {

    Context context;
    ArrayList<FeedItem> feedItems;


    public NewsFeedAdapter(Context context, ArrayList<FeedItem> feedItems) {
        this.context = context;
        this.feedItems = feedItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.news_row_item, parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        YoYo.with(Techniques.FadeIn).playOn(holder.cardView);
        final FeedItem currentItem = feedItems.get(position);
        holder.titleTextView.setText(currentItem.getTitle());
        holder.descriptionTextView.setText(currentItem.getDescription());

        PrettyTime pt = new PrettyTime();
        Date publishDate = DateHelper.formatAsDate(currentItem.getPubDate(),"EEE, d MMM yyyy HH:mm:ss Z",context);
        if(publishDate!=null)
            holder.publishDateTextView.setText(pt.format(publishDate));
        else
            holder.publishDateTextView.setText(currentItem.getPubDate());

        Uri imageUri = Uri.parse(currentItem.getThumbnailUri());
        ImageHelper.loadImage(context,holder.thumbnailImageView,imageUri,false);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, NewsDetailsActivity.class);
                intent.putExtra("Link",currentItem.getLink());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return feedItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView,descriptionTextView,publishDateTextView;
        ImageView thumbnailImageView;
        CardView cardView;


        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.nri_title_text_view);
            descriptionTextView = itemView.findViewById(R.id.nri_description_text_view);
            publishDateTextView = itemView.findViewById(R.id.nri_publish_date_text_view);
            thumbnailImageView = itemView.findViewById(R.id.nri_thumbnail_image_view);
            cardView = itemView.findViewById(R.id.nri_card_view);


        }
    }
}
