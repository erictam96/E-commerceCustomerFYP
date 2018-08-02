package com.ecommerce.customer.fypproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ecommerce.customer.fypproject.adapter.UploadProcess;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class ProfileActivity extends Fragment {
    private String uid;
    private TextView chkoutNotf,orderNotf,feedbackNotf;
    private final String PHPURL="https://ecommercefyp.000webhostapp.com/retailer/customer_function.php";
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_profile, container, false);

        GetFirebaseAuth();
        RelativeLayout orderToShip = rootView.findViewById(R.id.orderToShipRelative);
        RelativeLayout checkOutOrder = rootView.findViewById(R.id.checkOutOrderRelative);
        RelativeLayout returnOrder = rootView.findViewById(R.id.returnOrderRelative);
        RelativeLayout itemFeedback = rootView.findViewById(R.id.feedbackRelative);
        RelativeLayout setting = rootView.findViewById(R.id.settingRelative);
        RelativeLayout purchaseHistory = rootView.findViewById(R.id.purchasedHistoryRelative);

        chkoutNotf=rootView.findViewById(R.id.chkoutNotification);
        orderNotf=rootView.findViewById(R.id.manageOrderNotification);
        feedbackNotf=rootView.findViewById(R.id.feedbackNotification);



        checkOutOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CheckoutItemActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

               // Toast.makeText(getActivity(),"clicked check out order",Toast.LENGTH_SHORT).show();
            }
        });


        orderToShip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ManageOrderActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                //Toast.makeText(getActivity(),"clicked order to ship",Toast.LENGTH_SHORT).show();
            }
        });


        returnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RefundActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

               // Toast.makeText(getActivity(),"clicked returned order",Toast.LENGTH_SHORT).show();
            }
        });

        purchaseHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PurchaseHistory.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        itemFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ItemFeedbackActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setCancelable(true);
        progressDialog.setTitle(R.string.loading);
        progressDialog.setMessage(getResources().getString(R.string.prepareProfile));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        chkoutNotf.setVisibility(View.INVISIBLE);
        orderNotf.setVisibility(View.INVISIBLE);
        feedbackNotf.setVisibility(View.INVISIBLE);

        JSON_HTTP_CALL();
        return  rootView;
    }

    public void JSON_HTTP_CALL() {
        progressDialog.show();
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


                        chkoutNotf.setVisibility(View.INVISIBLE);
                        orderNotf.setVisibility(View.INVISIBLE);
                        feedbackNotf.setVisibility(View.INVISIBLE);
                    }else{
                        ParseJSonResponse(response);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("Response notf count", response);
            }

            @Override
            protected String doInBackground(Void... params) {
                UploadProcess ProcessClass = new UploadProcess();

                HashMap<String,String> HashMapParams = new HashMap<>();

                HashMapParams.put("getNotificationCounter",uid);
                return ProcessClass.HttpRequest(PHPURL, HashMapParams);
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClass = new AsyncTaskUploadClass();
        AsyncTaskUploadClass.execute();

    }

    //get response string and set into recyclerView
    private void ParseJSonResponse(String array) throws JSONException {

        JSONArray jarr = new JSONArray(array);//lv 1 array


        JSONObject json;
        json = jarr.getJSONObject(0);
        if(Integer.parseInt(json.getString("chknum"))>0){
            chkoutNotf.setVisibility(View.VISIBLE);
            chkoutNotf.setText(json.getString("chknum"));
        }
        if(Integer.parseInt(json.getString("ordnum"))>0){
            orderNotf.setVisibility(View.VISIBLE);
            orderNotf.setText(json.getString("ordnum"));
        }

        //feednum
        if(Integer.parseInt(json.getString("feednum"))>0){
            feedbackNotf.setVisibility(View.VISIBLE);
            feedbackNotf.setText(json.getString("feednum"));
        }



        progressDialog.dismiss();
        }
    private void GetFirebaseAuth(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            Intent intent = new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), SplashScreenActivity.class);
            try {
                FirebaseInstanceId.getInstance().deleteInstanceId();
            } catch (IOException e) {
                e.printStackTrace();
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(getActivity(),R.string.sessionexp,Toast.LENGTH_LONG).show();
        }else uid = firebaseAuth.getCurrentUser().getUid();
    }
    }
