package com.pujexx.tifsdk;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebViewClient;

import com.pujexx.tifsdk.config.URLOauth;

import java.util.UUID;

/**
 * Created by pujexx on 6/9/15.
 */
public class WebviewOauth extends Dialog {

    private WebView webview;

    private UUID state;

    private static final String TAG = "Twitter-WebView";

    private Context context;


    public WebviewOauth(Context context) {
        super(context);


        this.context = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        state = UUID.randomUUID();
        URLOauth.OAUTH_CSRF = String.valueOf(state);
        String URL_SIGNIN = URLOauth.OAUTH_SIGNIN_URL;
        URL_SIGNIN += "&state="+URLOauth.OAUTH_CSRF;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.webview);


        webview = (WebView) this.findViewById(R.id.webview);

        WindowManager.LayoutParams params = this.getWindow().getAttributes();

        Window window = this.getWindow();

        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        window.setAttributes(params);

        CookieSyncManager.createInstance(this.context);
        CookieManager cookieManager = CookieManager.getInstance();

        cookieManager.removeAllCookie();

        webview.setVerticalScrollBarEnabled(false);
        webview.setHorizontalScrollbarOverlay(false);
        webview.setWebViewClient(new implementWebview());
        webview.getSettings().setJavaScriptEnabled(true);

        webview.loadUrl(URL_SIGNIN);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    private class implementWebview extends WebViewClient{
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url.startsWith("myapp://oauthresponse")){
                Uri uri = Uri.parse(url);
                String code = uri.getQueryParameter("code");


                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    }
}
