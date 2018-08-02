package com.ecommerce.customer.fypproject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;


import com.ecommerce.customer.fypproject.adapter.Address;
import com.ecommerce.customer.fypproject.adapter.AddressAdapter;
import com.ecommerce.customer.fypproject.adapter.OnItemClick;
import com.ecommerce.customer.fypproject.adapter.UploadProcess;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.PurchaseEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ipay.IpayResultDelegate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AddressDeliveryActivity extends AppCompatActivity implements OnItemClick{
    private String uid;
    private  CheckBox dedicatedAddChkBox,customAddChkBox;
    private RelativeLayout addAddressRelative,spinnerRelative;
    private RecyclerView addressRecycleView;
    private Spinner dropOffSpinner;
    private Button submitAddressBut;
    private  ProgressDialog placeOrderDialog;
    private final String PHPURL="https://ecommercefyp.000webhostapp.com/retailer/customer_function.php";
    private final String ipayURL="https://ecommercefyp.000webhostapp.com/retailer/ipay88response.php";
    private FirebaseAuth firebaseAuth;
    private final String merchantkey="JTKBto70Xe",merchantcode="M03216";
    private ResultDelegate resultDeligate;
    private final int OUT_SUCCESS_IPAY=1,OUT_FAIL_IPAY=2,OUT_CANCEL_IPAY=3;
    private static int ipay_response=0;
    private static String ipay_error_msg="";
    private List<Address> addressList;
    private ProgressDialog progressDialog;
    private AddressAdapter addressAdapter;
    private ImageView downimageView;
    private String orderid;
    private SharedPreferences pref;
    private AlertDialog alertDialog;
    private Dialog cancelDialog;
    private static String ipay_transid="";
    //int previousChkBoxID=0;
    //RecyclerView recyclerView;

    @Override
    public void onClick(String value) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_delivery);
        addressList=new ArrayList<>();
        progressDialog=new ProgressDialog(AddressDeliveryActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(R.string.loading);
        progressDialog.setMessage(getResources().getString(R.string.prepareAddressList));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        GetFirebaseAuth();
        Toolbar addresstoolbar = findViewById(R.id.addressDeliverytoolbar);
        setSupportActionBar(addresstoolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.deliverydetail);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        placeOrderDialog=new ProgressDialog(AddressDeliveryActivity.this);
        placeOrderDialog.setCancelable(false);
        placeOrderDialog.setTitle(R.string.processingUrOrder);
        placeOrderDialog.setMessage(getResources().getString(R.string.pleaseWait));
        placeOrderDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        dedicatedAddChkBox=findViewById(R.id.dropOffChkBox);
        customAddChkBox=findViewById(R.id.customeAddressChkBox);
        addAddressRelative=findViewById(R.id.addAddressRelative);
        addressRecycleView=findViewById(R.id.addressRecycleView);
        dropOffSpinner=findViewById(R.id.dropOffSpinner);
        submitAddressBut=findViewById(R.id.submitAddressBut);
        spinnerRelative=findViewById(R.id.spinnerRelative);
        firebaseAuth= FirebaseAuth.getInstance();
        downimageView=findViewById(R.id.addressRecycleImg);
        viewPhotoDialog();
        pref = AddressDeliveryActivity.this.getSharedPreferences("MyPref", 0);

        downimageView.setVisibility(View.INVISIBLE);

        addressRecycleView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManagerOfrecyclerView = new LinearLayoutManager(this);

        addressRecycleView.setLayoutManager(layoutManagerOfrecyclerView);
        //default setting
        submitAddressBut.setEnabled(false);
        dropOffSpinner.setEnabled(true);
        dedicatedAddChkBox.setChecked(true);
        customAddChkBox.setChecked(false);
        addAddressRelative.setEnabled(false);
        addAddressRelative.setBackground(ContextCompat.getDrawable(AddressDeliveryActivity.this, R.drawable.dark_frame));
        addressRecycleView.setEnabled(false);
        addressRecycleView.setVisibility(View.INVISIBLE);
        addressRecycleView.setBackground(ContextCompat.getDrawable(AddressDeliveryActivity.this, R.drawable.dark_frame));

        dedicatedAddChkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                customAddChkBox.setChecked(false);
                addAddressRelative.setEnabled(false);
                addAddressRelative.setBackground(ContextCompat.getDrawable(AddressDeliveryActivity.this, R.drawable.dark_frame));
                addressRecycleView.setEnabled(false);
                addressRecycleView.setVisibility(View.INVISIBLE);
                addressRecycleView.setBackground(ContextCompat.getDrawable(AddressDeliveryActivity.this, R.drawable.dark_frame));

                dropOffSpinner.setEnabled(true);
                spinnerRelative.setBackground(ContextCompat.getDrawable(AddressDeliveryActivity.this, R.drawable.ripple_effect));
               // dropOffSpinner.
                submitAddressBut.setEnabled(false);
                if(!dedicatedAddChkBox.isChecked()){
                    submitAddressBut.setEnabled(false);
                    dropOffSpinner.setEnabled(false);
                    spinnerRelative.setBackground(ContextCompat.getDrawable(AddressDeliveryActivity.this, R.drawable.dark_frame));
                }
                if(dropOffSpinner.getSelectedItemPosition()>0&&dedicatedAddChkBox.isChecked()){
                    submitAddressBut.setEnabled(true);
                }
            }
        });

        customAddChkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dedicatedAddChkBox.setChecked(false);
                dropOffSpinner.setEnabled(false);
                spinnerRelative.setBackground(ContextCompat.getDrawable(AddressDeliveryActivity.this, R.drawable.dark_frame));
               // dropOffSpinner

                addAddressRelative.setEnabled(true);
                addAddressRelative.setBackground(ContextCompat.getDrawable(AddressDeliveryActivity.this, R.drawable.ripple_effect));
                addressRecycleView.setVisibility(View.VISIBLE);
                addressRecycleView.setEnabled(true);
                addressRecycleView.setBackground(ContextCompat.getDrawable(AddressDeliveryActivity.this, R.drawable.ripple_effect));
                if(addressList.isEmpty()){
                    submitAddressBut.setEnabled(false);
                }else{
                    submitAddressBut.setEnabled(true);
                }

                if(!customAddChkBox.isChecked()){
                    submitAddressBut.setEnabled(false);
                    addAddressRelative.setEnabled(false);
                    addAddressRelative.setBackground(ContextCompat.getDrawable(AddressDeliveryActivity.this, R.drawable.dark_frame));
                    addressRecycleView.setEnabled(false);
                    addressRecycleView.setVisibility(View.INVISIBLE);
                    addressRecycleView.setBackground(ContextCompat.getDrawable(AddressDeliveryActivity.this, R.drawable.dark_frame));
                }
            }
        });


        // Creating adapter for spinner

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.dedicatedLocation, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        dropOffSpinner.setAdapter(adapter);

        dropOffSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if(position>0){
                    submitAddressBut.setEnabled(true);
                }else{
                    submitAddressBut.setEnabled(false);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                submitAddressBut.setEnabled(false);
            }
        });

