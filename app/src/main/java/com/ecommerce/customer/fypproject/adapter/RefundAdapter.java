package com.ecommerce.customer.fypproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ecommerce.customer.fypproject.R;

import java.text.DecimalFormat;
import java.util.List;

public class RefundAdapter extends RecyclerView.Adapter<RefundAdapter.ViewHolder>  {
    private final List<Refund> dataAdapters;
    private final Context context;
    private final DecimalFormat df2 = new DecimalFormat("0.00");

    public  RefundAdapter(List<Refund> getDataAdapter, Context context){

        super();
        this.dataAdapters = getDataAdapter;
        this.context = context;

        boolean[] checked = new boolean[dataAdapters.size()];
        if(dataAdapters.size()>0){
            checked[0]=true;

        }

    }
    @Override
    public RefundAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.refund_card, parent, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RefundAdapter.ViewHolder Viewholder, final int position) {

        // checked=new boolean[dataAdapters.size()];
        final Refund dataAdapterOBJ = dataAdapters.get(position);
        // Viewholder.shopIcon.setImageBitmap(dataAdapterOBJ.getprofBitmap());
        // Viewholder.itemImage.setImageBitmap(dataAdapterOBJ.getCoverBitmap());
        Viewholder.shortSellDate.setText(dataAdapterOBJ.getShortSellDate());
        Viewholder.status.setText(R.string.processing);
        Viewholder.itemName.setText(dataAdapterOBJ.getRefundItemName());
        Viewholder.price.setText("RM"+df2.format(Double.parseDouble(dataAdapterOBJ.getRefundPrice())));

        Glide.with(context)
                .load(dataAdapterOBJ.getImgURL()) // image url
                .apply(new RequestOptions()
                        .placeholder(R.drawable.photo) // any placeholder to load at start
                        .error(R.drawable.photo)  // any image in case of error
                        .override(500, 500) // resizing
                        .centerCrop())
                .into(Viewholder.refundImage);  // imageview object

    }
    class ViewHolder extends RecyclerView.ViewHolder{

        final ImageView refundImage;
        final TextView shortSellDate;
        final TextView itemName;
        final TextView price;
        final TextView status;




        ViewHolder(View itemView) {

            super(itemView);

            refundImage=itemView.findViewById(R.id.refundImg);
            shortSellDate=itemView.findViewById(R.id.shortSellDate);
            itemName=itemView.findViewById(R.id.refundItemName);
            price=itemView.findViewById(R.id.refundPrice);
            status=itemView.findViewById(R.id.refundStatus);

        }
    }
    @Override
    public int getItemCount() {

        return dataAdapters.size();
    }
}
