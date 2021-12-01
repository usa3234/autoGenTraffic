package com.example.dummya;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity_DummyA";

    private static final String TAG_JSON="dummyA";
    private static final String TAG_D1 = "D1";
    private static final String TAG_D2 = "D2";
    private static final String TAG_D3 ="D3";

    ArrayList<HashMap<String, String>> mArrayList;
    ListView mlistView;
    String mJsonString;
    ListAdapter adapter;

    private final Timer mTimer = new Timer();
    private TimerTask mTimerTask;

    private DummyAService mService;
    private boolean isBind;

    ServiceConnection sconn = new ServiceConnection() {
        @Override //서비스가 실행될 때 호출
        public void onServiceConnected(ComponentName name, IBinder service) {
            DummyAService.MyBinder myBinder = (DummyAService.MyBinder) service;
            mService = myBinder.getService();

            isBind = true;
            Log.e("LOG", "onServiceConnected()");
        }

        @Override //서비스가 종료될 때 호출
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            isBind = false;
            Log.e("LOG", "onServiceDisconnected()");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);

        mlistView = (ListView) findViewById(R.id.listView_main_list);

        Button btnSearch = (Button) findViewById(R.id.search);
        Button btnQuit = (Button) findViewById(R.id.quit);

        EditText etPeriod = (EditText) findViewById(R.id.period);
        int period = Integer.parseInt(etPeriod.getText().toString());
        mArrayList = new ArrayList<>();

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimerTask = createTimerTask();
                mTimer.schedule(mTimerTask, 1000, period * 1000);
            }
        });

        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimerTask.cancel();
                Log.d(TAG, "DummyA 중지되었습니다.");
                stopService(new Intent(MainActivity.this, DummyAService.class)); // 서비스 종료
                Toast.makeText(MainActivity.this, "DummyA 중지되었습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //타이머 테스크 생성, 동작은 핸들러로 메세지를 넘겨서 동작시킨다
    private TimerTask createTimerTask() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                startService(new Intent(MainActivity.this, DummyAService.class)); // 서비스 시작
                Log.d(TAG, "DummyA 실행중");

                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run()
                    {
                        Toast.makeText(MainActivity.this, "DummyA 실행중", Toast.LENGTH_SHORT).show();
                    }
                }, 0);

                mArrayList.clear();
                GetData task = new GetData();
                task.execute("https://xxxx.php");
            }
        };
        return timerTask;
    }

    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

           /* progressDialog = ProgressDialog.show(MainActivity.this,
                    "Please Wait", null, true, true);*/
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            //progressDialog.dismiss();
            Log.d(TAG, "response  - " + result);

            mJsonString = result;
            showResult();
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];

            try {

                URL url = new URL(serverURL);

                SSLConnect ssl = new SSLConnect();

                ssl.postHttps(serverURL, 1000, 1000);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

    private void showResult(){
        try {
            //JSONObject jsonObject = new JSONObject(mJsonString);
            //Log.d(TAG, jsonObject.getString("D1"));
            //JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            JSONArray JA =new JSONArray(mJsonString);
            JSONObject json= null;
            final String[] str1 = new String[JA.length()];

            for(int i=0;i<JA.length();i++)
            {
                json=JA.getJSONObject(i);
                String D1 =json.getString("D1");
                String D2 =json.getString("D2");
                String D3 =json.getString("D3");
                //Log.d(TAG, str1[i]);

                HashMap<String,String> hashMap = new HashMap<>();

                hashMap.put(TAG_D1, D1);
                hashMap.put(TAG_D2, D2);
                hashMap.put(TAG_D3, D3);

                mArrayList.add(hashMap);

            }

            adapter = new SimpleAdapter(
                    MainActivity.this, mArrayList, R.layout.item_list,
                    new String[]{TAG_D1,TAG_D2, TAG_D3},
                    new int[]{R.id.textView_list_d1, R.id.textView_list_d2, R.id.textView_list_d3}
            );

            mlistView.setAdapter(adapter);

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }


}
