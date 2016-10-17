package com.example.mpe.smssender;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    EditText msg;
    Button send;
    Spinner sp;
    String groupid;
    String monumber1;
    ArrayList<String> groups;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
        groups = new ArrayList<String>();
        MyTask mt = new MyTask();
        mt.execute("http://192.168.0.112:8000/userdata/");

        sp = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, groups);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(aa);
        /*sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(parent.getContext(), "onItemSelected", Toast.LENGTH_SHORT).show();
            Log.d("Console", "item selected");
            groupid=sp.getSelectedItem().toString();
            Log.i(TAG,groupid);
        }


        public void onNothingSelected(AdapterView<?> parent) {
            Log.d("Console", "nothing selected");
        }
    });*/
        msg = (EditText) findViewById(R.id.editText);
        send = (Button) findViewById(R.id.send);

        send.setOnClickListener(this);
    }

    public void onClick(View view) {

        if (view.getId() == R.id.send) {
            try {

                smsapi();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            Toast.makeText(MainActivity.this, "Message Sent Succesfully.. ", Toast.LENGTH_LONG).show();
            msg.setText(" ");
            msg.setHint("Enter your msg here..");
        }
    }


    public void smsapi() throws UnsupportedEncodingException {
//        groupid=  sp.getSelectedItem().toString();
        groupid = String.valueOf(sp.getSelectedItem());

      //  groupid = "2";
        String data = URLEncoder.encode("groupid", "UTF-8")
                + "=" + URLEncoder.encode(groupid, "UTF-8");

        String text = "";
        BufferedReader reader = null;

        // Send data
        try {
            URL url = new URL("http://192.168.0.112:8000/Requestes/");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the server response

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }


            text = sb.toString();
        } catch (Exception ex) {

        } finally {
            try {

                reader.close();
            } catch (Exception ex) {
            }
        }


    }


    class MyTask extends AsyncTask<String, String, String> {

        String res = " ";

        protected String doInBackground(String... strings) {
            try {
                URL u1 = new URL(strings[0]);
                InputStream is = u1.openStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String s1 = br.readLine();
                while (s1 != null) {
                    res += s1;
                    s1 = br.readLine();
                }

            } catch (Exception e) {
                Log.e("erooorrrrrrr...", "nnnnnnnaveeeeen", e);

            }
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                Log.i(TAG, s);
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("Groups");
                Log.i(TAG, "Json array" + jsonArray.toString());
                Log.i(TAG, " length==" + jsonArray.length());

                int i = 0;

                String l1 = jsonArray.getString(0);
                groups.add(i++, l1);
                String l2 = jsonArray.getString(1);
                groups.add(i++, l2);
                String l3 = jsonArray.getString(2);
                groups.add(i++, l3);
                String l4 = jsonArray.getString(3);
                groups.add(i++, l4);
                String l5 = jsonArray.getString(4);
                groups.add(i++, l5);
            } catch (Exception e) {
                Log.e("eroor22", "aaya", e);
            }
        }
    }

}

