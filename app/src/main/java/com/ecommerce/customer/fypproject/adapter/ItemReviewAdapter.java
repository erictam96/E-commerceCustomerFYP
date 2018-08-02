package com.ecommerce.customer.fypproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ecommerce.customer.fypproject.R;

import java.util.List;

public class ItemReviewAdapter extends RecyclerView.Adapter<ItemReviewAdapter.ViewHolder>{

    private final List<ItemReview> dataAdapters;
    private final Context context;

    public  ItemReviewAdapter(List<ItemReview> getDataAdapter, Context context){

        super();
        this.dataAdapters = getDataAdapter;
        this.context = context;



    }
    @Override
    public ItemReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback_card, parent, false);


        return new ItemReviewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemReviewAdapter.ViewHolder Viewholder, final int position) {

        final ItemReview dataAdapterOBJ = dataAdapters.get(position);
        Viewholder.userName.setText(dataAdapterOBJ.getCustName());
        Viewholder.star.setText(dataAdapterOBJ.getRatingStar());
        Viewholder.ratingtime.setText(dataAdapterOBJ.getRatingTime());
        Viewholder.variant.setText(dataAdapterOBJ.getProdvariant());
        Viewholder.comment.setText(dataAdapterOBJ.getComment());


    }
    class ViewHolder extends RecyclerView.ViewHolder{


        final TextView userName,star,ratingtime,variant,comment;

        ViewHolder(View itemView) {

            super(itemView);

            userName=itemView.findViewById(R.id.userName);
            star=itemView.findViewById(R.id.starCount);
            ratingtime=itemView.findViewById(R.id.commentDate);
            variant=itemView.findViewById(R.id.prodvariant);
            comment=itemView.findViewById(R.id.feedbackText);


        }
    }
    @Override
    public int getItemCount() {

        return dataAdapters.size();
    }
}
