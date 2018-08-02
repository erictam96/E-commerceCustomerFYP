package com.ecommerce.customer.fypproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ReviewActivity extends AppCompatActivity {

    private ImageView image1,image2,image3,profilepic;
    private TextView postusername, postdesc, postdate;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference custpost = db.collection("CustPost");
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        progressDialog = ProgressDialog.show(ReviewActivity.this,"Loadin Full Post","Please Wait",false,false);

        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        profilepic = findViewById(R.id.profile_pic);
        postusername = findViewById(R.id.name);
        postdesc = findViewById(R.id.desc);
        postdate = findViewById(R.id.date);

        Intent i = this.getIntent();

        String currentpostid = Objects.requireNonNull(i.getExtras()).getString("postID");

        custpost.document(Objects.requireNonNull(currentpostid)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                progressDialog.dismiss();
                String img1 = Objects.requireNonNull(documentSnapshot.get("imageUrl1")).toString();
                //String img2 = documentSnapshot.get("imageUrl2").toString();
                if(Objects.requireNonNull(documentSnapshot.get("imageUrl2")).toString().equalsIgnoreCase("none")){
                    image2.setVisibility(View.GONE);
                }
                else{
                    Glide.with(ReviewActivity.this)
                            .load(Objects.requireNonNull(documentSnapshot.get("imageUrl2")).toString()) // image url
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.photo) // any placeholder to load at start
                                    .error(R.drawable.photo)  // any image in case of error
                                    .override(200, 200) // resizing
                                    .centerCrop())
                            .into(image2);
                }
                //String img3 = documentSnapshot.get("imageUrl3").toString();
                if(Objects.requireNonNull(documentSnapshot.get("imageUrl3")).toString().equalsIgnoreCase("none")){
                    image3.setVisibility(View.GONE);
                }
                else{
                    Glide.with(ReviewActivity.this)
                            .load(Objects.requireNonNull(documentSnapshot.get("imageUrl3")).toString()) // image url
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.photo) // any placeholder to load at start
                                    .error(R.drawable.photo)  // any image in case of error
                                    .override(200, 200) // resizing
                                    .centerCrop())
                            .into(image3);
                }

                Glide.with(ReviewActivity.this)
                        .load(R.drawable.profile_pic_icon) // image url
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.profile_pic_icon) // any placeholder to load at start
                                .error(R.drawable.photo)  // any image in case of error
                                .override(200, 200) // resizing
                                .centerCrop())
                        .into(profilepic);

                postusername.setText(Objects.requireNonNull(documentSnapshot.get("username")).toString());
                postdesc.setText(Objects.requireNonNull(documentSnapshot.get("postdescription")).toString());
                postdate.setText("Posted on: " + Objects.requireNonNull(documentSnapshot.get("postdate")).toString());

                Glide.with(ReviewActivity.this)
                        .load(img1) // image url
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.photo) // any placeholder to load at start
                                .error(R.drawable.photo)  // any image in case of error
                                .override(200, 200) // resizing
                                .centerCrop())
                        .into(image1);




            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ReviewActivity.this,e.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Toast.makeText(ReviewActivity.this,"got in dao", Toast.LENGTH_SHORT).show();
        switch (requestCode){
            case 101:
                if (resultCode == RESULT_OK && null != data) {
                    String newText = data.getStringExtra("checkout");
                    if(newText.equalsIgnoreCase("111")){
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("checkout", "111");
                        setResult(ItemDetailActivity.RESULT_OK);

                        finish();
                    }
                }
        }
    }
}
