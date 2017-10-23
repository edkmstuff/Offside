package com.offsidegame.offside.events;

/**
 * Created by user on 10/24/2017.
 */

public class NotificationBubbleEvent {
    private String navigationItemName;

    public static String navigationItemChat = "chat";

    public NotificationBubbleEvent(String eventName) {

        this.navigationItemName = eventName;
    }

    public String getNavigationItemName() {
        return navigationItemName;
    }

    public void setNavigationItemName(String navigationItemName) {
        this.navigationItemName = navigationItemName;
    }


}
