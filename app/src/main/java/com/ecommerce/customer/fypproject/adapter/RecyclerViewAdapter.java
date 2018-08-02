package com.ecommerce.customer.fypproject.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ecommerce.customer.fypproject.R;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Eric on 02-Nov-17.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>  {

    private final Context context;

    private final List<Product> dataAdapters;

    ImageLoader imageLoader;

    private final boolean big;


    public RecyclerViewAdapter(List<Product> getDataAdapter, Context context,boolean big){

        super();
        this.dataAdapters = getDataAdapter;
        this.context = context;
        this.big=big;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(big) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_layout, parent, false);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_layout_small, parent, false);
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder Viewholder, int position) {


        Product dataAdapterOBJ = dataAdapters.get(position);
//        imageLoader = ImageAdapter.getInstance(context).getImageLoader();
//
//        imageLoader.get(dataAdapterOBJ.getProductURL(),
//                ImageLoader.getImageListener(
//                        Viewholder.itemCardImage,//Server Image
//                        R.mipmap.ic_photodefault,//Before loading server image the default showing image.
//                        android.R.drawable.ic_dialog_alert //Error image if requested image dose not found on server.
//                )
//        );


        Glide.with(context)
                .load(dataAdapterOBJ.getProductURL()) // image url
                .apply(new RequestOptions()
                .placeholder(R.drawable.photo) // any placeholder to load at start
                .error(R.drawable.photo)  // any image in case of error
                .override(200, 200) // resizing
                .centerCrop())
                .into(Viewholder.itemCardImage);

        //Viewholder.itemCardImage.setImageUrl(dataAdapterOBJ.getProductURL(), imageLoader);


        Viewholder.itemCardTitle.setText(dataAdapterOBJ.getProdName());



        DecimalFormat df2 = new DecimalFormat("0.00");
        double formatPrice=dataAdapterOBJ.getProdPrice();




        Viewholder.itemCardPrice.setText("RM "+df2.format(formatPrice));
        Viewholder.itemCardPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
//        if(dataAdapterOBJ.getProdPrice()==Double.parseDouble(dataAdapterOBJ.getDiscountedPrice())){
//
//        }
        Viewholder.itemCardSeller.setText(dataAdapterOBJ.getShopName());
        Viewholder.itemCardPromotion.setText("-"+Integer.toString(dataAdapterOBJ.getProdDiscount())+"%");
        if(dataAdapterOBJ.getProdDiscount()>0){
            Viewholder.itemCardPromotion.bringToFront();
            Viewholder.itemCardPromotion.setVisibility(View.VISIBLE);
            Viewholder.itemCardPrice.setVisibility(View.VISIBLE);
        }else {
            Viewholder.itemCardPromotion.setVisibility(View.INVISIBLE);
            Viewholder.itemCardPrice.setVisibility(View.INVISIBLE);
        }

        double discountPrice=Double.parseDouble(dataAdapterOBJ.getDiscountedPrice());

       Viewholder.discountedPrice.setText("RM "+df2.format(discountPrice));


    }

    @Override
    public int getItemCount() {

        return dataAdapters.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        final TextView itemCardTitle;
        final TextView itemCardPrice;
        final TextView itemCardSeller;
        final TextView itemCardPromotion;
        final TextView discountedPrice;
        final ImageView itemCardImage ;

        ViewHolder(View itemView) {

            super(itemView);
            itemCardTitle = itemView.findViewById(R.id.itemCardTitle);
            itemCardPrice = itemView.findViewById(R.id.itemCardPrice);
            itemCardSeller = itemView.findViewById(R.id.itemCardSeller);
            itemCardPromotion = itemView.findViewById(R.id.itemCardPromotion);
            itemCardImage =  itemView.findViewById(R.id.itemCardImage) ;
            discountedPrice=itemView.findViewById(R.id.discountedCardPrice);


        }
    }

    public void addList(List<Product> dataAdapter){
        dataAdapters.addAll(dataAdapter);
        notifyItemRangeChanged(0,dataAdapters.size());
    }
    public void refreshList(){
        notifyItemRangeChanged(0,dataAdapters.size());
    }
    public List<Product>getListData(){
        return dataAdapters;
    }

}
