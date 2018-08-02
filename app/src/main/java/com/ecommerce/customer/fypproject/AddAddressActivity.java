package com.ecommerce.customer.fypproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.ecommerce.customer.fypproject.adapter.UploadProcess;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class AddAddressActivity extends AppCompatActivity {
    private EditText addresstxt,statetxt,citytxt,postcodetxt,emailtxt,fullnametxt,contacttxt;
    private Switch defaultSwitch;
    private String uid;
    private ProgressDialog placeOrderDialog;
    private FirebaseAuth firebaseAuth;
    private final String PHPURL = "https://ecommercefyp.000webhostapp.com/retailer/customer_function.php";


    /* REMINDER!!!! : please disable this activity first, currently our platform not yet support custom delivery address yet*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        placeOrderDialog=new ProgressDialog(AddAddressActivity.this);
        placeOrderDialog.setCancelable(false);
        placeOrderDialog.setTitle(R.string.processingUrOrder);
        placeOrderDialog.setMessage(getResources().getString(R.string.pleaseWait));
        placeOrderDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        Toolbar addAddressToolbar = findViewById(R.id.addAddressToolbar);
        setSupportActionBar(addAddressToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.addAddress);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        GetFirebaseAuth();
        addresstxt=findViewById(R.id.addressText);
        statetxt=findViewById(R.id.stateText);
        citytxt=findViewById(R.id.cityText);
        postcodetxt=findViewById(R.id.postcodeText);
        emailtxt=findViewById(R.id.emailText);
        fullnametxt=findViewById(R.id.fullNameText);
        contacttxt=findViewById(R.id.contactText);
        defaultSwitch=findViewById(R.id.defaultAddressSwitch);
        Button addAddressBut = findViewById(R.id.submitAddressButton);



        addAddressBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean check =true;
                JSONArray JSONUploadArray=new JSONArray();


                    if(emailtxt.getText().toString().equalsIgnoreCase("")){ //default email
                        emailtxt.setText(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail());
                    }

                    if( addresstxt.getText().toString().equalsIgnoreCase("")){
                        addresstxt.setError(getResources().getString(R.string.addressCantBlank));
                        check=false;
                    }
                    if(statetxt.getText().toString().isEmpty()){
                        statetxt.setError(getResources().getString(R.string.stateCantBlank));
                        check=false;
                    }
                    if(citytxt.getText().toString().isEmpty()){
                        citytxt.setError(getResources().getString(R.string.cityCantBlank));
                        check=false;
                    }
                    if(postcodetxt.getText().toString().isEmpty()){
                        postcodetxt.setError(getResources().getString(R.string.postcodeCantBlank));
                        check=false;
                    }
                    if(fullnametxt.getText().toString().isEmpty()){
                        fullnametxt.setError(getResources().getString(R.string.receptCantBlank));
                        check=false;
                    }
                    if(contacttxt.getText().toString().isEmpty()){
                        contacttxt.setError(getResources().getString(R.string.contactCantBlank));
                        check=false;
                    }
                    if(check){
                        JSONObject JSONPlaceOrderProdcut = new JSONObject();
                        try {
                            JSONPlaceOrderProdcut.put("address", addresstxt.getText().toString());
                            JSONPlaceOrderProdcut.put("state", statetxt.getText().toString());
                            JSONPlaceOrderProdcut.put("city", citytxt.getText().toString());
                            JSONPlaceOrderProdcut.put("postcode", postcodetxt.getText().toString());
                            JSONPlaceOrderProdcut.put("fullname", fullnametxt.getText().toString());
                            JSONPlaceOrderProdcut.put("contact", contacttxt.getText().toString());
                            JSONPlaceOrderProdcut.put("email", emailtxt.getText().toString());
                            if(defaultSwitch.isChecked()){
                                JSONPlaceOrderProdcut.put("defaultAddress","yes" );
                            }else {
                                JSONPlaceOrderProdcut.put("defaultAddress","no" );
                            }

                            JSONPlaceOrderProdcut.put("userid", uid);

                            JSONUploadArray.put(JSONPlaceOrderProdcut);

                    }catch (JSONException e){
                        Crashlytics.logException(e);
                        // handle your exception here!
                        e.printStackTrace();
                    }
                    JSON_HTTP_SEND(JSONUploadArray);
                }
            }
        });

    }

    private void JSON_HTTP_SEND(final JSONArray addressObj) {



        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                placeOrderDialog.show();
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                Log.d("Response", response);
                placeOrderDialog.dismiss();
                finish();
            }

            @Override
            protected String doInBackground(Void... params) {
                UploadProcess ProcessClass = new UploadProcess();

                HashMap<String,String> HashMapParams = new HashMap<>();

                HashMapParams.put("newAddress", addressObj.toString());
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

    public  boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
