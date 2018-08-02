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

import java.util.List;

public class PurchasedAdapter extends RecyclerView.Adapter<PurchasedAdapter.ViewHolder> {

    private final List<Purchased> dataAdapters;
    private final Context context;

    public  PurchasedAdapter(List<Purchased> getDataAdapter, Context context){

        super();
        this.dataAdapters = getDataAdapter;
        this.context = context;

        boolean[] checked = new boolean[dataAdapters.size()];
        if(dataAdapters.size()>0){
            checked[0]=true;

        }

    }
    @Override
    public PurchasedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchased_card, parent, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PurchasedAdapter.ViewHolder Viewholder, final int position) {

        final Purchased dataAdapterOBJ = dataAdapters.get(position);
        Viewholder.purchasedName.setText(dataAdapterOBJ.getPurchasedItemName());
        Viewholder.purchasedDate.setText(dataAdapterOBJ.getPurchasedDate());
        Glide.with(context)
                .load(dataAdapterOBJ.getImgURL()) // image url
                .apply(new RequestOptions()
                        .placeholder(R.drawable.photo) // any placeholder to load at start
                        .error(R.drawable.photo)  // any image in case of error
                        .override(500, 500) // resizing
                        .centerCrop())
                .into(Viewholder.purchasedPic);  // imageview object

    }
    class ViewHolder extends RecyclerView.ViewHolder{

        final ImageView purchasedPic;
        final TextView purchasedName;
        final TextView purchasedDate;

        ViewHolder(View itemView) {

            super(itemView);

            purchasedPic=itemView.findViewById(R.id.purchasedPic);
            purchasedName=itemView.findViewById(R.id.purchasedName);
            purchasedDate=itemView.findViewById(R.id.purchasedDate);


        }
    }
    @Override
    public int getItemCount() {

        return dataAdapters.size();
    }
}
