package com.ecommerce.customer.fypproject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ecommerce.customer.fypproject.adapter.ItemDetailViewAdapter;
import com.ecommerce.customer.fypproject.adapter.ItemReview;
import com.ecommerce.customer.fypproject.adapter.ItemReviewAdapter;
import com.ecommerce.customer.fypproject.adapter.Product;
import com.ecommerce.customer.fypproject.adapter.UploadProcess;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.AddToCartEvent;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class ItemDetailActivity extends AppCompatActivity {

    private final String AddCartToServerPath = "https://ecommercefyp.000webhostapp.com/retailer/customer_function.php";
    private final String PHPURL = "https://ecommercefyp.000webhostapp.com/retailer/customer_function.php";
    private String prodCode;
    private final String addCartPathFieldOnServer = "addCart";
    private String selectedProductVariation;
    private String getprodcode;
    private String coverURL,img1URL,img2URL,img3URL,img4URL;
    private String uid,rid,getProdName,getProdCategory,getProdPrice;

    private TextView itemName,itemPrice,itemQuantity,itemSize,shopName,shopName2,shopOwner,prodCategory,prodDescription,discountText,shopDescription,itemDetailQty,itemDetailPrice,discountedPrice;
    private ImageView ImageCover = null,Image1 = null,Image2 = null,Image3=null,Image4=null,RetailerProfilePic=null,viewImage;

    private ProgressDialog progressDialog,placeCartDialog;
    private TextView selectedQty,itemDetailDiscountedPrice,itemDetailDiscountPriceText,soldQtyTxt;
    private ImageView itemDetailImage;
    private Button confirmAddToCart;
    private Button checkoutBut;
    private View view ;
    private int currentindex=0;
    private int maxImgCount=4;
    private String shopOwnerName;

    private int RecyclerViewItemPosition;
    //Product product;
    private final ArrayList<String> ImageID = new ArrayList<>();
    private final ArrayList<String> CartDetails = new ArrayList<>();
    private ArrayList<Product> itemDetailModel=new ArrayList<>();
    private Dialog addToCartDialog,viewPhotoDialog;
    private boolean loaded=false;
    private boolean doneplaceorder=false;
    private RecyclerView recyclerView;
    private List<ItemReview> orderList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
//RECEIVE DATA
        Intent i = this.getIntent();
        prodCode = Objects.requireNonNull(i.getExtras()).getString("prodCode");
        loaded=false;
        doneplaceorder=false;

        GetFirebaseAuth();
        Toolbar myposttoolbar = findViewById(R.id.itemtoolbar);
        setSupportActionBar(myposttoolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.itemdetail);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        itemDetailModel=new ArrayList<>();

        itemName=findViewById(R.id.viewTitle);
        itemPrice=findViewById(R.id.viewPrice);
        itemQuantity=findViewById(R.id.quantity_text);
        itemSize=findViewById(R.id.item_size);
        shopName=findViewById(R.id.seller_shop);
        shopName2=findViewById(R.id.viewShop);
        shopOwner=findViewById(R.id.viewUser);
        prodCategory=findViewById(R.id.viewCategory);
        prodDescription=findViewById(R.id.viewDesc);
        shopDescription=findViewById(R.id.shopDescription);
        discountText=findViewById(R.id.itemDetailPromotion);
        discountedPrice=findViewById(R.id.viewDiscountedPrice);
        soldQtyTxt=findViewById(R.id.sold_quantity);
        recyclerView=findViewById(R.id.item_review_recycleview);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManagerOfrecyclerView = new LinearLayoutManager(ItemDetailActivity.this);

        recyclerView.setLayoutManager(layoutManagerOfrecyclerView);


        ImageCover = findViewById(R.id.coverphoto);
        Image1 = findViewById(R.id.image1);
        Image2 = findViewById(R.id.image2);
        Image3 = findViewById(R.id.image3);
        Image4 = findViewById(R.id.image4);
        ImageButton shopBut = findViewById(R.id.shopButton);
        ImageButton chatBut = findViewById(R.id.chatButton);

        JSON_HTTP_CALL();

        shopBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemDetailActivity.this,ShopViewActivity.class);
                intent.putExtra("rid",rid);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent,123);
               // finish();
            }
        });

        chatBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemDetailActivity.this,ChatActivity.class);
                intent.putExtra("UID",rid);
                intent.putExtra("username",shopOwnerName);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


        ImageCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImage(coverURL);
                viewPhotoDialog.show();
                currentindex=0;
            }
        });
        Image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImage(img1URL);
                viewPhotoDialog.show();
                currentindex=1;
            }
        });
        Image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImage(img2URL);
                viewPhotoDialog.show();
                currentindex=2;
            }
        });
        Image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImage(img3URL);
                viewPhotoDialog.show();
                currentindex=3;
            }
        });
        Image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImage(img4URL);
                viewPhotoDialog.show();
                currentindex=4;
            }
        });

        RetailerProfilePic=findViewById(R.id.retailerProfilePic);
        RetailerProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemDetailActivity.this,ShopViewActivity.class);
                intent.putExtra("rid",rid);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        Button addToCartBut = findViewById(R.id.addToCartButton);




        //prodCode = Objects.requireNonNull(i.getExtras()).getString("prodCode");
        discountText.bringToFront();


        createAddToCartDialog();
