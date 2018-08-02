package com.ecommerce.customer.fypproject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ecommerce.customer.fypproject.adapter.OrderItem;
import com.ecommerce.customer.fypproject.adapter.OrderItemAdapter;
import com.ecommerce.customer.fypproject.adapter.OrderViewAdapter;
import com.ecommerce.customer.fypproject.adapter.UploadProcess;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ManageOrderActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private String uid;
    private final String PHPURL="https://ecommercefyp.000webhostapp.com/retailer/customer_function.php";
    private List<OrderItem> orderList;
    private OrderViewAdapter orderViewAdapter;
    private ProgressDialog progressDialog;
    private View view;
    int RecyclerViewItemPosition;
    private int defaultbrightess=0;
    private ImageView viewImage;
    private ImageView qrimg;
    private Dialog viewPhotoDialog;
    private RelativeLayout emptyRelative;
    private RelativeLayout oriRelative;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mange_order);
        EnableRuntimePermission();
        createViewPhotoDialog();

        Toolbar toolbar = findViewById(R.id.manageOrderToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.manageorder);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        recyclerView=findViewById(R.id.recycleManageOrder);
        oriRelative=findViewById(R.id.manageOrderRelative);
        oriRelative.setVisibility(View.INVISIBLE);
        emptyRelative=findViewById(R.id.emptyManageOrderRelative);
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setTitle(R.string.loading);
        progressDialog.setMessage(getResources().getString(R.string.prepareorderlist));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        orderList=new ArrayList<>();

        GetFirebaseAuth();

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManagerOfrecyclerView = new LinearLayoutManager(ManageOrderActivity.this);

        recyclerView.setLayoutManager(layoutManagerOfrecyclerView);

        qrimg=findViewById(R.id.QRcodeImg);








        qrimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                viewPhotoDialog.show();
                if(defaultbrightess >= 0 && defaultbrightess <= 255){
                    Settings.System.putInt(getApplicationContext().getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
                    defaultbrightess = Settings.System.getInt(
                            getApplicationContext().getContentResolver()
                            ,
                            Settings.System.SCREEN_BRIGHTNESS,
                            0
                    );

                    Settings.System.putInt(getApplicationContext().getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
                    Settings.System.putInt(
                            getApplicationContext().getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS,
                            255
                    );
                }
            }
        });

        JSON_CALL();
        JSON_CALL_LIST();

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
                Log.e("Response order",response);
                try {
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

                HashMapParams.put("getQR",uid);
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

            JSONObject json;
            json=jarr.getJSONObject(a);

            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            try{
                BitMatrix bitMatrix = multiFormatWriter.encode(json.getString("key"), BarcodeFormat.QR_CODE,500,500);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                qrimg.setImageBitmap(bitmap);
                viewImage.setImageBitmap(bitmap);
            }
            catch (WriterException e){
                e.printStackTrace();
            }
          //  x.setNumberPackingItem(json.getString("packingItem"));
//            x.setNumberReadyCollectItem(json.getString("rdyCollectItem"));
//     //       x.setNumberReadyDeliverItem(json.getString("deliveryItem"));
//            x.setTotalItem(json.getString("totalItem"));
//            x.setDate(json.getString("orderDate"));
//

        }