//add address button listener, unlock and check this function when cashierbook support custom delivery address
        addAddressRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addAddress = new Intent(AddressDeliveryActivity.this, AddAddressActivity.class);
                addAddress.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(addAddress);
            }
        });

        //submit confirmed address and perform ipay transaction
        submitAddressBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOnline()){
                    placeOrderDialog.show();

                    JSONObject objectDetail=new JSONObject();
                    JSONArray array=new JSONArray();

                    try{
                        String addressID;
                        if(dedicatedAddChkBox.isChecked()){
                            addressID="1";
                        }else{
                            addressID=addressAdapter.getAddressID();
                        }
                        objectDetail.put("addressid",addressID);
                        objectDetail.put("delivery","0");
                        objectDetail.put("paymentmethod","ipay88");
                        objectDetail.put("custid",uid);

                        array.put(objectDetail);
                    }catch (Exception e){
                        Crashlytics.logException(e);
                        // handle your exception here!
                        e.printStackTrace();
                    }
                    CREATE_ORDER_JSON_HTTP_CALL(array.toString());
                }else{
                    Toast.makeText(AddressDeliveryActivity.this,R.string.pleaseEnableInternet,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        addressList=new ArrayList<>();

        if(isOnline()){
            JSON_HTTP_CALL();
        }else{
            callToast(getResources().getString(R.string.pleaseEnableInternet));
            finish();
        }

    }

    private void JSON_HTTP_CALL() {
        progressDialog.show();
        addressList=new ArrayList<>();

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
                        submitAddressBut.setEnabled(false);
                        downimageView.setVisibility(View.VISIBLE);
                    }else{
                        downimageView.setVisibility(View.INVISIBLE);
                    }
                    ParseJSonResponse(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("Response", response);
            }

            @Override
            protected String doInBackground(Void... params) {
                UploadProcess ProcessClass = new UploadProcess();

                HashMap<String,String> HashMapParams = new HashMap<>();

                HashMapParams.put("custaddress",uid);
                return ProcessClass.HttpRequest(PHPURL, HashMapParams);
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClass = new AsyncTaskUploadClass();
        AsyncTaskUploadClass.execute();

    }

    //get response string and set into recyclerView
    private void ParseJSonResponse(String array) throws JSONException {

        JSONArray jarr = new JSONArray(array);//lv 1 array

        for(int a=0;a<jarr.length();a++){
            Address x=new Address();
            JSONObject json;
            json=jarr.getJSONObject(a);

            x.setAddress(json.getString("address"));
            x.setState(json.getString("state"));
            x.setCity(json.getString("city"));
            x.setPostcode(json.getString("postcode"));
            x.setName(json.getString("name"));
            x.setContact(json.getString("contact"));
            x.setEmail(json.getString("email"));
            x.setAddressID(json.getString("addId"));

            addressList.add(x);
        }

        addressAdapter= new AddressAdapter(addressList, this,this);
        RecyclerView.Adapter recyclerViewadapter = addressAdapter;
        addressRecycleView.setAdapter(recyclerViewadapter);

        progressDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            if(requestCode==223){

                switch (ipay_response){
                    case OUT_SUCCESS_IPAY:
                        pref = AddressDeliveryActivity.this.getSharedPreferences("MyPref", 0); // 0 - for private mode
                        orderid=pref.getString("currentOrderid",null);

                        String orderList=pref.getString("shop_cart_list", null);
                        Log.e("success ipay",ipay_transid);
                        JSON_HTTP_SEND("cartList",orderList,orderid,ipay_transid);
                        break;
                    default:
                        CANCEL_ORDER_JSON_HTTP_CALL(orderid);
                        placeOrderDialog.dismiss();
                        Answers.getInstance().logPurchase(new PurchaseEvent()
                                .putSuccess(false)
                                .putCustomAttribute("Failed Reason",ipay_error_msg));
                        Toast.makeText(AddressDeliveryActivity.this,getResources().getString(R.string.transFail)+ipay_error_msg,Toast.LENGTH_SHORT).show();
                        finish();
                }
//                if(ipay_response==OUT_SUCCESS_IPAY){ //perform server update only when success payment is made
//
//
//
//                }else{
//                    placeOrderDialog.dismiss();
//                    Toast.makeText(AddressDeliveryActivity.this,"TransactionFailed",Toast.LENGTH_SHORT).show();
//
//
//                }

            }else{
                CANCEL_ORDER_JSON_HTTP_CALL(orderid);
                placeOrderDialog.dismiss();
                Toast.makeText(AddressDeliveryActivity.this,getResources().getString(R.string.unexpectError)+ipay_error_msg,Toast.LENGTH_SHORT).show();
                Answers.getInstance().logPurchase(new PurchaseEvent()
                        .putSuccess(false)
                        .putCustomAttribute("Failed Reason",ipay_error_msg));
                finish();
            }
        }else if(requestCode==124){
            //Toast.makeText(this,"recreate activity",Toast.LENGTH_SHORT).show();

            Intent intent = getIntent();
            finish();
            startActivity(intent);

        }else{
            CANCEL_ORDER_JSON_HTTP_CALL(orderid);
            placeOrderDialog.dismiss();
            Toast.makeText(AddressDeliveryActivity.this,getResources().getString(R.string.transFail),Toast.LENGTH_SHORT).show();
            Answers.getInstance().logPurchase(new PurchaseEvent()
                    .putSuccess(false)
                    .putCustomAttribute("Failed Reason","Cancel by user"));
            cancelDialog.show();
        }
    }

