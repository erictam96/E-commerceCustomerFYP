package com.ecommerce.customer.fypproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ecommerce.customer.fypproject.adapter.Refund;
import com.ecommerce.customer.fypproject.adapter.RefundAdapter;
import com.ecommerce.customer.fypproject.adapter.UploadProcess;
import com.crashlytics.android.Crashlytics;
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

public class RefundActivity extends AppCompatActivity {

    private  RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private RefundAdapter refundAdapter;
    private String uid;
    private final String PHPURL="https://ecommercefyp.000webhostapp.com/retailer/customer_function.php";
    //String PHPURL="http://10.0.2.2/cashierbookPHP/Eric/customer_function.php";
    private List<Refund> refundList;
    private ImageView emptyImg;
    private TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund);

        Toolbar toolbar = findViewById(R.id.refundToolbar);
        recyclerView=findViewById(R.id.refundRecycleView);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.refundorder);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        GetFirebaseAuth();
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManagerOfrecyclerView = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManagerOfrecyclerView);

        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setTitle(R.string.loading);
        progressDialog.setMessage(getResources().getString(R.string.prepareRefundList));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        emptyImg=findViewById(R.id.refundEmptyImg);
        emptyText=findViewById(R.id.refundEmptyText);

        JSON_HTTP_CALL();
    }

    public  boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }


    private void JSON_HTTP_CALL() {

        progressDialog.show();
        refundList=new ArrayList<>();
        refundAdapter=null;

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
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

                HashMapParams.put("fetchRefund", uid);
                return ProcessClass.HttpRequest(PHPURL, HashMapParams);
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClass = new AsyncTaskUploadClass();
        AsyncTaskUploadClass.execute();

    }

    //get response string and set into recyclerView
    private void ParseJSonResponse(String array) throws JSONException {

        JSONArray jarr = new JSONArray(array);//lv 1 array

        int totalQty=0;
        for(int a=0;a<jarr.length();a++){
            Refund x=new Refund();
            JSONObject json;
            json=jarr.getJSONObject(a);

            x.setImgURL(json.getString("imgurl"));
            x.setRefundItemName(json.getString("prodname"));
            x.setRefundPrice(json.getString("prodprice"));
            x.setShortSellDate(json.getString("shortselldate"));

            refundList.add(x);
        }



        refundAdapter= new RefundAdapter(refundList, this);
        RecyclerView.Adapter recyclerViewadapter = refundAdapter;
        recyclerView.setAdapter(recyclerViewadapter);
        progressDialog.dismiss();


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

}
