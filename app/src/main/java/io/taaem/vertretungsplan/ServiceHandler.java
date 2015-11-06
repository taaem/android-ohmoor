package io.taaem.vertretungsplan;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import java.net.URL;
import java.net.ProtocolException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.net.HttpURLConnection;
import android.net.ConnectivityManager;
import android.content.Context;
import android.net.NetworkInfo;

import org.json.JSONObject;


public class ServiceHandler {

    static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;
    Context mContext;
    NetworkInfo mNetworkInfo;

    public ServiceHandler() {
        /*//this.mContext = mContext;
        ConnectivityManager connMgr = (ConnectivityManager)
                this.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        mNetworkInfo = connMgr.getActiveNetworkInfo();
*/
    }

    /**
     * Check Internet Connection
     */
    public boolean checkInternet() {
        if (mNetworkInfo != null && mNetworkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }



    /**
     * Making service call
     *
     * @url - url to make request
     * @param - apiKey  - ApiKey for Server
     */
    public String makeServiceCall(String url, String apiKey) {
        int len = 1000;
        InputStream in = null;
        String response = "";
        try {
            URL nUrl = new URL(url);
            // Http Client
            HttpURLConnection urlConnection = (HttpURLConnection) nUrl.openConnection();
            urlConnection.setRequestProperty("x-apikey", apiKey);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream is = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line;
            while ( (line = br.readLine()) != null)
                response += line;

            br.close();
            is.close();
            return response;
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally{
            if (in != null) {
                try {
                    in.close();
                }catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }
}
