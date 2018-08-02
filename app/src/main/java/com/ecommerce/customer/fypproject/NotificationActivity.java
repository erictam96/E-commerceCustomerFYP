package com.ecommerce.customer.fypproject;

import android.os.AsyncTask;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.ecommerce.customer.fypproject.adapter.NotificationAdapter;
import com.ecommerce.customer.fypproject.adapter.NotificationList;
import com.ecommerce.customer.fypproject.adapter.UploadProcess;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class NotificationActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private String uid;
    private ProgressBar progressBarNotify;
    private List<NotificationList> notificationList;
    private ImageView image_notification;
    private TextView txt_notification;
    private final String NotificationPathOnServer = "https://ecommercefyp.000webhostapp.com/retailer/customer_manage_user.php";
    //String NotificationPathOnServer = "http://10.0.2.2/cashierbookPHP/Eric/customer_manage_user.php";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar notification_toolbar = findViewById(R.id.notification_toolbar);
        setSupportActionBar(notification_toolbar);
        Objects.requireNonNull(Objects.requireNonNull(getSupportActionBar())).setTitle(this.getString(R.string.notification));

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        progressBarNotify = findViewById(R.id.progressBarNotify);
        image_notification = findViewById(R.id.notification_image);
        txt_notification = findViewById(R.id.txt_no_notification);
        recyclerView = findViewById(R.id.notificationRecyler);
        refreshLayout = findViewById(R.id.notificationRefresh);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManagerOfrecyclerView = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManagerOfrecyclerView);
        GetFirebaseAuth();
        //JSON_HTTP_CALL();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                JSON_HTTP_CALL();
                refreshLayout.setRefreshing(false);

            }
        });

        Answers.getInstance().logCustom(new CustomEvent("Notification"));
    }

    private void JSON_HTTP_CALL() {

        notificationList=new ArrayList<>();

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                try {
                    if(!(response.equalsIgnoreCase("[]"))){
                        ParseJSonResponse(response);
                    }else{
                            progressBarNotify.setVisibility(View.GONE);
                            image_notification.setVisibility(View.VISIBLE);
                            txt_notification.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    Crashlytics.logException(e);
                    // handle your exception here!
                    e.printStackTrace();
                }
                Log.d("Response", response);
            }

            @Override
            protected String doInBackground(Void... params) {
                UploadProcess ProcessClass = new UploadProcess();

                HashMap<String,String> HashMapParams = new HashMap<>();

                HashMapParams.put("getNotification", uid);
                return ProcessClass.HttpRequest(NotificationPathOnServer, HashMapParams);
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClass = new AsyncTaskUploadClass();
        AsyncTaskUploadClass.execute();
    }

    //get response string and set into recyclerView
    private void ParseJSonResponse(String array) throws JSONException {
        JSONArray jarr = new JSONArray(array);//lv 1 array

        for(int a=0;a<jarr.length();a++){
            NotificationList x = new NotificationList();
            JSONObject json;
            json=jarr.getJSONObject(a);

            x.setNotifyID(json.getString("notifyID"));
            x.setNotifyTitle(json.getString("notifyTitle"));
            x.setNotifyMsg(json.getString("notifyMsg"));
            x.setNotifyDate(json.getString("notifyDate"));
            x.setNotifyAction(json.getString("notifyAction"));
            x.setNotifyStatus(json.getString("notifyStatus"));
            x.setNotifyURL(json.getString("notifyURL"));
            notificationList.add(x);
        }
        progressBarNotify.setVisibility(View.GONE);
        recyclerView.setAdapter(new NotificationAdapter(notificationList, this));
    }

    private void GetFirebaseAuth(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            Intent intent = new Intent(this.getApplicationContext(), SplashScreenActivity.class);
            try {
                FirebaseInstanceId.getInstance().deleteInstanceId();
            } catch (IOException e) {
                e.printStackTrace();
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(this,R.string.sessionexp,Toast.LENGTH_LONG).show();
        }else uid = firebaseAuth.getCurrentUser().getUid();
    }

    @Override
    public void onResume() {
        super.onResume();
        JSON_HTTP_CALL();
    }

    public  boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
