package com.ecommerce.customer.fypproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ecommerce.customer.fypproject.R;

import java.util.List;

/**
 * Created by leeyipfung on 3/14/2018.
 */

public class ItemDetailViewAdapter extends RecyclerView.Adapter<ItemDetailViewAdapter.ViewHolder>  {

    private final List<Product> dataAdapters;
    private ViewHolder currentView;



    public ItemDetailViewAdapter(List<Product> getDataAdapter, Context context){

        super();
        this.dataAdapters = getDataAdapter;

    }

    @Override
    public ItemDetailViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemDetailViewAdapter.ViewHolder Viewholder, int position) {


        Product dataAdapterOBJ = dataAdapters.get(position);


       Viewholder.itemModelName.setText(dataAdapterOBJ.getProductVariant());

        Viewholder.itemModelName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentView==null){
                    currentView=Viewholder;
                    Viewholder.itemModelName.setEnabled(false);
                }else{
                    currentView.itemModelName.setEnabled(true);
                    Viewholder.itemModelName.setEnabled(false);
                    currentView=Viewholder;
                }

            }
        });



    }

    @Override
    public int getItemCount() {

        return dataAdapters.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        final Button itemModelName;


        ViewHolder(View itemView) {

            super(itemView);
            itemModelName = itemView.findViewById(R.id.itemModelName);




        }
    }
}
