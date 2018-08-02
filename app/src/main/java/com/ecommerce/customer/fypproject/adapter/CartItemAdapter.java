package com.ecommerce.customer.fypproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ecommerce.customer.fypproject.ItemDetailActivity;
import com.ecommerce.customer.fypproject.R;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

/**
 * Created by leeyipfung on 3/16/2018.
 */

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder> {
    private final List<CartItem> dataAdapters;
    private final Context context;
    ItemDetailViewAdapter.ViewHolder currentView;
    //int[] currentQty;
    private final boolean[] checked;
    private final OnItemClick mCallback;
    boolean triggerChange=false;
    RequestQueue requestQueue ;
    private final String PHPURL="https://ecommercefyp.000webhostapp.com/retailer/customer_function.php";
    private final DecimalFormat df2 = new DecimalFormat("0.00");




    public  CartItemAdapter(List<CartItem> getDataAdapter, Context context, OnItemClick listener){

        super();
        this.dataAdapters = getDataAdapter;
        this.context = context;
        this.mCallback = listener;
        checked=new boolean[dataAdapters.size()];
    }
    @Override
    public CartItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CartItemAdapter.ViewHolder Viewholder, final int position) {

           // checked=new boolean[dataAdapters.size()];
            final CartItem dataAdapterOBJ = dataAdapters.get(position);
           // Viewholder.shopIcon.setImageBitmap(dataAdapterOBJ.getprofBitmap());
           // Viewholder.itemImage.setImageBitmap(dataAdapterOBJ.getCoverBitmap());
            Viewholder.shopName.setText(dataAdapterOBJ.getRetailerShopName());
            Viewholder.itemName.setText(dataAdapterOBJ.getProdName());

            Viewholder.itemPrice.setText("RM "+df2.format(Double.parseDouble(dataAdapterOBJ.getProdPrice())));
            Viewholder.itemPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

            Viewholder.itemQty.setText(dataAdapterOBJ.getCartQty());
            Viewholder.itemVariant.setText("Variant:" + dataAdapterOBJ.getProdVariant());
            Viewholder.deleteBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String cartID= dataAdapterOBJ.getCartID();
                    JSON_HTTP_CALL(cartID);
                    mCallback.onClick("refresh");
                }
            });
            Viewholder.stocklimitText.setText(dataAdapterOBJ.getLimitqty());
            if(Integer.parseInt(dataAdapterOBJ.getLimitqty())<=5){
                Viewholder.stocklimitText.setTextColor(context.getResources().getColor(R.color.scarletRed));
            }


        Glide.with(context)
                .load(dataAdapterOBJ.getRetailerProfPicURL()) // image url
                .apply(new RequestOptions()
                .placeholder(R.drawable.photo) // any placeholder to load at start
                .error(R.drawable.photo)  // any image in case of error
                .override(200, 200) // resizing
                .centerCrop())
                .into(Viewholder.shopIcon);  // imageview object


        Glide.with(context)
                .load(dataAdapterOBJ.getProdprofURL()) // image url
                .apply(new RequestOptions()
                .placeholder(R.drawable.photo) // any placeholder to load at start
                .error(R.drawable.photo)  // any image in case of error
                .override(200, 200) // resizing
                .centerCrop())
                .into(Viewholder.itemImage);  // imageview object

