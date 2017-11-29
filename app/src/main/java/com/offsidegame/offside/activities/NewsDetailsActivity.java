package com.offsidegame.offside.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.offsidegame.offside.R;
import com.offsidegame.offside.events.LoadingEvent;
import com.offsidegame.offside.events.NavigationEvent;
import com.offsidegame.offside.models.OffsideApplication;

import org.acra.ACRA;
import org.greenrobot.eventbus.EventBus;

public class NewsDetailsActivity extends AppCompatActivity {

    private WebView newsDetailsWebView;
    private LinearLayout backRoot;
    private LinearLayout loadingRoot;
    private TextView loadingMessageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_news_details);

            loadingRoot = findViewById(R.id.shared_loading_root);
            loadingMessageTextView = findViewById(R.id.shared_loading_message_text_view);
            newsDetailsWebView = findViewById(R.id.nd_news_details_web_view);
            backRoot = findViewById(R.id.nd_back_root);

            loadingMessageTextView.setText("Loading article...");
            loadingRoot.setVisibility(View.VISIBLE);

            Bundle bundle = getIntent().getExtras();

            newsDetailsWebView.setWebViewClient(new WebViewClient() {

                public void onPageFinished(WebView view, String url) {
                    loadingRoot.setVisibility(View.GONE);

                }
            });
            EventBus.getDefault().post(new LoadingEvent(true,"Loading article..."));
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
