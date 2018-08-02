package com.ecommerce.customer.fypproject.Service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


/**
 * Created by erict on 3/21/2018.
 */

public class FirebaseInstanceIDService extends FirebaseInstanceIdService{
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        String TAG = "FirebaseInstanceID";
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        storeTokenPref(refreshedToken);

    }

    private void storeTokenPref(String token){
        Log.e("TokenStored ",token);

//        OkHttpClient client =  new OkHttpClient();
//        RequestBody body = new FormBody.Builder()
//                .add("Token",token)
//                .build();
//        Request request = new Request.Builder()
//                .url("https://ecommercefyp.000webhostapp.com/retailer/customer_manage_user.php")
//                //.url("http://10.0.2.2/cashierbookPHP/Eric/customer_manage_user.php")
//                .post(body)
//                .build();
//        try {
//            client.newCall(request).execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
