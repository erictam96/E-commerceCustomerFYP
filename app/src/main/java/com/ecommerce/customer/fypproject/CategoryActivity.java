package com.ecommerce.customer.fypproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ecommerce.customer.fypproject.adapter.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

public class CategoryActivity extends Fragment {
    private final Timer timer = new Timer();
    private List<Product> ListOfProduct;

    private RecyclerView recyclerViewTop;
    private final String HTTP_JSON_URL = "https://ecommercefyp.000webhostapp.com/retailer/customer_function.php";
    private String uid;
    private View view ;
    private int RecyclerViewItemPosition ;

    private final int loadIndex=-1;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_category, container, false);
        GetFirebaseAuth();
        ListOfProduct = new ArrayList<>();
        recyclerViewTop = rootView.findViewById(R.id.rcvHome1);
        recyclerViewTop.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManagerOfrecyclerViewTop = new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewTop.setLayoutManager(layoutManagerOfrecyclerViewTop);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerViewTop);

        RelativeLayout electronicAcc = rootView.findViewById(R.id.category_electronic_accessories);
        RelativeLayout automotive = rootView.findViewById(R.id.category_automotive);
        RelativeLayout electronicDevice = rootView.findViewById(R.id.category_electronic_devices);
        RelativeLayout homeAndLifestyle = rootView.findViewById(R.id.category_home_lifestyle);
        RelativeLayout homeAppliance = rootView.findViewById(R.id.category_home_appliance);
        RelativeLayout healthAndBeauty = rootView.findViewById(R.id.category_health_and_beauty);
        RelativeLayout babyAndToy = rootView.findViewById(R.id.category_baby_and_toy);
        RelativeLayout groceryAndPet = rootView.findViewById(R.id.category_grocery_pets);
        RelativeLayout womenFashion = rootView.findViewById(R.id.category_women_fashion);
        RelativeLayout menFashion = rootView.findViewById(R.id.category_men_fashion);
        RelativeLayout fashionAcc = rootView.findViewById(R.id.category_fashion_accessories);
        RelativeLayout sportAndTravel = rootView.findViewById(R.id.category_sports_and_travel);

        electronicAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentSearch("electronic accessories");
            }
        });

        automotive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentSearch("automotive");
            }
        });
        electronicDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentSearch("electronic devices");
            }
        });
        homeAndLifestyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentSearch("home and life-style");
            }
        });
        homeAppliance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentSearch("home appliance");
            }
        });
        healthAndBeauty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentSearch("health and beauty");
            }
        });
        babyAndToy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentSearch("babies and toys");
            }
        });
        groceryAndPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentSearch("grocery and pets");
            }
        });
        womenFashion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentSearch("women fashion");
            }
        });
        menFashion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentSearch("man fashion");
            }
        });
        fashionAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentSearch("fashion accessories");
            }
        });
        sportAndTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentSearch("sport and travel");
            }
        });

        JSON_HTTP_CALL();

        // Implementing Click Listener on RecyclerView.
        recyclerViewTop.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override public boolean onSingleTapUp(MotionEvent motionEvent) {

                    return true;
                }

            });
            @Override
            public boolean onInterceptTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {

                view = Recyclerview.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if(view != null && gestureDetector.onTouchEvent(motionEvent)) {

                    //Getting RecyclerView Clicked Item value.
                    RecyclerViewItemPosition = Recyclerview.getChildAdapterPosition(view);


                    itemDetailActivity(ListOfProduct.get(RecyclerViewItemPosition));
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        return rootView;

    }

    private void JSON_HTTP_CALL() {
        ListOfProduct = new ArrayList<>();
        boolean doneLoad = false;

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);

                try {
                    ParseJSonResponse(response);
                } catch(JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                UploadProcess ProcessClass = new UploadProcess();

                HashMap<String,String> HashMapParams = new HashMap<>();
                HashMapParams.put("searchKey","");
                HashMapParams.put("loadIndex",Integer.toString(loadIndex));
                HashMapParams.put("uid",uid);
                return ProcessClass.HttpRequest(HTTP_JSON_URL, HashMapParams);
            }

        }
        AsyncTaskUploadClass AsyncTaskUploadClass = new AsyncTaskUploadClass();
        AsyncTaskUploadClass.execute();
    }

    private void ParseJSonResponse(String array) throws JSONException {
        JSONArray jarr = new JSONArray(array);
        RecyclerView.Adapter recyclerViewAdapter;
        if(ListOfProduct!=null){
            ListOfProduct.clear();
            recyclerViewAdapter = new RecyclerViewAdapter(ListOfProduct, CategoryActivity.this.getContext(),false);
            recyclerViewTop.setAdapter(recyclerViewAdapter);

        }
        //ArrayClear();
        if (ListOfProduct != null) {
            ListOfProduct.clear();
        }
        for(int i = 0; i<jarr.length(); i++) {

            //DataAdapter GetDataAdapter2 = new DataAdapter();
            Product product=new Product();
            JSONObject json;

            json = jarr.getJSONObject(i);
//
            product.setProdCode(json.getString("prodcode"));
            product.setProdName(json.getString("prodname"));
            product.setProdPrice(Double.parseDouble(json.getString("price")));
            product.setShopName(json.getString("shopname"));

            product.setProdDiscount(Integer.parseInt(json.getString("discount")));
            product.setProductURL(json.getString("imageurl"));
            product.setDiscountedPrice(json.getString("discountPrice"));


            ListOfProduct.add(product);

            // ListOfdataAdapter.add(GetDataAdapter2);
        }

        recyclerViewAdapter = new RecyclerViewAdapter(ListOfProduct, this.getContext(),false);

        recyclerViewTop.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();


    }

    private void itemDetailActivity(Product x){

        Intent intent = new Intent(this.getContext(),ItemDetailActivity.class);
        //Pack Data to Send
        intent.putExtra("prodCode",x.getProdCode());
        //intent.putExtra("onlickListener",ClassUtils.getAllInterfaces);

        //open activity
        startActivity(intent);
    }
    private void GetFirebaseAuth(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            Intent intent = new Intent(getActivity(), SplashScreenActivity.class);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timer.purge();
    }
    private void intentSearch(String category){
        Intent myIntent2 = new Intent(getContext(), FindActivity.class);
        myIntent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        myIntent2.putExtra("category",category);
        startActivity(myIntent2);
    }


}
