package com.ecommerce.customer.fypproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class ChatActivity extends AppCompatActivity {
    private LinearLayout layout;
    private RelativeLayout layout_2;
    private ImageView sendButton;
    private EditText messageArea;
    private ScrollView scrollView;
    private String uid,ShopOwnerName;
    private String user;
    private int pxMargin,pxMarginText,pxMarginBox;
    private DatabaseReference mDatabase;
    private int addMargin =-1;
    private String displaydate="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar chat_toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(chat_toolbar);

        Intent intent=getIntent();
        uid=intent.getStringExtra("UID");
        ShopOwnerName= intent.getStringExtra("username");
        Log.e("getExtra",ShopOwnerName);

        Objects.requireNonNull(getSupportActionBar()).setTitle(ShopOwnerName);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //Initialize Firebase variable
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        String referenceURL = "Chat/"+user+"/"+uid;
        DatabaseReference reference1 = database.getReference(referenceURL);
        Log.e("getRef",reference1.toString());

        //Get screen pixel and convert dp into px
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        pxMargin = dm.widthPixels/6                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              ;
        pxMarginText= dm.widthPixels/50;
        pxMarginBox = dm.heightPixels/85;

        layout = findViewById(R.id.layout1);
        layout_2 = findViewById(R.id.layout2);
        sendButton = findViewById(R.id.sendButton);
        messageArea = findViewById(R.id.messageArea);
        scrollView = findViewById(R.id.scrollView);
        messageArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Handler handler = new Handler();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {Thread.sleep(200);} catch (InterruptedException ignored) {}
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.fullScroll(View.FOCUS_DOWN);
                            }
                        });
                    }
                }).start();
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String messageText = messageArea.getText().toString();
                if(!messageText.equals("")){
                    Map<String,String> stringMap = new HashMap<>();
                    stringMap.put("message", messageText);
                    stringMap.put("user", user);
                    Map<String, Object> map = new HashMap<>();
                    map.putAll(stringMap);
                    map.put("time",ServerValue.TIMESTAMP);

                    mDatabase.child("Chat").child(user).child(uid).push().setValue(map);
                    mDatabase.child("Chat").child(uid).child(user).push().setValue(map);
                    //mDatabase.child("Chat").child(user).child(user+"_"+"odmeJpkFufNDfUN7RCcFEsybrFX2").push().setValue(map);
                    //mDatabase.child("Chat").child("odmeJpkFufNDfUN7RCcFEsybrFX2").child("odmeJpkFufNDfUN7RCcFEsybrFX2"+"_"+user).push().setValue(map);
                    messageArea.setText("");
                }
            }
        });
        reference1.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("data",dataSnapshot.toString());
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                String message = Objects.requireNonNull(map).get("message").toString();
                String userName = map.get("user").toString();
                Long timestamp = (Long) map.get("time");
                if(userName.equals(user)){
                    addMessageBox(message,timestamp, 1);
                }
                else{
                    addMessageBox(message,timestamp, 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void addMessageBox(String message, Long timestamp, int type){

        //Initialize Variable
        TextView textDate = new TextView(this);
        TextView textmsg = new TextView(this);
        TextView texttime = new TextView(this);

        int minuteElapsed = Integer.parseInt(getMinutePassed(timestamp));
        if(minuteElapsed<1440){
            textDate.setText("TODAY");
        }
        else if(minuteElapsed>1440&&minuteElapsed<4320){
            textDate.setText("Yesterday");
        }else{
            textDate.setText(convertDate(timestamp));
        }

        textDate.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
        textDate.setGravity(Gravity.CENTER);
        textDate.setPadding(10,pxMarginBox,10,pxMarginBox);

        textmsg.setText(message);
        textmsg.setTextSize(18);
        textmsg.setTextColor(Color.BLACK);
        String time = convertTime(timestamp);
        texttime.setText(time);
        texttime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        texttime.setSingleLine();

        //Initialize Linear Layout
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        lp1.weight=1.0f;
        lp1.setMarginEnd(pxMarginText);
        lp3.gravity=Gravity.BOTTOM|Gravity.RIGHT;
        texttime.setLayoutParams(lp3);
        textmsg.setLayoutParams(lp1);

        if(type == 1) {
            lp2.gravity = Gravity.RIGHT;
            lp2.setMarginStart(Math.round(pxMargin));
            linearLayout.setBackgroundResource(R.drawable.text_out);
        }
        else{
            lp2.gravity = Gravity.LEFT;
            lp2.setMarginEnd(Math.round(pxMargin));
            linearLayout.setBackgroundResource(R.drawable.text_in);
        }

        if(addMargin!=type){
            addMargin=type;
            lp2.topMargin=pxMarginBox;
        }

        linearLayout.setLayoutParams(lp2);
        linearLayout.addView(textmsg);
        linearLayout.addView(texttime);

        //Add all into the final layout
        String getDate= textDate.getText().toString();
        if(!(displaydate.equals(getDate))){
            layout.addView(textDate);
            displaydate = getDate;
        }
        layout.addView(linearLayout);
        scrollView.post(new Runnable() {
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("hh:mm aa");
        return format.format(date);
    }

    private String convertDate(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("EEE,d MMM yyyy ");
        return format.format(date);
    }

    private String getMinutePassed(long time){
        long unixTime = System.currentTimeMillis();
        Log.e("time",time+"      "+unixTime);
        Long diffTime = unixTime-time;
        diffTime/=60000;
       Log.e("ms",diffTime.toString());
       return diffTime.toString();
    }

    public  boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}