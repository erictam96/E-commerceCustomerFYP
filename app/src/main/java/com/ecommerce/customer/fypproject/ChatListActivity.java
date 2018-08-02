package com.ecommerce.customer.fypproject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.ecommerce.customer.fypproject.adapter.ChatList;
import com.ecommerce.customer.fypproject.adapter.ChatListAdapter;
import com.ecommerce.customer.fypproject.adapter.UploadProcess;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class ChatListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private String user;
    private String DisplayNamePathOnServer = "https://ecommercefyp.000webhostapp.com/retailer/retailer_login.php";
    private List<ChatList> chatList;

    ChatListAdapter customAdapter;
    ProgressBar msgProgressBar;
    boolean firstRun=true;
    long firstTime;
    int count=0;
    private static final int AUTO_DISMISS_MILLIS = 3500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        Toolbar chatlist_toolbar = findViewById(R.id.chatlisttoolbar);
        setSupportActionBar(chatlist_toolbar);
        Objects.requireNonNull(Objects.requireNonNull(getSupportActionBar())).setTitle(this.getString(R.string.chat));

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        chatList=new Stack<>();
        msgProgressBar=findViewById(R.id.msgProgressBar);
        recyclerView = findViewById(R.id.chatlistRecyler);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManagerOfrecyclerView = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManagerOfrecyclerView);

//        customAdapter=new ChatListAdapter(getApplicationContext());//bind empty list first
//        recyclerView.setAdapter(customAdapter);


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        String referenceURL = "Chat/"+user;
        Query reference1 = database.getReference(referenceURL);

        Log.d("url", reference1.toString());


       // chatList=new Stack<>();


        reference1.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final String getKey = dataSnapshot.getKey();
               // Log.e("getKey",dataSnapshot.getKey());
                Query reference2 = database.getReference("Chat/"+user+"/"+getKey).limitToLast(1);
                reference2.addChildEventListener(new ChildEventListener() {
                                                     @Override
                                                     public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                                       //  Log.e("getMsg2", Objects.requireNonNull(dataSnapshot.child("message").getValue()).toString());
                                                         String message = Objects.requireNonNull(dataSnapshot.child("message").getValue()).toString();
                                                         String time = convertTime(Objects.requireNonNull(dataSnapshot.child("time").getValue()).toString());
                                                         addtoList(getKey, message,time,Long.valueOf(Objects.requireNonNull(dataSnapshot.child("time").getValue()).toString()));
                                                     }

                                                     @Override
                                                     public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                                     }

                                                     @Override
                                                     public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                                     }

                                                     @Override
                                                     public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                                     }

                                                     @Override
                                                     public void onCancelled(@NonNull DatabaseError databaseError) {

                                                     }
                                                 });
//                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//                    Map<String, Object> map = (Map<String, Object>) postSnapshot.getValue();
//                     message = map.get("message").toString();
//                }
//                Log.e("getMsg",message.toString());


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setVisibility(View.INVISIBLE);
        msgProgressBar.setVisibility(View.VISIBLE);

    }


    private void addtoList(final String key, final String msg,final String time,final long compareTime) {
        Log.e("addList",key+"   "+msg+"   "+time);
        //chatList=new Stack<>();

        class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {
            protected void onPreExecute() {
                super.onPreExecute();
                //progressDialog = ProgressDialog.show(SplashScreenActivity.this,"Authenticating user...","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
              //  Log.e("REsponse", response);
                recyclerView.setVisibility(View.VISIBLE);
                msgProgressBar.setVisibility(View.INVISIBLE);
                ChatList x = new ChatList();
                x.setName(response);
                x.setMsg(msg);
                x.setDate(time);
                x.setRecvUID(key);

                //customAdapter.addNode(x);
                //chatList.push(x);
                if(customAdapter==null){
                    count++;
                    chatList.add(x);
                    Log.e("setadapter"+Integer.toString(count),response);

                    firstTime=compareTime;
                    customAdapter=new ChatListAdapter(chatList,getApplicationContext());
                    recyclerView.setAdapter(customAdapter);
                   // recyclerView.getAdapter().notifyItemRangeChanged(0,chatList.size());
                }else{
                    Log.e("add to adapter",response);
                    if(firstTime<compareTime){
                        customAdapter.addNode(x);
                    }else{
                        customAdapter.addLastNode(x);
                    }

                }

                //progressDialog.dismiss();
        }

            @Override
            protected String doInBackground(Void... params) {

                UploadProcess ProcessClass = new UploadProcess();

                HashMap<String,String> HashMapParams = new HashMap<>();
                HashMapParams.put("getDisplayName",key);
                return ProcessClass.HttpRequest(DisplayNamePathOnServer, HashMapParams);
            }
        }

        AsyncTaskUploadClass AsyncTaskUploadClass = new AsyncTaskUploadClass();
        AsyncTaskUploadClass.execute();
    }

    public String convertTime(String time){
        Long convertTime = Long.valueOf(time);
        Date date = new Date(convertTime);
        Format format = new SimpleDateFormat("hh:mm aa");
        return format.format(date);
    }

    public  boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
