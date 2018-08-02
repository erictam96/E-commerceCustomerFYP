package com.ecommerce.customer.fypproject.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ecommerce.customer.fypproject.R;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder>{
    private final Context context;

    private final List<OrderItem> dataAdapters;
    ItemDetailViewAdapter.ViewHolder currentView;



    public OrderItemAdapter(List<OrderItem> getDataAdapter, Context context){

        super();
        this.dataAdapters = getDataAdapter;
        this.context = context;

    }

    @Override
    public OrderItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OrderItemAdapter.ViewHolder Viewholder, int position) {


        OrderItem dataAdapterOBJ = dataAdapters.get(position);


        Viewholder.itemtext.setText(dataAdapterOBJ.getItemname());
        Viewholder.qtytxt.setText(context.getString(R.string.qty3)+dataAdapterOBJ.getQty());
        Viewholder.vartext.setText(context.getString(R.string.variant2)+dataAdapterOBJ.getVariant());
        Viewholder.ETAtext.setVisibility(View.GONE);
        Viewholder.lineTop.setVisibility(View.GONE);
        Viewholder.collectLocation.setVisibility(View.GONE);
         if(dataAdapterOBJ.getStatus().equalsIgnoreCase("ready to collect")){
            Viewholder.stattext.setTextColor(Color.GREEN);
             Viewholder.collectLocation.setVisibility(View.VISIBLE);
             Viewholder.collectLocation.setText(context.getResources().getString(R.string.collectLocation)+dataAdapterOBJ.getAddress());
            Viewholder.stattext.setText(R.string.readyToCollect);
        }else if(dataAdapterOBJ.getStatus().equalsIgnoreCase("ready to deliver")){
           // Viewholder.stattext.setTextColor(Color.GREEN);
             Viewholder.stattext.setTextColor(Color.GRAY);
            Viewholder.stattext.setText(R.string.readytodeliver2);
        }else if(dataAdapterOBJ.getStatus().equalsIgnoreCase("packing")){
           // Viewholder.stattext.setTextColor(Color.GREEN);
             Viewholder.stattext.setTextColor(Color.GRAY);
            Viewholder.stattext.setText(R.string.packing2);
        }else if(dataAdapterOBJ.getStatus().equalsIgnoreCase("delivered")){
             // Viewholder.stattext.setTextColor(Color.GREEN);
             Viewholder.stattext.setTextColor(context.getResources().getColor(R.color.orange));
             Viewholder.stattext.setText(R.string.delivered2);
         } else if(dataAdapterOBJ.getStatus().equalsIgnoreCase("Delivering")){
             // Viewholder.stattext.setTextColor(Color.GREEN);
             Viewholder.stattext.setTextColor(context.getResources().getColor(R.color.orange));
             Viewholder.stattext.setText(R.string.delivering);
             Viewholder.ETAtext.setVisibility(View.VISIBLE);
             Viewholder.lineTop.setVisibility(View.VISIBLE);
             Viewholder.ETAtext.setText(context.getString(R.string.ETA)+dataAdapterOBJ.getETA());
         }else{
             Viewholder.stattext.setTextColor(Color.GRAY);
            Viewholder.stattext.setText(dataAdapterOBJ.getStatus());
        }
        Glide.with(context)
                .load(dataAdapterOBJ.getUrl()) // image url
                .apply(new RequestOptions()
                        .placeholder(R.drawable.photo) // any placeholder to load at start
                        .error(R.drawable.photo)  // any image in case of error
                        .override(500, 500) // resizing
                        .centerCrop())
                .into(Viewholder.image);  // imageview object

        Viewholder.datetext.setText(dataAdapterOBJ.getDate());
        Viewholder.seller.setText(context.getString(R.string.seller)+dataAdapterOBJ.getSeller());
    }
    @Override
    public int getItemCount() {

        return dataAdapters.size();
    }





    class ViewHolder extends RecyclerView.ViewHolder{


        final TextView itemtext;
        final TextView qtytxt;
        final TextView vartext;
        final TextView stattext;
        final TextView datetext;
        final TextView seller;
        final TextView ETAtext;
        final ImageView image;
        final View lineTop;
        final TextView collectLocation;


        ViewHolder(View itemView) {

            super(itemView);

            itemtext=itemView.findViewById(R.id.itemText);
            qtytxt=itemView.findViewById(R.id.qtylisttext);
            vartext=itemView.findViewById(R.id.vartext);
            stattext=itemView.findViewById(R.id.stattext);
            datetext=itemView.findViewById(R.id.orderdatetext);
            image=itemView.findViewById(R.id.orderlistPic);
            seller=itemView.findViewById(R.id.sellertext);
            ETAtext=itemView.findViewById(R.id.ETAtext);
            lineTop=itemView.findViewById(R.id.line_top);
            collectLocation=itemView.findViewById(R.id.destinationAddress);


        }
    }
}
