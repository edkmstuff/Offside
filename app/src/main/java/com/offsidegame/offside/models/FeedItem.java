package com.offsidegame.offside.models;

/**
 * Created by user on 10/3/2017.
 */

public class FeedItem {

    String title;
    String link;
    String description;
    String pubDate;
    String thumbnailUri;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getThumbnailUri() {
        return thumbnailUri;
    }

    public void setThumbnailUri(String thumbnailUri) {
        this.thumbnailUri = thumbnailUri;
    }

//    public FeedItem(String title, String link, String description, String pubDate, String thumbnailUri) {
//
//        this.title = title;
//        this.link = link;
//        this.description = description;
//        this.pubDate = pubDate;
//        this.thumbnailUri = thumbnailUri;
//    }
}
