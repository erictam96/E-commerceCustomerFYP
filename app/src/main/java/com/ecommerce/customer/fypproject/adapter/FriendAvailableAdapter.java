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

public class FriendAvailableAdapter extends RecyclerView.Adapter<FriendAvailableAdapter.ViewHolder> {

    private final Context context;
    private final List<Friend> dataAdapters;

    public FriendAvailableAdapter(List<Friend> getDataAdapter, Context context){

        super();
        this.dataAdapters = getDataAdapter;
        this.context = context;
        boolean[] temp = new boolean[dataAdapters.size()];
    }

    @Override
    public FriendAvailableAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_available_card, parent, false);

        FriendAvailableAdapter.ViewHolder viewHolder = new FriendAvailableAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final FriendAvailableAdapter.ViewHolder Viewholder, final int position) {
        final Friend dataAdapterOBJ = dataAdapters.get(position);

        Glide.with(context)
                .load(R.drawable.profile_pic_icon) // image url
                .apply(new RequestOptions()
                        .placeholder(R.drawable.profile_pic_icon) // any placeholder to load at start
                        .error(R.drawable.profile_pic_icon)  // any image in case of error
                        .override(200, 200) // resizing
                        .centerCrop())
                .into(Viewholder.friendpic);

        Viewholder.friendaname.setText(dataAdapterOBJ.getFriendname());
        Viewholder.addfrienddate.setText("Added Friend On: " + dataAdapterOBJ.getAddfrienddate());
    }

    @Override
    public int getItemCount() {
        return dataAdapters.size();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView friendpic;
        final TextView friendaname, addfrienddate;


        ViewHolder (View itemView){
            super(itemView);
            friendpic = itemView.findViewById(R.id.friend_avai_pic);
            friendaname = itemView.findViewById(R.id.friend_avai_name);
            addfrienddate = itemView.findViewById(R.id.friend_add_date);

        }
    }

}
