package com.ecommerce.customer.fypproject;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.ecommerce.customer.fypproject.adapter.UploadProcess;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import io.fabric.sdk.android.Fabric;

public class SplashScreenActivity extends Activity {
    private static final int RC_SIGN_IN = 0;

    private final String LoginPathOnServer = "https://ecommercefyp.000webhostapp.com/retailer/customer_manage_user.php";
    //String LoginPathOnServer = "http://10.0.2.2/cashierbookPHP/Eric/customer_manage_user.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash_screen);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.ecommerce.customer.fypproject",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException ignored) {

        }

        if (isOnline()) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                if (firebaseAuth.getCurrentUser() != null) {
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    //go to MAIN activity
                } else {
                    startFirebaseAuth();
                }
            } else {
                new AlertDialog.Builder(this)
                            .setIcon(R.drawable.error)
                            .setTitle(R.string.Error)
                            .setMessage(R.string.noInternet)
                            .setPositiveButton(R.string.tryAgn, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);
                                    //SplashScreenActivity.this.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                                }
                            }).setNeutralButton(R.string.setting, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(Settings.ACTION_WIRELESS_SETTINGS),1);
                    }
                }).show();
                }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            IdpResponse idpResponse = IdpResponse.fromResultIntent(data);
            if(resultCode== RESULT_OK){
                Answers.getInstance().logLogin(new LoginEvent()
                        .putSuccess(true)
                        .putMethod(Objects.requireNonNull(idpResponse).getProviderType()));
                //This is facebook/google access token

                //Log.e("providerType",idpResponse.getProviderType());
                //String providerId = idpResponse.getProviderType();
                //String idpToken = idpResponse.getIdpToken();

               // Log.e("fb",providerId+" "+idpToken);

                CashierbookServerAuth();
            }else{
                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.logout_icon)
                        .setTitle(R.string.exit)
                        .setMessage(R.string.exitMsg)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                System.exit(0);
                            }

                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            }
                        })
                        .show();
                //send answer to crashlytic
                Answers.getInstance().logLogin(new LoginEvent()
                        .putSuccess(false));
            }
        }else if(requestCode==1){
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

    private void CashierbookServerAuth(){
        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {
            protected void onPreExecute() {
                super.onPreExecute();
                //progressDialog = ProgressDialog.show(SplashScreenActivity.this,"Authenticating user...","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                Log.e("REsponse",response);
                //progressDialog.dismiss();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        String topic= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                        FirebaseMessaging.getInstance().subscribeToTopic(topic);
                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }else{
                        FirebaseAuth.getInstance().signOut();
                        try {
                            FirebaseInstanceId.getInstance().deleteInstanceId();
                        } catch (IOException e) {
                            Crashlytics.logException(e);
                            e.printStackTrace();
                        }
                        Answers.getInstance().logLogin(new LoginEvent()
                                .putSuccess(false)
                                .putMethod("Verify Role Failed"));
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(SplashScreenActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(getApplicationContext(), SplashScreenActivity.class));
                    }
                }
                catch(JSONException e){
                    Crashlytics.logException(e);
                    // handle your exception here!
                    e.printStackTrace();
                }
            }
            @Override
            protected String doInBackground(Void... params) {
                String uid =null,email=null,name=null;
                try{
                     uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                     email  = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
                     name = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
                }catch (Exception e){
                    Crashlytics.logException(e);
                    // handle your exception here!
                    e.printStackTrace();
                    Toast.makeText(SplashScreenActivity.this,"Something went wrong!",Toast.LENGTH_LONG).show();

                    finish();
                    startActivity(new Intent(getApplicationContext(),SplashScreenActivity.class));
                }
                UploadProcess ProcessClass = new UploadProcess();

                HashMap<String,JSONObject> HashMapParams = new HashMap<>();
                JSONObject VerifyUserJSON = new JSONObject();
                try {
                    VerifyUserJSON.put("uid",uid);
                    VerifyUserJSON.put("email",email);
                    VerifyUserJSON.put("name",name);
                } catch (JSONException e) {
                    Crashlytics.logException(e);
                    // handle your exception here!
                    e.printStackTrace();
                }

                HashMapParams.put("verifyRole", VerifyUserJSON);
                return ProcessClass.HttpRequestObject(LoginPathOnServer, HashMapParams);
            }
        }

        AsyncTaskUploadClass AsyncTaskUploadClass = new AsyncTaskUploadClass();
        AsyncTaskUploadClass.execute();
    }

    private void startFirebaseAuth(){
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.LoginTheme)
                .setLogo(R.drawable.ecommercelogo)
                .setIsSmartLockEnabled(false,true)
                .setAvailableProviders(Arrays.asList(
                        new AuthUI.IdpConfig.EmailBuilder().build(),
                        new AuthUI.IdpConfig.GoogleBuilder().build(),
                        new AuthUI.IdpConfig.FacebookBuilder().build()))
                .build(),RC_SIGN_IN);
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = Objects.requireNonNull(cm).getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
