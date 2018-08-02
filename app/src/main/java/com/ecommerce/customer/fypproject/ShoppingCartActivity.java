package com.ecommerce.customer.fypproject;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ecommerce.customer.fypproject.adapter.CartItem;
import com.ecommerce.customer.fypproject.adapter.CartItemAdapter;
import com.ecommerce.customer.fypproject.adapter.OnItemClick;
import com.ecommerce.customer.fypproject.adapter.UploadProcess;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.StartCheckoutEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShoppingCartActivity extends Fragment implements OnItemClick{
    private  RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private CheckBox selectAllChkBox;
    private TextView subtotal,orderDialogText,orderDialogAnswer,emptyText;
    private ImageView emptyImg;
    private Button finalChkOutBtn;
    private String uid;
    private List<CartItem> cartItemList;
    private final String PHPURL="https://ecommercefyp.000webhostapp.com/retailer/customer_function.php";
    //String PHPURL="http://10.0.2.2/cashierbookPHP/Eric/customer_function.php";

    private CartItemAdapter cartAdapter;
    private View view ;
    int RecyclerViewItemPosition;
    double cartsubtotal=0;
    private ProgressDialog progressDialog,deleteDialog,placeOrderDialog;
    private boolean isEmpty=false;
    private SwipeRefreshLayout refreshLayout;
    private boolean doneLoading=false;
    private final DecimalFormat df2 = new DecimalFormat("0.00");
    private Dialog orderDialog;
    private FirebaseAuth firebaseAuth;


    public ShoppingCartActivity() {
        // Required empty public constructor
    }


    @Override
    public void onClick(String value) {
        //value receive
        if(value.equalsIgnoreCase("refresh")){
            deleteDialog.show();
        }else if(value.equalsIgnoreCase("uncheck")){
            selectAllChkBox.setChecked(false);
        }else if(value.equalsIgnoreCase("check")) {
            selectAllChkBox.setChecked(true);
        }else if(value.equalsIgnoreCase("done refresh")){
            JSON_HTTP_CALL();
            callToast(getResources().getString(R.string.itemRemovedFromCart));

            Log.d("delete but:","Item removed from cart");
        }else{
            subtotal.setText("RM"+df2.format(Double.parseDouble(value)));
            String getsubtotal = df2.format(Double.parseDouble(value));
            if(Double.parseDouble(value)==0){
                finalChkOutBtn.setEnabled(false);
            }else{
                finalChkOutBtn.setEnabled(true);
            }
        }


    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        selectAllChkBox.setChecked(false);
        finalChkOutBtn.setEnabled(false);
        super.onAttachFragment(childFragment);
        JSON_HTTP_CALL();
    }

    @Override
    public void onResume() {
        selectAllChkBox.setChecked(false);
        finalChkOutBtn.setEnabled(false);
        super.onResume();
        JSON_HTTP_CALL();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_shopping_cart, container, false);
        GetFirebaseAuth();
        recyclerView=rootView.findViewById(R.id.shoppingCartRecycleView);
        selectAllChkBox=rootView.findViewById(R.id.shopCartSelectAllChkBox);
        subtotal=rootView.findViewById(R.id.subTotal);
        finalChkOutBtn=rootView.findViewById(R.id.finalChkOutBtn);
        refreshLayout=rootView.findViewById(R.id.cartRefresh);
        emptyImg=rootView.findViewById(R.id.shopCartEmptyImg);
        emptyText=rootView.findViewById(R.id.shopCartEmptyText);

        refreshLayout.setColorSchemeResources(R.color.orange);
        cartItemList=new ArrayList<>();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                JSON_HTTP_CALL();
                refreshLayout.setRefreshing(false);

            }
        });
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManagerOfrecyclerView = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManagerOfrecyclerView);

        //progressDialog = ProgressDialog.show(getActivity(),"Preparing your cart","Please Wait",false,false);
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setCancelable(true);
        progressDialog.setTitle(R.string.loading);
        progressDialog.setMessage(getResources().getString(R.string.preparingCart));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        deleteDialog=new ProgressDialog(getActivity());
        deleteDialog.setCancelable(false);
        deleteDialog.setTitle(R.string.deleteItem);
        deleteDialog.setMessage(getResources().getString(R.string.pleaseWait));
        deleteDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        placeOrderDialog=new ProgressDialog(getActivity());
        placeOrderDialog.setCancelable(false);
        placeOrderDialog.setTitle("Placing your order");
        placeOrderDialog.setMessage("Please wait...");
        placeOrderDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        createOrderDialog();
        //JSON_HTTP_CALL();
        selectAllChkBox.setChecked(false);

        selectAllChkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cartAdapter==null||!doneLoading){
                    JSON_HTTP_CALL();
                }else {
                    cartAdapter.chkAll(selectAllChkBox.isChecked());
                    subtotal.setText("RM" + df2.format(cartAdapter.calculateTotal()) );
                    if (selectAllChkBox.isChecked() && isEmpty) {
                        finalChkOutBtn.setEnabled(true);
                    } else {
                        finalChkOutBtn.setEnabled(false);
                    }
                }

            }
        });

        finalChkOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getContext(),"UID"+ Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail(),Toast.LENGTH_SHORT).show();
                int getItemCount;
                if(cartAdapter==null||!doneLoading){
                    JSON_HTTP_CALL();
                }else {
                    boolean[] checkList= cartAdapter.getCheckedList();
                    List<CartItem> returnedCartItem=cartAdapter.getCartItemList(); //checked item get from adapter, further code implementation please refer "CartItemAdapter.java"

                    JSONArray JSONUploadArray=new JSONArray();

                    placeOrderDialog.show();
                    getItemCount = checkList.length;
                    Answers.getInstance().logStartCheckout(new StartCheckoutEvent()
                            .putCurrency(Currency.getInstance("MYR"))
                            .putTotalPrice(new BigDecimal(df2.format(cartAdapter.calculateTotal())))
                            .putItemCount(getItemCount));
                    for(int a=0;a<checkList.length;a++){
                        if(checkList[a]){  //if checked the item
                            Log.d("order qty",returnedCartItem.get(a).getCartQty());
                            Log.d("order cartID",returnedCartItem.get(a).getCartID());
                            Log.d("order prodVar",returnedCartItem.get(a).getProdVariant());

                            //get quantity
                            //cartID
                            //product variant
                            JSONObject JSONPlaceOrderProdcut = new JSONObject();
                            try {
                                JSONPlaceOrderProdcut.put("cartQty", returnedCartItem.get(a).getCartQty());
                                JSONPlaceOrderProdcut.put("cartID", returnedCartItem.get(a).getCartID());
                                JSONPlaceOrderProdcut.put("prodVariant", returnedCartItem.get(a).getProdVariant());

                                JSONUploadArray.put(JSONPlaceOrderProdcut);


                            }catch (JSONException e){
                                Crashlytics.logException(e);
                                // handle your exception here!
                                e.printStackTrace();
                            }
                        }
                    }
                    //JSON_HTTP_SEND("cartList",JSONUploadArray);
//intent here
                    String orderList=JSONUploadArray.toString();

//                SharedPreferences pref = getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
//                SharedPreferences.Editor editor = pref.edit();
//                editor.clear();
//                editor.putString("shop_cart_list", orderList); //save the string array in share preference and will use it when user done ipay88 payment
//                editor.commit();
                    JSON_HTTP_SEND(orderList);
                    JSON_HTTP_CALL();

//                Intent addressDelivery = new Intent(getActivity(), AddressDeliveryActivity.class);
//                addressDelivery.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(addressDelivery);
                }

            }
        });
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        return rootView;
    }

    private void JSON_HTTP_SEND(final String orderList) {

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                Log.e("Response",response);
                String returnMsg=response.replace("</br>","\n");
                returnMsg=returnMsg.replace("</t>","\t\tx");
                orderDialogText.setText(returnMsg);
                orderDialog.show();
                placeOrderDialog.dismiss();
            }

            @Override
            protected String doInBackground(Void... params) {
                UploadProcess ProcessClass = new UploadProcess();

                HashMap<String,String> HashMapParams = new HashMap<>();
                HashMapParams.put("placeorderitem", orderList);

                return ProcessClass.HttpRequest(PHPURL, HashMapParams);
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClass = new AsyncTaskUploadClass();
        AsyncTaskUploadClass.execute();
    }

    public void JSON_HTTP_CALL() {

        progressDialog.show();
        selectAllChkBox.setChecked(false);
        finalChkOutBtn.setEnabled(false);

        subtotal.setText("RM0.00");
        cartItemList=new ArrayList<>();
        cartAdapter=null;
        doneLoading=false;

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
                        isEmpty=false;
                        emptyImg.setVisibility(View.VISIBLE);
                        emptyText.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                    }else{
                        isEmpty=true;
                        emptyImg.setVisibility(View.INVISIBLE);
                        emptyText.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    ParseJSonResponse(response);
                } catch (JSONException e) {
                    Crashlytics.logException(e);
                    // handle your exception here!
                    e.printStackTrace();
                }
                Log.d("Response", response);
            }

            @Override
            protected String doInBackground(Void... params) {
                UploadProcess ProcessClass = new UploadProcess();

                HashMap<String,String> HashMapParams = new HashMap<>();

                HashMapParams.put("custID", uid);
                return ProcessClass.HttpRequest(PHPURL, HashMapParams);
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClass = new AsyncTaskUploadClass();
        AsyncTaskUploadClass.execute();

    }

    //get response string and set into recyclerView
    private void ParseJSonResponse(String array) throws JSONException {
        cartItemList=new ArrayList<>();
        cartAdapter=null;
        JSONArray jarr = new JSONArray(array);//lv 1 array
       
        int totalQty=0;
        for(int a=0;a<jarr.length();a++){
            CartItem x=new CartItem();
            JSONObject json;
            json=jarr.getJSONObject(a);

            x.setProdVariant(json.getString("prodVariant"));
            x.setProdprofURL(json.getString("prodImgURL"));
            x.setProdName(json.getString("prodName"));
            x.setProdPrice(json.getString("prodPrice"));
            x.setCartQty(json.getString("cartQty"));
            x.setCartID(json.getString("cartID"));
            x.setRetailerProfPicURL(json.getString("retailerProfPicURL"));
            x.setRetailerShopName(json.getString("retailerShopName"));
            x.setLimitqty(json.getString("limitqty"));
            x.setDiscount(json.getString("discount"));
            x.setDiscountedPrice(json.getString("discountPrice"));
            x.setProdcode(json.getString("prodcode"));
            cartItemList.add(x);
        }



        cartAdapter= new CartItemAdapter(cartItemList, getActivity(),this);
        RecyclerView.Adapter recyclerViewadapter = cartAdapter;


        recyclerView.setAdapter(recyclerViewadapter);

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
        doneLoading=true;
        if(deleteDialog.isShowing()){

            subtotal.setText(df2.format(cartAdapter.calculateTotal()));
            selectAllChkBox.setChecked(false);
            cartAdapter.notifyItemRangeChanged(0,cartItemList.size());
            deleteDialog.dismiss();
        }

    }

    private void callToast(String x){
        Toast.makeText(getActivity(),x,Toast.LENGTH_LONG).show();

    }

    private void GetFirebaseAuth(){
        firebaseAuth = FirebaseAuth.getInstance();
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

    private void createOrderDialog(){
        orderDialog = new Dialog(Objects.requireNonNull(getActivity()), R.style.MaterialDialogSheet);
        orderDialog.setContentView(R.layout.order_dialog); // your custom view.
        orderDialog.setCancelable(true);
        Objects.requireNonNull(orderDialog.getWindow()).setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        orderDialog.getWindow().setGravity(Gravity.CENTER);

        orderDialogText=orderDialog.findViewById(R.id.orderDialogItemText);
        Button orderDialogBut = orderDialog.findViewById(R.id.orderDialogOKBut);
        ImageView orderDialogQuestion = orderDialog.findViewById(R.id.orderDialogQuestion);
        orderDialogAnswer=orderDialog.findViewById(R.id.orderDialogAnswer);
        orderDialogAnswer.setVisibility(View.GONE);

        orderDialogBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderDialog.dismiss();
            }
        });

        orderDialogQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(orderDialogAnswer.getVisibility()==View.VISIBLE){
                    orderDialogAnswer.setVisibility(View.GONE);
                }else{
                    orderDialogAnswer.setVisibility(View.VISIBLE);
                }

            }
        });

    }
}
