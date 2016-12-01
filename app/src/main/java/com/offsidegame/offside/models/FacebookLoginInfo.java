package com.offsidegame.offside.models;

import android.net.Uri;

import com.facebook.Profile;

/**
 * Created by USER on 11/29/2016.
 */
public class FacebookLoginInfo  {
    private String firstName;
    private String lastName;
    private String name;
    private String id;
    private String linkUri;

    public FacebookLoginInfo(){}

    public FacebookLoginInfo(String firstName, String lastName, String name, String id, String linkUri ){
        this.firstName = firstName;
        this.lastName = lastName;
        this.name= name;
        this.id= id;
        this.linkUri = linkUri;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public String getLinkUri() {return linkUri; }





    //":"Eran","id":"10153837762934596","lastName":"Dagan Bokobza","linkUri":{"cachedFsi":-2,"cachedSsi":-2,"scheme":"NOT CACHED","uriString":"https://www.facebook.com/app_scoped_user_id/10153837762934596/","host":"NOT CACHED","port":-2},"middleName":"","name":"Eran Dagan Bokobza"}

}
