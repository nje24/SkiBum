package nadim.skibum;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;


/**
 * Created by nadim on 1/24/17.
 */
public class HttpUrlConnectionJson extends AsyncTask<String, Void, String>{
    private static final String TAG = "HttpUrlConnectionJson";

    @Override
    protected String doInBackground(String... strings) {
        String API = strings[0];
        String query = strings[1];
        HttpURLConnection connection = null;
        JSONObject obj = new JSONObject();
        try {
            obj.put("query", query);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            URL url = new URL(API);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            OutputStreamWriter streamWriter = new OutputStreamWriter(connection.getOutputStream());
            streamWriter.write(obj.toString());
            streamWriter.flush();
            StringBuilder stringBuilder = new StringBuilder();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(streamReader);
                String response = null;
                while ((response = bufferedReader.readLine()) != null) {
                    stringBuilder.append(response + "\n");
                }
                bufferedReader.close();

                Log.d(TAG, stringBuilder.toString());
                String[] nodes = stringBuilder.toString().split("edges");
                nodes[1] = "{" + "\"" + "edges" + nodes[1];
                String s = nodes[1].substring(0,nodes[1].length()-3);
                Log.d(TAG, s);
                JSONObject json = new JSONObject(s);
                JSONArray jsonArray = json.getJSONArray("edges");
                ArrayList<String> allNames = new ArrayList<String>();
                ArrayList<String> allLats = new ArrayList<String>();
                ArrayList<String> allLongs = new ArrayList<String>();
                for (int i=0; i<jsonArray.length(); i++) {
                    JSONObject node = jsonArray.getJSONObject(i);
                    Log.d(TAG, node.toString(1));
                    String name = node.getString("Name");

                    Log.d(TAG, "Name " + name.toString());

                }

               // Log.d(TAG, jsonArray.toString());
               // Log.d(TAG, allNames.toString());
              //  Log.d(TAG, allLats.toString());
              //  Log.d(TAG, allLongs.toString());


                return stringBuilder.toString();

            } else {
                Log.e(TAG, connection.getResponseMessage());
                return null;
            }
        } catch (Exception exception) {
            Log.e(TAG, exception.toString());
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
