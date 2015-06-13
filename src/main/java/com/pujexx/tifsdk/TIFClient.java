package com.pujexx.tifsdk;

import android.util.Log;

import com.pujexx.tifsdk.config.TIFClientConfig;
import com.pujexx.tifsdk.config.URLOauth;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AUTH;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by pujexx on 6/13/15.
 */
public class TIFClient {


    public String output_string(InputStream is){
        StringBuilder str = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        try {
            while ((line = br.readLine()) != null) {
                str.append(line+"\n");
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return str.toString();
    }

    public String access_token(String code) throws MalformedURLException {
        String uri = URLOauth.OAUTH_ACCESS_TOKEN_URI+code;
        StringBuilder str = new StringBuilder();
        try {
            HttpURLConnection con = (HttpURLConnection) ( new URL(uri)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            InputStream is = con.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                str.append(line+"\n");
            }
            br.close();


            con.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return str.toString();
    }

    public String getSecure2(String path, String token) throws MalformedURLException, UnsupportedEncodingException {

        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("admin", "kampret".toCharArray());
            }
        });

        StringBuilder str = new StringBuilder();

        String any = URLEncoder.encode(token,"UTF-8").toString();
        String id = URLEncoder.encode(TIFClientConfig.OAUTH_CLIENT_ID,"UTF-8").toString();

        String uri = TIFClientConfig.SITE_URL+path+"/code/"+any+"/client_id/"+id;
        Log.d("uri", uri);
        try {
            HttpURLConnection con = (HttpURLConnection) ( new URL(uri)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            InputStream is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                str.append(line+"\n");
            }
            br.close();



        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }


    public String getSecure (String uri,String token) throws ClientProtocolException, IOException{
        String responseBody = null;
        DefaultHttpClient httpclient = new DefaultHttpClient();
        DefaultHttpClient httpclient2 = new DefaultHttpClient();
        String ur = TIFClientConfig.SITE_URL+uri+"/code/"+URLEncoder.encode(token)+"/client_id/"+URLEncoder.encode(TIFClientConfig.OAUTH_CLIENT_ID);
        HttpGet httpget = new HttpGet(ur);
        System.out.println("Requesting : " + httpget.getURI());

        try {
            //Initial request without credentials returns "HTTP/1.1 401 Unauthorized"
            HttpResponse response = httpclient.execute(httpget);
            System.out.println(response.getStatusLine());

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {

                //Get current current "WWW-Authenticate" header from response
                // WWW-Authenticate:Digest realm="My Test Realm", qop="auth",
                //nonce="cdcf6cbe6ee17ae0790ed399935997e8", opaque="ae40d7c8ca6a35af15460d352be5e71c"
                Header authHeader = response.getFirstHeader(AUTH.WWW_AUTH);
                System.out.println("authHeader = " + authHeader);

                DigestScheme digestScheme = new DigestScheme();

                //Parse realm, nonce sent by server.
                digestScheme.processChallenge(authHeader);

                UsernamePasswordCredentials creds = new UsernamePasswordCredentials("admin", "kampret");
                httpget.addHeader(digestScheme.authenticate(creds, httpget));

                ResponseHandler<String> responseHandler = new BasicResponseHandler();

                try {
                    responseBody = httpclient2.execute(httpget, responseHandler);
                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                System.out.println("responseBody : " + responseBody);
            }

        } catch (MalformedChallengeException e) {
            e.printStackTrace();
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }  finally {
            httpclient.getConnectionManager().shutdown();
            httpclient2.getConnectionManager().shutdown();
        }


        return responseBody;
    }

}
