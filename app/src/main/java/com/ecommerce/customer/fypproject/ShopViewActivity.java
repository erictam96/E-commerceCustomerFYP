package com.ecommerce.customer.fypproject;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ecommerce.customer.fypproject.adapter.Product;
import com.ecommerce.customer.fypproject.adapter.RecyclerViewAdapter;
import com.ecommerce.customer.fypproject.adapter.UploadProcess;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShopViewActivity extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener {
    private MaterialSearchBar searchBar;
    private String uid,rid;
    private int loadIndex=0;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter ownRecycle;
    private List<Product> ListOfProduct;
    private ImageView coverPhoto;
    private String search;
    private boolean doneLoad;
    private ProgressBar progressBar;
    private Snackbar snackbar;
    private Snackbar snackbar2;
    private TextView txtNoResult;
    private TextView shopName;
    private final String HTTP_JSON_URL = "https://ecommercefyp.000webhostapp.com/retailer/customer_function.php";
    private View view ;
    private int RecyclerViewItemPosition ;
    private ScrollView scrollView;
    private CircleImageView profilepic;



//    @TargetApi(Build.VERSION_CODES.M)
//    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_view);

        GetFirebaseAuth();
        Intent intent=getIntent();
        rid=intent.getStringExtra("rid");
        searchBar=findViewById(R.id.shopSearchbar);
        recyclerView=findViewById(R.id.shopViewRecycle);
        coverPhoto=findViewById(R.id.shopCoverPhoto);
        progressBar=findViewById(R.id.shopViewProgressBar);
        RelativeLayout layout = findViewById(R.id.shopViewLayout);
        txtNoResult=findViewById(R.id.txtNoResult2);
        scrollView=findViewById(R.id.shopScroll);
        profilepic=findViewById(R.id.shopProfilePhoto);
        shopName=findViewById(R.id.shopNameText);
        snackbar = Snackbar.make(layout, "Loading more...", Snackbar.LENGTH_SHORT);
        (snackbar.getView()).setBackgroundColor(getResources().getColor(R.color.orange));

        snackbar2 = Snackbar.make(layout, "Reach bottom page", Snackbar.LENGTH_LONG);
        (snackbar2.getView()).setBackgroundColor(getResources().getColor(R.color.scarletRed));


        searchBar.setHint(getResources().getString(R.string.searchItem2));
        searchBar.setSpeechMode(true);
        searchBar.setOnSearchActionListener(this);
        searchBar.setNavButtonEnabled(true);
        searchBar.setNavigationIcon(R.mipmap.ic_back);


        ListOfProduct = new ArrayList<>();

        recyclerView.setHasFixedSize(false);

        RecyclerView.LayoutManager layoutManagerOfrecyclerView = new GridLayoutManager(this, 2);

        recyclerView.setLayoutManager(layoutManagerOfrecyclerView);

        //Toast.makeText(ShopViewActivity.this,rid,Toast.LENGTH_LONG).show();
        JSON_HTTP_CALL();
        callShopCoverPhoto();

        // Implementing Click Listener on RecyclerView.
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            final GestureDetector gestureDetector = new GestureDetector(ShopViewActivity.this, new GestureDetector.SimpleOnGestureListener() {

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


                    itemDetailActivity(ownRecycle.getListData().get(RecyclerViewItemPosition));

                    Log.e("clicked position",Integer.toString(RecyclerViewItemPosition));
                    Log.e("size list",Integer.toString(ListOfProduct.size()));
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

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)&&doneLoad) {

                    //Toast.makeText(FindActivity.this,"LAst",Toast.LENGTH_LONG).show();
                    loadIndex+=6;
                    JSON_HTTP_CALL();

                }
            }
        });

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                // Grab the last child placed in the ScrollView, we need it to determinate the bottom position.
                View view = scrollView.getChildAt(scrollView.getChildCount()-1);

                // Calculate the scrolldiff
                int diff = (view.getBottom()-(scrollView.getHeight()+scrollView.getScrollY()));

                // if diff is zero, then the bottom has been reached
                if( diff == 0 &&doneLoad)
                {
                    // notify that we have reached the bottom
                    loadIndex+=6;
                    JSON_HTTP_CALL();
                }

            }
        });


        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setNestedScrollingEnabled(false);

    }


    private void callShopCoverPhoto(){
        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String response){
                super.onPostExecute(response);
                Log.e("cover url",response);
                try {
                    JSONArray jarr = new JSONArray(response);
                    JSONObject json;
                    json = jarr.getJSONObject(0);

                    Glide.with(ShopViewActivity.this)
                            .load(json.getString("coverurl")) // image url
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.photo) // any placeholder to load at start
                                    .error(R.drawable.photo)  // any image in case of error
                                    .override(500, 500) // resizing
                                    .centerCrop())
                            .into(coverPhoto);  // imageview object

                    Glide.with(ShopViewActivity.this)
                            .load(json.getString("profileurl")) // image url
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.photo) // any placeholder to load at start
                                    .error(R.drawable.photo)  // any image in case of error
                                    .override(500, 500) // resizing
                                    .centerCrop())
                            .into(profilepic);


                    shopName.setText(json.getString("shopname"));




                    } catch (JSONException e) {
                        Crashlytics.logException(e);
                        // handle your exception here!
                        e.printStackTrace();
                    }


                    // ListOfdataAdapter.add(GetDataAdapter2);


            }

            @Override
            protected String doInBackground(Void... params) {
                UploadProcess ProcessClass = new UploadProcess();

                HashMap<String,String> HashMapParams = new HashMap<>();
                HashMapParams.put("getCoverPic",rid);

                return ProcessClass.HttpRequest(HTTP_JSON_URL, HashMapParams);
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClass = new AsyncTaskUploadClass();
        AsyncTaskUploadClass.execute();
    }

    private void JSON_HTTP_CALL(){
        search=searchBar.getText();
        ListOfProduct = new ArrayList<>();
        doneLoad=false;

//

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if(loadIndex==0) {
                    progressBar.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                }else{
                    snackbar.show();
                }



            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);

                Log.d("search result",response);
                if(response.equalsIgnoreCase("[]")&&loadIndex!=0){
                    Toast.makeText(ShopViewActivity.this,"Reach max result",Toast.LENGTH_LONG).show();
                    ownRecycle.refreshList();
                    recyclerView.setVisibility(View.VISIBLE);
                    txtNoResult.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    snackbar.dismiss();
                    snackbar2.show();

                }else if(response.equalsIgnoreCase("[]")&&loadIndex==0){
                    recyclerView.setVisibility(View.INVISIBLE);
                    txtNoResult.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }else{
                    try {
                        txtNoResult.setVisibility(View.INVISIBLE);
                        ParseJSonResponse(response);
                        progressBar.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Crashlytics.logException(e);
                        // handle your exception here!
                    }
                    Log.d("Response", response);
                }

            }

            @Override
            protected String doInBackground(Void... params) {
                UploadProcess ProcessClass = new UploadProcess();

                HashMap<String,String> HashMapParams = new HashMap<>();
                HashMapParams.put("searchingKey",search);
                HashMapParams.put("loadingIndex",Integer.toString(loadIndex));
                HashMapParams.put("userid",uid);
                HashMapParams.put("retailerid",rid);

                return ProcessClass.HttpRequest(HTTP_JSON_URL, HashMapParams);
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClass = new AsyncTaskUploadClass();
        AsyncTaskUploadClass.execute();

    }



    private void ParseJSonResponse(String array) throws JSONException {
        Log.d("returned:",array);
        JSONArray jarr = new JSONArray(array);
//        if(ListOfProduct!=null){
//            ListOfProduct.clear();
//            recyclerViewadapter = new RecyclerViewAdapter(ListOfProduct, FindActivity.this);
//            recyclerView.setAdapter(recyclerViewadapter);
//
//        }
        ArrayClear();
        for(int i = 0; i<jarr.length(); i++) {

            //DataAdapter GetDataAdapter2 = new DataAdapter();
            Product product=new Product();
            JSONObject json;
            try {

                json = jarr.getJSONObject(i);
//
                product.setProdCode(json.getString("prodcode"));
                product.setProdName(json.getString("prodname"));
                product.setProdPrice(Double.parseDouble(json.getString("price")));
                product.setShopName(json.getString("shopname"));

                product.setProdDiscount(Integer.parseInt(json.getString("discount")));
                product.setProductURL(json.getString("imageurl"));
                product.setDiscountedPrice(json.getString("discountPrice"));



            } catch (JSONException e) {
                Crashlytics.logException(e);
                // handle your exception here!
                e.printStackTrace();
            }
            ListOfProduct.add(product);

            // ListOfdataAdapter.add(GetDataAdapter2);
        }
        Log.d("list search",ListOfProduct.toString());



        if(loadIndex==0){
            ownRecycle=new RecyclerViewAdapter(ListOfProduct, this,true);
           // recyclerViewadapter = ownRecycle;
            recyclerView.setAdapter(ownRecycle);
            ownRecycle.notifyItemRangeChanged(0,ListOfProduct.size());
        }else{
            //recyclerViewadapter.notifyItemRangeChanged(loadIndex,ListOfProduct.size());
            ownRecycle.addList(ListOfProduct);
        }

        if(snackbar.isShown()){
            snackbar.dismiss();
        }
        doneLoad=true;
        progressBar.setVisibility(View.GONE);
    }



    @Override
    public void onSearchStateChanged(boolean b) {
        loadIndex=0;
        //JSON_HTTP_CALL();

    }

    @Override
    public void onSearchConfirmed(CharSequence charSequence) {
       // Toast.makeText(this,"Searching "+charSequence.toString(),Toast.LENGTH_SHORT).show();
        //if(check1==true){
        //    check1=false;
        loadIndex=0;
        JSON_HTTP_CALL();
        //}


    }

    //search bar button implementation
    @Override
    public void onButtonClicked(int i) {
        Log.d("respones on click",Integer.toString(i));
        loadIndex=0;

        switch (i){
            case MaterialSearchBar.BUTTON_NAVIGATION:

                finish();
                break;
            case MaterialSearchBar.BUTTON_SPEECH:
                promptSpeechInput();
                //Toast.makeText(FindActivity.this,"Speech ",Toast.LENGTH_SHORT).show();

        }
    }


    //Showing google speech input dialog

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void itemDetailActivity(Product x){

        Intent intent = new Intent(ShopViewActivity.this,ItemDetailActivity.class);
        //Pack Data to Send
        intent.putExtra("prodCode",x.getProdCode());
        //intent.putExtra("onlickListener",ClassUtils.getAllInterfaces);

        //open activity
        startActivityForResult(intent,123);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 100: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Toast.makeText(getApplicationContext(), "Searching " +result.get(0), Toast.LENGTH_SHORT).show();
                    search= result.get(0);
                    searchBar.setText(result.get(0));
                    loadIndex=0;
                    JSON_HTTP_CALL();
                }
                break;
            }
            case 123:{
                if (resultCode == RESULT_OK && null != data) {
                    String newText = data.getStringExtra("checkout");
                    if(newText.equalsIgnoreCase("111")){
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("checkout", "111");
                        setResult(ItemDetailActivity.RESULT_OK, resultIntent);

                        finish();
                    }
                }
            }
        }
    }
    private void GetFirebaseAuth(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            Intent intent = new Intent(this, SplashScreenActivity.class);
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
    private void ArrayClear(){
        ListOfProduct.clear();

    }
}