//server update after successful ipay88 payment
private void JSON_HTTP_SEND(final String name, final String orderList, final String ORDERID, final String ipayid) {

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                Log.e("Response.....",response);
                Answers.getInstance().logPurchase(new PurchaseEvent()
                        .putSuccess(true));
                placeOrderDialog.dismiss();
                finish();
            }

            @Override
            protected String doInBackground(Void... params) {
                UploadProcess ProcessClass = new UploadProcess();

                HashMap<String,String> HashMapParams = new HashMap<>();
                HashMapParams.put(name, orderList);// name=cartList
               // HashMapParams.put("orderobj",orderobj);


                HashMapParams.put("custid",uid);
                HashMapParams.put("orderid",ORDERID);
                HashMapParams.put("ipay_transid",ipayid);
                return ProcessClass.HttpRequest(PHPURL, HashMapParams);
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClass = new AsyncTaskUploadClass();
        AsyncTaskUploadClass.execute();

    }

    public  boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private void callToast(String x){
        Toast.makeText(AddressDeliveryActivity.this,x,Toast.LENGTH_LONG).show();

    }

//ipay88 response handling here
    static class ResultDelegate implements IpayResultDelegate,Serializable{

        private int STATUS=0;
        private final int SUCCESS_IPAY=1;
    private final int FAIL_IPAY=2;
    private final int CANCEL_IPAY=3;




        public void onPaymentSucceeded (String transId, String refNo, String amount, String remarks, String auth)
        {
            STATUS=SUCCESS_IPAY;

            ipay_response=STATUS;
            ipay_transid=transId;

            Log.e("transaction ID",transId);
            Log.e("transaction ID",transId);
            Log.e("transaction ID",transId);
            Log.e("transaction ID",transId);
            Log.e("transaction ID",transId);
            Log.e("transaction ID",transId);
            Log.e("transaction ID",transId);
            Log.e("transaction ID",transId);
        }

        public void onPaymentFailed (String transId, String refNo, String amount, String remarks, String err)
        {

            STATUS=FAIL_IPAY;
           // STATUS=FAIL_IPAY;
            ipay_response=STATUS;
          //  mCallback.onClick("fail ipay");
            Log.d("ipay fail response:",err);
            Log.d("ipay fail response:",err);
            Log.d("ipay fail response:",err);
            Log.d("ipay fail response:",err);
            Log.d("ipay fail response:",err);
            Log.d("ipay fail response:",err);
            Log.d("ipay fail response:",err);
            Log.d("ipay fail response:",err);
            Log.d("ipay fail response:",err);
            Log.d("ipay fail response:",err);
            Log.d("ipay fail response:",err);
            Log.d("ipay fail response:",err);
            Log.d("ipay fail response:",err);
            ipay_error_msg=err;
        }

        public void onPaymentCanceled (String transId, String refNo, String amount, String remarks, String errDesc)
        {
//

            STATUS=CANCEL_IPAY;
           // STATUS=CANCEL_IPAY;
           // mCallback.onClick("success ipay");
            //  mCallback.onClick("fail ipay");
            ipay_response=STATUS;
            Log.d("ipay cancel response:",errDesc);
            Log.d("ipay cancel response:",errDesc);
            Log.d("ipay cancel response:",errDesc);
            Log.d("ipay cancel response:",errDesc);
            Log.d("ipay cancel response:",errDesc);
            Log.d("ipay cancel response:",errDesc);
            Log.d("ipay cancel response:",errDesc);
            Log.d("ipay cancel response:",errDesc);
            Log.d("ipay cancel response:",errDesc);
            Log.d("ipay cancel response:",errDesc);
            Log.d("ipay cancel response:",errDesc);
            Log.d("ipay cancel response:",errDesc);
            Log.d("ipay cancel response:",errDesc);
            ipay_error_msg=errDesc;
        }

        public void onRequeryResult (String merchantCode, String refNo, String amount, String result)
        {
            Log.e("ipay requery:",result);
            Log.e("ipay requery:",result);
            Log.e("ipay requery:",result);
            Log.e("ipay requery:",result);
            Log.e("ipay requery:",result);
            Log.e("ipay requery:",result);
            Log.e("ipay requery:",result);

        }
    }

    private void CREATE_ORDER_JSON_HTTP_CALL(final String orderDetail) {



        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                try {
                    CREATE_ORDER_ParseJSonResponse(response);
                   // orderid=response;
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    // handle your exception here!
                    e.printStackTrace();
                }
                Log.e("ResponseCreateOrder", response);
                Log.e("ResponseCreateOrder", response);
                Log.e("ResponseCreateOrder", response);
            }

            @Override
            protected String doInBackground(Void... params) {
                UploadProcess ProcessClass = new UploadProcess();

                HashMap<String,String> HashMapParams = new HashMap<>();


                HashMapParams.put("createOrder",orderDetail);
                return ProcessClass.HttpRequest(PHPURL, HashMapParams);
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClass = new AsyncTaskUploadClass();
        AsyncTaskUploadClass.execute();

    }

    private void CREATE_ORDER_ParseJSonResponse(String array) throws JSONException {

        JSONArray jarr = new JSONArray(array);//lv 1 array
        JSONObject json;
        json=jarr.getJSONObject(0);
        String orderdate=json.getString("orderdate");
        orderid=json.getString("orderid");

        pref = AddressDeliveryActivity.this.getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        //editor.clear();
        editor.putString("currentOrderid", orderid); //save the string array in share preference and will use it when user done ipay88 payment
        editor.apply();

//        resultDeligate=new ResultDelegate();
//        //ipay88 setting
//        final IpayPayment payment = new IpayPayment();
//        payment.setMerchantKey (merchantkey);
//        payment.setMerchantCode (merchantcode);
//        payment.setPaymentId ("2");
//        payment.setCurrency ("MYR");
//
//        //total amount here, unlock this when it launch,TQ
//        String ipayTotalAmount=pref.getString("total_amount", null);
//        Log.e("total amount here:",ipayTotalAmount);
//        Log.e("total amount here:",ipayTotalAmount);
//        Log.e("total amount here:",ipayTotalAmount);
//        Log.e("total amount here:",ipayTotalAmount);
//
//        payment.setAmount ("1");

        //show all item string here
        pref = AddressDeliveryActivity.this.getSharedPreferences("MyPref", 0); // 0 - for private mode
        orderid=pref.getString("currentOrderid",null);

//        payment.setProdDesc ("Order: "+orderid);
////        payment.setUserName (Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail());
////        payment.setUserEmail (firebaseAuth.getCurrentUser().getEmail());
////        payment.setUserContact ("test contact");
////        payment.setRemark ("testing payment gateway");
////        payment.setCountry ("MY");
////        payment.setBackendPostURL (ipayURL);
////        payment.setRefNo (orderdate+orderid);
////
////        Intent checkoutIntent = Ipay.getInstance().checkout(payment, AddressDeliveryActivity.this, resultDeligate);
////
////        startActivityForResult(checkoutIntent, 223);

        pref = AddressDeliveryActivity.this.getSharedPreferences("MyPref", 0); // 0 - for private mode
        orderid=pref.getString("currentOrderid",null);

        String orderList=pref.getString("shop_cart_list", null);
        Log.e("success ipay",ipay_transid);
        JSON_HTTP_SEND("cartList",orderList,orderid,ipay_transid);

    }

    private void CANCEL_ORDER_JSON_HTTP_CALL(final String orderid) {

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                Log.d("Response", response);
            }

            @Override
            protected String doInBackground(Void... params) {
                UploadProcess ProcessClass = new UploadProcess();

                HashMap<String,String> HashMapParams = new HashMap<>();

                HashMapParams.put("cancelOrder",orderid);
                HashMapParams.put("cancelUID",uid);
                return ProcessClass.HttpRequest(PHPURL, HashMapParams);
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClass = new AsyncTaskUploadClass();
        AsyncTaskUploadClass.execute();

    }

    private void GetFirebaseAuth(){
        firebaseAuth=FirebaseAuth.getInstance();//get firebase object
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

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = Objects.requireNonNull(cm).getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void viewPhotoDialog(){
        cancelDialog = new Dialog(this, R.style.MaterialDialogSheet);
        cancelDialog.setContentView(R.layout.cancel_dialog); // your custom view.
        cancelDialog.setCancelable(true);
        Objects.requireNonNull(cancelDialog.getWindow()).setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cancelDialog.getWindow().setGravity(Gravity.CENTER);

        Button helpBut=cancelDialog.findViewById(R.id.cancelHelpBut);
        Button closeHelpBut = cancelDialog.findViewById(R.id.cancelCloseBut);

        closeHelpBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDialog.dismiss();
            }
        });

        helpBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog=new AlertDialog.Builder(AddressDeliveryActivity.this)
                        .setMessage(R.string.internetErrorTip)
                        .setCancelable(false)
                        .setPositiveButton(R.string.close,null)
                        .setNeutralButton(R.string.setting, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cancelDialog.dismiss();
                                startActivityForResult(new Intent(Settings.ACTION_WIRELESS_SETTINGS),124);
                                alertDialog.dismiss();
                            }
                        }).show();
            }
        });






    }
}