Viewholder.itemImage.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context,ItemDetailActivity.class);
        //Pack Data to Send
        intent.putExtra("prodCode",dataAdapterOBJ.getProdcode());
        //intent.putExtra("onlickListener",ClassUtils.getAllInterfaces);

        //open activity
        context.startActivity(intent);
    }
});


            //currentQty=new int[position+1];
            // checked=null;

            Viewholder.cartChkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    checked[Viewholder.getAdapterPosition()] = Viewholder.cartChkBox.isChecked();

                    if(loopChk()){
                        mCallback.onClick("check");
                    }else{
                        mCallback.onClick("uncheck");
                    }
                    mCallback.onClick(Double.toString(calculateTotal()));
                    //here adadasdasdasdasdasdasdasdsdasdasdas


                }
            });


            Viewholder.incBut.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View view) {

//                if(currentQty[position]==0){
//                    currentQty[position]=Integer.parseInt(dataAdapters.get(position).getCartQty());
//                }


                    int currentQty = Integer.parseInt(dataAdapters.get(Viewholder.getAdapterPosition()).getCartQty());
                    Log.d("currentQty:", Integer.toString(Viewholder.getAdapterPosition()));
                    if (Integer.parseInt(dataAdapters.get(Viewholder.getAdapterPosition()).getCartQty()) < Integer.parseInt(dataAdapters.get(Viewholder.getAdapterPosition()).getLimitqty())) {
                        currentQty = Integer.parseInt(dataAdapters.get(Viewholder.getAdapterPosition()).getCartQty());
                        currentQty++;
                        Viewholder.itemQty.setText(Integer.toString(currentQty));
                    }
                    dataAdapters.get(Viewholder.getAdapterPosition()).setCartQty(Integer.toString(currentQty));
                    mCallback.onClick(Double.toString(calculateTotal()));
                }
            });
            Viewholder.decBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int currentQty = Integer.parseInt(dataAdapters.get(Viewholder.getAdapterPosition()).getCartQty());
                    Log.d("currentQty:", Integer.toString(Viewholder.getAdapterPosition()));
                    if (Integer.parseInt(dataAdapters.get(Viewholder.getAdapterPosition()).getCartQty()) > 1) {
                        currentQty = Integer.parseInt(dataAdapters.get(Viewholder.getAdapterPosition()).getCartQty());
                        currentQty--;
                        Viewholder.itemQty.setText(Integer.toString(currentQty));
                    }
                    dataAdapters.get(Viewholder.getAdapterPosition()).setCartQty(Integer.toString(currentQty));
                    mCallback.onClick(Double.toString(calculateTotal()));

                }
            });


            Viewholder.cartChkBox.setChecked(checked[Viewholder.getAdapterPosition()]);
            Viewholder.txtDiscount.setText("-"+dataAdapterOBJ.getDiscount()+"% off");
            Viewholder.discountedtxtPrice.setText("RM"+df2.format(Double.parseDouble(dataAdapterOBJ.getDiscountedPrice())));


            if(Integer.parseInt(dataAdapterOBJ.getDiscount())==0){
                Viewholder.txtDiscount.setVisibility(View.INVISIBLE);
                Viewholder.itemPrice.setVisibility(View.INVISIBLE);
            }else{
                Viewholder.txtDiscount.setVisibility(View.VISIBLE);
                Viewholder.itemPrice.setVisibility(View.VISIBLE);
            }

    }



    private boolean cartIsChecked(int index){
        return checked[index];
    }
    public boolean[] getCheckedList(){
        return checked;
    }


    public List<CartItem> getCartItemList(){
        return dataAdapters;
    }
    @Override
    public int getItemCount() {

        return dataAdapters.size();
    }

    public void chkAll(boolean value){

     //   triggerChange=true;
        notifyItemRangeChanged(0,dataAdapters.size());
        for(int a=0;a<dataAdapters.size();a++){
            checked[a]=value;
            Log.d("trigger no.",Integer.toString(a)+Boolean.toString(checked[a]));
        }
        //triggerChange=false;

    }
    class ViewHolder extends RecyclerView.ViewHolder{
        final Button incBut;
        final Button decBut;
        final ImageView shopIcon;
        final ImageView itemImage;
        final TextView shopName;
        final TextView itemName;
        final TextView itemPrice;
        final TextView itemQty;
        final TextView itemVariant;
        final TextView subTotal;
        final TextView stocklimitText;
        final TextView txtDiscount;
        final TextView discountedtxtPrice;
        final CheckBox cartChkBox;
        final ImageButton deleteBut;


        ViewHolder(View itemView) {

            super(itemView);

            incBut=itemView.findViewById(R.id.cartIncQty);
            decBut=itemView.findViewById(R.id.cartDecQty);
            shopIcon=itemView.findViewById(R.id.cartShopIcon);
            itemImage=itemView.findViewById(R.id.VolleyImageView);
            shopName=itemView.findViewById(R.id.cartShopName);
            itemName=itemView.findViewById(R.id.txtTitle);
            itemPrice=itemView.findViewById(R.id.txtPrice);
            itemQty=itemView.findViewById(R.id.cartQty);
            itemVariant=itemView.findViewById(R.id.txtVariant);
            cartChkBox=itemView.findViewById(R.id.cartChkBox);
            subTotal=itemView.findViewById(R.id.subTotal);
            deleteBut=itemView.findViewById(R.id.removecartBut);
            stocklimitText=itemView.findViewById(R.id.stockLimitText);
            txtDiscount=itemView.findViewById(R.id.txtDiscount);
            discountedtxtPrice=itemView.findViewById(R.id.discountedtxtPrice);
        }
    }


    private void JSON_HTTP_CALL(final String cartID) {

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //placeOrderDialog.show();
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                mCallback.onClick("done refresh");

                Log.d("Response", response);
            }

            @Override
            protected String doInBackground(Void... params) {
                UploadProcess ProcessClass = new UploadProcess();

                HashMap<String,String> HashMapParams = new HashMap<>();

                HashMapParams.put("cartID", cartID);
                return ProcessClass.HttpRequest(PHPURL, HashMapParams);
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClass = new AsyncTaskUploadClass();
        AsyncTaskUploadClass.execute();


    }
    public double calculateTotal(){
        double cartsubtotal = 0;
        for(int a=0;a<dataAdapters.size();a++){
            if(cartIsChecked(a)){
                cartsubtotal = cartsubtotal +Integer.parseInt(dataAdapters.get(a).getCartQty())*Double.parseDouble(dataAdapters.get(a).getDiscountedPrice());
            }
        }


        Log.d("total",df2.format(cartsubtotal));
        return cartsubtotal;
    }


    private boolean loopChk(){
        for(int a=0;a<dataAdapters.size();a++){
            if(!cartIsChecked(a)){
                return false;
            }
        }
        return true;
    }
}
