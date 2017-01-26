package nadim.skibum;

import android.os.AsyncTask;
import android.util.JsonReader;
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
public class HttpUrlConnectionJson extends AsyncTask<String, Void, ArrayList>{
    private static final String TAG = "HttpUrlConnectionJson";

    @Override
    protected ArrayList doInBackground(String... strings) {
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
                    stringBuilder.append(response);
                }
                bufferedReader.close();

                JSONObject json = new JSONObject(stringBuilder.toString()).getJSONObject("data").getJSONObject("viewer").getJSONObject("allMountains");
                JSONArray edges = json.getJSONArray("edges");

                Log.d(TAG, stringBuilder.toString());

                ArrayList<String> allNames = new ArrayList<String>();
                ArrayList<String> allLats = new ArrayList<String>();
                ArrayList<String> allLongs = new ArrayList<String>();
                ArrayList<ArrayList> mapVals =new ArrayList<ArrayList>();
                for (int i=0; i<edges.length(); i++) {
                    JSONObject node = edges.getJSONObject(i);

                    JSONObject node2 = node.getJSONObject("node");
                    allNames.add(node2.getString("Name"));
                    allLats.add(node2.getString("Latitude"));
                    allLongs.add(node2.getString("Longitude"));

                }
                mapVals.add(allNames);
                mapVals.add(allLats);
                mapVals.add(allLongs);



                return mapVals;

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