//
        progressDialog = ProgressDialog.show(this,getResources().getString(R.string.loadItemDetail),getResources().getString(R.string.pleaseWait),false,true);

        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(!loaded){
                    finish();
                    Toast.makeText(ItemDetailActivity.this,R.string.itemDetailFail,Toast.LENGTH_SHORT).show();
                }
            }
        });



//        progressDialog=new ProgressDialog(getActivity());
//        progressDialog.setCancelable(true);
//        progressDialog.setTitle(R.string.loading);
//        progressDialog.setMessage(getResources().getString(R.string.preparingCart));
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


        placeCartDialog= new ProgressDialog(ItemDetailActivity.this);
        placeCartDialog.setCancelable(true);
        placeCartDialog.setTitle(getResources().getString(R.string.addingTocart));
        placeCartDialog.setMessage(getResources().getString(R.string.pleaseWait));
        placeCartDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


       // placeCartDialog.show(ItemDetailActivity.this,getResources().getString(R.string.addingTocart),getResources().getString(R.string.pleaseWait),false,true);
        placeCartDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(!doneplaceorder){

                    Toast.makeText(ItemDetailActivity.this,R.string.addingToCartBackground,Toast.LENGTH_SHORT).show();
                }
            }
        });
        createViewPhotoDialog();


        addToCartBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                addToCartDialog.show();

            }
        });

        JSON_CALL_LIST();

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
                        recyclerView.setVisibility(View.INVISIBLE);
                    }else{


                        recyclerView.setVisibility(View.VISIBLE);
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

                HashMapParams.put("getReview",prodCode);
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
            ItemReview x=new ItemReview();
            JSONObject json;
            json=jarr.getJSONObject(a);

            x.setCustName(json.getString("custName"));
            x.setRatingStar(json.getString("star"));
            x.setRatingTime(json.getString("ratingTime"));
            x.setProdvariant(json.getString("prodVariant"));
            x.setComment(json.getString("comment"));

            orderList.add(x);
        }

        recyclerView.setAdapter(new ItemReviewAdapter(orderList, this));

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

    //request update to server
    private void JSON_HTTP_CALL() {

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //placeOrderDialog.show();
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                try {
                    ParseJSonResponse(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Crashlytics.logException(e);
                    Toast.makeText(ItemDetailActivity.this,R.string.itemNA,Toast.LENGTH_SHORT).show();
                    finish();
                    // handle your exception here!
                }
                Log.d("Response", response);
            }

            @Override
            protected String doInBackground(Void... params) {
                UploadProcess ProcessClass = new UploadProcess();

                HashMap<String,String> HashMapParams = new HashMap<>();

                HashMapParams.put("viewProdKey", prodCode);
                return ProcessClass.HttpRequest(PHPURL, HashMapParams);
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClass = new AsyncTaskUploadClass();
        AsyncTaskUploadClass.execute();

    }


    //get response string and set into recyclerView
    private void ParseJSonResponse(String array) throws JSONException {
       // product = new Product();
        JSONArray jarr = new JSONArray(array);//lv 1 array
        JSONArray json;
        json = jarr.getJSONArray(0);
        // get the prodcode
        JSONObject codeJson;
        codeJson=json.getJSONObject(0);
        getprodcode = codeJson.getString("prodcode");

        //JSONArray innerJarr=new JSONArray(json);
        JSONObject innerJson;
        int totalQty=0,maxDiscount;
        double minprice,maxprice;

        minprice=Double.parseDouble(json.getJSONObject(0).getString("discountedPRice"));
        maxprice=Double.parseDouble(json.getJSONObject(0).getString("discountedPRice"));
        maxDiscount=Integer.parseInt(json.getJSONObject(0).getString("proddiscount"));

        for(int a=0;a<json.length();a++){

            innerJson=json.getJSONObject(a);
            itemName.setText(innerJson.getString("prodname"));
            getProdName = innerJson.getString("prodname");
            itemPrice.setText("RM"+innerJson.getString("prodprice"));
            itemPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

            rid=innerJson.getString("rid");
            itemSize.setText(innerJson.getString("prodSize"));
            shopName.setText(innerJson.getString("shopName"));
            shopName2.setText(innerJson.getString("shopName"));
            shopOwnerName = innerJson.getString("shopOwner");
            shopOwner.setText(innerJson.getString("shopOwner"));
            prodCategory.setText(innerJson.getString("prodcategory"));
            getProdCategory = innerJson.getString("prodcategory");
            prodDescription.setText(innerJson.getString("proddesc"));

            shopDescription.setText(innerJson.getString("shopDesc"));

            if(Integer.parseInt(json.getJSONObject(a).getString("proddiscount"))>maxDiscount){
                maxDiscount=Integer.parseInt(json.getJSONObject(a).getString("proddiscount"));
            }
            if(Double.parseDouble(innerJson.getString("proddiscount"))==0){
                discountText.setVisibility(View.GONE);
                itemPrice.setVisibility(View.GONE);
            }
            if(Double.parseDouble(innerJson.getString("discountedPRice"))>maxprice){
                maxprice=Double.parseDouble(innerJson.getString("discountedPRice"));
            }
            if(Double.parseDouble(innerJson.getString("discountedPRice"))<minprice){
                minprice=Double.parseDouble(innerJson.getString("discountedPRice"));
            }
            //discountedPRice

            try {
                Glide.with(ItemDetailActivity.this)
                        .load(innerJson.getString("shopProfilePicURL")) // image url
                        .apply(new RequestOptions()
                        .placeholder(R.drawable.photo) // any placeholder to load at start
                        .error(R.drawable.photo)  // any image in case of error
                        .override(200, 200) // resizing
                        .centerCrop())
                        .into(RetailerProfilePic);
            }catch (Exception e){
                Log.d("Glide error",e.toString());
            }

            Product x=new Product();
            x.setProductVariant(innerJson.getString("prodvariant"));
            int chkQty=Integer.parseInt(innerJson.getString("prodvarqty"));
            if(chkQty<1){
                chkQty=Integer.parseInt(innerJson.getString("prodTotalQty"));
            }
            x.setProdVariantQty(chkQty);
            DecimalFormat decim = new DecimalFormat("0.00");
            Double formatPrice=Double.parseDouble(innerJson.getString("prodprice"));
            x.setProdVariantPrice(Double.parseDouble(decim.format(formatPrice)));
            x.setDiscountedPrice(innerJson.getString("discountedPRice"));
            itemDetailModel.add(x);
            int currentqty=chkQty;
            totalQty=currentqty+totalQty;
            x.setProdDiscount(Integer.parseInt(innerJson.getString("proddiscount")));

        }
        itemQuantity.setText(Integer.toString(totalQty));
        DecimalFormat df2 = new DecimalFormat("0.00");
        if(minprice==maxprice){
            discountedPrice.setText("RM"+df2.format(minprice));
        }else{
            discountedPrice.setText("RM"+df2.format(minprice)+" - RM"+df2.format(maxprice));
        }

        discountText.setText("-"+Integer.toString(maxDiscount)+"%");

        final CharSequence[] item = getResources().getStringArray(R.array.target_array);

        json = jarr.getJSONArray(1);

        int count = 0;
        for (int i = 0; i < json.length(); i++) {
            innerJson=json.getJSONObject(i);
            Log.d("URL",innerJson.getString("imageurl"));
            try {
                switch (count) {
                    case 0:
                        ImageID.add(innerJson.getString("imageid"));

                        if(!innerJson.getString("imageurl").equalsIgnoreCase("null")){

                                Glide.with(ItemDetailActivity.this)
                                        .load(innerJson.getString("imageurl")) // image url
                                        .apply(new RequestOptions()
                                        .placeholder(R.drawable.photo) // any placeholder to load at start
                                        .error(R.drawable.photo)  // any image in case of error
                                        .override(200, 200) // resizing
                                        .centerCrop())
                                        .into(ImageCover);
                                coverURL = innerJson.getString("imageurl");
                                Glide.with(ItemDetailActivity.this)
                                        .load(coverURL) // image url
                                        .apply(new RequestOptions()
                                        .placeholder(R.drawable.photo) // any placeholder to load at start
                                        .error(R.drawable.photo)  // any image in case of error
                                        .override(200, 200) // resizing
                                        .centerCrop())
                                        .into(itemDetailImage);

                        }else{
                            ImageCover.setVisibility(View.GONE);
                            maxImgCount--;
                        }
                        break;
                    case 1:
                        ImageID.add(innerJson.getString("imageid"));

                        if(!innerJson.getString("imageurl").equalsIgnoreCase("null")) {
                            Glide.with(ItemDetailActivity.this)
                                    .load(innerJson.getString("imageurl")) // image url
                                    .apply(new RequestOptions()
                                    .placeholder(R.drawable.photo) // any placeholder to load at start
                                    .error(R.drawable.photo)  // any image in case of error
                                    .override(200, 200) // resizing
                                    .centerCrop())
                                    .into(Image1);
                            img1URL=innerJson.getString("imageurl");

                        }else{
                            Image1.setVisibility(View.GONE);
                            maxImgCount--;
                        }

                        break;
                    case 2:
                        ImageID.add(innerJson.getString("imageid"));

                        if(!innerJson.getString("imageurl").equalsIgnoreCase("null")) {
                            Glide.with(ItemDetailActivity.this)
                                    .load(innerJson.getString("imageurl")) // image url
                                    .apply(new RequestOptions()
                                    .placeholder(R.drawable.photo) // any placeholder to load at start
                                    .error(R.drawable.photo)  // any image in case of error
                                    .override(200, 200) // resizing
                                    .centerCrop())
                                    .into(Image2);
                            img2URL=innerJson.getString("imageurl");

                        }else{
                            Image2.setVisibility(View.GONE);
                            maxImgCount--;
                        }
                        break;
                    case 3:
                        ImageID.add(innerJson.getString("imageid"));

                        if(!innerJson.getString("imageurl").equalsIgnoreCase("null")) {

                            Glide.with(ItemDetailActivity.this)
                                    .load(innerJson.getString("imageurl")) // image url
                                    .apply(new RequestOptions()
                                    .placeholder(R.drawable.photo) // any placeholder to load at start
                                    .error(R.drawable.photo)  // any image in case of error
                                    .override(200, 200) // resizing
                                    .centerCrop())
                                    .into(Image3);

                            img3URL=innerJson.getString("imageurl");
                        }else{
                            Image3.setVisibility(View.GONE);
                            maxImgCount--;
                        }
                        break;
                    case 4:
                        ImageID.add(innerJson.getString("imageid"));

                        if(!innerJson.getString("imageurl").equalsIgnoreCase("null")) {
                            Glide.with(ItemDetailActivity.this)
                                    .load(innerJson.getString("imageurl")) // image url
                                    .apply(new RequestOptions()
                                    .placeholder(R.drawable.photo) // any placeholder to load at start
                                    .error(R.drawable.photo)  // any image in case of error
                                    .override(200, 200) // resizing
                                    .centerCrop())
                                    .into(Image4);

                            img4URL=innerJson.getString("imageurl");
                        }else{

                            Image4.setVisibility(View.GONE);
                            maxImgCount--;
                        }
                        break;
                    default:
                        break;
                }
                count++;
            } catch (JSONException e) {
                e.printStackTrace();
                Crashlytics.logException(e);
                // handle your exception here!
            }catch (Exception e){
            Log.d("Glide error",e.toString());
            }
        }

        json = jarr.getJSONArray(2);
        innerJson=json.getJSONObject(0);
        soldQtyTxt.setText(innerJson.getString("soldQty")+getResources().getString(R.string.unit));
       // ;asdasdasdasdasdasdasdasda

        loaded=true;
        progressDialog.dismiss();

        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName(getProdName)
                .putContentType(getProdCategory)
                .putContentId(getprodcode));
        //Toast.makeText(ItemDetailActivity.this,rid,Toast.LENGTH_LONG).show();

    }


    public  boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }


    //add to cart dialog setting here
    private void createAddToCartDialog(){
        addToCartDialog = new Dialog(ItemDetailActivity.this, R.style.MaterialDialogSheet);
        addToCartDialog.setContentView(R.layout.add_to_cart_layout); // your custom view.
        addToCartDialog.setCancelable(true);
        Objects.requireNonNull(addToCartDialog.getWindow()).setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        addToCartDialog.getWindow().setGravity(Gravity.BOTTOM);

        Button increaseBut = addToCartDialog.findViewById(R.id.increaseQtyBut);
        Button decreaseBut = addToCartDialog.findViewById(R.id.buttondecreaseQtyBut);
        confirmAddToCart = addToCartDialog.findViewById(R.id.confirmAddToCart);
        checkoutBut=addToCartDialog.findViewById(R.id.checkoutNowButton);
        itemDetailImage= addToCartDialog.findViewById(R.id.itemDetailImage);
        selectedQty=addToCartDialog.findViewById(R.id.selectedQuantity);
        itemDetailQty=addToCartDialog.findViewById(R.id.itemDetailQuantityText);
        itemDetailPrice=addToCartDialog.findViewById(R.id.itemDetailPriceText);
        itemDetailDiscountedPrice=addToCartDialog.findViewById(R.id.itemDetailDiscountedPriceText);
        RecyclerView recyclerView = addToCartDialog.findViewById(R.id.recyclerviewItemVariation);
        itemDetailDiscountPriceText=addToCartDialog.findViewById(R.id.itemDetailDiscountPriceText);
        ImageButton closeBut=addToCartDialog.findViewById(R.id.closeItemDetailBut);

        itemDetailImage.bringToFront();

        itemDetailQty.setText(R.string.chooseAProductVariant);
        itemDetailPrice.setText(R.string.chooseAProductVariant);
        itemDetailPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        itemDetailDiscountedPrice.setText(R.string.chooseAProductVariant);
        itemDetailDiscountPriceText.setVisibility(View.GONE);
        itemDetailPrice.setVisibility(View.GONE);


        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManagerOfrecyclerView = new LinearLayoutManager(ItemDetailActivity.this);
        recyclerView.setLayoutManager(layoutManagerOfrecyclerView);


        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            final GestureDetector gestureDetector = new GestureDetector(ItemDetailActivity.this, new GestureDetector.SimpleOnGestureListener() {

                @Override public boolean onSingleTapUp(MotionEvent motionEvent) {
                    //this function to detect is scrolling
                    return true;
                }

            });
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                view = rv.findChildViewUnder(e.getX(), e.getY());
                if(view != null && gestureDetector.onTouchEvent(e)) {


                    if (rv.getChildAdapterPosition(view) >= 0 && rv.getChildAdapterPosition(view) < itemDetailModel.size()) {
                        RecyclerViewItemPosition = rv.getChildAdapterPosition(view);
                        confirmAddToCart.setEnabled(true);
                        checkoutBut.setEnabled(true);
                    }
                    selectedProductVariation = itemDetailModel.get(RecyclerViewItemPosition).getProductVariant();//selected item variant
                    itemDetailQty.setText(Integer.toString(itemDetailModel.get(RecyclerViewItemPosition).getProdVariantQty())); //quantify available for that variant
                    itemDetailPrice.setText("RM " + Double.toString(itemDetailModel.get(RecyclerViewItemPosition).getProdVariantPrice()));  //price for that variant
                    itemDetailDiscountedPrice.setText("RM "+itemDetailModel.get(RecyclerViewItemPosition).getDiscountedPrice());
                    getProdPrice=itemDetailModel.get(RecyclerViewItemPosition).getDiscountedPrice();
                    itemDetailDiscountPriceText.setText("-"+Integer.toString(itemDetailModel.get(RecyclerViewItemPosition).getProdDiscount())+"% off");
                    selectedQty.setText("1");
                    if(itemDetailModel.get(RecyclerViewItemPosition).getProdDiscount()==0){
                        itemDetailDiscountPriceText.setVisibility(View.GONE);
                        itemDetailPrice.setVisibility(View.GONE);
                    }else{
                        itemDetailDiscountPriceText.setVisibility(View.VISIBLE);
                        itemDetailPrice.setVisibility(View.VISIBLE);
                    }
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        RecyclerView.Adapter recyclerViewadapter = new ItemDetailViewAdapter(itemDetailModel, ItemDetailActivity.this);
        recyclerView.setAdapter(recyclerViewadapter);


        closeBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCartDialog.dismiss();
            }
        });


        confirmAddToCart.setEnabled(false);
        checkoutBut.setEnabled(false);
        confirmAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart();
            }
        });

        increaseBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty=Integer.parseInt(selectedQty.getText().toString());
                if(qty<itemDetailModel.get(RecyclerViewItemPosition).getProdVariantQty()) {
                    qty++;
                }
                selectedQty.setText(Integer.toString(qty));
            }
        });

        decreaseBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty=Integer.parseInt(selectedQty.getText().toString());
                if(qty>1) {
                    qty--;
                }
                selectedQty.setText(Integer.toString(qty));
            }
        });


        checkoutBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart();
                addToCartDialog.dismiss();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("checkout", "111");

                setResult(ItemDetailActivity.RESULT_OK, resultIntent);
                finish();



                finish();

            }
        });
    }

    private void createViewPhotoDialog(){
        viewPhotoDialog = new Dialog(ItemDetailActivity.this, R.style.MaterialDialogSheet);
        viewPhotoDialog.setContentView(R.layout.photo_layout); // your custom view.
        viewPhotoDialog.setCancelable(true);
        Objects.requireNonNull(viewPhotoDialog.getWindow()).setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        viewPhotoDialog.getWindow().setGravity(Gravity.CENTER);

        viewImage=viewPhotoDialog.findViewById(R.id.viewImage);
        Button viewleft = viewPhotoDialog.findViewById(R.id.viewleft);
        Button viewright = viewPhotoDialog.findViewById(R.id.viewright);


        viewleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentindex>0){
                    rollDie();
                    currentindex--;
                    switch (currentindex){
                        case 0:
                            loadImage(coverURL);
                            break;
                        case 1:
                            loadImage(img1URL);
                            break;
                        case 2:
                            loadImage(img2URL);
                            break;
                        case 3:
                            loadImage(img3URL);
                            break;
                        case 4:
                            loadImage(img4URL);
                            break;
                        default:
                                break;
                    }

                }
            }
        });


        viewright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("max photo:",Integer.toString(maxImgCount));
                Log.d("max photo:",Integer.toString(maxImgCount));
                Log.d("max photo:",Integer.toString(maxImgCount));
                if(currentindex<maxImgCount){
                    rollDie();
                    currentindex++;
                    switch (currentindex){
                        case 0:
                            loadImage(coverURL);
                            break;
                        case 1:
                            loadImage(img1URL);
                            break;
                        case 2:
                            loadImage(img2URL);
                            break;
                        case 3:
                            loadImage(img3URL);
                            break;
                        case 4:
                            loadImage(img4URL);
                            break;
                        default:
                            break;
                    }

                }
            }
        });
        //((BitmapDrawable)itemDetailImage.getDrawable()).getBitmap()
        viewPhotoDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Glide.with(ItemDetailActivity.this).pauseRequestsRecursive();
                //Toast.makeText(ItemDetailActivity.this,"glide load cancelled",Toast.LENGTH_SHORT).show();
                Glide.with(ItemDetailActivity.this).resumeRequestsRecursive();
            }
        });
    }
    private void loadImage(String url){
        try{
            Glide.with(ItemDetailActivity.this)
                    .load(url) // image url
                    .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
                    .apply(new RequestOptions()
                    .placeholder(R.drawable.photo) // any placeholder to load at start
                    .error(R.drawable.photo)  // any image in case of error
                    .override(1000, 1000) // resizing
                    .fitCenter())
                    .into(viewImage);
            viewImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        }catch (Exception e){
            Log.d("Glide error",e.toString());
        }

    }
    private void rollDie() {
        // Setup the animation.
        Animation shake = AnimationUtils.loadAnimation(getApplicationContext(),
                android.R.anim.fade_in);

        viewImage.setAnimation(shake);

        shake.start();

    }
    private void addToCart(){
        //here
        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                placeCartDialog.show();
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                Log.e("Response",response);
                doneplaceorder=true;
                addToCartDialog.dismiss();
                if(placeCartDialog.isShowing()){
                    placeCartDialog.dismiss();
                }

                //Event for crashlytic analysis
                // Answers.getInstance().logAddToCart(new AddToCartEvent());
                Answers.getInstance().logAddToCart(new AddToCartEvent()
                        .putItemId(getprodcode)
                        .putItemName(getProdName)
                        .putItemType(getProdCategory)
                        .putItemPrice(new BigDecimal(getProdPrice))
                        .putCustomAttribute("Quantity",selectedQty.getText().toString()));
                Toast.makeText(ItemDetailActivity.this,R.string.itemAddedToCart,Toast.LENGTH_SHORT).show();
                // Setting image as transparent after done uploading.
                //imageView.setImageResource(android.R.color.transparent);
            }
            @Override
            protected String doInBackground(Void... params) {
                Log.e("final ", selectedProductVariation);
                CartDetails.add(uid);
                CartDetails.add(getprodcode);
                CartDetails.add(selectedProductVariation);
                CartDetails.add(selectedQty.getText().toString());

                JSONObject JSONCart = new JSONObject();
                JSONObject EverythingJSON = new JSONObject();




                for (int i = 0; i < CartDetails.size(); i++) {
                    try {
                        JSONCart.put("CartDetails:" + String.valueOf(i + 1), CartDetails.get(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    EverythingJSON.put("cart",JSONCart);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                UploadProcess ProcessClass = new UploadProcess();

                HashMap<String,JSONObject> HashMapParams = new HashMap<>();
                HashMapParams.put(addCartPathFieldOnServer, EverythingJSON);//addCartPathFieldOnServer=='addcart'
                String FinalData = ProcessClass.HttpRequestObject(AddCartToServerPath, HashMapParams);
                CartDetails.clear();
                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

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

}
