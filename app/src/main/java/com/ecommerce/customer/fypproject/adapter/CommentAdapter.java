package com.ecommerce.customer.fypproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ecommerce.customer.fypproject.R;

import java.util.List;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context context;
    private List<Comment> dataAdapters;
   //private McallBack mCallback;

    public CommentAdapter(List<Comment> getDataAdapter, Context context){

        super();
        this.dataAdapters = getDataAdapter;
        this.context = context;
        //this.mCallback = listener;
    }


    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_card_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CommentAdapter.ViewHolder Viewholder, final int position) {
        final Comment dataAdapterOBJ = dataAdapters.get(position);
        if(dataAdapterOBJ.getCommentPicUrl()==null) {
            Viewholder.image.setVisibility(View.GONE);
        }
        else{
            Viewholder.image.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(dataAdapterOBJ.getCommentPicUrl()) // image url
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.photo) // any placeholder to load at start
                            .error(R.drawable.photo)  // any image in case of error
                            .override(200, 200) // resizing
                            .centerCrop())
                    .into(Viewholder.image);
        }

        Viewholder.name.setText(dataAdapterOBJ.getCommentUsername());
        Viewholder.comment.setText(dataAdapterOBJ.getCommentDesc());
        Viewholder.date.setText(dataAdapterOBJ.getCommentDate());
        Glide.with(context)
                .load(dataAdapterOBJ.getProfilePicUrl()) // image url
                .apply(new RequestOptions()
                        .placeholder(R.drawable.profile_pic_icon) // any placeholder to load at start
                        .error(R.drawable.profile_pic_icon)  // any image in case of error
                        .override(200, 200) // resizing
                        .centerCrop())
                .into(Viewholder.profilePic);

    }

    @Override
    public int getItemCount() {
        return dataAdapters.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView comment;
        TextView date;
        TextView like;
        TextView reply;
        ImageView profilePic;
        ImageView image;
        ProgressBar progressBar;

        ViewHolder(View itemView){
            super(itemView);
            profilePic=itemView.findViewById(R.id.profile_pic);
            name = itemView.findViewById(R.id.name);
            comment = itemView.findViewById(R.id.comment);
            date = itemView.findViewById(R.id.date);
            like = itemView.findViewById(R.id.commentLike);
            reply = itemView.findViewById(R.id.commentReply);
            image = itemView.findViewById(R.id.image);
            progressBar = itemView.findViewById(R.id.progressbar);

            //mCallback.callback(itemView);


        }
    }

    public void refreshList(){
        notifyItemRangeChanged(0,dataAdapters.size());
        // liked = new boolean[dataAdapters.size()];
        //System.arraycopy(temp,0,liked,0,temp.length);
        //temp = new boolean[dataAdapters.size()];
    }
}


