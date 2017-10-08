package com.offsidegame.offside.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.offsidegame.offside.R;
import com.offsidegame.offside.events.NavigationEvent;
import com.offsidegame.offside.models.OffsideApplication;

import org.acra.ACRA;
import org.greenrobot.eventbus.EventBus;

public class NewsDetailsActivity extends AppCompatActivity {

    private WebView newsDetailsWebView;
    private LinearLayout backRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_news_details);

            newsDetailsWebView = findViewById(R.id.nd_news_details_web_view);
            backRoot = findViewById(R.id.nd_back_root);

            Bundle bundle = getIntent().getExtras();
            newsDetailsWebView.loadUrl(bundle.getString("Link"));

            backRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OffsideApplication.setIsBackFromNewsFeed(true);
                    EventBus.getDefault().post(new NavigationEvent(R.id.nav_action_news));
                    finish();
                }
            });

        } catch (Exception ex) {
                    ACRA.getErrorReporter().handleSilentException(ex);
        }

    }


}
