package com.hmkcode;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView tvIsConnected;
    EditText etName;
    EditText etCountry;
    EditText etTwitter;
    TextView tvResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);
        etName = findViewById(R.id.etName);
        etCountry = findViewById(R.id.etCountry);
        etTwitter = findViewById(R.id.etTwitter);
        tvResult = (TextView) findViewById(R.id.tvResult);
        checkNetworkConnection();



    }

    // check network connection
    public boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean isConnected = false;
        if (networkInfo != null && (isConnected = networkInfo.isConnected())) {
            // show "Connected" & type of network "WIFI or MOBILE"
            tvIsConnected.setText("Connected "+networkInfo.getTypeName());
            // change background color to red
            tvIsConnected.setBackgroundColor(0xFF7CCC26);


        } else {
            // show "Not Connected"
            tvIsConnected.setText("Not Connected");
            // change background color to green
            tvIsConnected.setBackgroundColor(0xFFFF0000);
        }

        return isConnected;
    }


    private String httpPost(String myUrl) throws IOException, JSONException {
        String result = "";

        URL url = new URL(myUrl);

        // 1. create HttpURLConnection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

        // jak robisz geta to punkt 2 i 3 zakomentuj, a jak postem coś gdzieś wrzucasz to możesz odkomentować
        // i w linijce 155 i niżej możesz dać parametry

        // 2. build JSON object
        //JSONObject jsonObject = buidJsonObject();

        // 3. add JSON content to POST request body
        //setPostRequestContent(conn, jsonObject);

        // 4. make POST request to the given URL
        conn.connect();

        // 5. return response message
        BufferedReader br;
        if (200 <= conn.getResponseCode() && conn.getResponseCode() <= 299) {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            sb.append(output);
        }
        return sb.toString();


        //return conn.getResponseMessage()+"";

    }


    private class HTTPAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                try {
                    return httpPost(urls[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return "Error!";
                }
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            tvResult.setText(result);
        }
    }


    public void send(View view) {
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
        // perform HTTP POST request
        if(checkNetworkConnection())
            //w tej linijce zmieniasz urla
            new HTTPAsyncTask().execute("https://jsonplaceholder.typicode.com/posts/47");
        else
            Toast.makeText(this, "Not Connected!", Toast.LENGTH_SHORT).show();

    }

    private JSONObject buidJsonObject() throws JSONException {

        //tu możesz dodać parametry które idą do body zapytania
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("name", etName.getText().toString());
        jsonObject.accumulate("country",  etCountry.getText().toString());
        jsonObject.accumulate("twitter",  etTwitter.getText().toString());

        return jsonObject;
    }

    private void setPostRequestContent(HttpURLConnection conn, JSONObject jsonObject) throws IOException {

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(jsonObject.toString());
        Log.i(MainActivity.class.toString(), jsonObject.toString());
        writer.flush();
        writer.close();
        os.close();
    }

}
