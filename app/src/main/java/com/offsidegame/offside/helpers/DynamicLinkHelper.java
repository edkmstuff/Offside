package com.offsidegame.offside.helpers;

import android.net.Uri;

import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.zxing.common.StringUtils;

import static com.offsidegame.offside.models.OffsideApplication.getContext;

/**
 * Created by user on 10/29/2017.
 */

public class DynamicLinkHelper {

    public static String buildDynamicLink(String link, String description, String titleSocial, String source)
    {
        String generatedLink= null;
//        generatedLink =
//                FirebaseDynamicLinks.getInstance().createDynamicLink()
//                .setDynamicLinkDomain("tmg9s.app.goo.gl")
//                .setLink(Uri.parse("http://www.sidekickgame.com"))
//                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
//                .setSocialMetaTagParameters(new DynamicLink.SocialMetaTagParameters.Builder().setTitle("Share Sidekick"))
//                .setGoogleAnalyticsParameters(new DynamicLink.GoogleAnalyticsParameters.Builder().setSource("SidekickApp"))
//                .buildDynamicLink().getUri().toString();

        generatedLink = "https://tmg9s.app.goo.gl/?"+
                "link="+link+
                "&apn="+getContext().getPackageName()+
                "&st="+titleSocial+
                "&sd="+description+
                "&utm_source="+source;


        return generatedLink;


    }

}
