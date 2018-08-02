package com.ecommerce.customer.fypproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.ecommerce.customer.fypproject.adapter.CartItem;
import com.ecommerce.customer.fypproject.adapter.CheckoutItemAdapter;
import com.ecommerce.customer.fypproject.adapter.OnItemClick;
import com.ecommerce.customer.fypproject.adapter.UploadProcess;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.PurchaseEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CheckoutItemActivity extends AppCompatActivity implements OnItemClick {

    private CheckBox payall;
    private String uid;
    private TextView totaltopay,subtotal,fees,emptyText;
    private ImageView emptyImg;
    private Button paynowBut;
    private ProgressDialog deleteDialog,progressDialog;
    private  List<CartItem> cartItemList;
    private CheckoutItemAdapter checkoutItemAdapter;
    private final String PHPURL="https://ecommercefyp.000webhostapp.com/retailer/customer_function.php";
    //String PHPURL="http://10.0.2.2/cashierbookPHP/Eric/customer_function.php";
    private  RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private boolean doneLoading=false;
    private final DecimalFormat df2 = new DecimalFormat("0.00");



    @Override
    public void onClick(String value) {
        //value receive
        if(value.equalsIgnoreCase("refresh")){
            deleteDialog.show();
        }else if(value.equalsIgnoreCase("uncheck")){
            payall.setChecked(false);
        }else if(value.equalsIgnoreCase("check")) {
            payall.setChecked(true);
        }else if(value.equalsIgnoreCase("done refresh")){
            JSON_HTTP_CALL();
            callToast(getResources().getString(R.string.itemRemovedFromCart));

            Log.d("delete but:","Item removed from cart");
        }else{
            totaltopay.setText("RM"+df2.format(Double.parseDouble(value)));
            double tempTotal=Double.parseDouble(value);


            double fee=tempTotal/100;
            tempTotal=tempTotal+fee;
            fees.setText("RM"+df2.format(fee));
            subtotal.setText("RM"+df2.format(tempTotal));

            if(Double.parseDouble(value)==0){
                paynowBut.setEnabled(false);
            }else{
                paynowBut.setEnabled(true);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        JSON_HTTP_CALL();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_item);

        payall=findViewById(R.id.payAllChkBox);
        totaltopay=findViewById(R.id.payTotalText);
        subtotal=findViewById(R.id.paySubTotalText);
        fees=findViewById(R.id.feesText);
        paynowBut=findViewById(R.id.payNowButton);
        emptyText=findViewById(R.id.checkoutEmptyText);
        emptyImg=findViewById(R.id.checkoutEmptyImg);

        Toolbar toolbar = findViewById(R.id.checkoutToolbar);
        recyclerView=findViewById(R.id.chkoutRecycleView);
        refreshLayout=findViewById(R.id.chkoutRefresh);
        refreshLayout.setColorSchemeResources(R.color.orange);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManagerOfrecyclerView = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManagerOfrecyclerView);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.chkoutorder);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        GetFirebaseAuth();

        deleteDialog=new ProgressDialog(this);
        deleteDialog.setCancelable(false);
        deleteDialog.setTitle(R.string.dltOrderItem);
        deleteDialog.setMessage(getResources().getString(R.string.pleaseWait));
        deleteDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setTitle(R.string.loading);
        progressDialog.setMessage(getResources().getString(R.string.prepareOrderItem));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        cartItemList=new ArrayList<>();

        //JSON_HTTP_CALL();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                JSON_HTTP_CALL();
                refreshLayout.setRefreshing(false);

            }
        });

        payall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkoutItemAdapter==null||!doneLoading){
                    JSON_HTTP_CALL();
                }else {
                    checkoutItemAdapter.chkAll(payall.isChecked());
                }
            }
        });


        paynowBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkoutItemAdapter==null||!doneLoading){
                    JSON_HTTP_CALL();
                }else {
                    boolean[] checkList=checkoutItemAdapter.getCheckedList();
                    JSONArray JSONUploadArray=new JSONArray();
                    // cartItemList;
                    for(int a=0;a<checkList.length;a++){
                        if(checkList[a]){  //if checked the item

                            Log.d("expdate", cartItemList.get(a).getExpdate());
                            Log.d("prodVariant", cartItemList.get(a).getProdVariant());
                            Log.d("prodname",cartItemList.get(a).getProdName());
                            //get quantity
                            //cartID
                            //product variant
                            JSONObject JSONPlaceOrderProdcut = new JSONObject();
                            try {
                                JSONPlaceOrderProdcut.put("expdate", cartItemList.get(a).getExpdate());
                                JSONPlaceOrderProdcut.put("prodVariant", cartItemList.get(a).getProdVariant());
                                JSONPlaceOrderProdcut.put("prodname",cartItemList.get(a).getProdName());
                                JSONUploadArray.put(JSONPlaceOrderProdcut);
                                Answers.getInstance().logPurchase(new PurchaseEvent()
                                .putItemName(cartItemList.get(a).getProdName())
                                .putItemPrice(new BigDecimal(cartItemList.get(a).getProdPrice()))
                                .putCurrency(Currency.getInstance("MYR")));
                            }catch (JSONException e){
                                Crashlytics.logException(e);
                                // handle your exception here!
                                e.printStackTrace();
                            }
                        }
                    }
                    String orderList=JSONUploadArray.toString();

                    Log.d("pay item list",orderList);
                    SharedPreferences pref = CheckoutItemActivity.this.getSharedPreferences("MyPref", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.clear();
                    editor.putString("shop_cart_list", orderList); //save the string array in share preference and will use it when user done ipay88 payment
                    editor.putString("total_amount",Double.toString(checkoutItemAdapter.calculateTotal()));
                    editor.apply();

                    Intent addressDelivery = new Intent(CheckoutItemActivity.this, AddressDeliveryActivity.class);
                    addressDelivery.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(addressDelivery);

                }


            }
        });

        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    public  boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
    private void callToast(String x){
        Toast.makeText(CheckoutItemActivity.this,x,Toast.LENGTH_LONG).show();

    }

    private void JSON_HTTP_CALL() {
        totaltopay.setText("RM0.00");
        subtotal.setText("RM0.00");
        fees.setText("RM0.00");
        payall.setChecked(false);
        progressDialog.show();
        doneLoading=false;
        paynowBut.setEnabled(false);
        cartItemList=new ArrayList<>();
        checkoutItemAdapter=null;


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
                        recyclerView.setVisibility(View.INVISIBLE);
                        emptyImg.setVisibility(View.VISIBLE);
                        emptyText.setVisibility(View.VISIBLE);
                    }else{
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyImg.setVisibility(View.INVISIBLE);
                        emptyText.setVisibility(View.INVISIBLE);
                    }
                    ParseJSonResponse(response);
                } catch (JSONException e) {
                    Crashlytics.logException(e);
                    // handle your exception here!
                    e.printStackTrace();
                }
                Log.d("ResponseCheckout", response);
            }

            @Override
            protected String doInBackground(Void... params) {
                UploadProcess ProcessClass = new UploadProcess();

                HashMap<String,String> HashMapParams = new HashMap<>();

                HashMapParams.put("chkoutCustID",uid);
                return ProcessClass.HttpRequest(PHPURL, HashMapParams);
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClass = new AsyncTaskUploadClass();
        AsyncTaskUploadClass.execute();

    }
    //get response string and set into recyclerView
    private void ParseJSonResponse(String array) throws JSONException {
        cartItemList=new ArrayList<>();
        checkoutItemAdapter=null;
        JSONArray jarr = new JSONArray(array);//lv 1 array

        for(int a=0;a<jarr.length();a++){
            CartItem x=new CartItem();
            JSONObject json;
            json=jarr.getJSONObject(a);

            x.setProdVariant(json.getString("prodVariant"));
            x.setProdprofURL(json.getString("prodImgURL"));
            x.setProdName(json.getString("prodName"));
            x.setProdPrice(json.getString("prodPrice"));
            x.setCartQty(json.getString("cartQty"));

            x.setRetailerProfPicURL(json.getString("retailerProfPicURL"));
            x.setRetailerShopName(json.getString("retailerShopName"));
            x.setItemStatus(json.getString("pendingStatus"));
            x.setRealPhoto1(json.getString("photo1"));
            x.setRealPhoto2(json.getString("photo2"));
            x.setPhotodate(json.getString("photodate"));
            x.setExpdate(json.getString("expdate"));
            x.setDiscountedPrice(json.getString("discountPrice"));
            x.setDiscount(json.getString("discount"));
            x.setProdcode(json.getString("prodcode"));

            cartItemList.add(x);
        }

        checkoutItemAdapter= new CheckoutItemAdapter(cartItemList, this,this);
        RecyclerView.Adapter recyclerViewadapter = checkoutItemAdapter;
        recyclerView.setAdapter(recyclerViewadapter);

//        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
//            @Override
//            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
//                return false;
//            }
//
//            @Override
//            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
//
//            }
//
//            @Override
//            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//
//            }
//
//        });

        progressDialog.dismiss();
        doneLoading=true;
        if(deleteDialog.isShowing()){

            totaltopay.setText("RM"+df2.format(checkoutItemAdapter.calculateTotal()));
            double tempTotal=checkoutItemAdapter.calculateTotal();


            double fee=tempTotal/100;
            tempTotal=tempTotal+fee;
            fees.setText("RM"+df2.format(fee));
            subtotal.setText("RM"+df2.format(tempTotal));
            payall.setChecked(false);
            checkoutItemAdapter.notifyItemRangeChanged(0,cartItemList.size());
            deleteDialog.dismiss();
        }
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
