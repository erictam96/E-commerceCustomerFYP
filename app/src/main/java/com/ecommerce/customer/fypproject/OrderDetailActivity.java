package com.ecommerce.customer.fypproject;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;

import com.ecommerce.customer.fypproject.adapter.OrderItem;
import com.ecommerce.customer.fypproject.adapter.OrderItemAdapter;
import com.ecommerce.customer.fypproject.adapter.UploadProcess;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class OrderDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private final String PHPURL="https://ecommercefyp.000webhostapp.com/retailer/customer_function.php";
    private List<OrderItem> orderList;
    private ProgressDialog progressDialog;
    private String orderid;
    Bundle extras;

    //Variable to store brightness value
    private int brightness;
    //Content resolver used as a handle to the system's settings
    private ContentResolver cResolver;
    //Window object, that will store a reference to the current window
    private Window window;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        recyclerView=findViewById(R.id.recycleOrderToReceive);
        Toolbar toolbar = findViewById(R.id.orderToReceiveToolbar);
        setSupportActionBar(toolbar);

        String zzz=getResources().getString(R.string.orderdetail);
        Objects.requireNonNull(getSupportActionBar()).setTitle(zzz+orderid);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(R.string.loading);
        progressDialog.setMessage(getResources().getString(R.string.prepareOrderDetail));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);





        orderList=new ArrayList<>();

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManagerOfrecyclerView = new LinearLayoutManager(OrderDetailActivity.this);

        recyclerView.setLayoutManager(layoutManagerOfrecyclerView);
        JSON_CALL();


    }


    private void JSON_CALL() {
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
                    ParseJSonResponse(response);
                }catch (Exception e){
                    Log.e("Error",e.toString());
                }
                //placeOrderDialog.dismiss();
            }

            @Override
            protected String doInBackground(Void... params) {
                UploadProcess ProcessClass = new UploadProcess();

                HashMap<String,String> HashMapParams = new HashMap<>();

                HashMapParams.put("fetchorderdetail",orderid);
                return ProcessClass.HttpRequest(PHPURL, HashMapParams);
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClass = new AsyncTaskUploadClass();
        AsyncTaskUploadClass.execute();

    }
    private void ParseJSonResponse(String array) throws JSONException {
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




}
