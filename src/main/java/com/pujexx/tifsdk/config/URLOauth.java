package com.pujexx.tifsdk.config;

import com.pujexx.tifsdk.TIFClient;

import java.net.URLEncoder;

/**
 * Created by pujexx on 6/9/15.
 */
public class URLOauth {



    public static String OAUTH_SCOPE ="basic";
    public static String OAUTH_URI ="";
    public static String OAUTH_BASE = "http://oauth.tif.uad.ac.id/index.php/oauth";
    public static String OAUTH_REDIRECT_URI = "http://tif.uad.ac.id/redirect/mobile";
    public static String OAUTH_SIGNIN_URL = OAUTH_BASE
            +"?response_type=code&scope="
            +OAUTH_SCOPE+"&client_id="+TIFClientConfig.OAUTH_CLIENT_ID+"&redirect_uri="+OAUTH_REDIRECT_URI;

    static String redirect_encode = URLEncoder.encode(OAUTH_REDIRECT_URI);
    public static String OAUTH_ACCESS_TOKEN_URI = OAUTH_BASE
            +"/access_token?client_id="+ TIFClientConfig.OAUTH_CLIENT_ID
            +"&client_secret="+TIFClientConfig.OAUTH_CLIENT_SECRET
            +"&redirect_uri="+redirect_encode
            +"&grant_type=authorization_code&"
            +"code=";

    public static String OAUTH_CSRF = null;

    public static String TOKEN = "";

}
