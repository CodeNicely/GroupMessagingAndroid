package com.example.mpe.smssender;

import android.graphics.Color;
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
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = "MainActivity";
    EditText msg;
    Button send;
    Spinner spinner;
    String groupid;
    String monumber1;
    String spinnervalue;
    String data;
    ArrayList<String> groups;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        groups = new ArrayList<String>();
        MyTask mt = new MyTask();
        mt.execute("http://192.168.0.112:8000/userdata/");

        this.spinner = (Spinner) findViewById(R.id.myspinner);
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, groups);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);
        spinner.setOnItemSelectedListener(this);

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

            Toast.makeText(MainActivity.this,"Sucessfully sent", Toast.LENGTH_LONG).show();

            msg.setText(" ");
            msg.setHint("Enter your msg here..");
        }
    }


    public void smsapi() throws UnsupportedEncodingException {
//        groupid=  sp.getSelectedItem().toString();
        groupid = String.valueOf(spinner.getSelectedItem());

        Log.i(TAG,groupid);
        Log.i(TAG,String.valueOf(spinner.getSelectedItemPosition()));

       // Log.i(TAG,spinnervalue);
        //Log.i(TAG,spinner.getSelectedView().toString());

      //  groupid = "2";
        String data = URLEncoder.encode("groupid", "UTF-8")
                + "=" + URLEncoder.encode(groupid, "UTF-8");
        data += "&" + URLEncoder.encode("textmsg", "UTF-8") + "="
                + URLEncoder.encode(msg.getText().toString(), "UTF-8");
        String text = "";
        BufferedReader reader = null;
        try {
            URL url = new URL("http://192.168.0.112:8000/Requestes/");


            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
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

    @Override
    public void onItemSelected(AdapterView<?> arg0, View view, int i, long l) {
        int index = arg0.getSelectedItemPosition();
        Toast.makeText(getBaseContext(),"Hello item selected"+ groups.get(index), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
                for (i=0;i<jsonArray.length();i++){
                    groups.add(i,jsonArray.getString(i));
                }

            } catch (Exception e) {
                Log.e("eroor22", "aaya", e);
            }
        }
    }

}


