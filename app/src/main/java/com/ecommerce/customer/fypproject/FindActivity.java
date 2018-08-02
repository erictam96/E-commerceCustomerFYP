package com.ecommerce.customer.fypproject;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.ecommerce.customer.fypproject.adapter.Product;
import com.ecommerce.customer.fypproject.adapter.RecyclerViewAdapter;
import com.ecommerce.customer.fypproject.adapter.UploadProcess;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.SearchEvent;
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
import java.util.Objects;

//kj
public class FindActivity extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener {
   // List<DataAdapter> ListOfdataAdapter;
   private List<Product> ListOfProduct;
    private MaterialSearchBar searchBar;
    private String uid;
    private RecyclerView recyclerView;
    private final int REQ_CODE_SPEECH_INPUT = 100;
///cashierbook.com/public_html/retailer/customer_info.php
    private final String HTTP_JSON_URL = "https://ecommercefyp.000webhostapp.com/retailer/customer_function.php";
    private boolean doneLoad=false;
    private String search="";
    TextView txtCategory;
    private TextView txtNoResult;
    TextView txtCondition;
    JsonArrayRequest RequestOfJSonArray ;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    RequestQueue requestQueue ;
    LinearLayout catLayout,conditionLayout;
    RelativeLayout filterLayout;
    private View view ;

    private int RecyclerViewItemPosition ;
    private ProgressBar progressBar;
    private RecyclerViewAdapter ownRecycle;
    private int loadIndex=0;
    private Snackbar snackbar;
    private Snackbar snackbar2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        GetFirebaseAuth();
        searchBar = findViewById(R.id.searchbar);
        searchBar.setHint(getResources().getString(R.string.searchItem));
        searchBar.setSpeechMode(true);
        searchBar.setOnSearchActionListener(this);
        searchBar.setNavButtonEnabled(true);


        searchBar.setNavigationIcon(R.mipmap.ic_back);

        ListOfProduct = new ArrayList<>();

        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout1);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        progressBar = findViewById(R.id.progressBar);

        txtNoResult = findViewById(R.id.txtNoResult);

        RelativeLayout layout = findViewById(R.id.findlayout);

        Intent intentData=this.getIntent();




        if(intentData.hasExtra("category")){

            searchBar.setText(Objects.requireNonNull(intentData.getExtras()).getString("category"));
        }

        snackbar = Snackbar.make(layout, getResources().getString(R.string.loadingMore), Snackbar.LENGTH_SHORT);
        (snackbar.getView()).setBackgroundColor(getResources().getColor(R.color.orange));

        snackbar2 = Snackbar.make(layout, getResources().getString(R.string.reachBtm), Snackbar.LENGTH_LONG);
        (snackbar2.getView()).setBackgroundColor(getResources().getColor(R.color.scarletRed));



        recyclerView = findViewById(R.id.recyclerview1);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManagerOfrecyclerView = new GridLayoutManager(this, 2);

        recyclerView.setLayoutManager(layoutManagerOfrecyclerView);


        JSON_HTTP_CALL();
        boolean check1 = true;

    // Implementing Click Listener on RecyclerView.
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

        final GestureDetector gestureDetector = new GestureDetector(FindActivity.this, new GestureDetector.SimpleOnGestureListener() {

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
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                loadIndex=0;
                JSON_HTTP_CALL();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        //Listener on Filter layout
     /*   filterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] items = {"Newest first"," Price - Low to High "," Price - High to Low "};
                AlertDialog.Builder builder = new AlertDialog.Builder(FindActivity.this);
                builder.setTitle( "Filter/Sort" );
                builder.setIcon(R.mipmap.ic_filtericon);
                builder.setSingleChoiceItems(items, selected1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        selected1 = item;
                        switch(item)
                        {
                            case 0:
                                selectedFilter="DEFAULT";
                                break;
                            case 1:
                                selectedFilter="LOW";

                                break;
                            case 2:
                                selectedFilter="HIGH";
                                break;

                        }
                        JSON_HTTP_CALL();
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });*/

        //Listener on Condition layout
      /*  conditionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final CharSequence[] items= getResources().getStringArray(R.array.condition_array1);
                AlertDialog.Builder builder = new AlertDialog.Builder(FindActivity.this);
                builder.setTitle( "Select Category" );
                builder.setIcon(R.mipmap.ic_conditionicon);
                builder.setSingleChoiceItems( items, selected, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        selected = i;
                        txtCondition.setText(items[i].toString());
                        if(items[i].equals("All")){
                            selectedCondition = "DEFAULT";
                        }else {
                            selectedCondition = items[i].toString();
                        }
                        JSON_HTTP_CALL();
                        dialog.dismiss();
                    }
                } );
                builder.show();
            }
        });*/
        // Listener on Category layout
       /* catLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] items= getResources().getStringArray(R.array.category_array);

                AlertDialog.Builder builder = new AlertDialog.Builder(FindActivity.this);
                builder.setTitle( "Select Category" );
                builder.setIcon(R.mipmap.ic_category);
                builder.setItems( items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        txtCategory.setText(items[i].toString());
                        if(items[i].equals("All Categories")){
                            selectedCategory = "DEFAULT";
                        }else {
                            selectedCategory = items[i].toString();
                        }
                        JSON_HTTP_CALL();
                    }
                } );
                builder.show();
            }
        });*/

        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

}


    private void itemDetailActivity(Product x){

        Intent intent = new Intent(FindActivity.this,ItemDetailActivity.class);
        //Pack Data to Send
        intent.putExtra("prodCode",x.getProdCode());
        //intent.putExtra("onlickListener",ClassUtils.getAllInterfaces);

        //open activity
        startActivityForResult(intent,123);
    }



    private void JSON_HTTP_CALL(){
        search=searchBar.getText();
        ListOfProduct = new ArrayList<>();
        doneLoad=false;

        if(!search.equals("")){
            Answers.getInstance().logSearch(new SearchEvent()
                    .putQuery(search));
        }

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


                if(response.equalsIgnoreCase("[]")&&loadIndex!=0){
                    //Toast.makeText(FindActivity.this,"Reach max result",Toast.LENGTH_LONG).show();
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
                HashMapParams.put("searchKey",search);
                HashMapParams.put("loadIndex",Integer.toString(loadIndex));
                HashMapParams.put("uid",uid);
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


        doneLoad=true;
        if(loadIndex==0){
            ownRecycle=new RecyclerViewAdapter(ListOfProduct, this,true);
            RecyclerView.Adapter recyclerViewadapter = ownRecycle;
            recyclerView.setAdapter(recyclerViewadapter);
            recyclerViewadapter.notifyItemRangeChanged(0,ListOfProduct.size());
        }else{
            //recyclerViewadapter.notifyItemRangeChanged(loadIndex,ListOfProduct.size());
            ownRecycle.addList(ListOfProduct);
        }

        if(snackbar.isShown()){
            snackbar.dismiss();
        }

        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onSearchStateChanged(boolean b) {
        //JSON_HTTP_CALL();
        loadIndex=0;
    }

    @Override
    public void onSearchConfirmed(CharSequence charSequence) {
        //Toast.makeText(this,"Searching "+charSequence.toString(),Toast.LENGTH_SHORT).show();
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


     //Receiving speech input

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

    private void ArrayClear(){
        ListOfProduct.clear();

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
    public  boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
