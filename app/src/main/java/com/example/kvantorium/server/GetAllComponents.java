package com.example.kvantorium.server;

import android.os.AsyncTask;
import android.util.Log;
import android.webkit.HttpAuthHandler;
import android.widget.ProgressBar;

import com.example.kvantorium.Component;
import com.example.kvantorium.OnComponentsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class GetAllComponents extends AsyncTask<URL, Integer, ArrayList<Component>> {
    //initiate vars
    private OnComponentsListener mListener;
    ProgressBar progressBar;
    ArrayList<Component> components = new ArrayList<Component>();

    public GetAllComponents(OnComponentsListener listener, ProgressBar progressBar) {
        mListener = listener;
        this.progressBar = progressBar;
    }

    protected ArrayList<Component> doInBackground(URL... urls) {
        //do stuff


        HashMap<String, String> params = new HashMap<>();
        params.put("REQUEST", "getAllComponents");

        String data = null;

        StringBuilder sbParams = new StringBuilder();
        int i = 0;
        for (String key : params.keySet()) {
            try {
                if (i != 0) {
                    sbParams.append("&");
                }
                sbParams.append(key).append("=")
                        .append(URLEncoder.encode(params.get(key), "UTF-8"));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            i++;
        }


        try {
//            String params = "REQUEST=js";
            String url = "http://192.168.243.90/PythonProject/server_test.py";
            URL urlObj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();

            try {
                byte[]postDataBytes = sbParams.toString().getBytes("UTF-8");
                //conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/text");
                conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000);
                conn.setUseCaches(false);
                conn.setDoOutput(true);
                conn.getOutputStream().write(postDataBytes);

                conn.connect();

                /*
                String paramsString = sbParams.toString();
                //  String paramsString = "a=test";

                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(paramsString);
                wr.flush();
                wr.close();
                 */
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader1 = new BufferedReader(new InputStreamReader(in));
                StringBuilder result1 = new StringBuilder();
                String line;
                while ((line = reader1.readLine()) != null) {
                    result1.append(line + "\n");
                }

                in.close();

                String result = result1.toString();
                System.out.println("From server: " + result);
/*
                JSONObject JObject = new JSONObject(result);
                String name = JObject.getString("name");
                int count = JObject.getInt("count");
                Component c = new Component();
                c.setNameComponent(name);
                c.setNumber(count);
                components.add(c);
*/
                JSONObject JObject = new JSONObject(result);
                JSONArray jArray = JObject.getJSONArray("components");
                for (i = 0; i < jArray.length(); i++) {

                    JSONObject jObject = jArray.getJSONObject(i);

                    int id = jObject.getInt("id");
                    String name = jObject.getString("name");
                    int count = jObject.getInt("count");
                    String image = jObject.getString("image");
                    Component c = new Component();
                    c.setId(id);
                    c.setNameComponent(name);
                    c.setNumber(count);
                    c.setImage(image);
                    components.add(c);
                    //String tab1_text = jObject.getString("tab1_text");
                    //int active = jObject.getInt("active");
                }

                } finally{
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            } catch (Exception e) {
            e.printStackTrace();
            }
            return components;




/*
        try {

            //String url = "http://192.168.1.69/PythonProject/server_test.py";
            String url = "http://192.168.243.90/PythonProject/server_test.py";
            URL urlObj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            try {
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept","application/json");

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.connect();

                String paramsString = sbParams.toString();
                //  String paramsString = "a=test";

                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(paramsString);
                wr.flush();
                wr.close();
            /*

            Component c = new Component();
            c.setNameComponent("Arduino");
            c.setNumber(30);
            components.add(c);
            return components;

            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader1 = new BufferedReader(new InputStreamReader(in));
                StringBuilder result1 = new StringBuilder();
                String line;
                while ((line = reader1.readLine()) != null) {
                    result1.append(line + "\n");
                }

                in.close();
                String result = result1.toString();

                JSONArray jArray = new JSONArray(result);
                for(i=0; i < jArray.length(); i++) {

                    JSONObject jObject = jArray.getJSONObject(i);

                    String name = jObject.getString("name");
                    int count = jObject.getInt("count");
                    Component c = new Component();
                    c.setNameComponent(name);
                    c.setNumber(count);
                    components.add(c);
                    //String tab1_text = jObject.getString("tab1_text");
                    //int active = jObject.getInt("active");

                } // End Loop

            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return components;
        */
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (this.progressBar != null) {
            progressBar.setProgress(values[0]);
        }
    }

    @Override
    protected void onPostExecute(ArrayList<Component> components) {
        //do stuff
        //super.onPostExecute(components);
        if (mListener != null) {
            mListener.onComponentsCompleted(components);
        }
    }
}
