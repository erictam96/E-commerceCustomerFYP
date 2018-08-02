package com.ecommerce.customer.fypproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PrivacyPolicy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        WebView webview = findViewById(R.id.help_webview);
        webview.getSettings().setJavaScriptEnabled(false);
        webview.loadUrl("https://ecommercefyp.000webhostapp.com/privacypolicy.htm");
        webview.setWebViewClient(new WebViewController());
    }

    class WebViewController extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}

