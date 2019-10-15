package com.example.gocar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import activity.LoginActivity;
import helper.SQLiteHandler;
import helper.SessionManager;

public class MainActivity extends Activity {

    private Button btnLogout;

    private SQLiteHandler db;
    private SessionManager session;

        String URL_TABLE= "http://172.20.10.4/android_login_api/table.php";
        String[] Model_Name;
        String[] Production_Year;
        String[] image_path;
        String[] Fuel_Level;
        String[] Longitude;
        String[] Latitude;
        ListView listView;
        BufferedInputStream is;
        String line =null;
        String result=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=(ListView)findViewById(R.id.listView);

        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));
        collectdata();
        CustomListView customListView=new CustomListView(this,Model_Name,Production_Year,Latitude,Longitude,image_path,Fuel_Level);
        listView.setAdapter(customListView);
       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    for(int i=0;i<14;i++){
                        if(position==i){
                            String msg1=Latitude[position];
                            String msg2 =Longitude[position];
                            Intent intent1 = new Intent(MainActivity.this, MapActivity.class);
                            intent1.putExtra("EXTRA_MESSAGE_1",msg1);
                            intent1.putExtra("EXTRA_MESSAGE_2",msg2);
                            startActivity(intent1);


                        }

                    }
            }
        });




        btnLogout = (Button) findViewById(R.id.btnLogout);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");


        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

    }

    private void collectdata() {
        try {
            URL url=new URL(URL_TABLE);
            HttpURLConnection con=(HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            is=new BufferedInputStream(con.getInputStream());

        }catch (Exception ex){
            ex.printStackTrace();
        }
        try {
            BufferedReader br=new BufferedReader(new InputStreamReader(is));
            StringBuilder sb=new StringBuilder();
            while((line=br.readLine())!=null) {
                sb.append(line+"\n");
            }
            is.close();
            result=sb.toString();

        }catch (Exception ex){
            ex.printStackTrace();
        }

        try {
            JSONArray ja=new JSONArray(result);
            JSONObject jo=null;
            Model_Name=new String[ja.length()];
            Production_Year= new String[ja.length()];
            Latitude=new String[ja.length()];
            Longitude=new String[ja.length()];
            image_path=new String[ja.length()];
            Fuel_Level=new String[ja.length()];



            for (int i=0;i<=ja.length();i++){
                jo=ja.getJSONObject(i);
                Model_Name[i]=jo.getString("Model_Name");
                Production_Year[i]=jo.getString("Production_Year");
                Latitude[i]= jo.getString("Latitude");
                Longitude[i]= jo.getString("Longitude");
                image_path[i]="http://172.20.10.4/"+jo.getString("image_path");
                Fuel_Level[i]=jo.getString("Fuel_Level");


            }



        }catch (Exception ex){
            ex.printStackTrace();
        }




    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}