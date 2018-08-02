package com.ecommerce.customer.fypproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ViewUserProfile extends AppCompatActivity {

    private ImageView profilepic;
    private TextView profilename;
    private Button btnaddfriend;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference socialuser = db.collection("SocialUser");
    private String uid,udisplayname;
    private FirebaseAuth firebaseAuth;
    private String currentviewuid;
    private String friendstatus="";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_profile);

        progressDialog = ProgressDialog.show(ViewUserProfile.this,"Loading User Profile","Please Wait",false,false);
        GetFirebaseAuth();

        Toolbar myposttoolbar = findViewById(R.id.viewprofiletoolbar);
        setSupportActionBar(myposttoolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.viewprofile);

        profilepic = findViewById(R.id.view_profilepic);
        profilename = findViewById(R.id.view_username);
        btnaddfriend = findViewById(R.id.btn_addfriend);

        Intent i = this.getIntent();
        currentviewuid = Objects.requireNonNull(i.getExtras()).getString("userID");

        if(Objects.requireNonNull(currentviewuid).equals(uid))
            btnaddfriend.setVisibility(View.GONE);

        socialuser.document(currentviewuid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                profilename.setText(Objects.requireNonNull(documentSnapshot.get("username")).toString());
                documentSnapshot.getReference().collection("Friends").document(uid).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists()) {
                                    friendstatus = Objects.requireNonNull(documentSnapshot.get("status")).toString();
                                    if(friendstatus.equalsIgnoreCase("pending")){
                                        btnaddfriend.setText(R.string.pendingreq);
                                        btnaddfriend.setBackgroundColor(getResources().getColor(R.color.darkerGrey));
                                    }
                                    else if(friendstatus.equalsIgnoreCase("friend")){
                                        btnaddfriend.setText(R.string.friended);
                                        btnaddfriend.setBackgroundColor(getResources().getColor(R.color.colorFriends));
                                        btnaddfriend.setClickable(false);
                                    }
                                }
                                progressDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ViewUserProfile.this,e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ViewUserProfile.this,e.toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void GetFirebaseAuth(){
        firebaseAuth= FirebaseAuth.getInstance();//get firebase object
        if(firebaseAuth.getCurrentUser()==null){
            Intent intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
            try {
                FirebaseInstanceId.getInstance().deleteInstanceId();
            } catch (IOException e) {
                e.printStackTrace();
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(this,R.string.sessionexp,Toast.LENGTH_LONG).show();
        }
        else{
            uid = firebaseAuth.getCurrentUser().getUid();
            udisplayname = firebaseAuth.getCurrentUser().getDisplayName();
        }
    }

    public void AddFriend(View view){
        if(btnaddfriend.getText().toString().equalsIgnoreCase("add friend")){
            Map<String,Object> friendreq = new HashMap<>();
            friendreq.put("friendname", udisplayname);
            friendreq.put("status", "pending");

            socialuser.document(currentviewuid).collection("Friends").document(uid).set(friendreq)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            btnaddfriend.setText(R.string.pendingreq);
                            btnaddfriend.setBackgroundColor(getResources().getColor(R.color.darkerGrey));
                        }
                    });
        }
        else if(btnaddfriend.getText().toString().equalsIgnoreCase("pending request")){
            socialuser.document(currentviewuid).collection("Friends").document(uid).delete();
            btnaddfriend.setText(R.string.addfriend);
            btnaddfriend.setBackgroundColor(getResources().getColor(R.color.cashierbookBlue));
        }
    }
}
