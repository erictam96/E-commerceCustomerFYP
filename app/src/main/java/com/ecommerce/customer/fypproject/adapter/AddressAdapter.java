package com.ecommerce.customer.fypproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ecommerce.customer.fypproject.R;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {
    private final List<Address> dataAdapters;
    private final OnItemClick mCallback;
    private boolean[] checked;
    private int currentPosition=0;

    public  AddressAdapter(List<Address> getDataAdapter, Context context, OnItemClick listener){

        super();
        this.dataAdapters = getDataAdapter;
        this.mCallback = listener;
        checked=new boolean[dataAdapters.size()];
        if(dataAdapters.size()>0){
            checked[0]=true;

        }

    }
    @Override
    public AddressAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_card, parent, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AddressAdapter.ViewHolder Viewholder, final int position) {

        // checked=new boolean[dataAdapters.size()];
        final Address dataAdapterOBJ = dataAdapters.get(position);
        // Viewholder.shopIcon.setImageBitmap(dataAdapterOBJ.getprofBitmap());
        // Viewholder.itemImage.setImageBitmap(dataAdapterOBJ.getCoverBitmap());
        Viewholder.addressText.setText(dataAdapterOBJ.getAddress()+",");
        Viewholder.stateText.setText(dataAdapterOBJ.getState()+",");
        Viewholder.cityText.setText(dataAdapterOBJ.getCity()+",");
        Viewholder.postCode.setText(dataAdapterOBJ.getPostcode());
        Viewholder.fullNameText.setText(dataAdapterOBJ.getName());
        Viewholder.emailText.setText(" ( "+dataAdapterOBJ.getEmail()+" ) ");
        Viewholder.contactNo.setText(dataAdapterOBJ.getContact());
        Viewholder.addressChk.setChecked(checked[Viewholder.getAdapterPosition()]);
        Viewholder.addressChk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPosition=Viewholder.getAdapterPosition();
                refresh();
                //mCallback.onClick(Integer.toString(v.getId()));


            }
        });
//            if(position==0){
//                mCallback.onClick(Integer.toString(Viewholder.addressChk.getId()));
//                Viewholder.addressChk.setChecked(true);
//            }







    }






    class ViewHolder extends RecyclerView.ViewHolder{

        final CheckBox addressChk;
        final TextView addressText;
        final TextView stateText;
        final TextView cityText;
        final TextView postCode;
        final TextView emailText;
        final TextView fullNameText;
        final TextView contactNo;




        ViewHolder(View itemView) {

            super(itemView);

            fullNameText=itemView.findViewById(R.id.cardfullnameText);
            addressText=itemView.findViewById(R.id.cardaddressText);
            stateText=itemView.findViewById(R.id.cardstateText);
            cityText=itemView.findViewById(R.id.cardcityText);
            postCode=itemView.findViewById(R.id.cardpostcodeText);
            emailText=itemView.findViewById(R.id.cardemailText);
            contactNo=itemView.findViewById(R.id.cardcontactText);
            addressChk=itemView.findViewById(R.id.cardaddressChkbox);
        }
    }
    @Override
    public int getItemCount() {

        return dataAdapters.size();
    }
    private void refresh(){

        checked=new boolean[dataAdapters.size()];
        checked[currentPosition]=true;
        if(checked[currentPosition]){
            mCallback.onClick("enable submit");
        }else{
            mCallback.onClick("disable submit");
        }

        notifyItemRangeChanged(0,dataAdapters.size());

    }

    public String getAddressID(){
        return dataAdapters.get(currentPosition).getAddressID();
    }

}
