package com.offsidegame.offside.events;

/**
 * Created by user on 9/10/2017.
 */

public class FriendInviteReceivedEvent {

    private String InvitationUrl;
    public String getInvitationUrl() {
        return InvitationUrl;
    }

    public void setInvitationUrl(String invitationUrl) {
        InvitationUrl = invitationUrl;
    }

    public FriendInviteReceivedEvent(String invitationUrl) {

        InvitationUrl = invitationUrl;
    }




}
