package com.ecommerce.customer.fypproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ecommerce.customer.fypproject.R;

import java.util.List;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {

    private final Context context;
    private final List<Friend> dataAdapters;
    private OnItemClick_2 mcallback;

    private String friendid;

    public FriendRequestAdapter(List<Friend> getDataAdapter, Context context, OnItemClick_2 listener){

        super();
        this.dataAdapters = getDataAdapter;
        this.context = context;
        this.mcallback = listener;
        boolean[] temp = new boolean[dataAdapters.size()];
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_card, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final FriendRequestAdapter.ViewHolder Viewholder, final int position) {
        final Friend dataAdapterOBJ = dataAdapters.get(position);

        Glide.with(context)
                .load(R.drawable.profile_pic_icon) // image url
                .apply(new RequestOptions()
                        .placeholder(R.drawable.profile_pic_icon) // any placeholder to load at start
                        .error(R.drawable.profile_pic_icon)  // any image in case of error
                        .override(200, 200) // resizing
                        .centerCrop())
                .into(Viewholder.friendreqpic);

        Viewholder.friendreqname.setText(dataAdapterOBJ.getFriendname());

        Viewholder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendid = dataAdapterOBJ.getFriendid();
                mcallback.onClick("accept", friendid);
            }
        });
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
        final ImageView friendreqpic;
        final TextView friendreqname;
        final Button btnAccept;
        final Button btnDelete;

        ViewHolder (View itemView){
            super(itemView);
            friendreqpic = itemView.findViewById(R.id.friend_req_pic);
            friendreqname = itemView.findViewById(R.id.friend_req_name);
            btnAccept = itemView.findViewById(R.id.btnAcceptReq);
            btnDelete = itemView.findViewById(R.id.btnDeleteReq);
        }
    }
}
