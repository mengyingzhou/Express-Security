package com.comyou.QRScan;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aerber on 16-9-22.
 */
public class LinkerServer {

    private String url = "http://123.206.70.166/QR/";
    private String response = null;
    List<NameValuePair> params = new ArrayList<NameValuePair>();

    public LinkerServer(String tagPage, List<NameValuePair> newParams){
        tagPage += ".php";
        url += tagPage;
        params = newParams;
    }

    public LinkerServer(String tagPage) {
        tagPage += ".php";
        url += tagPage;
    }

    public boolean Linker(){
        HttpPost httpRequest = new HttpPost(url);
        try {
            HttpEntity httpEntity = new UrlEncodedFormEntity(params, "utf-8");
            httpRequest.setEntity(httpEntity);

            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpRequest);

            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                response = EntityUtils.toString(httpResponse.getEntity());
                Log.i("zmy", "response = " + response);
            } else {
                Log.i("zmy", "Link error");
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        if (!response.equals("0") && !response.equals("2") && !response.equals(""))
            return true;

        return false;
    }

    public String getResponse(){
        return response;
    }
}
