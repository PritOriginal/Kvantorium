package com.example.kvantorium.server;

import android.os.AsyncTask;
import android.widget.ProgressBar;

import com.example.kvantorium.Objective;
import com.example.kvantorium.OnTeammatesListener;
import com.example.kvantorium.Teammate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class GetTeammatesProject extends AsyncTask<URL, Integer, ArrayList<Teammate>> {
    private OnTeammatesListener mListener;
    ProgressBar progressBar;
    ArrayList<Teammate> teammates = new ArrayList<Teammate>();
    int id;

    public GetTeammatesProject(OnTeammatesListener mListener, int id, ProgressBar progressBar) {
        this.mListener = mListener;
        this.id = id;
        this.progressBar = progressBar;
    }

    @Override
    protected ArrayList<Teammate> doInBackground(URL... urls) {
        HashMap<String, String> params = new HashMap<>();
        params.put("REQUEST", "getTeammates");
        params.put("ID", String.valueOf(id));

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

                JSONObject JObject = new JSONObject(result);
                JSONArray jArray = JObject.getJSONArray("teammates");
                for (i = 0; i < jArray.length(); i++) {

                    JSONObject jObject = jArray.getJSONObject(i);

                    int id = jObject.getInt("id");
                    String firstName = jObject.getString("name");
                    String secondName = jObject.getString("secondname");
                    String role = jObject.getString("role");
                    Teammate teammate = new Teammate(id, firstName, secondName, role);
                    teammates.add(teammate);
                }
            } finally{
                if (conn != null) {
                    conn.disconnect();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return teammates;
    }
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (this.progressBar != null) {
            progressBar.setProgress(values[0]);
        }
    }

    @Override
    protected void onPostExecute(ArrayList<Teammate> teammates) {
        if (mListener != null) {
            mListener.onTeammatesCompleted(teammates);
        }
    }
}
