package com.ecommerce.customer.fypproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ecommerce.customer.fypproject.R;

import java.util.List;

public class OrderViewAdapter extends RecyclerView.Adapter<OrderViewAdapter.ViewHolder>   {

    private final List<Order> dataAdapters;
    ItemDetailViewAdapter.ViewHolder currentView;



    public OrderViewAdapter(List<Order> getDataAdapter, Context context){

        super();
        this.dataAdapters = getDataAdapter;

    }

    @Override
    public OrderViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_card_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OrderViewAdapter.ViewHolder Viewholder, int position) {


        Order dataAdapterOBJ = dataAdapters.get(position);


        Viewholder.orderText.setText(dataAdapterOBJ.getOrderNumber());
        Viewholder.collectText.setText(dataAdapterOBJ.getNumberReadyCollectItem());
        Viewholder.totalItem.setText(dataAdapterOBJ.getTotalItem());
        Viewholder.dateText.setText(dataAdapterOBJ.getDate());
        if(Integer.parseInt(dataAdapterOBJ.getNumberReadyCollectItem())>0){
            Viewholder.notifyLabel.setVisibility(View.VISIBLE);
        }else{
            Viewholder.notifyLabel.setVisibility(View.INVISIBLE);
        }
//
    }
    @Override
    public int getItemCount() {

        return dataAdapters.size();
    }





    class ViewHolder extends RecyclerView.ViewHolder{

       // public Button itemModelName;
       final TextView orderText;
        final TextView totalItem;
        final TextView dateText;
        final TextView collectText;
        final TextView notifyLabel;


        ViewHolder(View itemView) {

            super(itemView);
           // itemModelName = (Button) itemView.findViewById(R.id.itemModelName) ;
            orderText=itemView.findViewById(R.id.orderText);

            totalItem=itemView.findViewById(R.id.totalItemText);
            dateText=itemView.findViewById(R.id.orderdateText);
            collectText=itemView.findViewById(R.id.rdyCollectItemText);
            notifyLabel=itemView.findViewById(R.id.notifyLabel);




        }
    }
}
