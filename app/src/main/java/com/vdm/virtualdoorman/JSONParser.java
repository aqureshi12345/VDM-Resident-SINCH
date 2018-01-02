package com.vdm.virtualdoorman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.util.Log;

public class JSONParser {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    HttpResponse httpResponse;
    HttpEntity httpEntity;

    // constructor
    public JSONParser() {

    }

    // function get json from url
    // by making HTTP POST or GET mehtod
    @SuppressLint("LongLogTag")
    public JSONObject  makeHttpRequest(String url, String method, JSONObject data) {
//JSONObject data
        // Making HTTP request
        try {
            StringEntity se;
            // check for request method
            if (method.equals("POST")) {
                // request method is POST
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);

                boolean cont = data.getBoolean("newApi");
                if(data.getBoolean("newApi")){
                    JSONArray je = new JSONArray();
                    je.put(data);
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                    nameValuePairs.add(new BasicNameValuePair("post_data", je.toString()));
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                   // httpPost.setEntity(se);
                    Log.e("URL is---------", url);
                    Log.e("json is+++++++++++", je.toString());
                }else{
                    se = new StringEntity(data.toString());
                    Log.e("URL is---------", url);
                    Log.e("json is+++++++++++", data.toString());
                    httpPost.setEntity(se);
                }



                //httppost.setEntity(new UrlEncodedFormEntity(se));
                //StringEntity json = new StringEntity(se.toString());
                //se.setContentType("application/json;charset=UTF-8");



                httpResponse = httpClient.execute(httpPost);
                httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

            } else if (method.equals("GET")) {
                // request method is GET
                DefaultHttpClient httpClient = new DefaultHttpClient();
                //String paramString = URLEncodedUtils.format(params, "utf-8");
                String params="";
                if(data.has("newApi")) {
                    JSONArray json = new JSONArray();
                    json.put(data);
                    params = json.toString();

                }else{
                    params= data.toString() ;
                }
                params = URLEncoder.encode(params, "UTF-8");
                if (params.contains("rid")) {
                    url += "&rid=" + data.getString("rid");
                    Log.e("PAram IS?????????", params);
                    Log.e("URL IS??????????", url);
                } else if (params.contains("CALL_ID")) {

                    String callStoreURL = url + params;
                    url = callStoreURL;
                    Log.e("PAram IS---------------", params);
                    Log.e("URL IS---------------", url);
                } else {
                    if (params.isEmpty() || params.equalsIgnoreCase("[{}]") || params.equalsIgnoreCase("%5B%7B%7D%5D")) {
                            Log.e("PAram IS---------------", params);
                            Log.e("URL IS when params is empty---------------", url);

                     } else if(data.has("newApi")) {
                            url =url+params;
                            Log.e("PAram IS---------------", params);
                            Log.e("URL IS---------------", url);
                    }
                    else{
                        url += "&data="+ params;
                        Log.e("PAram IS---------------", params);
                        Log.e("URL IS---------------", url);

                    }
                }
                HttpGet httpGet = new HttpGet(url);
                HttpResponse httpResponse = httpClient.execute(httpGet);

                //Log.i("Status Code:",httpResponse.getStatusLine().getStatusCode().toString());
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();


            }
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                json = sb.toString();
                Log.e("Response is ++ ", json);
            } catch (Exception e) {

                Log.e("Buffer Error", "Error converting result " + e.toString());
            }


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        // try parse the string to a JSON object
        try {
            Log.e("Response is +++++ ", json);
            jObj = new JSONObject(json);


        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String


        catch (Exception e) {

            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        return jObj;
    }
}
