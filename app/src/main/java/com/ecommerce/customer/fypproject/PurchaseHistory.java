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
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ecommerce.customer.fypproject.adapter.OrderItem;
import com.ecommerce.customer.fypproject.adapter.OrderItemAdapter;
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

public class PurchaseHistory extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<OrderItem> orderList;
    private String uid;
    private ProgressDialog progressDialog;
    private final String PHPURL="https://ecommercefyp.000webhostapp.com/retailer/customer_function.php";
    private TextView emptyText;
    private ImageView emptyImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_history);

        Toolbar toolbar = findViewById(R.id.purchaseHistoryToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.purchasedHistory);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        GetFirebaseAuth();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setTitle(R.string.loading);
        progressDialog.setMessage(getResources().getString(R.string.preparePurchasedHistory));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        emptyImg=findViewById(R.id.purchaseHistoryEmptyImg);
        emptyText=findViewById(R.id.purchaseHistoryEmptyText);

        recyclerView=findViewById(R.id.purchasedHistoryRecycle);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManagerOfrecyclerView = new LinearLayoutManager(PurchaseHistory.this);

        recyclerView.setLayoutManager(layoutManagerOfrecyclerView);

        JSON_CALL_LIST();
    }

    private void JSON_CALL_LIST() {
        orderList=new ArrayList<>();
        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                Log.e("Response order detail",response);
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
                    ParseJSonResponseList(response);
                }catch (Exception e){
                    Log.e("Error",e.toString());
                }
                //placeOrderDialog.dismiss();
            }

            @Override
            protected String doInBackground(Void... params) {
                UploadProcess ProcessClass = new UploadProcess();

                HashMap<String,String> HashMapParams = new HashMap<>();

                HashMapParams.put("getPurchaseHistory",uid);
                return ProcessClass.HttpRequest(PHPURL, HashMapParams);
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClass = new AsyncTaskUploadClass();
        AsyncTaskUploadClass.execute();

    }
    private void ParseJSonResponseList(String array) throws JSONException {
        // product = new Product();
        JSONArray jarr = new JSONArray(array);//lv 1 array


        // get the prodcode


        //JSONArray innerJarr=new JSONArray(json);

        //int totalQty=0;
        // orderList.clear();

        for(int a=0;a<jarr.length();a++){
            OrderItem x=new OrderItem();
            JSONObject json;
            json=jarr.getJSONObject(a);

            x.setItemname(json.getString("itemName"));
            x.setQty(json.getString("itemQty"));
            x.setVariant(json.getString("itemVar"));
            x.setStatus(json.getString("itemStatus"));
            x.setDate(json.getString("orderdate"));
            x.setUrl(json.getString("url"));
            x.setSeller(json.getString("shopname"));

            orderList.add(x);
        }

        recyclerView.setAdapter(new OrderItemAdapter(orderList, this));

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }

        });



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
