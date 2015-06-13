package com.pujexx.tifsdk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.pujexx.tifsdk.config.URLOauth;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.UUID;

/**
 * Created by pujexx on 6/13/15.
 */
public class ActivityTIFOauth extends ActionBarActivity {

    private WebView webview;

    private UUID state;

    

    private static final String TAG = "Twitter-WebView";

    private Context context;

    private ProgressBarCircularIndeterminate progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        state = UUID.randomUUID();
        URLOauth.OAUTH_CSRF = String.valueOf(state);
        String URL_SIGNIN = URLOauth.OAUTH_SIGNIN_URL;
        URL_SIGNIN += "&state="+URLOauth.OAUTH_CSRF;

        webview = (WebView) this.findViewById(R.id.webview);
        progress = (ProgressBarCircularIndeterminate) findViewById(R.id.progresss);
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();

        cookieManager.removeAllCookie();

        webview.setVerticalScrollBarEnabled(false);
        webview.setHorizontalScrollbarOverlay(false);
        webview.setWebViewClient(new implementWebview());
        webview.getSettings().setJavaScriptEnabled(true);

        webview.loadUrl(URL_SIGNIN);
    }



    private class implementWebview extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progress.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            progress.setVisibility(View.INVISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url.startsWith("myapp://oauthresponse")){
                Uri uri = Uri.parse(url);
                String code = uri.getQueryParameter("code");
                String a[] = {code};
                new getToken().execute(a);

                return true;

            }

            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    private class getToken extends AsyncTask<String, String, String>{

        private String token = null;
        @Override
        protected String doInBackground(String... params) {
            String code = params[0];

            TIFClient client = new TIFClient();
            try {
               String result =  client.access_token(code);
                JSONObject jsonObject = new JSONObject(result);
                token = jsonObject.getString("access_token");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("token",token);
                setResult(RESULT_OK,returnIntent);
                ActivityTIFOauth.this.finish();
                progress.setVisibility(View.INVISIBLE);
        }
    }

}
