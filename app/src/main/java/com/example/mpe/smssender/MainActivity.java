package com.example.mpe.smssender;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonToken;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener ,AdapterView.OnItemSelectedListener{
    private static final String TAG = "MainActivity";
    EditText msg;
    Button send;
   Spinner sp;

    String monumber1;
    //String[] group = { "group1", "group2", "group3", "group3", "group5","group6"  };
    ArrayList<String> groups ;



    class MyTask extends AsyncTask<String, String, String> {

        String res=" ";
        protected String doInBackground(String... strings) {
            try {
                URL u1 = new URL(strings[0]);
                InputStream is=u1.openStream();
                InputStreamReader isr=new InputStreamReader(is);
                BufferedReader br=new BufferedReader(isr);
                String s1=br.readLine();
                while (s1!=null){
                    res +=s1;
                    s1=br.readLine();
                }

            } catch (Exception e) {
                Log.e("erooorrrrrrr...","nnnnnnnaveeeeen",e);

            }
            return res;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                Log.i(TAG,s);
                JSONObject jsonObject=new JSONObject(s);
                JSONArray jsonArray=jsonObject.getJSONArray("Groups");
                Log.i(TAG,"Json array"+jsonArray.toString());
                Log.i(TAG," length=="+jsonArray.length());

                int i=0;

                String l1=jsonArray.getString(0);
                    groups.add(i++,l1);
                    String l2=jsonArray.getString(1);
                    groups.add(i++,l2);
                    String l3=jsonArray.getString(2);
                    groups.add(i++,l3);
                    String l4=jsonArray.getString(3);
                    groups.add(i++,l4);
                    String l5=jsonArray.getString(4);
                    groups.add(i++,l5);






            }
            catch (Exception e){
                Log.e("eroor22","aaya",e);
            }}
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
        groups =new ArrayList<String>();
        sp= (Spinner) findViewById(R.id.spinner);
        sp.setOnItemSelectedListener(this);
        MyTask mt=new MyTask();
        mt.execute("http://192.168.0.112:8000/userdata/");



        msg = (EditText) findViewById(R.id.editText);
        send= (Button) findViewById(R.id.send);
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,groups);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        sp.setAdapter(aa);
        send.setOnClickListener(this);
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Toast.makeText(getApplicationContext(),i,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    public void onClick(View view) {

 if (view.getId()==R.id.send){
         smsapi();

         Toast.makeText(MainActivity.this,"Message Sent Succesfully.. ",Toast.LENGTH_LONG).show();
         msg.setText(" ");
     }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (1):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c =  managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
                                    null, null);
                            phones.moveToFirst();
                            String  cNumber = phones.getString(phones.getColumnIndex("data1"));

                        }
                        //   String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));



                    }

                }

                break;
        }

    }
    public void smsapi(){
 int i;
        for( i=0;i<1;i++) {
            String authkey = "124423Af7WzIdBB7m57cbda85";
            //String mobiles = mo.get(i);
            String senderId = "12345678";
            String message = msg.getText().toString();
            String route = "default";
            URLConnection myURLConnection = null;
            URL myURL = null;
            BufferedReader reader = null;
            String encoded_message = URLEncoder.encode(message);
            String mainUrl = "https://control.msg91.com/api/sendhttp.php?";
            StringBuilder sbPostData = new StringBuilder(mainUrl);
            sbPostData.append("authkey=" + authkey);
           // sbPostData.append("&mobiles=" + mobiles);
            sbPostData.append("&message=" + encoded_message);
            sbPostData.append("&route=" + route);
            sbPostData.append("&sender=" + senderId);
            mainUrl = sbPostData.toString();
            try {
                myURL = new URL(mainUrl);
                myURLConnection = myURL.openConnection();
                myURLConnection.connect();
                reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));

                String response;
                while ((response = reader.readLine()) != null)
                    //print response
                    Log.d("RESPONSE", "" + response);
                reader.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
