package com.ecommerce.customer.fypproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ecommerce.customer.fypproject.adapter.Friend;
import com.ecommerce.customer.fypproject.adapter.FriendAvailableAdapter;
import com.ecommerce.customer.fypproject.adapter.FriendRequestAdapter;
import com.ecommerce.customer.fypproject.adapter.OnItemClick_2;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ViewFriends extends AppCompatActivity implements OnItemClick_2{

    private RecyclerView rcvFriendReq, rcvFriendAvai;
    private TextView txtNoReq, txtNoAvai;

    private List<Friend> ListOfFriendReq;
    private List<Friend> ListOfFriendAvai;
    private RecyclerView.LayoutManager layoutManagerofFriendView;
    private RecyclerView.Adapter friendReqAdapter;
    private RecyclerView.Adapter friendAvaiAdapter;

    private FirebaseAuth firebaseAuth;
    private String uid,udisplayname,uemail;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference socialuser = db.collection("SocialUser");

    private String friendid;

    private Date c = Calendar.getInstance().getTime();
    private SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    private String currentTime = df.format(c);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friends);

        GetFirebaseAuth();
        Toolbar myposttoolbar = findViewById(R.id.viewfriendstoolbar);
        setSupportActionBar(myposttoolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.viewfriend);

        txtNoReq = findViewById(R.id.txtNoReq);
        txtNoAvai = findViewById(R.id.txtNoAvai);

        ListOfFriendReq = new ArrayList<>();
        ListOfFriendAvai = new ArrayList<>();

        rcvFriendReq = findViewById(R.id.rcvFriendRequest);
        rcvFriendReq.setHasFixedSize(true);
        layoutManagerofFriendView = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcvFriendReq.setLayoutManager(layoutManagerofFriendView);

        rcvFriendAvai = findViewById(R.id.rcvAvailableFriends);
        rcvFriendAvai.setHasFixedSize(true);
        layoutManagerofFriendView = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcvFriendAvai.setLayoutManager(layoutManagerofFriendView);

        loadFriend();
    }

    private void loadFriend() {
        ListOfFriendReq = new ArrayList<>();
        ListOfFriendAvai = new ArrayList<>();

        socialuser.document(uid).collection("Friends").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            if(documentSnapshot.exists()){
                                String tempstatus = Objects.requireNonNull(documentSnapshot.get("status")).toString();
                                if(tempstatus.equalsIgnoreCase("pending")){
                                    Friend friend = new Friend();
                                    friend.setFriendid(documentSnapshot.getId());
                                    friend.setFriendname(Objects.requireNonNull(documentSnapshot.get("friendname")).toString());

                                    ListOfFriendReq.add(friend);
                                }
                                else if(tempstatus.equalsIgnoreCase("friend")){
                                    Friend friend = new Friend();
                                    friend.setFriendid(documentSnapshot.getId());
                                    friend.setFriendname(Objects.requireNonNull(documentSnapshot.get("friendname")).toString());
                                    friend.setAddfrienddate(Objects.requireNonNull(documentSnapshot.get("addfrienddate")).toString());

                                    ListOfFriendAvai.add(friend);
                                }
                            }
                        }

                        if(ListOfFriendReq.isEmpty()){
                            txtNoReq.setVisibility(View.VISIBLE);
                            rcvFriendReq.setVisibility(View.GONE);
                        }
                        else {
                            txtNoReq.setVisibility(View.INVISIBLE);
                            rcvFriendReq.setVisibility(View.VISIBLE);
                            friendReqAdapter = new FriendRequestAdapter(ListOfFriendReq,getApplicationContext(),ViewFriends.this);
                            rcvFriendReq.setAdapter(friendReqAdapter);
                        }

                        if(ListOfFriendAvai.isEmpty()){
                            txtNoAvai.setVisibility(View.VISIBLE);
                            rcvFriendAvai.setVisibility(View.GONE);
                        }
                        else {
                            txtNoAvai.setVisibility(View.INVISIBLE);
                            rcvFriendAvai.setVisibility(View.VISIBLE);
                            friendAvaiAdapter = new FriendAvailableAdapter(ListOfFriendAvai,getApplicationContext());
                            rcvFriendAvai.setAdapter(friendAvaiAdapter);
                        }
                    }
                });

    }

    @Override
    public void onClick(String value, String value2) {
        if(value.equalsIgnoreCase("accept")){
            friendid = value2;
            socialuser.document(uid).collection("Friends").document(friendid)
                    .update("status","friend","addfrienddate", currentTime);
            Map<Object,String> friends = new HashMap<>();
            friends.put("addfrienddate", currentTime);
            friends.put("friendname", udisplayname);
            friends.put("status","friend");
            socialuser.document(friendid).collection("Friends").document(uid)
                    .set(friends);

            loadFriend();
        }
    }

    private void GetFirebaseAuth(){
        firebaseAuth= FirebaseAuth.getInstance();//get firebase object
        if(firebaseAuth.getCurrentUser()==null){
            Intent intent = new Intent(this, SplashScreenActivity.class);
            try {
                FirebaseInstanceId.getInstance().deleteInstanceId();
            } catch (IOException e) {
                e.printStackTrace();
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(this,R.string.sessionexp,Toast.LENGTH_LONG).show();
        }else{
            uid = firebaseAuth.getCurrentUser().getUid();
            udisplayname = firebaseAuth.getCurrentUser().getDisplayName();
            uemail = firebaseAuth.getCurrentUser().getEmail();
        }
    }
}
