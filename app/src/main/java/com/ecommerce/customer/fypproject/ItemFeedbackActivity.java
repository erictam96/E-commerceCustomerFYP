package com.ecommerce.customer.fypproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ecommerce.customer.fypproject.adapter.ItemFeedback;
import com.ecommerce.customer.fypproject.adapter.ItemFeedbackAdapter;
import com.ecommerce.customer.fypproject.adapter.OnItemClick;
import com.ecommerce.customer.fypproject.adapter.UploadProcess;
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


public class ItemFeedbackActivity extends AppCompatActivity implements OnItemClick{

    private RecyclerView recyclerView;
    private List<ItemFeedback> feedbackList;
    private View view;
    int RecyclerViewItemPosition;
    private final String PHPURL="https://ecommercefyp.000webhostapp.com/retailer/customer_function.php";
    private ProgressDialog progressDialog;
    private String uid;
    private ImageView emptyImg;
    private TextView emptyText;


    @Override
    public void onClick(String value) {
        if(value.equalsIgnoreCase("refresh")){
            JSON_CALL();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_feedback);

        Toolbar myposttoolbar = findViewById(R.id.itemFeedbackToolbar);
        emptyImg=findViewById(R.id.feedbackEmptyImg);
        emptyText=findViewById(R.id.feedbackEmptyText);

        setSupportActionBar(myposttoolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.itemFeedback);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        GetFirebaseAuth();
        recyclerView=findViewById(R.id.feedbackItemRecycle);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManagerOfrecyclerView = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManagerOfrecyclerView);

        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setTitle(R.string.loading);
        progressDialog.setMessage(getResources().getString(R.string.prepareFeedbackList));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        JSON_CALL();
    }


    private void JSON_CALL() {
        GetFirebaseAuth();
        feedbackList=new ArrayList<>();
        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.show();


            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                Log.e("Response order",response);
                try {
                    if(response.equalsIgnoreCase("[]")){

                        emptyImg.setVisibility(View.VISIBLE);
                        emptyText.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                    }else{

                        emptyImg.setVisibility(View.INVISIBLE);
                        emptyText.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    ParseJSonResponse(response);
                }catch (Exception e){
                    Log.e("Error",e.toString());
                }
                //placeOrderDialog.dismiss();
            }

            @Override
            protected String doInBackground(Void... params) {
                UploadProcess imageProcessClass = new UploadProcess();

                HashMap<String,String> HashMapParams = new HashMap<>();

                HashMapParams.put("FetchFeedback",uid);
                return imageProcessClass.HttpRequest(PHPURL, HashMapParams);
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClass = new AsyncTaskUploadClass();
        AsyncTaskUploadClass.execute();

//

    }
    private void ParseJSonResponse(String array) throws JSONException {

        JSONArray jarr = new JSONArray(array);//lv 1 array

        for(int a=0;a<jarr.length();a++){
            ItemFeedback x=new ItemFeedback();
            JSONObject json;
            json=jarr.getJSONObject(a);

            //  x.setNumberPackingItem(json.getString("packingItem"));
           x.setRetailerProfilePicURL(json.getString("retailerURL"));
           x.setShopname(json.getString("shopName"));
           x.setItemURL(json.getString("itemURL"));
           x.setItemName(json.getString("itemName"));
           x.setItemVariant(json.getString("itemVariant"));
           x.setQuantity(json.getString("itemQty"));
           x.setDeliveredDate(json.getString("deliveredDate"));

            feedbackList.add(x);
        }

        recyclerView.setAdapter(new ItemFeedbackAdapter(feedbackList, this, this));
        progressDialog.dismiss();


    }
    public  boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private void GetFirebaseAuth(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
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
        }else uid = firebaseAuth.getCurrentUser().getUid();
    }




}