//        orderViewAdapter= new OrderViewAdapter(orderList,this);
//        recyclerViewadapter =orderViewAdapter;
//        recyclerView.setAdapter(recyclerViewadapter);
//        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
//
//            GestureDetector gestureDetector = new GestureDetector(ManageOrderActivity.this, new GestureDetector.SimpleOnGestureListener() {
//
//                @Override public boolean onSingleTapUp(MotionEvent motionEvent) {
//                    return true;
//                }
//
//            });
//            @Override
//            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
//                view = rv.findChildViewUnder(e.getX(), e.getY());
//
//                if(view != null && gestureDetector.onTouchEvent(e)) {
//                    RecyclerViewItemPosition = rv.getChildAdapterPosition(view);
//                    Log.d("clicked: ", Integer.toString(RecyclerViewItemPosition));
//                    // calculateTotal();
//
//                    if (RecyclerViewItemPosition >= 0) {
//                        Intent orderDetail = new Intent(ManageOrderActivity.this, OrderDetailActivity.class);
//                        orderDetail.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        orderDetail.putExtra("orderID", orderList.get(RecyclerViewItemPosition).getOrderNumber());
//                        startActivity(orderDetail);
//                    }
//                }
//
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
                        emptyRelative.setVisibility(View.VISIBLE);
                        oriRelative.setVisibility(View.INVISIBLE);
                    }else{
                        emptyRelative.setVisibility(View.INVISIBLE);
                        oriRelative.setVisibility(View.VISIBLE);
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

                HashMapParams.put("fetchorderdetail",uid);
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
            x.setAddress(json.getString("address"));

            String time=json.getString("picktime");

            DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            //DateFormat df3 = new SimpleDateFormat("HH:mm:ss");

            try{
                final long ONE_MINUTE_IN_MILLIS = 60000;
                Date cdate = df2.parse(time);

                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                Date date1 =sdf.parse(new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(cdate));

                Date date2 = sdf.parse("10:45:00");
                Date date3 = sdf.parse("12:45:00");
                Date date4 = sdf.parse("14:45:00");
                Date date5 = sdf.parse("16:45:00");


                if(date1.compareTo(date2)<0){
                    System.out.println("arrive 11:30am");
                    cdate.setHours(11);

                }else if(date1.compareTo(date3)<0){
                    System.out.println("arrive 13:30am");
                    cdate.setHours(13);


                }else if(date1.compareTo(date4)<0){
                    System.out.println("arrive 15:30am");
                    cdate.setHours(15);


                }else if(date1.compareTo(date5)<0){
                    System.out.println("arrive 17:30am");
                    cdate.setHours(17);


                }else{
                    cdate.setHours(11);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(cdate);
                    cal.add(Calendar.DATE, 1);
                    cdate=cal.getTime();

                }
                cdate.setMinutes(30);
                cdate.setSeconds(00);
                x.setETA(df2.format(cdate));

//                long currenttime=cdate.getTime();
//                Date afterAddingMins = new Date(currenttime + (25 * ONE_MINUTE_IN_MILLIS));
//                x.setETA(df2.format(afterAddingMins));
            }catch (Exception ignored){

            }

//            x.setETA();

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
    public  boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private void createViewPhotoDialog(){
        viewPhotoDialog = new Dialog(ManageOrderActivity.this, R.style.MaterialDialogSheet);
        viewPhotoDialog.setContentView(R.layout.photo_layout); // your custom view.
        viewPhotoDialog.setCancelable(true);
        Objects.requireNonNull(viewPhotoDialog.getWindow()).setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        viewPhotoDialog.getWindow().setGravity(Gravity.CENTER);

        viewImage=viewPhotoDialog.findViewById(R.id.viewImage);
        Button viewleft = viewPhotoDialog.findViewById(R.id.viewleft);
        Button viewright = viewPhotoDialog.findViewById(R.id.viewright);

        viewleft.setVisibility(View.GONE);
        viewright.setVisibility(View.GONE);

        int id = getResources().getIdentifier("com.ecommerce.customer.fypproject:drawable/cashierbook_qr", null, null);
        viewImage.setImageResource(id);
        viewImage.setScaleType(ImageView.ScaleType.CENTER_CROP);


        viewPhotoDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Settings.System.putInt(getApplicationContext().getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
                Settings.System.putInt(
                        getApplicationContext().getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS,
                        defaultbrightess
                );
            }
        });

    }
    private void EnableRuntimePermission() {
        //check permission is granted or not
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(ManageOrderActivity.this)) {
                // Do stuff here
                Settings.System.putInt(getApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
                defaultbrightess = Settings.System.getInt(
                        getApplicationContext().getContentResolver()
                        ,
                        Settings.System.SCREEN_BRIGHTNESS,
                        0
                );
            } else {

                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

        }

    }

}
